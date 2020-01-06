package com.mramirid.firestoreexampleproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
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
    // Referensi ke dokumen langsung
    private val noteReference = database.document("Notebook/My First Note")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        // Memasang listener untuk mendownload data secara realtime
        // Argumen this agar listener ini dapat mendetach secara otomatis mengikuti lifecycle MainActivity
        noteReference.addSnapshotListener(this) { documentSnapshot, exception ->
            if (exception != null) {
                Toast.makeText(this@MainActivity, "Error while loading!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, exception.toString())
                return@addSnapshotListener
            }

            // Jika data ada setelah terunduh
            if (documentSnapshot?.exists()!!) {
                // Memasukan object yang diterima ke dalam object buatan kita
                val note = documentSnapshot.toObject(Note::class.java)

                tv_data.text = getString(R.string.loadResult, note?.title, note?.description)
            } else {
                // Jika data tidak ada
                 tv_data.text = ""
            }
        }
    }

    fun saveNote(view: View) {
        val title = edt_title.text.toString()
        val description = edt_description.text.toString()

        val note = Note(title, description)

        // Menulis ke firestore, set akan mereplace seluruh isi dokumen
        noteReference.set(note)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@MainActivity, "Error!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, exception.toString())
            }
    }

    fun updateDescription(view: View) {
        val description = edt_description.text.toString()

        // val note = mapOf<String, Any>(KEY_DESCRIPTION to description)

        // Karena method set akan mereplace dokumen, SetOptions.merge() berguna agar set tidak
        // mereplace melainkan menggabungkan sehingga title tidak hilang
        // noteReference.set(note, SetOptions.merge())

        // Cara bener nih. Hanya akan berfungsi jika data ada di firestore
        // noteReference.update(note)

        // Karena update 1 saja maka cukup passing langsung tanpa map
        noteReference.update(KEY_DESCRIPTION, description)
    }

    fun deleteDescription(view: View) {
        // Bisa gini
        // val note = mapOf<String, Any>(KEY_DESCRIPTION to FieldValue.delete())
        // noteReference.update(note)

        // Simpel, bisa ditambahi listener addOnSuccess dan addOnFailure di akhir
        noteReference.update(KEY_DESCRIPTION, FieldValue.delete())
    }

    fun deleteNote(view: View) {
        // Menghapus note, bisa ditambahi listener addOnSuccess dan addOnFailure di akhir
        noteReference.delete()
    }

    fun loadNote(view: View) {
        noteReference.get()
            .addOnSuccessListener { documentSnapshot ->
                // Jika data berhasil di unduh
                if (documentSnapshot.exists()) {
                    // Memasukan object yang diterima ke dalam object buatan kita
                    val note = documentSnapshot.toObject(Note::class.java)

                    tv_data.text = getString(R.string.loadResult, note?.title, note?.description)
                } else {
                    // Jika dokumen tidak ada, biasanya jika kita typo menuliskan referensinya
                    Toast.makeText(this@MainActivity, "Document does not exist", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@MainActivity, "Error!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, exception.toString())
            }
    }
}
