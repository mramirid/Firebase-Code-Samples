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

    // Referensi ke dokumen
    // private val noteReference = database.collection("Notebook").document("My First Note")

    // bisa juga ke collection saja sebenernya
    // private val noteReference = database.collection("Notebook")

    // Referensi ke dokumen langsung
    private val noteReference = database.document("Notebook/My First Note")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun saveNote(view: View) {
        val title = edt_title.text.toString()
        val description = edt_description.text.toString()

        val note = mapOf<String, Any>(
            KEY_TITLE to title,
            KEY_DESCRIPTION to description
        )

        // Menulis ke firestore
        database.collection("Notebook").document("My First Note").set(note)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@MainActivity, "Error!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, exception.toString())
            }
    }

    fun loadNote(view: View) {
        noteReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val title = documentSnapshot.getString(KEY_TITLE)
                    val description = documentSnapshot.getString(KEY_DESCRIPTION)

                    // Bisa juga untuk mendapatkan data dengan map
                    // val note = documentSnapshot.data

                    tv_data.text = getString(R.string.loadResult, title, description)
                } else {
                    // Jika dokumen tidak ada, biasanya jika kita typo menuliskan referensinya
                    Toast.makeText(this@MainActivity, "Document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@MainActivity, "Error!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, exception.toString())
            }
    }
}
