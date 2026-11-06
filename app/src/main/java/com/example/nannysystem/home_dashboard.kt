package com.example.nannysystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class home_dashboard : AppCompatActivity() {

    private lateinit var rvNannies: RecyclerView
    private lateinit var nannyAdapter: NannyAdapter
    private val nannyList = mutableListOf<Nanny>()

    private lateinit var db: FirebaseFirestore
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_dashboard)

        db = FirebaseFirestore.getInstance()
        sessionManager = SessionManager(this)

        rvNannies = findViewById(R.id.rvNannies)
        rvNannies.layoutManager = LinearLayoutManager(this)

        nannyAdapter = NannyAdapter(nannyList)
        rvNannies.adapter = nannyAdapter

       //log out button
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Clear the saved session data
            sessionManager.logoutUser()

            //Redirect to the Login screen
            val intent = Intent(this, login::class.java)

            //Clear the activity backstack so they can't use the back arrow to bypass the login
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            // Close the home_dashboard
            finish()
        }

        // Added Help Button Logic
        val btnHelp = findViewById<Button>(R.id.btnHelp)
        btnHelp.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }

        fetchNanniesFromFirebase()
    }

    private fun fetchNanniesFromFirebase() {
        // Query Firestore for users who are nannies
        db.collection("users")
            .whereEqualTo("role", "nanny")
            .get()
            .addOnSuccessListener { documents ->
                nannyList.clear()
                for (document in documents) {
                    val nanny = document.toObject(Nanny::class.java).copy(id = document.id)
                    nannyList.add(nanny)
                }
                nannyAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading nannies: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}