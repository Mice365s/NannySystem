package com.example.nannysystem

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminReportAdapter(private val reportList: List<BookingModel>) : RecyclerView.Adapter<AdminReportAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNames: TextView = view.findViewById(R.id.tvReportNames)
        val tvDateTime: TextView = view.findViewById(R.id.tvReportDateTime)
        val tvStatus: TextView = view.findViewById(R.id.tvReportStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = reportList[position]

        holder.tvNames.text = "Parent: ${booking.parentName} | Nanny: ${booking.nannyName}"
        holder.tvDateTime.text = "Date: ${booking.date} at ${booking.time}"
        holder.tvStatus.text = "Status: ${booking.status.uppercase()}"

        when (booking.status) {
            "paid" -> holder.tvStatus.setTextColor(Color.parseColor("#3A8F46")) // Green
            "declined", "rejected" -> holder.tvStatus.setTextColor(Color.parseColor("#D32F2F")) // Red
            "completed" -> holder.tvStatus.setTextColor(Color.parseColor("#0000FF")) // Blue
            else -> holder.tvStatus.setTextColor(Color.parseColor("#FFA500")) // Orange
        }
    }

    override fun getItemCount(): Int = reportList.size
}