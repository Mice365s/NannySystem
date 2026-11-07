package com.example.nannysystem

data class UserModel(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var role: String = "",
    var accountStatus: String = ""
)