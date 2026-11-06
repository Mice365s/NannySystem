package com.example.nannysystem

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class parent_view_nanny : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_view_nanny)

        // 1. Catch the data passed from the NannyAdapter
        val nannyId = intent.getStringExtra("NANNY_ID") ?: ""
        val nannyName = intent.getStringExtra("NANNY_NAME") ?: "Unknown Nanny"
        val nannyLocation = intent.getStringExtra("NANNY_LOCATION") ?: "Unknown Location"
        val nannyRate = intent.getStringExtra("NANNY_RATE") ?: "0.00"
        val nannyRating = intent.getStringExtra("NANNY_RATING") ?: "0.0"
        val nannyBio = intent.getStringExtra("NANNY_BIO") ?: "No bio provided."

        // 2. Bind the data to the UI text fields
        findViewById<TextView>(R.id.tvProfileName).text = nannyName
        findViewById<TextView>(R.id.tvProfileLocation).text = nannyLocation
        findViewById<TextView>(R.id.tvProfileRate).text = "R$nannyRate / hr"
        findViewById<TextView>(R.id.tvProfileRating).text = "⭐ $nannyRating"
        findViewById<TextView>(R.id.tvProfileBio).text = nannyBio

        // 3. Handle the Back Button
        val btnBack = findViewById<MaterialButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Closes this screen and returns to the list of nannies
        }

        // 4. Handle the "Book This Nanny" Button
        val btnGoToBooking = findViewById<MaterialButton>(R.id.btnGoToBooking)
        btnGoToBooking.setOnClickListener {
            // Open the existing booking form
            val intent = Intent(this, booking::class.java)

            // Pass the Nanny's ID and Name forward to the booking screen
            intent.putExtra("NANNY_ID", nannyId)
            intent.putExtra("NANNY_NAME", nannyName)

            startActivity(intent)
        }
    }
}