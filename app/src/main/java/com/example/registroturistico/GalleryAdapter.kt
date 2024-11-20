package com.example.registroturistico

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemacontrolefinancas.entity.Localizacao

class GalleryAdapter(
    private val locations: List<Localizacao>,
    private val onItemClick: (Localizacao) -> Unit,
    private val onItemLongClick: (Localizacao) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount() = locations.size

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(location: Localizacao) {
            val uri = Uri.parse(location.imageUri)
            imageView.setImageURI(uri)

            itemView.setOnClickListener {
                onItemClick(location)
            }

            itemView.setOnLongClickListener {
                onItemLongClick(location)
                true
            }
        }
    }
}
