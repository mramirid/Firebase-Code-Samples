package com.mramirid.firebaseui_firestoreexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.note_item.view.*

class NoteAdapter(options: FirestoreRecyclerOptions<Note>) :
    FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder>(options) {

    private lateinit var listener: OnItemClickListener

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

        // Listener ketika card ditekan
        view.setOnClickListener {
            val position = holder.adapterPosition

            /* Ketika kita delete item nih, bisa jadi user neken item itu lagi ketika transisi penghapusan
               masih jalan, app bakal crash. Tapi bisa dicegah sama ini, pengecekan RecyclerView.NO_POSITION */
            if (position != RecyclerView.NO_POSITION && this::listener.isInitialized) {
                listener.onItemClick(snapshots.getSnapshot(position), position)
            }
        }
    }

    fun deleteItem(position: Int) {
        // Snapshot itu bentuknya dokumen
        snapshots.getSnapshot(position).reference.delete()
    }

    inner class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnItemClickListener {
        fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}