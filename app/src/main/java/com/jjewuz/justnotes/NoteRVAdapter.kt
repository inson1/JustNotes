package com.jjewuz.justnotes

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteRVAdapter(
    val context: Context,
    val noteClickInterface: NoteClickInterface,
    val noteLongClickInterface: NoteLongClickInterface
) :
    RecyclerView.Adapter<NoteRVAdapter.ViewHolder>() {

    lateinit var sharedPref: SharedPreferences
    private lateinit var important: String
    private lateinit var useful: String
    private lateinit var hobby: String
    private lateinit var label1: String
    private lateinit var label2: String

    private val allNotes = ArrayList<Note>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTV: TextView = itemView.findViewById(R.id.idTVNote)
        val descTV: TextView = itemView.findViewById(R.id.notedesc)
        val dateTV: TextView = itemView.findViewById(R.id.idTVDate)
        val categoryChip: Chip = itemView.findViewById(R.id.category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.note_rv_item,
            parent, false
        )
        important = context.getString(R.string.important)
        useful = context.getString(R.string.useful)
        hobby = context.getString(R.string.hobby)
        label1 =
            context.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("label1", context.getString(R.string.extra_label))
                .toString()
        label2 =
            context.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("label2", context.getString(R.string.extra_label))
                .toString()
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        sharedPref = holder.descTV.context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isPreview = sharedPref.getBoolean("enabledPreview", false)

        holder.noteTV.text = allNotes[position].noteTitle
        val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm", Locale.getDefault())
        val date = allNotes[position].timeStamp
        val currentDateAndTime: String = try {
            sdf.format(date.toLong())
        } catch (e: Exception){
            date
        }

        holder.dateTV.text = context.getString(R.string.lastedit) + currentDateAndTime
        val currCategory = allNotes[position].label
        holder.categoryChip.visibility = View.GONE
        if(currCategory != "") {
            holder.categoryChip.visibility = View.VISIBLE
            when (currCategory) {
                "important" -> {
                    holder.categoryChip.text = important
                }
                "useful" -> {
                    holder.categoryChip.text = useful
                }
                "hobby" -> {
                    holder.categoryChip.text = hobby
                }
                "label1" -> {
                    holder.categoryChip.text = label1
                }
                "label2" -> {
                    holder.categoryChip.text = label2
                }
            }
        }

        if (isPreview){
            holder.descTV.text = Utils.fromHtml(allNotes[position].noteDescription.take(400))
        } else{
            holder.descTV.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            noteClickInterface.onNoteClick(allNotes[position], position)
        }

        holder.itemView.setOnLongClickListener {
            noteLongClickInterface.onNoteLongClick(allNotes[position])
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return allNotes.size
    }

    fun updateList(newList: List<Note>) {

        allNotes.clear()
        allNotes.addAll(newList)
        notifyDataSetChanged()
    }
}


interface NoteClickInterface {
    fun onNoteClick(note: Note, num: Int)
}

interface NoteLongClickInterface {
    fun onNoteLongClick(note: Note)
}




