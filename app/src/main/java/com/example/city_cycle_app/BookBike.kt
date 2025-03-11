package com.example.city_cycle_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent


class BookBike : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_bike)

        val stationSpinner: Spinner = findViewById(R.id.stationSpinner)
        val startTimeSpinner: Spinner = findViewById(R.id.startTimeSpinner)
        val durationSpinner: Spinner = findViewById(R.id.durationSpinner)
        val pickupTimeText: TextView = findViewById(R.id.pickupTimeText)
        val returnTimeText: TextView = findViewById(R.id.returnTimeText)
        val totalPriceText: TextView = findViewById(R.id.totalPriceText)
        val bookButton: Button = findViewById(R.id.bookButton)

        val stations = arrayOf("Station A", "Station B", "Station C", "Station D")
        val startTimes = arrayOf("08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM")
        val durations = arrayOf("1", "2", "3", "4", "5")
        val ratePerHour = 5.00

        stationSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, stations)
        startTimeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, startTimes)
        durationSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, durations)

        fun updateRentalSummary() {
            val selectedStartTime = startTimeSpinner.selectedItem as String
            val selectedDuration = (durationSpinner.selectedItem as String).toInt()

            val returnHour = selectedStartTime.split(":")[0].toInt() + selectedDuration
            val returnPeriod = if (returnHour >= 12) "PM" else "AM"
            val adjustedReturnHour = if (returnHour > 12) returnHour - 12 else returnHour
            val returnTime = "$adjustedReturnHour:00 $returnPeriod"

            val totalPrice = selectedDuration * ratePerHour

            pickupTimeText.text = selectedStartTime
            returnTimeText.text = returnTime
            totalPriceText.text = "$${"%.2f".format(totalPrice)}"
        }

        startTimeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                updateRentalSummary()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        durationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                updateRentalSummary()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        bookButton.setOnClickListener {
            val selectedStation = stationSpinner.selectedItem as String
            val selectedStartTime = startTimeSpinner.selectedItem as String
            val selectedDuration = durationSpinner.selectedItem as String

            Toast.makeText(this, "Bike booked at $selectedStation for $selectedStartTime, Duration: $selectedDuration hours", Toast.LENGTH_LONG).show()
        }
    }
}
