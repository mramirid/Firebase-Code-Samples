package com.mramirid.firebaseuploadexample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private var imageUri: Uri? = null

    // Referensi ke storage, foto akan disimpan di folder uploads
    private val  storageReference = FirebaseStorage.getInstance().getReference("uploads")
    // Referensi ke database, yakni pada dokumen uploads (dokumen di NoSQL = tabel)
    private val databaseReference = FirebaseDatabase.getInstance().getReference("uploads")

    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_choose_image.setOnClickListener {
            openFileChooser()
        }

        btn_upload.setOnClickListener {
            // Jika uploadTask sedang berjalan
            if (uploadTask != null && uploadTask?.isInProgress!!) {
                Toast.makeText(this@MainActivity, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                uploadFile()
            }
        }

        tv_show_upload.setOnClickListener {
            openImagesActivity()
        }
    }

    /*
    * Buat milih gambar
    * */
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*" // File chooser hanya akan melihat gambar saja
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    /*
    * Menerima gambar yang telah diambil
    * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null) {

            // Ambil uri gambar yang dipilih
            imageUri = data.data

            // Pasang gambar ke tampilan app
            imageUri.let { Picasso.get().load(it).into(img_view) }
        }
    }

    /*
    * Mendapatkan ekstensi dari gambar seperti .jpg .jpeg .png
    * */
    private fun getFileExtension(uri: Uri?): String? {
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun uploadFile() {
        if (imageUri != null) {
            // Set nama file upload, ini adalah nama di storage, misal uploads/blahblah.jpg
            val fileReference = storageReference.child("${System.currentTimeMillis()}.${getFileExtension(imageUri)}")

            // Penugasan ke variabel uploadTask disini untuk mengecek jika upload kita sedang berjalan
            // Agar user tidak bisa upload foto berkali2 ketika menekan tombol upload berkali2
            uploadTask = fileReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    // Pakai handler agar loading 100% tidak langsung diset ke-0, User Experience!
                    Handler().postDelayed({
                        progress_bar.progress = 0
                    }, 500)

                    Toast.makeText(this@MainActivity, "Upload successful", Toast.LENGTH_SHORT).show()

                    // Simpan informasi file yang diupload di storage ke dalam database
                    val firebaseUri = it.storage.downloadUrl
                    firebaseUri.addOnSuccessListener {uri ->
                        val imageUrl = uri.toString()
                        val upload = Upload(edt_file_name.text.toString().trim(), imageUrl)
                        val uploadId = databaseReference.push().key // Mendapatkan id unique
                        databaseReference.child(uploadId!!).setValue(upload) // Upload informasi file
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
                    progress_bar.progress = progress.toInt()
                }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImagesActivity() {
        val intent = Intent(this, ImagesActivity::class.java)
        startActivity(intent)
    }
}
