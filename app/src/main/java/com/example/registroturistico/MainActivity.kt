package com.example.registroturistico

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import android.view.View
import androidx.activity.enableEdgeToEdge

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var tvLatitude : TextView
    private lateinit var tvLongitude : TextView

    private lateinit var locationManager : LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvLatitude = findViewById( R.id.etLatitude )
        tvLongitude = findViewById( R.id.etLongitude )

        locationManager = getSystemService( Context.LOCATION_SERVICE ) as LocationManager


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
            locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,
                0, 0f, this );
        }

    }

    fun btVerMapaOnClick(view: View) {
        val intent = Intent(this, MapsActivity::class.java)

        val latitude = tvLatitude.text.toString().toDoubleOrNull()
        val longitude = tvLongitude.text.toString().toDoubleOrNull()

        if (latitude != null && longitude != null) {
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
        }

        startActivity(intent)
    }


    override fun onLocationChanged(location: Location) {
        tvLatitude.text = location.latitude.toString()
        tvLongitude.text = location.longitude.toString()
    }
}