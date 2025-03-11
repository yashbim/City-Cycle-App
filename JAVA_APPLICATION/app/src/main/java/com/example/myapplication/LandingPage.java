package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;


        import android.annotation.SuppressLint;
        import android.content.Intent;
        import android.os.Bundle;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import androidx.appcompat.app.AppCompatActivity;

public class LandingPage extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        LinearLayout find_bikes_button = findViewById(R.id.book_bikes_button);
        find_bikes_button.setOnClickListener(v -> {
            Intent intent_to_book_bikes = new Intent(this, BikeStationsActivity.class);
            startActivity(intent_to_book_bikes);
        });

        Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        });
    }
}