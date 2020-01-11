package com.mramirid.firebaseui_firestoreexample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // Referensi ke database
    private val database = FirebaseFirestore.getInstance()
    // Referensi ke collection
    private val notebookRef = database.collection("Notebook")

    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_add_note.setOnClickListener {
            startActivity(Intent(this, NewNoteActivity::class.java))
        }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        // Spesify query
        val query = notebookRef.orderBy("priority", Query.Direction.DESCENDING)
        // Bagaimana query diinputkan ke adapter?
        val options = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java)
            .build()
        // Set adapter
        adapter = NoteAdapter(options)

        // Setup RecyclerView
        rv_notes.setHasFixedSize(true)
        rv_notes.layoutManager = LinearLayoutManager(this)
        rv_notes.adapter = adapter

        // Swipe, dengan arah swipe hanya LEFT & RIGHT
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.deleteItem(viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(rv_notes)
    }

    override fun onStart() {
        super.onStart()
        // Listener adapter
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}
