package com.example.nannysystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messages: List<MessageModel>,
    private val currentUserId: String
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardSent: View = view.findViewById(R.id.cardSent)
        val tvSentMessage: TextView = view.findViewById(R.id.tvSentMessage)
        val cardReceived: View = view.findViewById(R.id.cardReceived)
        val tvReceivedMessage: TextView = view.findViewById(R.id.tvReceivedMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = messages[position]

        if (msg.senderId == currentUserId) {
            // I sent this message
            holder.cardSent.visibility = View.VISIBLE
            holder.tvSentMessage.text = msg.message
            holder.cardReceived.visibility = View.GONE
        } else {
            // I received this message
            holder.cardReceived.visibility = View.VISIBLE
            holder.tvReceivedMessage.text = msg.message
            holder.cardSent.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messages.size
}