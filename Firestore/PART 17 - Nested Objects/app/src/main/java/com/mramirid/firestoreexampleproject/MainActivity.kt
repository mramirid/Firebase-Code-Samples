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

        updateNestedValue()
    }

    fun addNote(view: View) {
        val title = edt_title.text.toString()
        val description = edt_description.text.toString()

        if (edt_priority.length() == 0) {
            edt_priority.setText("0")
        }
        val priority = edt_priority.text.toString().toInt()

        val tagInput = edt_tags.text.toString()
        val tagArray = tagInput.split("\\s*,\\s*".toRegex())  // maksud dari \\s* adalah trim()
        val tags = mutableMapOf<String, Boolean>()

        tagArray.forEach { tag -> tags[tag] = true }

        val note = Note(title, description, priority, tags)

        // Menambah data di firestore, bisa ditambahi listener addOnSuccess dan addOnFailure di akhir
        notebookReference.add(note)
    }

    fun loadNotes(view: View) {
        // Array Query
        notebookReference
            .whereEqualTo("tags.tag1", true) // Menquery data yang tag1 nya bernilai true
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                var data = ""

                for (documentSnapshot in queryDocumentSnapshots) {
                    val note = documentSnapshot.toObject(Note::class.java)
                    note.documentId = documentSnapshot.id

                    data += "ID: ${note.documentId}"

                    for (tag in note.tags!!.keys) {
                        data += "\n-$tag"
                    }

                    data += "\n\n"
                }

                tv_data.text = data
            }
    }

    private fun updateNestedValue() {
        notebookReference.document("AaM6P3IHlhewaLB91g5a")
//            .update("tags.tag1", false)  // Mengubah nilai tag1 menjadi false
//            .update("tags.tag1", FieldValue.delete()) // Menghapus tag1
            .update("tags.tag1.nested.nested1", true) // Karena nested tidak ada di database, maka akan ditambahkan
    }
}
