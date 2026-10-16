package com.example.nannysystem

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminReportsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var rvReports: RecyclerView
    private lateinit var reportList: ArrayList<BookingModel>
    private lateinit var adapter: AdminReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reports)

        db = FirebaseFirestore.getInstance()
        rvReports = findViewById(R.id.rvAdminReports)
        rvReports.layoutManager = LinearLayoutManager(this)

        reportList = arrayListOf()
        adapter = AdminReportAdapter(reportList)
        rvReports.adapter = adapter

        fetchAllBookings()
    }

    private fun fetchAllBookings() {
        db.collection("bookings").get()
            .addOnSuccessListener { documents ->
                reportList.clear()
                for (document in documents) {
                    val booking = document.toObject(BookingModel::class.java)
                    // Firestore requires empty constructor in data class, or manual mapping:
                    // If you don't have an empty constructor, manually map it like you did in other screens.
                    reportList.add(booking)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load reports: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}