package com.example.registroturistico

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.registroturistico.database.DatabaseHandler
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.registroturistico.databinding.ActivityMapsBinding
import com.example.sistemacontrolefinancas.entity.Localizacao
import com.google.android.gms.maps.model.BitmapDescriptorFactory

private const val SETTINGS_PATH = "com.example.registroturistico_preferences"

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var sharedPreference: SharedPreferences
    private var zoom = 15f
    private lateinit var mapa: String
    private var _id: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreference = getSharedPreferences(SETTINGS_PATH, Context.MODE_PRIVATE);
        zoom = sharedPreference.getString("zoom_padrao", "15").toString().toFloat();
        mapa = sharedPreference.getString("tipo_mapa", "ROADMAP").toString();

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        _id = intent.getIntExtra("_id", -1)

        binding.btVerPontosDeInteresse.setOnClickListener {
            carregarLocalizacoes()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        when (mapa) {
            "SATELLITE" -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE;
            }
            "HYBRID" -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID;
            }
            "TERRAIN" -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN;
            }
            else -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL;
            }
        }

        // precisa inicializar o mapa pra carregar
        if (_id > -1) {
            binding.btVerPontosDeInteresse.visibility = View.GONE
            return carregarLocalizacoes()
        }

        val latitude = intent.getDoubleExtra("latitude", Double.NaN)
        val longitude = intent.getDoubleExtra("longitude", Double.NaN)

        if (!latitude.isNaN() && !longitude.isNaN()) {
            val currentLocation = LatLng(latitude, longitude)
            mMap.addMarker(MarkerOptions().position(currentLocation).title("Minha Localização"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))
        }
    }

     private fun carregarLocalizacoes() {
         binding.btVerPontosDeInteresse.visibility = View.GONE
         try {
            val dbHandler = DatabaseHandler(this)
            var localizacoes: List<Localizacao> = dbHandler.getListLocalizacoes()

            if (_id > -1) {
                // caso tenha o ID, ele vai filtrar pra mostrar apenas ela
                localizacoes = localizacoes.filter { it._id == _id }
            }

            if (localizacoes.isNotEmpty()) {
                runOnUiThread {
                    localizacoes.forEach { localizacao ->
                        val latLng = LatLng(localizacao.latitude, localizacao.longitude)
                        val markerOptions = MarkerOptions()
                            .position(latLng)
                            .title(localizacao.nome)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                        mMap.addMarker(markerOptions)
                    }

                    val primeiraLocalizacao = localizacoes.first()
                    val primeiroPonto = LatLng(primeiraLocalizacao.latitude, primeiraLocalizacao.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(primeiroPonto, zoom))
                }
            } else {
                runOnUiThread {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Sem Localizações")
                    dialog.setMessage("Nenhuma localização foi encontrada.")
                    dialog.setNeutralButton("OK", null)
                    dialog.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}