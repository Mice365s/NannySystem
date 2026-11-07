package com.example.nannysystem

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class admin_manage_users : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AdminManageUserAdapter
    private val userList = mutableListOf<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_users)

        db = FirebaseFirestore.getInstance()

        findViewById<MaterialButton>(R.id.btnBackManage).setOnClickListener {
            finish()
        }

        val rvAllUsers = findViewById<RecyclerView>(R.id.rvAllUsers)
        rvAllUsers.layoutManager = LinearLayoutManager(this)
        adapter = AdminManageUserAdapter(userList)
        rvAllUsers.adapter = adapter

        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        // Fetch all users in real-time
        db.collection("users")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to load users.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                userList.clear()
                if (snapshots != null) {
                    for (doc in snapshots) {
                        val user = doc.toObject(UserModel::class.java).apply {
                            if (userId.isEmpty()) userId = doc.id
                        }
                        // Hide admins from the deletion list as a safety measure
                        if (user.role != "admin") {
                            userList.add(user)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }
}