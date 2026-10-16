package com.example.nannysystem

data class BookingModel(
    val id: String = "",
    val parentId: String = "",
    val nannyId: String = "",
    val parentName: String = "",
    val nannyName: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "pending" // Can be: pending, accepted, declined, paid
)