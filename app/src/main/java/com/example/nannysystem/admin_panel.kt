package com.example.nannysystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class admin_panel : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sessionManager = SessionManager(this)

        // Security Check: Only allow 'admin' role
        if (!sessionManager.isLoggedIn() || sessionManager.getUserRole() != "admin") {
            logoutAndRedirect()
            return
        }

        val tvLogout = findViewById<TextView>(R.id.tvLogoutAdmin)
        tvLogout.setOnClickListener {
            logoutAndRedirect()
        }

        // Load the dynamic statistics from the Firebase database
        loadStatistics()

        val btnManageUsers = findViewById<Button>(R.id.btnManageUsers)
        btnManageUsers.text = "Add New Admin" // Overriding the mockup text for functionality

        val btnViewReports = findViewById<Button>(R.id.btnViewReports)

        btnManageUsers.setOnClickListener {
            showAddAdminDialog()
        }

        // UPDATED: Now launches the new AdminReportsActivity
        btnViewReports.setOnClickListener {
            startActivity(Intent(this, AdminReportsActivity::class.java))
        }
    }

    private fun loadStatistics() {
        val tvUsers = findViewById<TextView>(R.id.tvTotalUsers)
        val tvBookings = findViewById<TextView>(R.id.tvTotalBookings)

        // Count total users in Firestore
        db.collection("users").get()
            .addOnSuccessListener { result ->
                tvUsers.text = getString(R.string.total_users_dynamic, result.size())
            }
            .addOnFailureListener {
                tvUsers.text = "Total Users: Error"
            }

        // Count total bookings in Firestore
        db.collection("bookings").get()
            .addOnSuccessListener { result ->
                tvBookings.text = getString(R.string.total_bookings_dynamic, result.size())
            }
            .addOnFailureListener {
                tvBookings.text = "Total Bookings: Error"
            }
    }

    private fun showAddAdminDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Create New Admin")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val nameInput = EditText(this).apply { hint = "Admin Name" }
        layout.addView(nameInput)

        val emailInput = EditText(this).apply { hint = "Admin Email" }
        layout.addView(emailInput)

        val passInput = EditText(this).apply { hint = "Password" }
        layout.addView(passInput)

        builder.setView(layout)

        builder.setPositiveButton("Create") { dialog, _ ->
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val pass = passInput.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty()) {
                // Register new admin via Firebase Auth
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener { authResult ->
                        val newUserId = authResult.user?.uid
                        if (newUserId != null) {
                            // Save admin role to Firestore
                            val adminMap = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "role" to "admin"
                            )

                            db.collection("users").document(newUserId).set(adminMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Admin Added! You must log in again.", Toast.LENGTH_LONG).show()
                                    logoutAndRedirect()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun logoutAndRedirect() {
        auth.signOut()
        sessionManager.logoutUser()
        val intent = Intent(this, login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}