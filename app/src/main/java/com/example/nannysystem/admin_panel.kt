package com.example.nannysystem

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class admin_panel : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        db = FirebaseFirestore.getInstance()
        sessionManager = SessionManager(this)

        val tvTotalParents = findViewById<TextView>(R.id.tvTotalParents)
        val tvTotalNannies = findViewById<TextView>(R.id.tvTotalNannies)

        // 1. Fetch and calculate user categorizations
        fetchUserCounts(tvTotalParents, tvTotalNannies)

        // 2. Setup Navigation Buttons (we will build these specific screens next)
        findViewById<MaterialButton>(R.id.btnApproveNannies).setOnClickListener {
            startActivity(Intent(this, admin_approve_nannies::class.java))
            Toast.makeText(this, "Opening Approvals...", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.btnManageUsers).setOnClickListener {
            startActivity(Intent(this, admin_manage_users::class.java))
            Toast.makeText(this, "Opening User List...", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.btnActiveBookings).setOnClickListener {
            startActivity(Intent(this, AdminReportsActivity::class.java))
            Toast.makeText(this, "Opening Bookings...", Toast.LENGTH_SHORT).show()
        }

        // 3. Admin Logout Security
        findViewById<MaterialButton>(R.id.btnAdminLogout).setOnClickListener {
            sessionManager.logoutUser()
            val intent = Intent(this, login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUserCounts(tvParents: TextView, tvNannies: TextView) {
        // Real-time listener for Parent count
        db.collection("users")
            .whereEqualTo("role", "parent")
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) return@addSnapshotListener
                tvParents.text = snapshots.size().toString()
            }

        // Real-time listener for Nanny count
        db.collection("users")
            .whereEqualTo("role", "nanny")
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) return@addSnapshotListener
                tvNannies.text = snapshots.size().toString()
            }
    }
}