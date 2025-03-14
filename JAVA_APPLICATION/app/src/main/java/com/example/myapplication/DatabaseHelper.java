package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Companion object constants translated to static final fields
    private static final String DATABASE_NAME = "CityCycle";
    private static final int DATABASE_VERSION = 2; // Increased version number for schema update
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // bike station data
    private static final String TABLE_BIKE_STATIONS = "bike_stations";
    private static final String COLUMN_STATION_ID = "station_id";
    private static final String COLUMN_STATION_NAME = "station_name";
    private static final String COLUMN_AVAILABLE_BIKES = "available_bikes";

    // booking
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String COLUMN_BOOKING_ID = "booking_id";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_TOTAL_PRICE = "total_price";
    private static final String COLUMN_STATION_NAME_IN_BOOKING = "station_name"; // New column

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT, " + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTable);

        String createBikeStationsTable = "CREATE TABLE " + TABLE_BIKE_STATIONS + " (\n" +
                "    " + COLUMN_STATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    " + COLUMN_STATION_NAME + " TEXT,\n" +
                "    " + COLUMN_AVAILABLE_BIKES + " INTEGER\n" +
                ")";
        db.execSQL(createBikeStationsTable);

        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + " (\n" +
                "    " + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    " + COLUMN_USER_EMAIL + " TEXT,\n" +
                "    " + COLUMN_STATION_ID + " INTEGER,\n" +
                "    " + COLUMN_STATION_NAME_IN_BOOKING + " TEXT,\n" +
                "    " + COLUMN_START_TIME + " TEXT,\n" +
                "    " + COLUMN_DURATION + " INTEGER,\n" +
                "    " + COLUMN_TOTAL_PRICE + " REAL,\n" +
                "    FOREIGN KEY (" + COLUMN_USER_EMAIL + ") REFERENCES users(email),\n" +
                "    FOREIGN KEY (" + COLUMN_STATION_ID + ") REFERENCES bike_stations(station_id)\n" +
                ")";
        db.execSQL(createBookingsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add station_name column to bookings table for existing installations
            db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COLUMN_STATION_NAME_IN_BOOKING + " TEXT");

            // Update existing records to include station names
            db.execSQL("UPDATE " + TABLE_BOOKINGS +
                    " SET " + COLUMN_STATION_NAME_IN_BOOKING + " = (" +
                    "    SELECT " + COLUMN_STATION_NAME + " " +
                    "    FROM " + TABLE_BIKE_STATIONS + " " +
                    "    WHERE " + TABLE_BIKE_STATIONS + "." + COLUMN_STATION_ID + " = " + TABLE_BOOKINGS + "." + COLUMN_STATION_ID +
                    " )");
        }
    }

    // retrieve bookings for a specific user
    public List<Map<String, String>> getBookingHistory(String userEmail) {
        List<Map<String, String>> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_STATION_NAME_IN_BOOKING + ", " + COLUMN_START_TIME + ", " + COLUMN_DURATION + ", " + COLUMN_TOTAL_PRICE +
                " FROM " + TABLE_BOOKINGS + " WHERE " + COLUMN_USER_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{userEmail});

        while (cursor.moveToNext()) {
            Map<String, String> booking = new HashMap<>();
            booking.put("station", cursor.getString(0));
            booking.put("startTime", cursor.getString(1));
            booking.put("duration", Integer.toString(cursor.getInt(2)));
            booking.put("price", Double.toString(cursor.getDouble(3)));
            bookings.add(booking);
        }
        cursor.close();
        db.close();

        Log.d("DatabaseHelper", "Returning bookings: " + bookings); // Debugging log

        return bookings;
    }

    // fetch station names dynamically
    public List<String> getStationNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_STATION_NAME + " FROM " + TABLE_BIKE_STATIONS;
        Cursor cursor = db.rawQuery(query, null);

        List<String> stations = new ArrayList<>();
        while (cursor.moveToNext()) {
            stations.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return stations;
    }

    // handle bookings
    public boolean bookBike(String userEmail, String stationName, String date, String startTime, int duration, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get station ID and available bikes
        String query = "SELECT " + COLUMN_STATION_ID + ", " + COLUMN_AVAILABLE_BIKES + " FROM " + TABLE_BIKE_STATIONS + " WHERE " + COLUMN_STATION_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{stationName});

        if (cursor.moveToFirst()) {
            int stationId = cursor.getInt(0);
            int availableBikes = cursor.getInt(1);

            if (availableBikes > 0) {
                // Convert 12-hour format to 24-hour format
                String[] parts = startTime.split(" ");
                String[] timePart = parts[0].split(":");
                int hour = Integer.parseInt(timePart[0]);
                String minute = timePart[1];
                String period = parts[1];

                // Convert to 24-hour format
                if (period.equals("PM") && hour != 12) {
                    hour += 12;
                } else if (period.equals("AM") && hour == 12) {
                    hour = 0;
                }

                // Format as 24-hour time
                String formattedTime = String.format("%02d:%s", hour, minute);
                String combinedDateTime = date + " " + formattedTime;

                // Insert booking
                ContentValues values = new ContentValues();
                values.put(COLUMN_USER_EMAIL, userEmail);
                values.put(COLUMN_STATION_ID, stationId);
                values.put(COLUMN_STATION_NAME_IN_BOOKING, stationName); // Store station name
                values.put(COLUMN_START_TIME, combinedDateTime);
                values.put(COLUMN_DURATION, duration);
                values.put(COLUMN_TOTAL_PRICE, totalPrice);
                db.insert(TABLE_BOOKINGS, null, values);

                // Update available bikes
                String updateQuery = "UPDATE " + TABLE_BIKE_STATIONS + " SET " + COLUMN_AVAILABLE_BIKES + " = " + COLUMN_AVAILABLE_BIKES + " - 1 WHERE " + COLUMN_STATION_ID + " = ?";
                db.execSQL(updateQuery, new Object[]{Integer.toString(stationId)});

                cursor.close();
                db.close();
                return true;
            }
        }
        cursor.close();
        db.close();
        return false;
    }

    public int getBikeCount(String stationName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_AVAILABLE_BIKES + " FROM " + TABLE_BIKE_STATIONS + " WHERE " + COLUMN_STATION_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{stationName});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public void initializeBikeStations() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if data already exists
        String countQuery = "SELECT COUNT(*) FROM bike_stations";
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) { // Only insert if the table is empty
            List<String> stations = Arrays.asList(
                    "Colombo", "Gampaha", "Kandy", "Galle", "Jaffna",
                    "Kurunegala", "Ratnapura", "Anuradhapura", "Badulla", "Trincomalee"
            );

            for (String station : stations) {
                ContentValues values = new ContentValues();
                values.put("station_name", station);
                values.put("available_bikes", 5);
                db.insert("bike_stations", null, values);
            }
        }
        db.close();
    }

    public List<Pair<String, Integer>> getBikeStations() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_STATION_NAME + ", " + COLUMN_AVAILABLE_BIKES + " FROM " + TABLE_BIKE_STATIONS;
        Cursor cursor = db.rawQuery(query, null);

        List<Pair<String, Integer>> stations = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            int bikes = cursor.getInt(1);
            stations.add(new Pair<>(name, bikes));
        }
        cursor.close();
        db.close();
        return stations;
    }

    public boolean registerUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1L;
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        // Update the password where email matches
        int result = db.update(
                TABLE_NAME,
                values,
                COLUMN_EMAIL + " = ?",
                new String[]{email}
        );
        db.close();

        return result > 0;  // Returns true if at least one row was updated
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean updateBikeStationsCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;

        try {
            db.beginTransaction();

            // Reset all stations to their initial bike count (5)
            String resetQuery = "UPDATE " + TABLE_BIKE_STATIONS + " SET " + COLUMN_AVAILABLE_BIKES + " = 5";
            db.execSQL(resetQuery);

            // Get current date and time in the format likely used in your database
            // Using SimpleDateFormat for compatibility with older Android versions
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String currentDateTimeString = dateFormat.format(new Date());

            Log.d("BikeUpdate", "Current time: " + currentDateTimeString);

            // Sample query to test if any records exist in bookings table
            Cursor testCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKINGS, null);
            testCursor.moveToFirst();
            int bookingCount = testCursor.getInt(0);
            Log.d("BikeUpdate", "Total bookings in database: " + bookingCount);
            testCursor.close();

            // Get a sample booking to verify date format
            if (bookingCount > 0) {
                Cursor sampleCursor = db.rawQuery("SELECT " + COLUMN_START_TIME + ", " + COLUMN_DURATION + " FROM " + TABLE_BOOKINGS + " LIMIT 1", null);
                if (sampleCursor.moveToFirst()) {
                    String sampleTime = sampleCursor.getString(0);
                    int sampleDuration = sampleCursor.getInt(1);
                    Log.d("BikeUpdate", "Sample booking: Start=" + sampleTime + ", Duration=" + sampleDuration + " minutes");
                }
                sampleCursor.close();
            }

            // Query for active bookings
            String query = "SELECT " + COLUMN_STATION_ID + ", COUNT(*) AS booked_bikes\n" +
                    "FROM " + TABLE_BOOKINGS + "\n" +
                    "WHERE \n" +
                    "    datetime(" + COLUMN_START_TIME + ") <= datetime(?)\n" +
                    "    AND \n" +
                    "    datetime(" + COLUMN_START_TIME + ", '+' || " + COLUMN_DURATION + " || ' hours') > datetime(?)\n" +
                    "GROUP BY " + COLUMN_STATION_ID;
            Cursor cursor = db.rawQuery(query, new String[]{currentDateTimeString, currentDateTimeString});

            Log.d("BikeUpdate", "Found " + cursor.getCount() + " stations with active bookings");

            // Update bike counts for stations with active bookings
            while (cursor.moveToNext()) {
                int stationId = cursor.getInt(0);
                int bookedBikes = cursor.getInt(1);

                Log.d("BikeUpdate", "Station ID: " + stationId + " has " + bookedBikes + " active bookings");

                String updateStationQuery = "UPDATE " + TABLE_BIKE_STATIONS + " \n" +
                        "SET " + COLUMN_AVAILABLE_BIKES + " = \n" +
                        "    CASE\n" +
                        "        WHEN (5 - ?) < 0 THEN 0\n" +
                        "        ELSE 5 - ?\n" +
                        "    END\n" +
                        "WHERE " + COLUMN_STATION_ID + " = ?";
                db.execSQL(updateStationQuery, new Object[]{Integer.toString(bookedBikes), Integer.toString(bookedBikes), Integer.toString(stationId)});
            }

            cursor.close();

            // Verify the update
            Cursor verificationCursor = db.rawQuery("SELECT " + COLUMN_STATION_ID + ", " + COLUMN_STATION_NAME + ", " + COLUMN_AVAILABLE_BIKES + " FROM " + TABLE_BIKE_STATIONS, null);
            Log.d("BikeUpdate", "Verification - Station counts after update:");
            while (verificationCursor.moveToNext()) {
                int id = verificationCursor.getInt(0);
                String name = verificationCursor.getString(1);
                int bikes = verificationCursor.getInt(2);
                Log.d("BikeUpdate", "Station ID: " + id + ", Name: " + name + ", Available bikes: " + bikes);
            }
            verificationCursor.close();

            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            Log.e("BikeUpdate", "Error updating bike counts", e);
            e.printStackTrace();
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
            db.close();
        }

        return success;
    }
}
