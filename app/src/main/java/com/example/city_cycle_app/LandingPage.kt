package com.example.city_cycle_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LandingPage : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        val find_bikes_button : Button = findViewById(R.id.book_bikes_button)
        find_bikes_button.setOnClickListener{
            val intent_to_book_bikes = Intent(this, BookBikes::class.java)
            startActivity(intent_to_book_bikes)
        }

    }
}