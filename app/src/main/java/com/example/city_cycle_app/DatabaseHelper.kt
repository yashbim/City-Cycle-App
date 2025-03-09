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

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
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
}
