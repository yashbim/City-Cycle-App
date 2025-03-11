package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BikeStationsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_stations_view);

        dbHelper = new DatabaseHelper(this);
        dbHelper.initializeBikeStations();

        Button book_page_button = findViewById(R.id.bookNowButton);
        book_page_button.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookBike.class);
            startActivity(intent);
        });

        List<SimpleEntry<String, Integer>> stations = dbHelper.getBikeStations();
        ListView listView = findViewById(R.id.listViewBikeStations);

        // Convert stations data to format needed by SimpleAdapter
        List<Map<String, String>> data = new ArrayList<>();
        for (SimpleEntry<String, Integer> station : stations) {
            Map<String, String> item = new HashMap<>();
            item.put("station", station.getKey());
            item.put("bikes", station.getValue() + " bikes");
            data.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                data,
                R.layout.custom_list_item, // Use your custom layout here
                new String[]{"station", "bikes"},
                new int[]{R.id.text1, R.id.text2}
        );

        listView.setAdapter(adapter);
    }
}