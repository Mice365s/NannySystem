package com.example.nannysystem

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NannyAdapter(private val nannyList: List<Nanny>) : RecyclerView.Adapter<NannyAdapter.NannyViewHolder>() {

    class NannyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPic: ImageView = view.findViewById(R.id.ivNannyPic)
        val tvName: TextView = view.findViewById(R.id.tvNannyName)
        val tvLocation: TextView = view.findViewById(R.id.tvNannyLocation)
        val tvRating: TextView = view.findViewById(R.id.tvNannyRating) // Added Rating View
        val tvRate: TextView = view.findViewById(R.id.tvNannyRate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NannyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.nanny_card, parent, false)
        return NannyViewHolder(view)
    }

    override fun onBindViewHolder(holder: NannyViewHolder, position: Int) {
        val nanny = nannyList[position]
        holder.tvName.text = nanny.name
        holder.tvLocation.text = nanny.location
        holder.tvRate.text = "R${nanny.hourlyRate} / hr"

        // Display the rating (Defaults to "New" if no rating exists yet)
        if (nanny.rating > 0.0) {
            holder.tvRating.text = "⭐ ${nanny.rating}"
        } else {
            holder.tvRating.text = "⭐ New"
        }

        // Load image from Firestore URL using Glide
        if (nanny.profileImageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(nanny.profileImageUrl)
                .circleCrop()
                .into(holder.ivPic)
        }

        // Make the card clickable to go to the Booking screen
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, booking::class.java)
            intent.putExtra("nannyId", nanny.id)
            intent.putExtra("nannyName", nanny.name)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = nannyList.size
}