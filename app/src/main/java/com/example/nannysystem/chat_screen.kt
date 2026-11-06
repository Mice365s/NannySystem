package com.example.nannysystem

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class chat_screen : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ChatAdapter
    private val messageList = mutableListOf<MessageModel>()
    private var currentUserId: String = ""
    private var bookingId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        db = FirebaseFirestore.getInstance()
        val sessionManager = SessionManager(this)
        currentUserId = sessionManager.getUserDetails()[SessionManager.KEY_USER_ID] ?: ""

        bookingId = intent.getStringExtra("BOOKING_ID") ?: ""
        val otherPersonName = intent.getStringExtra("OTHER_PERSON_NAME") ?: "Chat"

        findViewById<TextView>(R.id.tvChatTitle).text = otherPersonName

        val rvChatMessages = findViewById<RecyclerView>(R.id.rvChatMessages)
        val etMessageInput = findViewById<EditText>(R.id.etMessageInput)
        val btnSendMessage = findViewById<MaterialButton>(R.id.btnSendMessage)
        val btnBackChat = findViewById<MaterialButton>(R.id.btnBackChat)

        // Setup RecyclerView
        adapter = ChatAdapter(messageList, currentUserId)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true // Start at the bottom of the chat
        rvChatMessages.layoutManager = layoutManager
        rvChatMessages.adapter = adapter

        if (bookingId.isNotEmpty()) {
            listenForMessages(rvChatMessages)
        }

        btnSendMessage.setOnClickListener {
            val text = etMessageInput.text.toString().trim()
            if (text.isNotEmpty() && bookingId.isNotEmpty()) {
                sendMessage(text)
                etMessageInput.text.clear()
            }
        }

        btnBackChat.setOnClickListener { finish() }
    }

    private fun listenForMessages(recyclerView: RecyclerView) {
        db.collection("chats").document(bookingId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                messageList.clear()
                if (snapshots != null) {
                    for (doc in snapshots) {
                        messageList.add(doc.toObject(MessageModel::class.java))
                    }
                }
                adapter.notifyDataSetChanged()

                // Scroll to the newest message
                if (messageList.isNotEmpty()) {
                    recyclerView.smoothScrollToPosition(messageList.size - 1)
                }
            }
    }

    private fun sendMessage(text: String) {
        val newMsg = MessageModel(
            senderId = currentUserId,
            message = text,
            timestamp = System.currentTimeMillis()
        )

        db.collection("chats").document(bookingId).collection("messages")
            .add(newMsg)
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show()
            }
    }
}