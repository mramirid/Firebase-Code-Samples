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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun saveNote(view: View) {
        val title = edt_title.text.toString()
        val description = edt_description.text.toString()

        // Karena data di firestore berbentuk key-value pair, maka container yang
        // tepat untuk data kita adalah map
        val note = mapOf<String, Any>(
            KEY_TITLE to title,
            KEY_DESCRIPTION to description
        )

        // Menulis ke collection 'Notebook' dan di dalamnya dokumen 'My First Note'
        database.collection("Notebook").document("My First Note").set(note)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@MainActivity, "Error!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, exception.toString())
            }
        // Versi simple & nested
        // database.collection("Notebook/My First Note") -> Versi simple
        // database.collection("...").document("...").collection("...").document("...").set(note) -> Bisa nested
    }
}
