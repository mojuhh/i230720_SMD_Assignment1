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

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val recentCard = findViewById<View>(R.id.recentLoginCard)
        val tvRecentName = findViewById<TextView>(R.id.tvRecentName)
        val tvRecentAvatar = findViewById<TextView>(R.id.tvRecentAvatar)

        val prefs = getSharedPreferences("KinnectPrefs", MODE_PRIVATE)
        val lastEmail = prefs.getString("last_email", "") ?: ""

        if (lastEmail.isNotEmpty()) {
            recentCard?.visibility = View.VISIBLE
            tvRecentName?.text = lastEmail
            val avatarText = if (lastEmail.length >= 2) lastEmail.substring(0, 2).uppercase() else "U"
            tvRecentAvatar?.text = avatarText
        } else {
            recentCard?.visibility = View.GONE
        }

        recentCard?.setOnClickListener {
            if (lastEmail.isNotEmpty()) {
                etEmail?.setText(lastEmail)
                etPassword?.requestFocus()
                Toast.makeText(this, "Prefilled $lastEmail", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<View>(R.id.btnLogin)?.setOnClickListener {
            val email = etEmail?.text?.toString()?.trim() ?: ""
            val pass = etPassword?.text?.toString()?.trim() ?: ""

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""
                        val dbRef = FirebaseDatabase.getInstance().reference

                        // Mark user online in Realtime Database
                        if (uid.isNotEmpty()) {
                            dbRef.child("users").child(uid).child("status").setValue("online")
                        }

                        prefs.edit()
                            .putBoolean("is_logged_in", true)
                            .putString("last_email", email)
                            .apply()

                        // Check if user has setup profile (username/bio)
                        if (uid.isNotEmpty()) {
                            dbRef.child("users").child(uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val isFirstTime = snapshot.child("isFirstTime").getValue(Boolean::class.java) ?: false
                                        val username = snapshot.child("username").getValue(String::class.java) ?: ""

                                        if (isFirstTime || username.isEmpty()) {
                                            startActivity(Intent(this@LoginActivity, EditProfileActivity::class.java))
                                        } else {
                                            startActivity(Intent(this@LoginActivity, FeedActivity::class.java))
                                        }
                                        finish()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        startActivity(Intent(this@LoginActivity, FeedActivity::class.java))
                                        finish()
                                    }
                                })
                        } else {
                            startActivity(Intent(this, FeedActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        findViewById<View>(R.id.btnCreateAccount)?.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }
}
