import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "CityCycle"
        private const val DATABASE_VERSION = 2 // Increased version number for schema update
        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"

        //bike station data
        private const val TABLE_BIKE_STATIONS = "bike_stations"
        private const val COLUMN_STATION_ID = "station_id"
        private const val COLUMN_STATION_NAME = "station_name"
        private const val COLUMN_AVAILABLE_BIKES = "available_bikes"

        //booking
        private const val TABLE_BOOKINGS = "bookings"
        private const val COLUMN_BOOKING_ID = "booking_id"
        private const val COLUMN_USER_EMAIL = "user_email"
        private const val COLUMN_START_TIME = "start_time"
        private const val COLUMN_DURATION = "duration"
        private const val COLUMN_TOTAL_PRICE = "total_price"
        private const val COLUMN_STATION_NAME_IN_BOOKING = "station_name" // New column
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_EMAIL TEXT, $COLUMN_PASSWORD TEXT)"
        db.execSQL(createTable)

        val createBikeStationsTable = """
    CREATE TABLE $TABLE_BIKE_STATIONS (
        $COLUMN_STATION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_STATION_NAME TEXT,
        $COLUMN_AVAILABLE_BIKES INTEGER
    )
""".trimIndent()

        db.execSQL(createBikeStationsTable)

        val createBookingsTable = """
    CREATE TABLE $TABLE_BOOKINGS (
        $COLUMN_BOOKING_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_USER_EMAIL TEXT,
        $COLUMN_STATION_ID INTEGER,
        $COLUMN_STATION_NAME_IN_BOOKING TEXT,
        $COLUMN_START_TIME TEXT,
        $COLUMN_DURATION INTEGER,
        $COLUMN_TOTAL_PRICE REAL,
        FOREIGN KEY ($COLUMN_USER_EMAIL) REFERENCES users(email),
        FOREIGN KEY ($COLUMN_STATION_ID) REFERENCES bike_stations(station_id)
    )
""".trimIndent()

        db.execSQL(createBookingsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add station_name column to bookings table for existing installations
            db.execSQL("ALTER TABLE $TABLE_BOOKINGS ADD COLUMN $COLUMN_STATION_NAME_IN_BOOKING TEXT")

            // Update existing records to include station names
            db.execSQL("""
                UPDATE $TABLE_BOOKINGS 
                SET $COLUMN_STATION_NAME_IN_BOOKING = (
                    SELECT $COLUMN_STATION_NAME 
                    FROM $TABLE_BIKE_STATIONS 
                    WHERE $TABLE_BIKE_STATIONS.$COLUMN_STATION_ID = $TABLE_BOOKINGS.$COLUMN_STATION_ID
                )
            """)
        }
    }

    //fetch station names dynamically
    fun getStationNames(): List<String> {
        val db = readableDatabase
        val query = "SELECT $COLUMN_STATION_NAME FROM $TABLE_BIKE_STATIONS"
        val cursor = db.rawQuery(query, null)

        val stations = mutableListOf<String>()
        while (cursor.moveToNext()) {
            stations.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return stations
    }

    //handle bookings
    fun bookBike(userEmail: String, stationName: String, date: String, startTime: String, duration: Int, totalPrice: Double): Boolean {
        val db = writableDatabase

        // Get station ID and available bikes
        val query = "SELECT $COLUMN_STATION_ID, $COLUMN_AVAILABLE_BIKES FROM $TABLE_BIKE_STATIONS WHERE $COLUMN_STATION_NAME = ?"
        val cursor = db.rawQuery(query, arrayOf(stationName))

        if (cursor.moveToFirst()) {
            val stationId = cursor.getInt(0)
            val availableBikes = cursor.getInt(1)

            if (availableBikes > 0) {
                // Convert 12-hour format to 24-hour format
                val parts = startTime.split(" ")
                val timePart = parts[0].split(":")
                var hour = timePart[0].toInt()
                val minute = timePart[1]
                val period = parts[1]

                // Convert to 24-hour format
                if (period == "PM" && hour != 12) {
                    hour += 12
                } else if (period == "AM" && hour == 12) {
                    hour = 0
                }

                // Format as 24-hour time
                val formattedTime = String.format("%02d:%s", hour, minute)
                val combinedDateTime = "$date $formattedTime"

                // Insert booking
                val values = ContentValues().apply {
                    put(COLUMN_USER_EMAIL, userEmail)
                    put(COLUMN_STATION_ID, stationId)
                    put(COLUMN_STATION_NAME_IN_BOOKING, stationName) // Store station name
                    put(COLUMN_START_TIME, combinedDateTime)
                    put(COLUMN_DURATION, duration)
                    put(COLUMN_TOTAL_PRICE, totalPrice)
                }
                db.insert(TABLE_BOOKINGS, null, values)

                // Update available bikes
                val updateQuery = "UPDATE $TABLE_BIKE_STATIONS SET $COLUMN_AVAILABLE_BIKES = $COLUMN_AVAILABLE_BIKES - 1 WHERE $COLUMN_STATION_ID = ?"
                db.execSQL(updateQuery, arrayOf(stationId.toString()))

                cursor.close()
                db.close()
                return true
            }
        }
        cursor.close()
        db.close()
        return false
    }

    fun getBikeCount(stationName: String): Int {
        val db = readableDatabase
        val query = "SELECT $COLUMN_AVAILABLE_BIKES FROM $TABLE_BIKE_STATIONS WHERE $COLUMN_STATION_NAME = ?"
        val cursor = db.rawQuery(query, arrayOf(stationName))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun initializeBikeStations() {
        val db = writableDatabase

        // Check if data already exists
        val countQuery = "SELECT COUNT(*) FROM bike_stations"
        val cursor = db.rawQuery(countQuery, null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) { // Only insert if the table is empty
            val stations = listOf(
                "Colombo", "Gampaha", "Kandy", "Galle", "Jaffna",
                "Kurunegala", "Ratnapura", "Anuradhapura", "Badulla", "Trincomalee"
            )

            for (station in stations) {
                val values = ContentValues().apply {
                    put("station_name", station)
                    put("available_bikes", 5)
                }
                db.insert("bike_stations", null, values)
            }
        }
        db.close()
    }

    fun getBikeStations(): List<Pair<String, Int>> {
        val db = readableDatabase
        val query = "SELECT $COLUMN_STATION_NAME, $COLUMN_AVAILABLE_BIKES FROM $TABLE_BIKE_STATIONS"
        val cursor = db.rawQuery(query, null)

        val stations = mutableListOf<Pair<String, Int>>()
        while (cursor.moveToNext()) {
            val name = cursor.getString(0)
            val bikes = cursor.getInt(1)
            stations.add(Pair(name, bikes))
        }
        cursor.close()
        db.close()
        return stations
    }

    // Add a new method to get bookings with station names
    fun getBookings(userEmail: String): List<Map<String, Any>> {
        val db = readableDatabase
        val query = """
            SELECT b.$COLUMN_BOOKING_ID, b.$COLUMN_USER_EMAIL, 
                   b.$COLUMN_STATION_ID, b.$COLUMN_STATION_NAME_IN_BOOKING,
                   b.$COLUMN_START_TIME, b.$COLUMN_DURATION, b.$COLUMN_TOTAL_PRICE
            FROM $TABLE_BOOKINGS b
            WHERE b.$COLUMN_USER_EMAIL = ?
            ORDER BY b.$COLUMN_START_TIME DESC
        """
        val cursor = db.rawQuery(query, arrayOf(userEmail))

        val bookings = mutableListOf<Map<String, Any>>()
        while (cursor.moveToNext()) {
            val booking = mutableMapOf<String, Any>()
            booking["id"] = cursor.getInt(0)
            booking["email"] = cursor.getString(1)
            booking["stationId"] = cursor.getInt(2)
            booking["stationName"] = cursor.getString(3)
            booking["startTime"] = cursor.getString(4)
            booking["duration"] = cursor.getInt(5)
            booking["totalPrice"] = cursor.getDouble(6)
            bookings.add(booking)
        }
        cursor.close()
        db.close()
        return bookings
    }

    fun registerUser(email: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)

        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun updateBikeStationsCount(): Boolean {
        val db = writableDatabase
        var success = false

        try {
            db.beginTransaction()

            // Reset all stations to their initial bike count (5)
            val resetQuery = "UPDATE $TABLE_BIKE_STATIONS SET $COLUMN_AVAILABLE_BIKES = 5"
            db.execSQL(resetQuery)

            // Get current date and time in the format likely used in your database
            // Using SimpleDateFormat for compatibility with older Android versions
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            val currentDateTimeString = dateFormat.format(java.util.Date())

            android.util.Log.d("BikeUpdate", "Current time: $currentDateTimeString")

            // Sample query to test if any records exist in bookings table
            val testCursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_BOOKINGS", null)
            testCursor.moveToFirst()
            val bookingCount = testCursor.getInt(0)
            android.util.Log.d("BikeUpdate", "Total bookings in database: $bookingCount")
            testCursor.close()

            // Get a sample booking to verify date format
            if (bookingCount > 0) {
                val sampleCursor = db.rawQuery("SELECT $COLUMN_START_TIME, $COLUMN_DURATION FROM $TABLE_BOOKINGS LIMIT 1", null)
                if (sampleCursor.moveToFirst()) {
                    val sampleTime = sampleCursor.getString(0)
                    val sampleDuration = sampleCursor.getInt(1)
                    android.util.Log.d("BikeUpdate", "Sample booking: Start=$sampleTime, Duration=$sampleDuration minutes")
                }
                sampleCursor.close()
            }

            // Query for active bookings
            val query = """
                SELECT $COLUMN_STATION_ID, COUNT(*) AS booked_bikes
                FROM $TABLE_BOOKINGS
                WHERE 
                    datetime($COLUMN_START_TIME) <= datetime(?)
                    AND 
                    datetime($COLUMN_START_TIME, '+' || $COLUMN_DURATION || ' hours') > datetime(?)
                GROUP BY $COLUMN_STATION_ID
            """

            val cursor = db.rawQuery(query, arrayOf(currentDateTimeString, currentDateTimeString))

            android.util.Log.d("BikeUpdate", "Found ${cursor.count} stations with active bookings")

            // Update bike counts for stations with active bookings
            while (cursor.moveToNext()) {
                val stationId = cursor.getInt(0)
                val bookedBikes = cursor.getInt(1)

                android.util.Log.d("BikeUpdate", "Station ID: $stationId has $bookedBikes active bookings")

                val updateStationQuery = """
                    UPDATE $TABLE_BIKE_STATIONS 
                    SET $COLUMN_AVAILABLE_BIKES = 
                        CASE
                            WHEN (5 - ?) < 0 THEN 0
                            ELSE 5 - ?
                        END
                    WHERE $COLUMN_STATION_ID = ?
                """

                db.execSQL(updateStationQuery, arrayOf(bookedBikes.toString(), bookedBikes.toString(), stationId.toString()))
            }

            cursor.close()

            // Verify the update
            val verificationCursor = db.rawQuery("SELECT $COLUMN_STATION_ID, $COLUMN_STATION_NAME, $COLUMN_AVAILABLE_BIKES FROM $TABLE_BIKE_STATIONS", null)
            android.util.Log.d("BikeUpdate", "Verification - Station counts after update:")
            while (verificationCursor.moveToNext()) {
                val id = verificationCursor.getInt(0)
                val name = verificationCursor.getString(1)
                val bikes = verificationCursor.getInt(2)
                android.util.Log.d("BikeUpdate", "Station ID: $id, Name: $name, Available bikes: $bikes")
            }
            verificationCursor.close()

            db.setTransactionSuccessful()
            success = true
        } catch (e: Exception) {
            android.util.Log.e("BikeUpdate", "Error updating bike counts", e)
            e.printStackTrace()
        } finally {
            if (db.inTransaction()) {
                db.endTransaction()
            }
            db.close()
        }

        return success
    }
}