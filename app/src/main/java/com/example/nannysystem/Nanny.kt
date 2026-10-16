package com.example.nannysystem

data class Nanny(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val hourlyRate: String = "",
    val bio: String = "",
    val profileImageUrl: String = "",
    val rating: Double = 0.0
)
