package com.example.nannysystem

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class nanny_profile : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private var nannyId: String? = null // Changed to String for Firebase ID

    private lateinit var ivProfilePicture: ImageView
    private var imageUri: Uri? = null

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // Photo Picker setup
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            ivProfilePicture.setImageURI(uri) // Show image on screen
            imageUri = uri // Save URI to upload later
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nanny_profile)

        // Initialize Session and Firebase
        sessionManager = SessionManager(this)
        nannyId = sessionManager.getUserDetails()[SessionManager.KEY_USER_ID] // Fetching the Firebase String ID

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Bind Views
        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        val etLocation = findViewById<EditText>(R.id.etProfileLocation)
        val etRate = findViewById<EditText>(R.id.etProfileRate)
        val etBio = findViewById<EditText>(R.id.etProfileBio)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)
        val tvCancel = findViewById<TextView>(R.id.tvCancelProfile)

        // 1. Load existing data from Firestore
        if (nannyId != null) {
            db.collection("users").document(nannyId!!).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        etLocation.setText(document.getString("location") ?: "")
                        etBio.setText(document.getString("bio") ?: "")
                        etRate.setText(document.getString("hourlyRate") ?: "")

                        val imageUrl = document.getString("profileImageUrl")
                        if (!imageUrl.isNullOrEmpty()) {
                            com.bumptech.glide.Glide.with(this@nanny_profile)
                                .load(imageUrl)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .into(ivProfilePicture)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show()
                }
        }

        // 2. Launch Photo Picker when image is tapped
        ivProfilePicture.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // 3. Handle Cancel
        tvCancel.setOnClickListener {
            finish()
        }

        // 4. Handle Save
        btnSave.setOnClickListener {
            val loc = etLocation.text.toString().trim()
            val rateStr = etRate.text.toString().trim()
            val bio = etBio.text.toString().trim()

            if (loc.isEmpty() || rateStr.isEmpty() || bio.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nannyId == null) {
                Toast.makeText(this, "Error: User session invalid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled = false // Prevent double-clicking

            if (imageUri != null) {
                uploadImageAndSave(nannyId!!, loc, rateStr, bio)
            } else {
                saveDataToFirestore(nannyId!!, loc, rateStr, bio, null)
            }
        }
    }

    private fun uploadImageAndSave(userId: String, location: String, rate: String, bio: String) {
        val storageRef = storage.reference.child("profile_pictures/$userId.jpg")

        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()

        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveDataToFirestore(userId, location, rate, bio, downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                findViewById<Button>(R.id.btnSaveProfile).isEnabled = true
            }
    }

    private fun saveDataToFirestore(userId: String, location: String, rate: String, bio: String, imageUrl: String?) {
        val updates = hashMapOf<String, Any>(
            "location" to location,
            "hourlyRate" to rate,
            "bio" to bio
        )

        if (imageUrl != null) {
            updates["profileImageUrl"] = imageUrl
        }

        db.collection("users").document(userId).update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_LONG).show()
                findViewById<Button>(R.id.btnSaveProfile).isEnabled = true
            }
    }
}