package com.example.nannysystem

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class register : AppCompatActivity() {

    // Replace DatabaseHelper with Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 2. Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val rgRole = findViewById<RadioGroup>(R.id.rgRole)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)
        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)

        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            val passwordPattern = """^(?=.*[^a-zA-Z0-9]).{8,}$"""

            if (password.matches(passwordPattern.toRegex())) {
                // Password is valid - proceed with your app logic
            } else {
                if (password.length < 6) {
                    Toast.makeText(this, "Password must be at least 8 characters long and contain a special character", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            // Validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || !cbTerms.isChecked) {
                Toast.makeText(this, "Please fill all fields and accept terms", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val role = when (rgRole.checkedRadioButtonId) {
                R.id.rbNanny -> "nanny"
                else -> "parent"
            }

            // Disable button to prevent multiple clicks during network call
            btnSignup.isEnabled = false

            // 3. Create User in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""

                        // Determine their account status based on the role
                        val status = if (role == "nanny") {
                            "pending" // Nannies must wait for admin approval
                        } else {
                            "approved" // Parents are approved automatically
                        }

                        // 4. Save extra user details to Firestore
                        val userMap = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "role" to role,
                            "accountStatus" to status // Added the new status field here!
                        )

                        db.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener {
                                if (role == "nanny") {
                                    Toast.makeText(this, "Registration Successful! Please wait for admin approval.", Toast.LENGTH_LONG).show()

                                    // Force sign out immediately so they can't bypass the login screen lock
                                    auth.signOut()

                                    val intent = Intent(this, login::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "Registration Successful! Please login.", Toast.LENGTH_LONG).show()

                                    // Parents can proceed to login normally
                                    val intent = Intent(this, login::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                                btnSignup.isEnabled = true // Re-enable if it fails
                            }
                    } else {
                        // Registration Failed (e.g., email already exists)
                        Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        btnSignup.isEnabled = true // Re-enable if it fails
                    }
                }
        }

        tvSignIn.setOnClickListener { finish() }
    }
}