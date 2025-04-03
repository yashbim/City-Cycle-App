package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookBike extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String userEmail; // Assume this is passed from login session

    // Date picker related variables
    private TextView datePickerText;
    private TextView rentalDateText;
    private Calendar selectedDate = Calendar.getInstance();
    private String formattedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_bike);

        dbHelper = new DatabaseHelper(this);

        // Retrieve user email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", "");
        if (userEmail == null) {
            userEmail = "";
        }
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if no user is logged in
            return;
        }

        // Initialize all view elements
        Spinner stationSpinner = findViewById(R.id.stationSpinner);
        Spinner startTimeSpinner = findViewById(R.id.startTimeSpinner);
        Spinner durationSpinner = findViewById(R.id.durationSpinner);
        final TextView pickupTimeText = findViewById(R.id.pickupTimeText);
        final TextView returnTimeText = findViewById(R.id.returnTimeText);
        final TextView totalPriceText = findViewById(R.id.totalPriceText);
        Button bookButton = findViewById(R.id.bookButton);

        // Initialize date picker elements
        datePickerText = findViewById(R.id.datePickerText);
        rentalDateText = findViewById(R.id.rentalDateText);

        // Set initial date display
        updateDateDisplay();

        // Set up click listener for the date picker
        datePickerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Fetch real stations from DB
        List<String> stations = dbHelper.getStationNames();
        ArrayAdapter<String> stationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stations);
        stationSpinner.setAdapter(stationAdapter);

        // Generate a full 24-hour time range
        List<String> startTimes = generateTimeSlots();
        ArrayAdapter<String> startTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, startTimes);
        startTimeSpinner.setAdapter(startTimeAdapter);

        // Duration options (in hours)
        String[] durations = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, durations);
        durationSpinner.setAdapter(durationAdapter);

        final double ratePerHour = 5.00;

        // Local function to update rental summary using a Runnable
        final Runnable updateRentalSummary = new Runnable() {
            @Override
            public void run() {
                String selectedStartTime = startTimeSpinner.getSelectedItem().toString();
                int selectedDuration = Integer.parseInt(durationSpinner.getSelectedItem().toString());

                String returnTime = calculateReturnTime(selectedStartTime, selectedDuration);
                double totalPrice = selectedDuration * ratePerHour;

                pickupTimeText.setText(selectedStartTime);
                returnTimeText.setText(returnTime);
                totalPriceText.setText("$" + String.format(Locale.getDefault(), "%.2f", totalPrice));
            }
        };

        startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRentalSummary.run();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRentalSummary.run();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedStation = stationSpinner.getSelectedItem().toString();
                String selectedStartTime = startTimeSpinner.getSelectedItem().toString();
                int selectedDuration = Integer.parseInt(durationSpinner.getSelectedItem().toString());
                double totalPrice = selectedDuration * ratePerHour;

                // Now include the date in the booking
                boolean success = dbHelper.bookBike(
                        userEmail,
                        selectedStation,
                        formattedDate, // Pass the selected date
                        selectedStartTime,
                        selectedDuration,
                        totalPrice
                );

                if (success) {
                    Toast.makeText(
                            BookBike.this,
                            "Bike booked at " + selectedStation + " for " + formattedDate + ", " + selectedStartTime + ", Duration: " + selectedDuration + " hours",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    Toast.makeText(BookBike.this, "No bikes available at " + selectedStation, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the selected date
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        // Update the UI
                        updateDateDisplay();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        // Set min date to today (to prevent booking in the past)
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        // Optionally set a max date (e.g., 30 days in the future)
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_YEAR, 30);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String displayDate = displayFormat.format(selectedDate.getTime());
        formattedDate = dbFormat.format(selectedDate.getTime());

        datePickerText.setText(displayDate);
        rentalDateText.setText(displayDate);
    }

    private List<String> generateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        for (int hour = 0; hour <= 23; hour++) {
            String period = (hour < 12) ? "AM" : "PM";
            int formattedHour = (hour == 0) ? 12 : (hour > 12 ? hour - 12 : hour);
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
        int formattedReturnHour = (returnHour == 0) ? 12 : (returnHour > 12 ? returnHour - 12 : returnHour);

        return formattedReturnHour + ":00 " + returnPeriod;
    }
}
