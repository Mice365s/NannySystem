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
        val btnChat: Button = view.findViewById(R.id.btnChat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parent_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookingList[position]

        holder.tvNannyName.text = "Nanny: ${booking.nannyName}"
        holder.tvDateTime.text = "Date: ${booking.date} | Time: ${booking.time}"

        // Update colors and show/hide the Pay button based on the status
        when (booking.status) {
            "completed" -> {
                holder.tvStatus.text = "STATUS: AWAITING PAYMENT"
                holder.tvStatus.setTextColor(Color.parseColor("#FFA500")) // Orange
                holder.btnPayNow.visibility = View.VISIBLE // Reveal the Pay button here!
            }
            "paid" -> {
                holder.tvStatus.text = "STATUS: PAID - BOOKING CONFIRMED"
                holder.tvStatus.setTextColor(Color.parseColor("#0000FF")) // Blue
                holder.btnPayNow.visibility = View.GONE
            }
            "accepted" -> {
                holder.tvStatus.text = "STATUS: ACCEPTED"
                holder.tvStatus.setTextColor(Color.parseColor("#3A8F46")) // Green
                holder.btnPayNow.visibility = View.GONE // Hide until completed
            }
            "pending" -> {
                holder.tvStatus.text = "STATUS: PENDING"
                holder.tvStatus.setTextColor(Color.parseColor("#FFA500")) // Orange
                holder.btnPayNow.visibility = View.GONE
            }
            else -> { // declined / rejected
                holder.tvStatus.text = "STATUS: ${booking.status.uppercase()}"
                holder.tvStatus.setTextColor(Color.parseColor("#D32F2F")) // Red
                holder.btnPayNow.visibility = View.GONE
            }



        }

        // Show the chat button ONLY when the job is accepted or completed
        when (booking.status) {
            "accepted", "completed" -> holder.btnChat.visibility = View.VISIBLE
            else -> holder.btnChat.visibility = View.GONE
        }

        // Add the click listener
        holder.btnChat.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, chat_screen::class.java)

            intent.putExtra("BOOKING_ID", booking.id)
            intent.putExtra("OTHER_PERSON_NAME", booking.nannyName) // So the parent sees the Nanny's name at the top

            context.startActivity(intent)
        }

        // Handle Pay Now Click
        holder.btnPayNow.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, payment_dashboard::class.java)

            // Ensure these keys exactly match what your payment_dashboard.kt expects!
            intent.putExtra("BOOKING_ID", booking.id)
            intent.putExtra("NANNY_NAME", booking.nannyName)

            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = bookingList.size
}