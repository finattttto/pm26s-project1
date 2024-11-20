package com.example.registroturistico

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Button
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
import com.example.registroturistico.database.DatabaseHandler
import com.example.sistemacontrolefinancas.entity.Localizacao

class GaleryActivity : AppCompatActivity() {

    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var locations: List<Localizacao>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_galery)

        databaseHandler = DatabaseHandler(this)

        locations = databaseHandler.getListLocalizacoes()

        val recyclerView = findViewById<RecyclerView>(R.id.rvGallery)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = GalleryAdapter(locations, { location ->
            showPhotoDetailDialog(location)
        }, { location ->
            showDeleteDialog(location)
        })

    }

    private fun showDeleteDialog(localizacao: Localizacao) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Foto")
            .setMessage("Deseja excluir esta foto?")
            .setPositiveButton("Sim") { _, _ ->
                deletePhoto(localizacao)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deletePhoto(localizacao: Localizacao) {
            databaseHandler.delete(localizacao._id)
            locations = databaseHandler.getListLocalizacoes()
            findViewById<RecyclerView>(R.id.rvGallery).adapter?.notifyDataSetChanged()
            Toast.makeText(this, "Foto excluída!", Toast.LENGTH_SHORT).show()

    }

    private fun showPhotoDetailDialog(localizacao: Localizacao) {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_photo_detail, null)
        dialog.setContentView(dialogView)

        val imageView = dialogView.findViewById<ImageView>(R.id.ivDialogPhoto)
        val textViewInfo = dialogView.findViewById<TextView>(R.id.tvDialogInfo)
        val textViewDate = dialogView.findViewById<TextView>(R.id.tvDialogDate)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
        val btnViewOnMap = dialogView.findViewById<Button>(R.id.btnViewOnMap)

        val imageUri = Uri.parse(localizacao.imageUri)
        imageView.setImageURI(imageUri)
        textViewInfo.text = localizacao.nome
        textViewDate.text = localizacao.dataAdd.toString();

        btnDelete.setOnClickListener {
            showDeleteDialog(localizacao)
        }

        btnViewOnMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("_id", localizacao._id)
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }

}
