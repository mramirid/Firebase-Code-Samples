package com.mramirid.firestoreexampleproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val database = FirebaseFirestore.getInstance()
    // Referensi ke collection Notebook
    private val notebookReference = database.collection("Notebook")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        // Kita juga bisa menambahkan query di snapshotListener
        // notebookReference.whereEqualTo()

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

                data += getString(R.string.loadResult, note.documentId, note.title, note.description, note.priority)
            }

            tv_data.text = data
        }
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
        // 1 Query by default adalah index
        // Custom index diperlukan jika kita mengkombinasikan 2 query: range, syarat nilai atribut dan multiple order by

        // Firestore tidak memiliki logika OR melainkan hanya AND

        // ----- Simulasi alternatif logika OR -----
        val task1 = notebookReference.whereLessThan("priority", 2)
            .orderBy("priority")
            .get()

        val task2 = notebookReference.whereGreaterThan("priority", 2)
            .orderBy("priority")
            .get()

        // Mengecek selesai tidaknya kedua task di atas
        // Tasks.whenAll jika semua task complete, menerima success / failure
        // Tasks.whenAllSuccess jika semua task success saja, tidak bisa onFailure, jika gagal maka result tidak diberikan
        // Tasks.whenAllComplete sama seperti whenAllSuccess tapi bisa ditambahin onFailure listener
        val allTasks: Task<List<QuerySnapshot>> = Tasks.whenAllSuccess(task1, task2)
        allTasks.addOnSuccessListener { querySnapshots ->
            // Parameter querySnapshots adalah List hasil dari 2 task
            var data = ""

            querySnapshots.forEach { queryDocumentSnapshots -> // querySnapshot berisi seluruh isi collection Notebook (dokumen-dokumen)
                queryDocumentSnapshots.forEach { documentSnapshot ->
                    val note = documentSnapshot.toObject(Note::class.java)

                    // Mendapatkan id note dari database
                    note.documentId = documentSnapshot.id

                    data += getString(R.string.loadResult, note.documentId, note.title, note.description, note.priority)
                }
            }

            tv_data.text = data
        }
    }
}
