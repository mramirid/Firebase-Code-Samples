package com.mramirid.firestoreexampleproject

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()
    // Referensi ke collection Notebook
    private val notebookReference = database.collection("Notebook")

    // Untuk pagination, menandai dokumen lanjutan (checkpoint)
    private lateinit var lastResult: DocumentSnapshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun addNote(view: View) {
        val title = edt_title.text.toString()
        val description = edt_description.text.toString()

        if (edt_priority.length() == 0) {
            edt_priority.setText("0")
        }

        val priority = edt_priority.text.toString().toInt()

        val note = Note(title, description, priority)

        // Menambah data di firestore, bisa ditambahi listener addOnSuccess dan addOnFailure di akhir
        notebookReference.add(note)
    }

    fun loadNotes(view: View) {
//        notebookReference.orderBy("priority")
//            .orderBy("title")
//            .startAt(3, "Title2") // Dimulai dari priority = 3 dengan title = Title2
//            .startAt(3) // Dimulai pada priority = 3

        val query = if (!this::lastResult.isInitialized) {
            // Jika lastResult belum terisi berati kita belum menerima data sekalipun
            notebookReference.orderBy("priority")
                    .limit(3)
            } else {
                notebookReference.orderBy("priority")
                    .startAfter(lastResult) // Jika lastResult ada, gunakan sebagai checkpoint
                    .limit(3)
            }

        query.get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                var data = ""

                for (documentSnapshot in queryDocumentSnapshots) {
                    val note = documentSnapshot.toObject(Note::class.java)
                    note.documentId = documentSnapshot.id

                    data += getString(R.string.loadResult, note.documentId, note.title, note.description, note.priority)
                }

                if (queryDocumentSnapshots.size() > 0) { // pagination dilakukan jika ada data
                    data += "---------------\n\n"   // Pemisah paging
                    tv_data.append(data)

                    // Simpan dokumen yang terakhir didapat (checkpoint)
                    lastResult = queryDocumentSnapshots.documents[queryDocumentSnapshots.size() - 1]
                }
            }
    }
}
