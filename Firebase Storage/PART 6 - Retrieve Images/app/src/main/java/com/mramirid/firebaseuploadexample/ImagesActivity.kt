package com.mramirid.firebaseuploadexample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_images.*

class ImagesActivity : AppCompatActivity() {

    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = LinearLayoutManager(this)
        rv_images.visibility = View.GONE

        val uploads = ArrayList<Upload>()

        // Referensi ke database
        val databaseReference = FirebaseDatabase.getInstance().getReference("uploads")

        // Listen perubahan di database
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { postSnapshot ->
                    val upload = postSnapshot.getValue(Upload::class.java)
                    uploads.add(upload!!)
                }

                imageAdapter = ImageAdapter(this@ImagesActivity, uploads)
                rv_images.adapter = imageAdapter

                progress_bar.visibility = View.GONE
                rv_images.visibility = View.VISIBLE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ImagesActivity, databaseError.message, Toast.LENGTH_SHORT).show()
                progress_bar.visibility = View.GONE
            }
        })
    }
}
