package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PromotionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);

        recyclerView = findViewById(R.id.recyclerViewPromotions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Hardcoded Promotions
        List<Promotion> promotions = new ArrayList<>();
        promotions.add(new Promotion("Christmas Special", "Rent a bike this Christmas and get 30% off!", "30%"));
        promotions.add(new Promotion("Weekend Discount", "Book a bike on weekends and enjoy a 20% discount!", "20%"));
        promotions.add(new Promotion("Summer Special", "Hot weather? Cool down with a 25% discount on rentals!", "25%"));
        promotions.add(new Promotion("Student Offer", "Students get a special 15% off with valid ID!", "15%"));
        promotions.add(new Promotion("Early Bird", "Book before 9 AM and get 10% off!", "10%"));

        recyclerView.setAdapter(new PromotionsAdapter(promotions));
    }
}