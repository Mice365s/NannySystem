package com.example.nannysystem

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminReportsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AdminReportAdapter
    private val bookingList = mutableListOf<BookingModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reports)

        db = FirebaseFirestore.getInstance()

        // Updated ID to match your XML exactly: rvAdminReports
        val rvReports = findViewById<RecyclerView>(R.id.rvAdminReports)
        rvReports.layoutManager = LinearLayoutManager(this)
        adapter = AdminReportAdapter(bookingList)
        rvReports.adapter = adapter

        fetchReports()
    }

    private fun fetchReports() {
        db.collection("bookings")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to load reports.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                bookingList.clear()
                if (snapshots != null) {
                    for (doc in snapshots) {
                        val booking = doc.toObject(BookingModel::class.java)
                        if (booking.id.isEmpty()) {
                            booking.id = doc.id
                        }
                        bookingList.add(booking)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }
}