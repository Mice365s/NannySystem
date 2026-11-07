package com.example.nannysystem

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminReportAdapter(private val reportList: List<BookingModel>) :
    RecyclerView.Adapter<AdminReportAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvReportNames: TextView = view.findViewById(R.id.tvReportNames)
        val tvReportDateTime: TextView = view.findViewById(R.id.tvReportDateTime)
        val tvReportStatus: TextView = view.findViewById(R.id.tvReportStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = reportList[position]

        // Combine both names into the single TextView provided in your XML
        holder.tvReportNames.text = "Parent: ${booking.parentName}\nNanny: ${booking.nannyName}"

        holder.tvReportDateTime.text = "Date: ${booking.date} | Time: ${booking.time}"
        holder.tvReportStatus.text = booking.status.uppercase()

        // Apply color coding based on the status
        when (booking.status.lowercase()) {
            "accepted" -> holder.tvReportStatus.setTextColor(Color.parseColor("#3A8F46")) // Green
            "completed" -> holder.tvReportStatus.setTextColor(Color.parseColor("#FFA500")) // Orange
            "paid" -> holder.tvReportStatus.setTextColor(Color.parseColor("#0000FF")) // Blue
            "pending" -> holder.tvReportStatus.setTextColor(Color.parseColor("#757575")) // Grey
            else -> holder.tvReportStatus.setTextColor(Color.parseColor("#D32F2F")) // Red (Declined)
        }
    }

    override fun getItemCount(): Int = reportList.size
}