package com.example.registroturistico

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GaleryActivity : AppCompatActivity() {

    private lateinit var photoUris:  MutableList<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_galery)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        photoUris = loadPhotos().toMutableList()

        val recyclerView = findViewById<RecyclerView>(R.id.rvGallery)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = GalleryAdapter(photoUris, { photoUri ->
            showPhotoDetailDialog(photoUri)
        }, { photoUri ->
            showDeleteDialog(photoUri)
        })
    }
    private fun showDeleteDialog(photoUri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Foto")
            .setMessage("Deseja excluir esta foto?")
            .setPositiveButton("Sim") { _, _ ->
                deletePhoto(photoUri)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deletePhoto(photoUri: Uri) {
        contentResolver.delete(photoUri, null, null)
        photoUris.remove(photoUri)
        findViewById<RecyclerView>(R.id.rvGallery).adapter?.notifyDataSetChanged()
        Toast.makeText(this, "Foto excluída!", Toast.LENGTH_SHORT).show()
    }

    private fun loadPhotos(): List<Uri> {
        val uris = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                uris.add(uri)
            }
        }

        return uris
    }

    private fun showPhotoDetailDialog(photoUri: Uri) {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_photo_detail, null)
        dialog.setContentView(dialogView)

        val imageView = dialogView.findViewById<ImageView>(R.id.ivDialogPhoto)
        val textViewInfo = dialogView.findViewById<TextView>(R.id.tvDialogInfo)

        imageView.setImageURI(photoUri)
        textViewInfo.text = "Informações da foto"

        dialog.show()
    }
}
