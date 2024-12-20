package com.example.registroturistico

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
        val textViewDescricao = dialogView.findViewById<TextView>(R.id.tvDescricao)
        val textViewDate = dialogView.findViewById<TextView>(R.id.tvDialogDate)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
        val btnEdit = dialogView.findViewById<Button>(R.id.btnEdit)
        val btnViewOnMap = dialogView.findViewById<Button>(R.id.btnViewOnMap)


        val imageUri = Uri.parse(localizacao.imageUri)
        imageView.setImageURI(imageUri)
        textViewInfo.text = localizacao.nome
        textViewDate.text = localizacao.dataAdd.toString()
        textViewDescricao.text = localizacao.descricao

        btnDelete.setOnClickListener {
            showDeleteDialog(localizacao)
        }

        btnEdit.setOnClickListener{
            dialog.dismiss()
            showEditDialog(localizacao)
        }

        btnViewOnMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("_id", localizacao._id)
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }



    private fun showEditDialog(localizacao: Localizacao) {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_photo_edit_insert, null)
        dialog.setContentView(dialogView)

        val editTextDescricao = dialogView.findViewById<EditText>(R.id.etDescription)
        val imageView = dialogView.findViewById<ImageView>(R.id.ivDialogPhoto)
        val textViewInfo = dialogView.findViewById<TextView>(R.id.tvDialogInfo)
        val textViewDate = dialogView.findViewById<TextView>(R.id.tvDialogDate)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSalvar)

        val imageUri = Uri.parse(localizacao.imageUri)
        imageView.setImageURI(imageUri)
        textViewInfo.text = localizacao.nome
        textViewDate.text = localizacao.dataAdd.toString();


        btnCancel.setOnClickListener {
            dialog.dismiss();
        }

        btnSave.setOnClickListener {
            localizacao.descricao = editTextDescricao.text.toString()
            val dbHandler = DatabaseHandler(this)
            dbHandler.update(localizacao);
            dbHandler.close();
            dialog.dismiss();
            Toast.makeText(
                this,
                "Foto Alterada no banco!\nEndereço: ${localizacao.nome}",
                Toast.LENGTH_LONG
            ).show()
        }

        dialog.show()
    }

}
