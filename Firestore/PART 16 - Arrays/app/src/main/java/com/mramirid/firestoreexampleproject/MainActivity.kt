package com.mramirid.firestoreexampleproject

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()
    // Referensi ke collection Notebook
    private val notebookReference = database.collection("Notebook")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateArray()
    }

    fun addNote(view: View) {
        val title = edt_title.text.toString()
        val description = edt_description.text.toString()

        if (edt_priority.length() == 0) {
            edt_priority.setText("0")
        }
        val priority = edt_priority.text.toString().toInt()

        val tagInput = edt_tags.text.toString()
        val tags = tagInput.split("\\s*,\\s*".toRegex())  // maksud dari \\s* adalah trim()

        val note = Note(title, description, priority, tags)

        // Menambah data di firestore, bisa ditambahi listener addOnSuccess dan addOnFailure di akhir
        notebookReference.add(note)
    }

    fun loadNotes(view: View) {
        // Array Query
        notebookReference
            .whereArrayContains("tags", "tag5") // Note yang punya tag5
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                var data = ""

                for (documentSnapshot in queryDocumentSnapshots) {
                    val note = documentSnapshot.toObject(Note::class.java)
                    note.documentId = documentSnapshot.id

                    data += "ID: ${note.documentId}"

                    for (tag in note.tags!!) {
                        data += "\n-$tag"
                    }

                    data += "\n\n"
                }

                tv_data.text = data
            }
    }

    private fun updateArray() {
        notebookReference.document("IuYa7CiUisZSCXjvSNxc")
//            .update("tags", FieldValue.arrayUnion("new tag")) // Tambah isi array: 'new tag', kalau sudah ada akan diabaikan
            .update("tags", FieldValue.arrayRemove("new tag"))  // Menghapus isi array: 'new tag'
    }
}
