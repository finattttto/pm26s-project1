package com.example.registroturistico

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.registroturistico.database.DatabaseHandler
import com.example.sistemacontrolefinancas.entity.Localizacao
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.app.ProgressDialog

private const val LOCATION_PERMISSION_REQUEST_CODE = 1
public const val API_KEY_GEO = "AIzaSyC4fATB3MfFoI_QDU0d4m8o--odmnTFcKU"

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager : LocationManager

    private val CAMERA_REQUEST_CODE = 2
    private lateinit var photoUri: Uri
    private val photoUris = mutableListOf<Uri>()

    private var latitude = 0.0
    private var longitude = 0.0

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        locationManager = getSystemService( Context.LOCATION_SERVICE ) as LocationManager

        progressDialog = ProgressDialog(this).apply {
            setMessage("Obtendo localização...")
            setCancelable(false)
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        } else {
            progressDialog.show()
            locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,
                0, 0f, this );
        }

    }

    fun btVerMapaOnClick(view: View) {
        val intent = Intent(this, MapsActivity::class.java)

        if (latitude != null && longitude != null) {
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
        }

        startActivity(intent)
    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        progressDialog.dismiss()
        locationManager.removeUpdates(this)
    }

    private fun fazerRequisicaoHttp(urlString: String): String? {
        return try {
            val url = URL(urlString)
            val urlConnection = url.openConnection()
            val inputStream = urlConnection.getInputStream()
            val entrada = BufferedReader(InputStreamReader(inputStream))

            val dados = StringBuilder()
            var linha = entrada.readLine()
            while (linha != null) {
                dados.append(linha)
                linha = entrada.readLine()
            }
            dados.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun obterEnderecoAtual(callback: (String) -> Unit) {
        if (latitude != null && longitude != null) {
            Thread {
                val enderecoUrl = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=$latitude,$longitude&key=$API_KEY_GEO"
                val dados = fazerRequisicaoHttp(enderecoUrl)

                val local = dados?.substring(
                    dados.indexOf("<formatted_address>") + 19,
                    dados.indexOf("</formatted_address>")
                ) ?: "Endereço não encontrado."

                runOnUiThread {
                    callback(local)
                }
            }.start()
        } else {
            callback("Coordenadas inválidas.")
        }
    }

    fun btVerEnderecoOnClick(view: View) {
        obterEnderecoAtual { endereco ->
            AlertDialog.Builder(this)
                .setTitle("Endereço:")
                .setMessage(endereco)
                .setNeutralButton("OK", null)
                .setCancelable(false)
                .show()
        }
    }

    // CONFIGURAÇÃO

    fun btGoToCfgOnClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    // CÂMERA

    fun btGoToCamera(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoUri = createImageUri()
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun createImageUri(): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "foto_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val dbHandler = DatabaseHandler(this)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            obterEnderecoAtual { endereco ->
                val localizacao = Localizacao(
                    _id = 0,
                    longitude = longitude,
                    latitude = latitude,
                    nome = endereco ?: "Sem nome",
                    dataAdd = getCurrentDateTime(),
                    imageUri = photoUri.toString()
                )

                dbHandler.insert(localizacao)

                Toast.makeText(this, "Foto salva no banco!\nEndereço: $endereco", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun btGoToGaleryOnClick(view: View) {
        val intent = Intent(this, GaleryActivity::class.java)
        startActivity(intent)
    }

    fun getCurrentDateTime(): String {
        val date = Date()
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) // Formato legível
        return formatter.format(date)
    }
}