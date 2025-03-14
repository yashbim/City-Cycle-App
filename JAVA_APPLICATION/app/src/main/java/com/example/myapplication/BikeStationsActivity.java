package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
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
        setContentView(R.layout.activity_bike_stations);

        dbHelper = new DatabaseHelper(this);
        dbHelper.initializeBikeStations();

        Button book_page_button = (Button) findViewById(R.id.bookNowButton);
        book_page_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BikeStationsActivity.this, BookBike.class);
                startActivity(intent);
            }
        });

        List<Pair<String, Integer>> stations = dbHelper.getBikeStations();
        ListView listView = (ListView) findViewById(R.id.listViewBikeStations);

        List<Map<String, String>> data = new ArrayList<>();
        for (Pair<String, Integer> station : stations) {
            Map<String, String> map = new HashMap<>();
            map.put("station", station.first);
            map.put("bikes", station.second + " bikes");
            data.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                data,
                R.layout.custom_list_item,  // Use your custom layout here
                new String[]{"station", "bikes"},
                new int[]{R.id.text1, R.id.text2}
        );

        listView.setAdapter(adapter);
    }
}
