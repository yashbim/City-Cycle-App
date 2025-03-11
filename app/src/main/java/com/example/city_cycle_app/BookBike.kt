package com.example.city_cycle_app

import DatabaseHelper
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class BookBike : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userEmail: String // Assume this is passed from login session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_bike)

        dbHelper = DatabaseHelper(this)

        // Retrieve user email from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userEmail = sharedPreferences.getString("EMAIL", "") ?: ""

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_LONG).show()
            finish() // Close the activity if no user is logged in
            return
        }


        val stationSpinner: Spinner = findViewById(R.id.stationSpinner)
        val startTimeSpinner: Spinner = findViewById(R.id.startTimeSpinner)
        val durationSpinner: Spinner = findViewById(R.id.durationSpinner)
        val pickupTimeText: TextView = findViewById(R.id.pickupTimeText)
        val returnTimeText: TextView = findViewById(R.id.returnTimeText)
        val totalPriceText: TextView = findViewById(R.id.totalPriceText)
        val bookButton: Button = findViewById(R.id.bookButton)

        // Fetch real stations from DB
        val stations = dbHelper.getStationNames()
        stationSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, stations)

        // Generate a full 24-hour time range
        val startTimes = generateTimeSlots()
        startTimeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, startTimes)

        // Duration options (in hours)
        val durations = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        durationSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, durations)

        val ratePerHour = 5.00

        fun updateRentalSummary() {
            val selectedStartTime = startTimeSpinner.selectedItem as String
            val selectedDuration = (durationSpinner.selectedItem as String).toInt()

            val returnTime = calculateReturnTime(selectedStartTime, selectedDuration)
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
            val selectedDuration = (durationSpinner.selectedItem as String).toInt()
            val totalPrice = selectedDuration * ratePerHour

            val success = dbHelper.bookBike(userEmail, selectedStation, selectedStartTime, selectedDuration, totalPrice)

            if (success) {
                Toast.makeText(this, "Bike booked at $selectedStation for $selectedStartTime, Duration: $selectedDuration hours", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No bikes available at $selectedStation", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun generateTimeSlots(): List<String> {
        val timeSlots = mutableListOf<String>()
        for (hour in 0..23) {
            val period = if (hour < 12) "AM" else "PM"
            val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            timeSlots.add("$formattedHour:00 $period")
        }
        return timeSlots
    }

    private fun calculateReturnTime(startTime: String, duration: Int): String {
        val (hourStr, period) = startTime.split(" ")
        val startHour = hourStr.split(":")[0].toInt()

        val militaryHour = if (period == "PM" && startHour != 12) startHour + 12 else if (period == "AM" && startHour == 12) 0 else startHour
        val returnHour = (militaryHour + duration) % 24

        val returnPeriod = if (returnHour < 12) "AM" else "PM"
        val formattedReturnHour = if (returnHour == 0) 12 else if (returnHour > 12) returnHour - 12 else returnHour

        return "$formattedReturnHour:00 $returnPeriod"
    }
}
