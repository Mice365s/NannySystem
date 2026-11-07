package com.example.nannysystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class AdminApproveNannyAdapter(private val nannyList: List<UserModel>) :
    RecyclerView.Adapter<AdminApproveNannyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvPendingNannyName)
        val tvEmail: TextView = view.findViewById(R.id.tvPendingNannyEmail)
        val btnApprove: MaterialButton = view.findViewById(R.id.btnApproveNanny)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_pending_nanny, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nanny = nannyList[position]

        holder.tvName.text = nanny.name
        holder.tvEmail.text = nanny.email

        holder.btnApprove.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            holder.btnApprove.isEnabled = false // Prevent double-clicks

            db.collection("users").document(nanny.userId)
                .update("accountStatus", "approved")
                .addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "${nanny.name} Approved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(holder.itemView.context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    holder.btnApprove.isEnabled = true
                }
        }
    }

    override fun getItemCount(): Int = nannyList.size
}