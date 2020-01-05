package com.mramirid.firebaseuploadexample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_item.view.*

class ImageAdapter(
    private val context: Context,
    private val uploads: List<Upload>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item, parent, false))
    }

    override fun getItemCount() = uploads.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindUpload(uploads[position])
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindUpload(uploadCurrent: Upload) {
            itemView.tv_name.setText(uploadCurrent.name)
            Picasso.get()
                .load(uploadCurrent.imageUrl)
                .fit()
                .centerCrop()
                .into(itemView.img_upload)
        }
    }
}