package com.example.nannysystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class login : AppCompatActivity() {

    // 1. Add Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 2. Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sessionManager = SessionManager(this)

        // Auto-Login: Skip this screen if the user is already logged in locally
        if (sessionManager.isLoggedIn()) {
            routeUserToDashboard(sessionManager.getUserRole())
            return
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button during network call
            btnLogin.isEnabled = false

            // 3. Authenticate with Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        if (userId != null) {
                            // 4. Fetch the user's Name and Role from Firestore
                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    if (document != null && document.exists()) {
                                        val name = document.getString("name") ?: "User"
                                        val role = document.getString("role") ?: "parent"

                                        val numericId = userId.hashCode()

                                        // Create the secure session
                                        sessionManager.createLoginSession(userId, name, email, role)
                                        Toast.makeText(this, "Welcome back, $name!", Toast.LENGTH_SHORT).show()

                                        routeUserToDashboard(role)
                                    } else {
                                        Toast.makeText(this, "User data not found in database.", Toast.LENGTH_LONG).show()
                                        btnLogin.isEnabled = true
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to fetch user role: ${e.message}", Toast.LENGTH_LONG).show()
                                    btnLogin.isEnabled = true
                                }
                        }
                    } else {
                        // Login Failed
                        Toast.makeText(this, "Invalid email or password: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        btnLogin.isEnabled = true
                    }
                }
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, register::class.java))
        }
    }

    private fun routeUserToDashboard(role: String) {
        val intent = when (role.lowercase()) {
            "admin" -> Intent(this, admin_panel::class.java)
            "nanny" -> Intent(this, nanny_dashboard::class.java)
            else -> Intent(this, home_dashboard::class.java) // Defaults to parent
        }
        // Clear the backstack so they can't hit "Back" and return to the login screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}