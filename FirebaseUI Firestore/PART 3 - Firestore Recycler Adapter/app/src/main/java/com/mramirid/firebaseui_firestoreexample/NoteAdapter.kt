package com.mramirid.firebaseui_firestoreexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.note_item.view.*

class NoteAdapter(options: FirestoreRecyclerOptions<Note>) :
    FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        return NoteHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.note_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int, model: Note) {
        val view = holder.itemView

        view.tv_title.text = model.title
        view.tv_description.text = model.description
        view.tv_priority.text = model.priority.toString()
    }

    inner class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}