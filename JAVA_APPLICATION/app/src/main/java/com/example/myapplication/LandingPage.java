package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LandingPage extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        final LinearLayout find_bikes_button = findViewById(R.id.book_bikes_button);
        find_bikes_button.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // Create a DatabaseHelper instance
                DatabaseHelper databaseHelper = new DatabaseHelper(LandingPage.this);

                // Log before update
                Log.d("BikeUpdate", "Before updating bike counts");

                // Call the update function
                boolean updated = databaseHelper.updateBikeStationsCount();

                // Provide feedback
                if (updated) {
                    Log.d("BikeUpdate", "Bike counts updated successfully");
//                    Toast.makeText(LandingPage.this, "Bike availability updated", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("BikeUpdate", "Failed to update bike counts");
                    Toast.makeText(LandingPage.this, "Could not update bike availability", Toast.LENGTH_SHORT).show();
                }

                // Navigate to the BikeStationsActivity
                Intent intent_to_book_bikes = new Intent(LandingPage.this, BikeStationsActivity.class);
                startActivity(intent_to_book_bikes);
            }
        });

        final Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(LandingPage.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        final LinearLayout my_rentals = findViewById(R.id.rentals_button);
        my_rentals.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent_to_book_bikes = new Intent(LandingPage.this, BookingHistory.class);
                startActivity(intent_to_book_bikes);
            }
        });

        final LinearLayout promotions_button = findViewById(R.id.promotions_button);
        promotions_button.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent_to_book_bikes = new Intent(LandingPage.this, PromotionsActivity.class);
                startActivity(intent_to_book_bikes);
            }
        });

        final LinearLayout account_button = findViewById(R.id.account_button);
        account_button.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent_to_book_bikes = new Intent(LandingPage.this, AccountActivity.class);
                startActivity(intent_to_book_bikes);
            }
        });
    }
}
