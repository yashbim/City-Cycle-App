package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class BookBike extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String userEmail; // Assume this is passed from login session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_bike);

        dbHelper = new DatabaseHelper(this);

        // Retrieve user email from SharedPreferences
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("EMAIL", "");

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if no user is logged in
            return;
        }

        Spinner stationSpinner = findViewById(R.id.stationSpinner);
        Spinner startTimeSpinner = findViewById(R.id.startTimeSpinner);
        Spinner durationSpinner = findViewById(R.id.durationSpinner);
        TextView pickupTimeText = findViewById(R.id.pickupTimeText);
        TextView returnTimeText = findViewById(R.id.returnTimeText);
        TextView totalPriceText = findViewById(R.id.totalPriceText);
        Button bookButton = findViewById(R.id.bookButton);

        // Fetch real stations from DB
        List<String> stations = dbHelper.getStationNames();
        stationSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stations));

        // Generate a full 24-hour time range
        List<String> startTimes = generateTimeSlots();
        startTimeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, startTimes));

        // Duration options (in hours)
        String[] durations = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        durationSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, durations));

        final double ratePerHour = 5.00;

        // Create an updateRentalSummary method
        final Runnable updateRentalSummary = new Runnable() {
            @Override
            public void run() {
                String selectedStartTime = (String) startTimeSpinner.getSelectedItem();
                int selectedDuration = Integer.parseInt((String) durationSpinner.getSelectedItem());

                String returnTime = calculateReturnTime(selectedStartTime, selectedDuration);
                double totalPrice = selectedDuration * ratePerHour;

                pickupTimeText.setText(selectedStartTime);
                returnTimeText.setText(returnTime);
                totalPriceText.setText("$" + String.format("%.2f", totalPrice));
            }
        };

        startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRentalSummary.run();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRentalSummary.run();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedStation = (String) stationSpinner.getSelectedItem();
                String selectedStartTime = (String) startTimeSpinner.getSelectedItem();
                int selectedDuration = Integer.parseInt((String) durationSpinner.getSelectedItem());
                double totalPrice = selectedDuration * ratePerHour;

                boolean success = dbHelper.bookBike(userEmail, selectedStation, selectedStartTime, selectedDuration, totalPrice);

                if (success) {
                    Toast.makeText(BookBike.this, "Bike booked at " + selectedStation + " for " + selectedStartTime + ", Duration: " + selectedDuration + " hours", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(BookBike.this, "No bikes available at " + selectedStation, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private List<String> generateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        for (int hour = 0; hour <= 23; hour++) {
            String period = (hour < 12) ? "AM" : "PM";
            int formattedHour = (hour == 0) ? 12 : (hour > 12) ? hour - 12 : hour;
            timeSlots.add(formattedHour + ":00 " + period);
        }
        return timeSlots;
    }

    private String calculateReturnTime(String startTime, int duration) {
        String[] parts = startTime.split(" ");
        String hourStr = parts[0];
        String period = parts[1];

        int startHour = Integer.parseInt(hourStr.split(":")[0]);

        int militaryHour;
        if (period.equals("PM") && startHour != 12) {
            militaryHour = startHour + 12;
        } else if (period.equals("AM") && startHour == 12) {
            militaryHour = 0;
        } else {
            militaryHour = startHour;
        }

        int returnHour = (militaryHour + duration) % 24;

        String returnPeriod = (returnHour < 12) ? "AM" : "PM";
        int formattedReturnHour = (returnHour == 0) ? 12 : (returnHour > 12) ? returnHour - 12 : returnHour;

        return formattedReturnHour + ":00 " + returnPeriod;
    }
}