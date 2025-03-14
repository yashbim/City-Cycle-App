package com.example.city_cycle_app


import DatabaseHelper
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log  // Import this


class BookingHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateMessage: TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userEmail: String // Assume this is retrieved from login session


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_history)

        recyclerView = findViewById(R.id.recyclerViewBookings)
        emptyStateMessage = findViewById(R.id.emptyStateMessage)
        dbHelper = DatabaseHelper(this)

        // Fetch the logged-in user's email from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        userEmail = sharedPreferences.getString("user_email", null) ?: ""

        if (userEmail.isEmpty()) {
            Log.e("BookingHistory", "User email not found! Redirecting to login.")
            return
        }

        Log.d("BookingHistory", "Logged-in user email: $userEmail")

        val bookings = dbHelper.getBookingHistory(userEmail)
        Log.d("BookingHistory", "Fetched bookings: $bookings")

        if (bookings.isEmpty()) {
            emptyStateMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = BookingHistoryAdapter(bookings)
        }
    }


}
