package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CityCycle";
    private static final int DATABASE_VERSION = 1;
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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT, " + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTable);

        String createBikeStationsTable =
                "CREATE TABLE " + TABLE_BIKE_STATIONS + " (" +
                        COLUMN_STATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_STATION_NAME + " TEXT, " +
                        COLUMN_AVAILABLE_BIKES + " INTEGER)";
        db.execSQL(createBikeStationsTable);

        String createBookingsTable =
                "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                        COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_USER_EMAIL + " TEXT, " +
                        COLUMN_STATION_ID + " INTEGER, " +
                        COLUMN_START_TIME + " TEXT, " +
                        COLUMN_DURATION + " INTEGER, " +
                        COLUMN_TOTAL_PRICE + " REAL, " +
                        "FOREIGN KEY (" + COLUMN_USER_EMAIL + ") REFERENCES users(email), " +
                        "FOREIGN KEY (" + COLUMN_STATION_ID + ") REFERENCES bike_stations(station_id))";
        db.execSQL(createBookingsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // fetch station names dynamically
    public List<String> getStationNames() {
        SQLiteDatabase db = getReadableDatabase();
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
    public boolean bookBike(String userEmail, String stationName, String startTime, int duration, double totalPrice) {
        SQLiteDatabase db = getWritableDatabase();

        // Get station ID and available bikes
        String query = "SELECT " + COLUMN_STATION_ID + ", " + COLUMN_AVAILABLE_BIKES +
                " FROM " + TABLE_BIKE_STATIONS + " WHERE " + COLUMN_STATION_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{stationName});

        if (cursor.moveToFirst()) {
            int stationId = cursor.getInt(0);
            int availableBikes = cursor.getInt(1);

            if (availableBikes > 0) {
                // Insert booking
                ContentValues values = new ContentValues();
                values.put(COLUMN_USER_EMAIL, userEmail);
                values.put(COLUMN_STATION_ID, stationId);
                values.put(COLUMN_START_TIME, startTime);
                values.put(COLUMN_DURATION, duration);
                values.put(COLUMN_TOTAL_PRICE, totalPrice);

                db.insert(TABLE_BOOKINGS, null, values);

                // Update available bikes
                String updateQuery = "UPDATE " + TABLE_BIKE_STATIONS + " SET " + COLUMN_AVAILABLE_BIKES +
                        " = " + COLUMN_AVAILABLE_BIKES + " - 1 WHERE " + COLUMN_STATION_ID + " = ?";
                db.execSQL(updateQuery, new String[]{String.valueOf(stationId)});

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
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + COLUMN_AVAILABLE_BIKES + " FROM " + TABLE_BIKE_STATIONS +
                " WHERE " + COLUMN_STATION_NAME + " = ?";
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
        SQLiteDatabase db = getWritableDatabase();

        // Check if data already exists
        String countQuery = "SELECT COUNT(*) FROM bike_stations";
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) { // Only insert if the table is empty
            String[] stations = {
                    "Colombo", "Gampaha", "Kandy", "Galle", "Jaffna",
                    "Kurunegala", "Ratnapura", "Anuradhapura", "Badulla", "Trincomalee"
            };

            for (String station : stations) {
                ContentValues values = new ContentValues();
                values.put("station_name", station);
                values.put("available_bikes", 5);
                db.insert("bike_stations", null, values);
            }
        }
        db.close();
    }

    public List<SimpleEntry<String, Integer>> getBikeStations() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + COLUMN_STATION_NAME + ", " + COLUMN_AVAILABLE_BIKES +
                " FROM " + TABLE_BIKE_STATIONS;
        Cursor cursor = db.rawQuery(query, null);

        List<SimpleEntry<String, Integer>> stations = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            int bikes = cursor.getInt(1);
            stations.add(new SimpleEntry<>(name, bikes));
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

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ? AND " +
                COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}