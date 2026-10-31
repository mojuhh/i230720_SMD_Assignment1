package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        TabNavigation.setupTabs(this)

        findViewById<View>(R.id.btnSearch)?.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<View>(R.id.btnSeeProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<View>(R.id.btnLogout)?.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val uid = auth.currentUser?.uid

            if (uid != null) {
                // Mark user offline in Realtime Database before logging out
                FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child(uid)
                    .child("status")
                    .setValue("offline")
            }

            // Sign out from Firebase Auth
            auth.signOut()

            // Update local preferences
            val prefs = getSharedPreferences("KinnectPrefs", MODE_PRIVATE)
            prefs.edit().putBoolean("is_logged_in", false).apply()

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Clear back stack and redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
