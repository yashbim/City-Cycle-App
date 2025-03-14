package com.example.city_cycle_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PromotionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotions)

        recyclerView = findViewById(R.id.recyclerViewPromotions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Hardcoded Promotions
        val promotions = listOf(
            Promotion("Christmas Special", "Rent a bike this Christmas and get 30% off!", "30%"),
            Promotion("Weekend Discount", "Book a bike on weekends and enjoy a 20% discount!", "20%"),
            Promotion("Summer Special", "Hot weather? Cool down with a 25% discount on rentals!", "25%"),
            Promotion("Student Offer", "Students get a special 15% off with valid ID!", "15%"),
            Promotion("Early Bird", "Book before 9 AM and get 10% off!", "10%")
        )

        recyclerView.adapter = PromotionsAdapter(promotions)
    }
}
