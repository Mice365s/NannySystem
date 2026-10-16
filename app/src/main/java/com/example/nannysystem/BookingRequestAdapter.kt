package com.example.nannysystem

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class BookingRequestAdapter(private val bookingList: List<BookingModel>) : RecyclerView.Adapter<BookingRequestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvReqParentName)
        val tvDateTime: TextView = view.findViewById(R.id.tvReqDateTime)
        val tvStatus: TextView = view.findViewById(R.id.tvReqStatus)
        val btnAccept: Button = view.findViewById(R.id.btnAcceptReq)
        val btnDecline: Button = view.findViewById(R.id.btnDeclineReq)
        val btnComplete: Button = view.findViewById(R.id.btnComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookingList[position]

        holder.tvName.text = "Parent: ${booking.parentName}"
        holder.tvDateTime.text = "Date: ${booking.date} at ${booking.time}"
        holder.tvStatus.text = "Status: ${booking.status.uppercase()}"

        // Update UI based on new flow
        when (booking.status) {
            "accepted" -> {
                holder.btnAccept.visibility = View.GONE
                holder.btnDecline.visibility = View.GONE
                holder.btnComplete.visibility = View.VISIBLE // Show complete button
                holder.tvStatus.setTextColor(Color.parseColor("#3A8F46")) // Green
            }
            "completed" -> {
                holder.btnAccept.visibility = View.GONE
                holder.btnDecline.visibility = View.GONE
                holder.btnComplete.visibility = View.GONE
                holder.tvStatus.setTextColor(Color.parseColor("#0000FF")) // Blue
                holder.tvStatus.text = "Status: COMPLETED - AWAITING PAYMENT"
            }
            "paid" -> {
                holder.btnAccept.visibility = View.GONE
                holder.btnDecline.visibility = View.GONE
                holder.btnComplete.visibility = View.GONE
                holder.tvStatus.setTextColor(Color.parseColor("#3A8F46")) // Green
                holder.tvStatus.text = "Status: PAID IN FULL"
            }
            "declined", "rejected" -> {
                holder.btnAccept.visibility = View.GONE
                holder.btnDecline.visibility = View.GONE
                holder.btnComplete.visibility = View.GONE
                holder.tvStatus.setTextColor(Color.parseColor("#D32F2F")) // Red
            }
            else -> { // "pending"
                holder.btnAccept.visibility = View.VISIBLE
                holder.btnDecline.visibility = View.VISIBLE
                holder.btnComplete.visibility = View.GONE
                holder.tvStatus.setTextColor(Color.parseColor("#FFA500")) // Orange
            }
        }

        val db = FirebaseFirestore.getInstance()

        holder.btnAccept.setOnClickListener {
            db.collection("bookings").document(booking.id).update("status", "accepted")
        }

        holder.btnDecline.setOnClickListener {
            db.collection("bookings").document(booking.id).update("status", "rejected")
        }

        // Handle Complete Click
        holder.btnComplete.setOnClickListener {
            db.collection("bookings").document(booking.id).update("status", "completed")
        }
    }

    override fun getItemCount(): Int = bookingList.size
}