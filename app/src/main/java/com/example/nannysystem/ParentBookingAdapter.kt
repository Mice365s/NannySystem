package com.example.nannysystem

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParentBookingAdapter(private val bookingList: List<BookingModel>) : RecyclerView.Adapter<ParentBookingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNannyName: TextView = view.findViewById(R.id.tvParentBookingNannyName)
        val tvDateTime: TextView = view.findViewById(R.id.tvParentBookingDateTime)
        val tvStatus: TextView = view.findViewById(R.id.tvParentBookingStatus)
        val btnPayNow: Button = view.findViewById(R.id.btnPayNow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parent_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookingList[position]

        holder.tvNannyName.text = "Nanny: ${booking.nannyName}"
        holder.tvDateTime.text = "Date: ${booking.date} | Time: ${booking.time}"
        holder.tvStatus.text = "Status: ${booking.status.uppercase()}"

        // Update colors and show/hide the Pay button based on the status
        when (booking.status) {
            "accepted" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#3A8F46")) // Green
                holder.btnPayNow.visibility = View.VISIBLE
            }
            "pending" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#FFA500")) // Orange
                holder.btnPayNow.visibility = View.GONE
            }
            "paid" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#0000FF")) // Blue
                holder.tvStatus.text = "Status: PAID - BOOKING CONFIRMED"
                holder.btnPayNow.visibility = View.GONE
            }
            else -> { // declined / rejected
                holder.tvStatus.setTextColor(Color.parseColor("#D32F2F")) // Red
                holder.btnPayNow.visibility = View.GONE
            }
        }

        // Handle Pay Now Click
        holder.btnPayNow.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, payment_dashboard::class.java)
            intent.putExtra("bookingId", booking.id)
            intent.putExtra("nannyName", booking.nannyName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = bookingList.size
}