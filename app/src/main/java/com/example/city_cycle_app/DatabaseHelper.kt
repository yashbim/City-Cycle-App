import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "CityCycle"
        private const val DATABASE_VERSION = 1
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
//        private const var COLUMN_STATION_ID = "station_id"
        private const val COLUMN_START_TIME = "start_time"
        private const val COLUMN_DURATION = "duration"
        private const val COLUMN_TOTAL_PRICE = "total_price"


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
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
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
                // Combine date and time for the start_time field
                val combinedDateTime = "$date $startTime"

                // Insert booking
                val values = ContentValues().apply {
                    put(COLUMN_USER_EMAIL, userEmail)
                    put(COLUMN_STATION_ID, stationId)
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

            // Step 1: Reset all stations to their initial bike count (5)
            val resetQuery = "UPDATE $TABLE_BIKE_STATIONS SET $COLUMN_AVAILABLE_BIKES = 5"
            db.execSQL(resetQuery)

            // Step 2: Get the current time in a format matching our stored dates
            val currentDateTime = System.currentTimeMillis()
            val simpleDateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            val currentDateTimeString = simpleDateFormat.format(java.util.Date(currentDateTime))

            android.util.Log.d("BikeUpdate", "Current time: $currentDateTimeString")

            // Step 3: Query for active bookings - where the start_time has passed but end time hasn't
            val query = """
            SELECT $COLUMN_STATION_ID, COUNT(*) AS booked_bikes
            FROM $TABLE_BOOKINGS
            WHERE 
                /* Booking has started */
                datetime($COLUMN_START_TIME) <= datetime(?) 
                AND 
                /* Booking hasn't ended yet */
                datetime($COLUMN_START_TIME, '+' || $COLUMN_DURATION || ' minutes') > datetime(?)
            GROUP BY $COLUMN_STATION_ID
        """

            val cursor = db.rawQuery(query, arrayOf(currentDateTimeString, currentDateTimeString))

            android.util.Log.d("BikeUpdate", "Found ${cursor.count} stations with active bookings")

            // Step 4: For each station with active bookings, reduce the available bikes
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

            // Verify the update worked by querying the bike stations table
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
