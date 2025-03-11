package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        dbHelper = new DatabaseHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showBikeStations();
    }

    private void showBikeStations() {
        // Using a custom class to represent the Triple from Kotlin
        List<StationInfo> stations = Arrays.asList(
                new StationInfo("Colombo", 6.9271, 79.8612),
                new StationInfo("Gampaha", 7.0873, 80.0144),
                new StationInfo("Kandy", 7.2906, 80.6337),
                new StationInfo("Galle", 6.0329, 80.2168),
                new StationInfo("Jaffna", 9.6615, 80.0255),
                new StationInfo("Kurunegala", 7.4863, 80.3647),
                new StationInfo("Ratnapura", 6.6828, 80.3992),
                new StationInfo("Anuradhapura", 8.3114, 80.4037),
                new StationInfo("Badulla", 6.9934, 81.0556),
                new StationInfo("Trincomalee", 8.5874, 81.2152)
        );

        for (StationInfo station : stations) {
            LatLng location = new LatLng(station.getLat(), station.getLng());
            int bikesAvailable = dbHelper.getBikeCount(station.getName());
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(station.getName() + " - " + bikesAvailable + " bikes available"));
        }

        // Move camera to Sri Lanka
        LatLng sriLanka = new LatLng(7.8731, 80.7718);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f));
    }

    // Helper class to replace Kotlin's Triple
    private static class StationInfo {
        private final String name;
        private final double lat;
        private final double lng;

        public StationInfo(String name, double lat, double lng) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
        }

        public String getName() {
            return name;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }
}