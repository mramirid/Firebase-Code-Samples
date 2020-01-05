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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_images.*

class ImagesActivity : AppCompatActivity(), ImageAdapter.OnItemClickListener {

    private val uploads = ArrayList<Upload>()   // List item
    private val imageAdapter = ImageAdapter(this, uploads)  // Set adapter

    // Referensi ke storage
    private val storageReference = FirebaseStorage.getInstance()
    // Referensi ke database
    private val databaseReference = FirebaseDatabase.getInstance().getReference("uploads")
    // Listener perubahan di database, tujuannya agar listener bisa dicabut ketika activity onDestroy
    private lateinit var databaseListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = LinearLayoutManager(this)
        rv_images.visibility = View.GONE

        // Pasang adapter ke Recyclerview
        rv_images.adapter = imageAdapter

        // Pasang listener untuk tiap item
        imageAdapter.setOnItemClickListener(this)

        // Listen perubahan di database
        databaseListener = databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                uploads.clear() // Clear dulu

                // Salin data yang telah didownload ke dalam list uploads
                dataSnapshot.children.forEach { postSnapshot ->
                    val upload = postSnapshot.getValue(Upload::class.java)
                    upload?.key = postSnapshot.key!!
                    uploads.add(upload!!)
                }

                imageAdapter.notifyDataSetChanged() // Update RecyclerView

                // Hilangkan progress bar & tampilkan Recyclerview ketika sudah selesai
                progress_bar.visibility = View.GONE
                rv_images.visibility = View.VISIBLE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ImagesActivity, databaseError.message, Toast.LENGTH_SHORT).show()
                progress_bar.visibility = View.GONE
            }
        })
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Normal click at position: $position", Toast.LENGTH_SHORT).show()
    }

    override fun onWhatEverClick(position: Int) {
        Toast.makeText(this, "WhatEver click at position: $position", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteClick(position: Int) {
        val selectedItem = uploads[position]
        val selectedKey = selectedItem.key

        // Lakukan penghapusan gambar di storage berdasarkan url gambar
        val imageReference = storageReference.getReferenceFromUrl(selectedItem.imageUrl)
        imageReference.delete().addOnSuccessListener {
            // Setelah gambar di storage terhapus lakukan penghapusan juga di database
            databaseReference.child(selectedKey!!).removeValue()
            Toast.makeText(this@ImagesActivity, "Item deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseReference.removeEventListener(databaseListener)
    }
}
