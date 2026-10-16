package com.example.nannysystem

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class booking : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking) // Replace with your exact XML name

        db = FirebaseFirestore.getInstance()
        sessionManager = SessionManager(this)

        // 1. Get the Parent's details from the session
        val parentId = sessionManager.getUserDetails()[SessionManager.KEY_USER_ID] ?: ""
        val parentName = sessionManager.getUserDetails()[SessionManager.KEY_NAME] ?: "Unknown Parent"
        // 2. Get the Nanny's details from the Intent (passed from NannyAdapter)
        val nannyId = intent.getStringExtra("nannyId") ?: ""
        val nannyName = intent.getStringExtra("nannyName") ?: "Unknown Nanny"

        // Bind XML views (Update these IDs if yours are different)
        val tvNannyName = findViewById<TextView>(R.id.tvBookingNannyName)
        val etDate = findViewById<EditText>(R.id.etBookingDate)
        val etTime = findViewById<EditText>(R.id.etBookingTime)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmBooking)
        val tvCancel = findViewById<TextView>(R.id.tvCancelBooking)

        // Show who we are booking
        tvNannyName.text = "Booking Request for: $nannyName"

        // Handle Confirmation
        btnConfirm.setOnClickListener {
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()

            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please enter date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (parentId == null || nannyId.isEmpty()) {
                Toast.makeText(this, "Error: Missing user data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnConfirm.isEnabled = false // Prevent double booking

            // 3. Create a new unique ID in the "bookings" collection
            val newBookingRef = db.collection("bookings").document()

            val bookingData = BookingModel(
                id = newBookingRef.id,
                parentId = parentId,
                nannyId = nannyId,
                parentName = parentName,
                nannyName = nannyName,
                date = date,
                time = time,
                status = "pending"
            )

            // 4. Save to Firestore
            newBookingRef.set(bookingData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Booking Request Sent!", Toast.LENGTH_SHORT).show()
                    finish() // Close screen, return to dashboard
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Booking failed: ${e.message}", Toast.LENGTH_LONG).show()
                    btnConfirm.isEnabled = true
                }
        }

        tvCancel.setOnClickListener {
            finish()
        }
    }
}