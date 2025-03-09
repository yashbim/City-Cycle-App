package com.example.city_cycle_app

import DatabaseHelper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        dbHelper = DatabaseHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        showBikeStations()
    }

    private fun showBikeStations() {
        val stations = listOf(
            Triple("Colombo", 6.9271, 79.8612),
            Triple("Gampaha", 7.0873, 80.0144),
            Triple("Kandy", 7.2906, 80.6337),
            Triple("Galle", 6.0329, 80.2168),
            Triple("Jaffna", 9.6615, 80.0255),
            Triple("Kurunegala", 7.4863, 80.3647),
            Triple("Ratnapura", 6.6828, 80.3992),
            Triple("Anuradhapura", 8.3114, 80.4037),
            Triple("Badulla", 6.9934, 81.0556),
            Triple("Trincomalee", 8.5874, 81.2152)
        )

        for ((name, lat, lng) in stations) {
            val location = LatLng(lat, lng)
            val bikesAvailable = dbHelper.getBikeCount(name)
            mMap.addMarker(MarkerOptions().position(location).title("$name - $bikesAvailable bikes available"))
        }

        // Move camera to Sri Lanka
        val sriLanka = LatLng(7.8731, 80.7718)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f))
    }
}
