package com.example.nannysystem

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "NannySystemDB.db"

        // Tables & Columns
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "user_id"
        const val COL_USER_NAME = "name"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_PASS = "password_hash"
        const val COL_USER_ROLE = "role"
        const val COL_USER_CREATED = "created_at"

        const val TABLE_PROFILES = "nanny_profiles"
        const val COL_PROF_USER_ID = "user_id"
        const val COL_PROF_BIO = "bio"
        const val COL_PROF_RATE = "hourly_rate"
        const val COL_PROF_LOCATION = "location"
        const val COL_PROF_ACTIVE = "is_active"

        const val TABLE_BOOKINGS = "bookings"
        const val COL_BOOKING_ID = "booking_id"
        const val COL_BOOKING_PARENT_ID = "parent_id"
        const val COL_BOOKING_NANNY_ID = "nanny_id"
        const val COL_BOOKING_DATE = "booking_date"
        const val COL_BOOKING_TIME = "start_time"
        const val COL_BOOKING_DURATION = "duration_hours"
        const val COL_BOOKING_COST = "total_cost"
        const val COL_BOOKING_STATUS = "status"

        const val TABLE_REVIEWS = "reviews"
        const val COL_REV_ID = "review_id"
        const val COL_REV_BOOKING_ID = "booking_id"
        const val COL_REV_RATING = "rating"
        const val COL_REV_COMMENT = "comment"
        const val COL_REV_CREATED = "created_at"
    }

    // --- Data Classes for UI ---
    data class NannyDisplayData(val userId: Int, val name: String, val bio: String, val hourlyRate: Double, val location: String, val averageRating: Double, val totalReviews: Int)
    data class BookingDisplayData(val bookingId: Int, val parentName: String, val date: String, val time: String, val duration: Int, val status: String)
    data class ParentBookingData(val bookingId: Int, val nannyName: String, val date: String, val time: String, val duration: Int, val status: String)
    data class ProfileDetails(val bio: String, val hourlyRate: Double, val location: String)

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_USERS ($COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_USER_NAME TEXT NOT NULL, $COL_USER_EMAIL TEXT UNIQUE NOT NULL, $COL_USER_PASS TEXT NOT NULL, $COL_USER_ROLE TEXT NOT NULL, $COL_USER_CREATED TEXT NOT NULL)")
        db.execSQL("CREATE TABLE $TABLE_PROFILES (profile_id INTEGER PRIMARY KEY AUTOINCREMENT, $COL_PROF_USER_ID INTEGER NOT NULL, $COL_PROF_BIO TEXT, $COL_PROF_RATE REAL, $COL_PROF_LOCATION TEXT, $COL_PROF_ACTIVE INTEGER DEFAULT 1, FOREIGN KEY($COL_PROF_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE $TABLE_BOOKINGS ($COL_BOOKING_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_BOOKING_PARENT_ID INTEGER NOT NULL, $COL_BOOKING_NANNY_ID INTEGER NOT NULL, $COL_BOOKING_DATE TEXT NOT NULL, $COL_BOOKING_TIME TEXT NOT NULL, $COL_BOOKING_DURATION INTEGER NOT NULL, $COL_BOOKING_COST REAL NOT NULL, $COL_BOOKING_STATUS TEXT NOT NULL, FOREIGN KEY($COL_BOOKING_PARENT_ID) REFERENCES $TABLE_USERS($COL_USER_ID), FOREIGN KEY($COL_BOOKING_NANNY_ID) REFERENCES $TABLE_USERS($COL_USER_ID))")
        db.execSQL("CREATE TABLE $TABLE_REVIEWS ($COL_REV_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_REV_BOOKING_ID INTEGER NOT NULL, $COL_REV_RATING INTEGER CHECK($COL_REV_RATING >= 1 AND $COL_REV_RATING <= 5), $COL_REV_COMMENT TEXT, $COL_REV_CREATED TEXT NOT NULL, FOREIGN KEY($COL_REV_BOOKING_ID) REFERENCES $TABLE_BOOKINGS($COL_BOOKING_ID) ON DELETE CASCADE)")

        val values = ContentValues().apply {
            put(COL_USER_NAME, "System Admin")
            put(COL_USER_EMAIL, "admin@nannysystem.com")
            put(COL_USER_PASS, "admin123")
            put(COL_USER_ROLE, "admin")
            put(COL_USER_CREATED, getCurrentDateTime())
        }
        db.insert(TABLE_USERS, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REVIEWS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROFILES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    // --- Authentication ---
    fun registerUser(name: String, email: String, passwordHash: String, role: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_NAME, name)
            put(COL_USER_EMAIL, email)
            put(COL_USER_PASS, passwordHash)
            put(COL_USER_ROLE, role)
            put(COL_USER_CREATED, getCurrentDateTime())
        }
        val userId = db.insert(TABLE_USERS, null, values)
        if (userId != -1L && role == "nanny") {
            db.insert(TABLE_PROFILES, null, ContentValues().apply { put(COL_PROF_USER_ID, userId); put(COL_PROF_ACTIVE, 1) })
        }
        db.close()
        return userId
    }

    fun authenticateUser(email: String, passwordHash: String): Cursor? {
        return this.readableDatabase.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COL_USER_EMAIL = ? AND $COL_USER_PASS = ?", arrayOf(email, passwordHash))
    }

    // --- Nanny Queries (Home Dashboard & Profile) ---
    fun getAllNannies(): List<NannyDisplayData> {
        val nannyList = mutableListOf<NannyDisplayData>()
        val db = this.readableDatabase
        val query = """
            SELECT u.$COL_USER_ID, u.$COL_USER_NAME, p.$COL_PROF_BIO, p.$COL_PROF_RATE, p.$COL_PROF_LOCATION,
                   COALESCE(AVG(r.$COL_REV_RATING), 0.0) AS avg_rating, COUNT(r.$COL_REV_ID) AS total_reviews
            FROM $TABLE_USERS u
            INNER JOIN $TABLE_PROFILES p ON u.$COL_USER_ID = p.$COL_PROF_USER_ID
            LEFT JOIN $TABLE_BOOKINGS b ON u.$COL_USER_ID = b.$COL_BOOKING_NANNY_ID
            LEFT JOIN $TABLE_REVIEWS r ON b.$COL_BOOKING_ID = r.$COL_REV_BOOKING_ID
            WHERE u.$COL_USER_ROLE = 'nanny' AND p.$COL_PROF_ACTIVE = 1
            GROUP BY u.$COL_USER_ID
        """.trimIndent()
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                nannyList.add(NannyDisplayData(cursor.getInt(0), cursor.getString(1), if (!cursor.isNull(2)) cursor.getString(2) else "No bio provided.", if (!cursor.isNull(3)) cursor.getDouble(3) else 0.0, if (!cursor.isNull(4)) cursor.getString(4) else "Location TBD", cursor.getDouble(5), cursor.getInt(6)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return nannyList
    }

    fun getNannyProfile(userId: Int): ProfileDetails {
        val db = this.readableDatabase
        var bio = ""; var rate = 0.0; var location = ""
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PROFILES WHERE $COL_PROF_USER_ID = ?", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            bio = if (!cursor.isNull(cursor.getColumnIndexOrThrow(COL_PROF_BIO))) cursor.getString(cursor.getColumnIndexOrThrow(COL_PROF_BIO)) else ""
            rate = if (!cursor.isNull(cursor.getColumnIndexOrThrow(COL_PROF_RATE))) cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROF_RATE)) else 0.0
            location = if (!cursor.isNull(cursor.getColumnIndexOrThrow(COL_PROF_LOCATION))) cursor.getString(cursor.getColumnIndexOrThrow(COL_PROF_LOCATION)) else ""
        }
        cursor.close()
        db.close()
        return ProfileDetails(bio, rate, location)
    }

    fun updateNannyProfile(userId: Int, bio: String, rate: Double, location: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply { put(COL_PROF_BIO, bio); put(COL_PROF_RATE, rate); put(COL_PROF_LOCATION, location) }
        val success = db.update(TABLE_PROFILES, values, "$COL_PROF_USER_ID = ?", arrayOf(userId.toString())) > 0
        db.close()
        return success
    }

    // --- Bookings & Reviews ---
    fun createBooking(parentId: Int, nannyId: Int, date: String, time: String, durationHours: Int, totalCost: Double): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply { put(COL_BOOKING_PARENT_ID, parentId); put(COL_BOOKING_NANNY_ID, nannyId); put(COL_BOOKING_DATE, date); put(COL_BOOKING_TIME, time); put(COL_BOOKING_DURATION, durationHours); put(COL_BOOKING_COST, totalCost); put(COL_BOOKING_STATUS, "Pending") }
        val result = db.insert(TABLE_BOOKINGS, null, values)
        db.close()
        return result
    }

    fun updateBookingStatus(bookingId: Int, newStatus: String): Boolean {
        val db = this.writableDatabase
        val success = db.update(TABLE_BOOKINGS, ContentValues().apply { put(COL_BOOKING_STATUS, newStatus) }, "$COL_BOOKING_ID = ?", arrayOf(bookingId.toString())) > 0
        db.close()
        return success
    }

    fun getNannyBookings(nannyId: Int): List<BookingDisplayData> {
        val list = mutableListOf<BookingDisplayData>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT b.$COL_BOOKING_ID, u.$COL_USER_NAME, b.$COL_BOOKING_DATE, b.$COL_BOOKING_TIME, b.$COL_BOOKING_DURATION, b.$COL_BOOKING_STATUS FROM $TABLE_BOOKINGS b INNER JOIN $TABLE_USERS u ON b.$COL_BOOKING_PARENT_ID = u.$COL_USER_ID WHERE b.$COL_BOOKING_NANNY_ID = ? AND b.$COL_BOOKING_STATUS IN ('Pending', 'Paid')", arrayOf(nannyId.toString()))
        if (cursor.moveToFirst()) { do { list.add(BookingDisplayData(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5))) } while (cursor.moveToNext()) }
        cursor.close()
        db.close()
        return list
    }

    fun getParentBookings(parentId: Int): List<ParentBookingData> {
        val list = mutableListOf<ParentBookingData>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT b.$COL_BOOKING_ID, u.$COL_USER_NAME, b.$COL_BOOKING_DATE, b.$COL_BOOKING_TIME, b.$COL_BOOKING_DURATION, b.$COL_BOOKING_STATUS FROM $TABLE_BOOKINGS b INNER JOIN $TABLE_USERS u ON b.$COL_BOOKING_NANNY_ID = u.$COL_USER_ID WHERE b.$COL_BOOKING_PARENT_ID = ? ORDER BY b.$COL_BOOKING_ID DESC", arrayOf(parentId.toString()))
        if (cursor.moveToFirst()) { do { list.add(ParentBookingData(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5))) } while (cursor.moveToNext()) }
        cursor.close()
        db.close()
        return list
    }

    fun addReview(bookingId: Int, rating: Int, comment: String): Boolean {
        val db = this.writableDatabase
        val result = db.insert(TABLE_REVIEWS, null, ContentValues().apply { put(COL_REV_BOOKING_ID, bookingId); put(COL_REV_RATING, rating); put(COL_REV_COMMENT, comment); put(COL_REV_CREATED, getCurrentDateTime()) })
        db.close()
        return result != -1L
    }

    // --- Admin Counts ---
    fun getTotalUsersCount(): Int { val cursor = this.readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_USERS", null); var count = 0; if (cursor.moveToFirst()) count = cursor.getInt(0); cursor.close(); return count }
    fun getTotalBookingsCount(): Int { val cursor = this.readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_BOOKINGS", null); var count = 0; if (cursor.moveToFirst()) count = cursor.getInt(0); cursor.close(); return count }
}