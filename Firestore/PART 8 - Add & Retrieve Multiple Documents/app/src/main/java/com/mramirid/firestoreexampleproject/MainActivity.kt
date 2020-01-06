package com.mramirid.firestoreexampleproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"

        // Untuk keperluan tulis ke firestore
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
    }

    private val database = FirebaseFirestore.getInstance()
    // Referensi ke collection Notebook
    private val notebookReference = database.collection("Notebook")
    // Referensi ke dokumen langsung
    private val noteReference = database.document("Notebook/My First Note")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        // Ingat this menyatakan activity owner
        notebookReference.addSnapshotListener(this) { queryDocumentSnapshots, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Toast.makeText(this@MainActivity, "Error while loading!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, firebaseFirestoreException.toString())
                return@addSnapshotListener
            }

            var data = ""
            queryDocumentSnapshots?.forEach { documentSnapshot ->
                val note = documentSnapshot.toObject(Note::class.java)

                // Mendapatkan id note dari database
                note.documentId = documentSnapshot.id

                data += getString(R.string.loadResult, note.documentId, note.title, note.description)
            }

            tv_data.text = data
        }
    }

    fun addNote(view: View) {
        val title = edt_title.text.toString()
        val description = edt_description.text.toString()

        val note = Note(title, description)

        // Menambah data di firestore, bisa ditambahi listener addOnSuccess dan addOnFailure di akhir
        notebookReference.add(note)
    }

    fun loadNotes(view: View) {
        notebookReference.get()
            .addOnSuccessListener { querySnapshot ->
                var data = ""
                // querySnapshot berisi seluruh isi collection Notebook
                querySnapshot.forEach { documentSnapshot ->
                    val note = documentSnapshot.toObject(Note::class.java)

                    // Mendapatkan id note dari database
                    note.documentId = documentSnapshot.id

                    data += getString(R.string.loadResult, note.documentId, note.title, note.description)
                }

                tv_data.text = data
            }
    }
}
