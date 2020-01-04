package com.mramirid.firebaseuploadexample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_choose_image.setOnClickListener {
            openFileChooser()
        }

        btn_upload.setOnClickListener {

        }

        tv_show_upload.setOnClickListener {

        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*" // File chooser hanya akan melihat gambar saja
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data?.data != null) {

            imageUri = data.data!!

            imageUri.let { Picasso.get().load(it).into(img_view) }
        }
    }
}
