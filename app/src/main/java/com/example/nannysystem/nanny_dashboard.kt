package com.example.nannysystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class nanny_dashboard : AppCompatActivity() {

    private lateinit var rvBookings: RecyclerView
    private lateinit var adapter: BookingRequestAdapter
    private val bookingList = mutableListOf<BookingModel>()

    private lateinit var db: FirebaseFirestore
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nanny_dashboard)

        db = FirebaseFirestore.getInstance()
        sessionManager = SessionManager(this)

        val nannyId = sessionManager.getUserDetails()[SessionManager.KEY_USER_ID]
        val nannyName = sessionManager.getUserDetails()[SessionManager.KEY_NAME] ?: "Nanny"

        // Setup Header & Profile Button
        val tvWelcome = findViewById<TextView>(R.id.tvNannyWelcome)
        tvWelcome.text = "Welcome, $nannyName!"

        findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            startActivity(Intent(this, nanny_profile::class.java))
        }

        findViewById<Button>(R.id.btnNannyLogout).setOnClickListener {
            sessionManager.logoutUser()
        }

        // Setup RecyclerView
        rvBookings = findViewById(R.id.rvBookingRequests)
        rvBookings.layoutManager = LinearLayoutManager(this)
        adapter = BookingRequestAdapter(bookingList)
        rvBookings.adapter = adapter

        if (nannyId != null) {
            fetchBookings(nannyId)
        }
    }

    private fun fetchBookings(nannyId: String) {
        // Use snapshot listener for real-time updates!
        db.collection("bookings")
            .whereEqualTo("nannyId", nannyId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to load bookings.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                bookingList.clear()
                if (snapshots != null) {
                    for (doc in snapshots) {
                        val booking = doc.toObject(BookingModel::class.java)
                        bookingList.add(booking)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }
}