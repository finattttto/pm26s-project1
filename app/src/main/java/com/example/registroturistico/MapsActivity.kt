package com.example.registroturistico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.registroturistico.databinding.ActivityMapsBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btVerPontosDeInteresse.setOnClickListener {

            binding.btVerPontosDeInteresse.visibility = View.GONE

            val latitude = intent.getDoubleExtra("latitude", Double.NaN)
            val longitude = intent.getDoubleExtra("longitude", Double.NaN)

            if (!latitude.isNaN() && !longitude.isNaN()) {
                buscarPontosDeInteresse(latitude, longitude, "tourist_attraction, museum, restaurant, cafe, park, point_of_interest")
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latitude = intent.getDoubleExtra("latitude", Double.NaN)
        val longitude = intent.getDoubleExtra("longitude", Double.NaN)
        val coordenadas = intent.getParcelableArrayListExtra<LatLng>("coordenadas")

        if (!latitude.isNaN() && !longitude.isNaN()) {
            val currentLocation = LatLng(latitude, longitude)
            mMap.addMarker(MarkerOptions().position(currentLocation).title("Minha Localização"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        }

        coordenadas?.forEach { local ->
            mMap.addMarker(MarkerOptions().position(local).title("Ponto de Interesse"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 15f))
        }
    }


    fun buscarPontosDeInteresse(latitude: Double, longitude: Double, tipo: String) {
        val apiKey = "AIzaSyABWyLQa1HmxEQuzy6K1_hRv_zarJFExYk"
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=1500&type=$tipo&key=$apiKey"

        Thread {
            try {
                val urlConnection = URL(url).openConnection()
                val inputStream = urlConnection.getInputStream()
                val reader = BufferedReader(InputStreamReader(inputStream))
                val resultado = StringBuilder()
                var linha = reader.readLine()

                while (linha != null) {
                    resultado.append(linha)
                    linha = reader.readLine()
                }

                val json = JSONObject(resultado.toString())
                val resultados = json.getJSONArray("results")

                runOnUiThread {
                    for (i in 0 until resultados.length()) {
                        val lugar = resultados.getJSONObject(i)
                        val nome = lugar.getString("name")
                        val endereco = lugar.optString("vicinity", "Endereço não disponível")
                        val location = lugar.getJSONObject("geometry").getJSONObject("location")
                        val lat = location.getDouble("lat")
                        val lng = location.getDouble("lng")

                        val latLng = LatLng(lat, lng)
                        mMap.addMarker(MarkerOptions().position(latLng).title(nome))
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Erro")
                    dialog.setMessage("Não foi possível buscar pontos de interesse.")
                    dialog.setNeutralButton("OK", null)
                    dialog.show()
                }
            }
        }.start()
    }
    //-22.95198 -43.2105 rio
}