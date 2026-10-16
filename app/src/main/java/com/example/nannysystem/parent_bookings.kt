package com.example.nannysystem

import android.os.Bundle
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class parent_bookings : AppCompatActivity() {

    private lateinit var rvParentBookings: RecyclerView
    private lateinit var adapter: ParentBookingAdapter
    private val bookingList = mutableListOf<BookingModel>()

    private lateinit var db: FirebaseFirestore
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure you have a RecyclerView with the ID 'rvParentBookings' in activity_parent_bookings.xml
        setContentView(R.layout.activity_parent_bookings)

        findViewById<TextView>(R.id.tvBackToHome).setOnClickListener {
            finish() // This returns the parent to the dashboard
        }

        db = FirebaseFirestore.getInstance()
        sessionManager = SessionManager(this)

        val parentId = sessionManager.getUserDetails()[SessionManager.KEY_USER_ID]

        rvParentBookings = findViewById(R.id.rvParentBookings)
        rvParentBookings.layoutManager = LinearLayoutManager(this)
        adapter = ParentBookingAdapter(bookingList)
        rvParentBookings.adapter = adapter

        if (parentId != null) {
            fetchParentBookings(parentId)
        } else {
            Toast.makeText(this, "Error: User session invalid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchParentBookings(parentId: String) {
        // Listen for real-time updates for this specific parent
        db.collection("bookings")
            .whereEqualTo("parentId", parentId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to load your bookings.", Toast.LENGTH_SHORT).show()
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