package com.mramirid.firebaseuploadexample

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_item.view.*

class ImageAdapter(
    private val context: Context,
    private val uploads: List<Upload>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.image_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = uploads.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindUpload(uploads[position])
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        fun bindUpload(uploadCurrent: Upload) {
            itemView.tv_name.text = uploadCurrent.name
            Picasso.get()
                .load(uploadCurrent.imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(itemView.img_upload)

            // Pasang listener untuk normal click, onClick()
            itemView.setOnClickListener(this)
            // Pasang listener untuk long click, onCreateContextMenu()
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(adapterPosition)
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu?, view: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.setHeaderTitle("Select Action")

            // 1 dan 2 maksudnya urutan letak menu
            val doWhatEver = menu?.add(Menu.NONE, 1, 1, "Do whatever")
            val delete = menu?.add(Menu.NONE, 2, 2, "Delete")

            // onMenuItemClick()
            doWhatEver?.setOnMenuItemClickListener(this)
            delete?.setOnMenuItemClickListener(this)
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when(item?.itemId) {
                    1 -> {
                        listener.onWhatEverClick(adapterPosition)
                        return true
                    }
                    2 -> {
                        listener.onDeleteClick(adapterPosition)
                        return true
                    }
                }
            }
            return false
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onWhatEverClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}