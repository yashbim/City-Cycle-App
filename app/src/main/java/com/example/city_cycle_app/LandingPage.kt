package com.example.city_cycle_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class LandingPage : AppCompatActivity(){
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        val find_bikes_button : LinearLayout = findViewById(R.id.book_bikes_button)
        find_bikes_button.setOnClickListener{
            val intent_to_book_bikes = Intent(this, BikeStationsActivity::class.java)
            startActivity(intent_to_book_bikes)
        }

        val mapButton: Button = findViewById(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }


    }
}