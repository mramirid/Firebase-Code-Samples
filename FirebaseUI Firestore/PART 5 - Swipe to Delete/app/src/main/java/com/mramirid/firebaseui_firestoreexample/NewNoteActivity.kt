package com.mramirid.firebaseui_firestoreexample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_note.*

class NewNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close) // Mengganti tanda <- menjadi x
        title = "Add Note"

        // Set rentang dari number picker 1-10
        number_picker_priority.minValue = 1
        number_picker_priority.maxValue = 10
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_note -> {
                saveNote()
                true // Memberi tahu sistem kalau kita nekan menu ini
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        val title = edt_title.text.toString()
        val description = edt_description.text.toString()
        val priority = number_picker_priority.value

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show()
            return
        }

        val notebookRef = FirebaseFirestore.getInstance().collection("Notebook")

        notebookRef.add(Note(title, description, priority))

        Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
        finish()
    }
}
