package com.example.nannysystem

data class MessageModel(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)