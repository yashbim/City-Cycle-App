package com.example.city_cycle_app

import DatabaseHelper
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class BookBike : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userEmail: String // Assume this is passed from login session

    // Date picker related variables
    private lateinit var datePickerText: TextView
    private lateinit var rentalDateText: TextView
    private val selectedDate = Calendar.getInstance()
    private var formattedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_bike)

        dbHelper = DatabaseHelper(this)

        // Retrieve user email from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        userEmail = sharedPreferences.getString("user_email", "") ?: ""

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_LONG).show()
            finish() // Close the activity if no user is logged in
            return
        }

        // Initialize all view elements
        val stationSpinner: Spinner = findViewById(R.id.stationSpinner)
        val startTimeSpinner: Spinner = findViewById(R.id.startTimeSpinner)
        val durationSpinner: Spinner = findViewById(R.id.durationSpinner)
        val pickupTimeText: TextView = findViewById(R.id.pickupTimeText)
        val returnTimeText: TextView = findViewById(R.id.returnTimeText)
        val totalPriceText: TextView = findViewById(R.id.totalPriceText)
        val bookButton: Button = findViewById(R.id.bookButton)

        // Initialize date picker elements
        datePickerText = findViewById(R.id.datePickerText)
        rentalDateText = findViewById(R.id.rentalDateText)

        // Set initial date display
        updateDateDisplay()

        // Set up click listener for the date picker
        datePickerText.setOnClickListener {
            showDatePickerDialog()
        }

        // Fetch real stations from DB and apply custom layout
        val stations = dbHelper.getStationNames()
        val stationAdapter = ArrayAdapter(this, R.layout.spinner_item, stations)
        stationAdapter.setDropDownViewResource(R.layout.spinner_item)
        stationSpinner.adapter = stationAdapter

        // Generate a full 24-hour time range and apply custom layout
        val startTimes = generateTimeSlots()
        val startTimeAdapter = ArrayAdapter(this, R.layout.spinner_item, startTimes)
        startTimeAdapter.setDropDownViewResource(R.layout.spinner_item)
        startTimeSpinner.adapter = startTimeAdapter

        // Duration options (in hours) and apply custom layout
        val durations = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val durationAdapter = ArrayAdapter(this, R.layout.spinner_item, durations)
        durationAdapter.setDropDownViewResource(R.layout.spinner_item)
        durationSpinner.adapter = durationAdapter

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

            // Now include the date in the booking
            val success = dbHelper.bookBike(
                userEmail,
                selectedStation,
                formattedDate, // Pass the selected date
                selectedStartTime,
                selectedDuration,
                totalPrice
            )

            if (success) {
                Toast.makeText(
                    this,
                    "Bike booked at $selectedStation for $formattedDate, $selectedStartTime, Duration: $selectedDuration hours",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, "No bikes available at $selectedStation", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Update the selected date
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Update the UI
                updateDateDisplay()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        // Set min date to today (to prevent booking in the past)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        // Optionally set a max date (e.g., 30 days in the future)
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.DAY_OF_YEAR, 30)
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        datePickerDialog.show()
    }

    private fun updateDateDisplay() {
        val displayFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        val dbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val displayDate = displayFormat.format(selectedDate.time)
        formattedDate = dbFormat.format(selectedDate.time)

        datePickerText.text = displayDate
        rentalDateText.text = displayDate
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