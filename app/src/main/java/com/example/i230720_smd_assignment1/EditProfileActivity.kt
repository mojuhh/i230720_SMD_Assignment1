package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etBio: EditText
    private lateinit var tvEmail: TextView
    private lateinit var etPhone: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        auth = FirebaseAuth.getInstance()

        etName = findViewById(R.id.etName)
        etUsername = findViewById(R.id.etUsername)
        etBio = findViewById(R.id.etBio)
        tvEmail = findViewById(R.id.tvEmail)
        etPhone = findViewById(R.id.etPhone)

        val currentUser = auth.currentUser
        val uid = currentUser?.uid ?: ""
        val email = currentUser?.email ?: ""
        tvEmail.text = email

        if (uid.isNotEmpty()) {
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").getValue(String::class.java) ?: ""
                        val username = snapshot.child("username").getValue(String::class.java) ?: ""
                        val bio = snapshot.child("bio").getValue(String::class.java) ?: ""
                        val phone = snapshot.child("phone").getValue(String::class.java) ?: ""

                        if (name.isNotEmpty()) etName.setText(name)
                        if (username.isNotEmpty()) etUsername.setText(username)
                        if (bio.isNotEmpty()) etBio.setText(bio)
                        if (phone.isNotEmpty()) etPhone.setText(phone)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnSave)?.setOnClickListener {
            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim().lowercase()
            val bio = etBio.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (bio.isEmpty()) {
                Toast.makeText(this, "Bio is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (uid.isEmpty()) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dbRef = FirebaseDatabase.getInstance().reference

            // Check username uniqueness in Realtime Database
            dbRef.child("usernames").child(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val existingUid = snapshot.getValue(String::class.java)
                        if (existingUid != null && existingUid != uid) {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Username '$username' is already taken. Please choose another.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            // Unique username confirmed, save to Realtime Database
                            val updates = mapOf<String, Any>(
                                "name" to name,
                                "username" to username,
                                "bio" to bio,
                                "phone" to phone,
                                "isFirstTime" to false
                            )

                            dbRef.child("users").child(uid).updateChildren(updates)
                            dbRef.child("usernames").child(username).setValue(uid)

                            val prefs = getSharedPreferences("KinnectPrefs", MODE_PRIVATE)
                            prefs.edit()
                                .putBoolean("is_first_login", false)
                                .putString("username", username)
                                .apply()

                            Toast.makeText(this@EditProfileActivity, "Profile saved successfully", Toast.LENGTH_SHORT).show()

                            // Navigate to Home/Feed
                            val intent = Intent(this@EditProfileActivity, FeedActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@EditProfileActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
