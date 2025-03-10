package com.example.city_cycle_app

import DatabaseHelper
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity

class BikeStationsActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_stations)

        dbHelper = DatabaseHelper(this)
        dbHelper.initializeBikeStations()

        val stations = dbHelper.getBikeStations()
        val listView: ListView = findViewById(R.id.listViewBikeStations)

        val data = stations.map {
            mapOf("station" to it.first, "bikes" to "${it.second} bikes")
        }

        val adapter = SimpleAdapter(
            this,
            data,
            R.layout.custom_list_item,  // Use your custom layout here
            arrayOf("station", "bikes"),
            intArrayOf(R.id.text1, R.id.text2)
        )

        listView.adapter = adapter
    }
}