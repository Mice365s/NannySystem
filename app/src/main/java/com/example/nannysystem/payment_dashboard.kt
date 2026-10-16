package com.example.nannysystem

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class payment_dashboard : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var bookingId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_dashboard)

        db = FirebaseFirestore.getInstance()

        // Get the booking details passed from ParentBookingAdapter
        bookingId = intent.getStringExtra("bookingId")
        val nannyName = intent.getStringExtra("nannyName") ?: "Nanny"

        val tvHeader = findViewById<TextView>(R.id.tvPaymentHeader)
        tvHeader.text = "Pay $nannyName"

        val etCardNumber = findViewById<EditText>(R.id.etCardNumber)
        val etExpiry = findViewById<EditText>(R.id.etExpiry)
        val etCvv = findViewById<EditText>(R.id.etCvv)
        val rbNannyRating = findViewById<RatingBar>(R.id.rbNannyRating) // Added RatingBar
        val btnSubmit = findViewById<Button>(R.id.btnSubmitPayment)
        val tvCancel = findViewById<TextView>(R.id.tvCancelPayment)

        btnSubmit.setOnClickListener {
            val card = etCardNumber.text.toString().trim()
            val expiry = etExpiry.text.toString().trim()
            val cvv = etCvv.text.toString().trim()
            val finalRating = rbNannyRating.rating // Capture the star rating

            if (card.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(this, "Please fill in all payment details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (bookingId == null) {
                Toast.makeText(this, "Error: Booking ID missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Optional: Force a minimum rating before they can pay
            if (finalRating == 0f) {
                Toast.makeText(this, "Please provide a rating before paying", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSubmit.isEnabled = false // Prevent double clicks

            // Update the booking status to "paid" AND save the rating to Firestore
            db.collection("bookings").document(bookingId!!)
                .update(
                    "status", "paid",
                    "rating", finalRating
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Payment & Rating Successful!", Toast.LENGTH_SHORT).show()
                    finish() // Close payment screen, return to bookings list
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Payment failed: ${e.message}", Toast.LENGTH_LONG).show()
                    btnSubmit.isEnabled = true
                }
        }

        tvCancel.setOnClickListener {
            finish()
        }
    }
}