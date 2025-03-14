package com.example.city_cycle_app

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LandingPage : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize MapView
        mapView = findViewById(R.id.mapPreview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val find_bikes_button: LinearLayout = findViewById(R.id.book_bikes_button)
        find_bikes_button.setOnClickListener {
            val databaseHelper = DatabaseHelper(this)
            Log.d("BikeUpdate", "Before updating bike counts")
            val updated = databaseHelper.updateBikeStationsCount()
            if (updated) {
                Log.d("BikeUpdate", "Bike counts updated successfully")
                Toast.makeText(this, "Bike availability updated", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("BikeUpdate", "Failed to update bike counts")
                Toast.makeText(this, "Could not update bike availability", Toast.LENGTH_SHORT).show()
            }
            val intent_to_book_bikes = Intent(this, BikeStationsActivity::class.java)
            startActivity(intent_to_book_bikes)
        }

        val mapButton: Button = findViewById(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        val my_rentals: LinearLayout = findViewById(R.id.rentals_button)
        my_rentals.setOnClickListener {
            val intent_to_book_bikes = Intent(this, BookingHistory::class.java)
            startActivity(intent_to_book_bikes)
        }

        val promotions_button: LinearLayout = findViewById(R.id.promotions_button)
        promotions_button.setOnClickListener {
            val intent_to_book_bikes = Intent(this, PromotionsActivity::class.java)
            startActivity(intent_to_book_bikes)
        }

        val account_button: LinearLayout = findViewById(R.id.account_button)
        account_button.setOnClickListener {
            val intent_to_book_bikes = Intent(this, AccountActivity::class.java)
            startActivity(intent_to_book_bikes)
        }

        // Check and request location permissions
        checkLocationPermission()
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        // Disable interactions for preview (Lite Mode enforces this already)
        googleMap?.uiSettings?.isZoomControlsEnabled = false
        googleMap?.uiSettings?.isScrollGesturesEnabled = false
        googleMap?.uiSettings?.isZoomGesturesEnabled = false

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
                getUserLocation()
            } else {
                // Permission denied, fallback to Sri Lanka
                val sriLanka = LatLng(7.8731, 80.7718)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f))
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
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10f))
                    showBikeStations()
                } else {
                    // Location null, fallback to Sri Lanka
                    val sriLanka = LatLng(7.8731, 80.7718)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f))
                    showBikeStations()
                }
            }.addOnFailureListener { e ->
                Log.e("LandingPage", "Failed to get location", e)
                // Fallback to Sri Lanka on failure
                val sriLanka = LatLng(7.8731, 80.7718)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f))
                showBikeStations()
            }
        } else {
            // Permission not granted yet, will be handled in onRequestPermissionsResult
            val sriLanka = LatLng(7.8731, 80.7718)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f))
            showBikeStations()
        }
    }

    private fun showBikeStations() {
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

        val stations = dbHelper.getBikeStations()
        for ((name, bikesAvailable) in stations) {
            stationCoordinates[name]?.let { location ->
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("$name - $bikesAvailable bikes available")
                )
            }
        }
    }

    // Lifecycle methods for MapView
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}