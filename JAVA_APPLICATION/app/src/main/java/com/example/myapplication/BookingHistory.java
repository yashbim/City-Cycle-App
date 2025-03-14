package com.example.myapplication;


//import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class BookingHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView emptyStateMessage;
    private DatabaseHelper dbHelper;
    private String userEmail; // Assume this is retrieved from login session

//    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewBookings);
        emptyStateMessage = (TextView) findViewById(R.id.emptyStateMessage);
        dbHelper = new DatabaseHelper(this);

        // Fetch the logged-in user's email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", null) != null ? sharedPreferences.getString("user_email", null) : "";

// In BookingHistory.java, modify the code after checking the email:
        if (userEmail.isEmpty()) {


            Toast.makeText(getApplicationContext(), "Email not found.", Toast.LENGTH_SHORT).show();
            Log.e("BookingHistory", "User email not found! Redirecting to login.");
            // Actually redirect to login
            Intent loginIntent = new Intent(this, LoginPage.class);
            startActivity(loginIntent);
            finish(); // Close this activity
            return;
        }
        Log.d("BookingHistory", "Logged-in user email: " + userEmail);

        List<Map<String, String>> bookings = dbHelper.getBookingHistory(userEmail);
        Log.d("BookingHistory", "Fetched bookings: " + bookings);

        if (bookings.isEmpty()) {
            emptyStateMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new BookingHistoryAdapter(bookings));
        }
    }

}
