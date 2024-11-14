package com.example.registroturistico

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class GalleryAdapter(
    private val photoUris: List<Uri>,
    private val onItemClick: (Uri) -> Unit,
    private val onItemLongClick: (Uri) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photoUris[position])
    }

    override fun getItemCount() = photoUris.size

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(uri: Uri) {
            imageView.setImageURI(uri)
            itemView.setOnClickListener {
                onItemClick(uri)
            }
            itemView.setOnLongClickListener {
                onItemLongClick(uri)
                true
            }
        }
    }
}