package com.example.city_cycle_app

import DatabaseHelper
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get the SupportMapFragment and request map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Check and request location permissions
        checkLocationPermission()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable the "My Location" layer if permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }

        // Get user's location and update map
        getUserLocation()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed to get location
            getUserLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable location layer and get location
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mMap.isMyLocationEnabled = true
                getUserLocation()
            } else {
                // Permission denied, fallback to Sri Lanka
                val sriLanka = LatLng(7.8731, 80.7718)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f))
                showBikeStations()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
                    showBikeStations()
                } else {
                    // Location null, fallback to Sri Lanka
                    val sriLanka = LatLng(7.8731, 80.7718)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f))
                    showBikeStations()
                }
            }.addOnFailureListener { e ->
                android.util.Log.e("MapsActivity", "Failed to get location", e)
                // Fallback to Sri Lanka on failure
                val sriLanka = LatLng(7.8731, 80.7718)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f))
                showBikeStations()
            }
        } else {
            // Permission not granted yet, will be handled in onRequestPermissionsResult
            val sriLanka = LatLng(7.8731, 80.7718)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f))
            showBikeStations()
        }
    }

    private fun showBikeStations() {
        // Define coordinates for stations (since database doesn’t store lat/lng)
        val stationCoordinates = mapOf(
            "Colombo" to LatLng(6.9271, 79.8612),
            "Gampaha" to LatLng(7.0873, 80.0144),
            "Kandy" to LatLng(7.2906, 80.6337),
            "Galle" to LatLng(6.0329, 80.2168),
            "Jaffna" to LatLng(9.6615, 80.0255),
            "Kurunegala" to LatLng(7.4863, 80.3647),
            "Ratnapura" to LatLng(6.6828, 80.3992),
            "Anuradhapura" to LatLng(8.3114, 80.4037),
            "Badulla" to LatLng(6.9934, 81.0556),
            "Trincomalee" to LatLng(8.5874, 81.2152)
        )

        // Fetch stations from database
        val stations = dbHelper.getBikeStations()

        for ((name, bikesAvailable) in stations) {
            stationCoordinates[name]?.let { location ->
                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("$name - $bikesAvailable bikes available")
                )
            }
        }
    }
}