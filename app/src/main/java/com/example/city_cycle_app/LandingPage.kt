package com.example.city_cycle_app

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LandingPage : AppCompatActivity(){
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        val find_bikes_button : LinearLayout = findViewById(R.id.book_bikes_button)
        find_bikes_button.setOnClickListener{
            // Create a DatabaseHelper instance
            val databaseHelper = DatabaseHelper(this)

            // Log before update
            Log.d("BikeUpdate", "Before updating bike counts")

            // Call the update function
            val updated = databaseHelper.updateBikeStationsCount()

            // Provide feedback
            if (updated) {
                Log.d("BikeUpdate", "Bike counts updated successfully")
                Toast.makeText(this, "Bike availability updated", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("BikeUpdate", "Failed to update bike counts")
                Toast.makeText(this, "Could not update bike availability", Toast.LENGTH_SHORT).show()
            }

            // Navigate to the BikeStationsActivity
            val intent_to_book_bikes = Intent(this, BikeStationsActivity::class.java)
            startActivity(intent_to_book_bikes)
        }

        val mapButton: Button = findViewById(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        val my_rentals : LinearLayout = findViewById(R.id.rentals_button)
        my_rentals.setOnClickListener{

            val intent_to_book_bikes = Intent(this, BookingHistory::class.java)
            startActivity(intent_to_book_bikes)
        }

        val promotions_button : LinearLayout = findViewById(R.id.promotions_button)
        promotions_button.setOnClickListener{

            val intent_to_book_bikes = Intent(this, PromotionsActivity::class.java)
            startActivity(intent_to_book_bikes)
        }

        val account_button : LinearLayout = findViewById(R.id.account_button)
        account_button.setOnClickListener{

            val intent_to_book_bikes = Intent(this, AccountActivity::class.java)
            startActivity(intent_to_book_bikes)
        }
    }
}