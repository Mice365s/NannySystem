package com.example.nannysystem

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class admin_approve_nannies : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AdminApproveNannyAdapter
    private val pendingList = mutableListOf<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_approve_nannies)

        db = FirebaseFirestore.getInstance()

        findViewById<MaterialButton>(R.id.btnBackAdmin).setOnClickListener {
            finish()
        }

        val rvPendingNannies = findViewById<RecyclerView>(R.id.rvPendingNannies)
        rvPendingNannies.layoutManager = LinearLayoutManager(this)
        adapter = AdminApproveNannyAdapter(pendingList)
        rvPendingNannies.adapter = adapter

        fetchPendingNannies()
    }

    private fun fetchPendingNannies() {
        // Query users where role is "nanny" AND accountStatus is "pending"
        db.collection("users")
            .whereEqualTo("role", "nanny")
            .whereEqualTo("accountStatus", "pending")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to load list.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                pendingList.clear()
                if (snapshots != null) {
                    for (doc in snapshots) {
                        // Ensure we capture the document ID as the userId if it isn't stored inside the document
                        val user = doc.toObject(UserModel::class.java).apply {
                            if (userId.isEmpty()) userId = doc.id
                        }
                        pendingList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }
}