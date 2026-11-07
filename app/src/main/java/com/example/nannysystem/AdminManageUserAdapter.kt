package com.example.nannysystem

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class AdminManageUserAdapter(private val userList: List<UserModel>) :
    RecyclerView.Adapter<AdminManageUserAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvManageUserName)
        val tvEmail: TextView = view.findViewById(R.id.tvManageUserEmail)
        val tvRole: TextView = view.findViewById(R.id.tvManageUserRole)
        val btnDelete: MaterialButton = view.findViewById(R.id.btnDeleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_manage_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        val context = holder.itemView.context

        holder.tvName.text = user.name
        holder.tvEmail.text = user.email
        holder.tvRole.text = "Role: ${user.role}"

        // Color code the roles for easy reading
        if (user.role == "nanny") {
            holder.tvRole.setTextColor(Color.parseColor("#3A8F46")) // Green
        } else {
            holder.tvRole.setTextColor(Color.parseColor("#A06A3B")) // Brown
        }

        // Handle Delete Button with Confirmation Dialog
        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to permanently delete ${user.name}? This cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(user.userId).delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "${user.name} deleted.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int = userList.size
}