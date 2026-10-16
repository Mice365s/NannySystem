package com.example.nannysystem

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("NannySystemSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        const val KEY_USER_ID = "userId"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
    }


    fun createLoginSession(id: String, name: String, email: String, role: String) {
        editor.putBoolean("isLoggedIn", true)
        editor.putString(KEY_USER_ID, id)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("isLoggedIn", false)

    fun getUserRole(): String = prefs.getString(KEY_ROLE, "parent") ?: "parent"

    fun getUserDetails(): HashMap<String, String> {
        return HashMap<String, String>().apply {
            // Changed getInt to getString
            put(KEY_USER_ID, prefs.getString(KEY_USER_ID, "") ?: "")
            put(KEY_NAME, prefs.getString(KEY_NAME, "") ?: "")
            put(KEY_EMAIL, prefs.getString(KEY_EMAIL, "") ?: "")
            put(KEY_ROLE, prefs.getString(KEY_ROLE, "") ?: "")
        }
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()
    }
}