package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                val uid = currentUser.uid
                val dbRef = FirebaseDatabase.getInstance().reference

                // Mark user online
                dbRef.child("users").child(uid).child("status").setValue("online")

                // Check profile completion status
                dbRef.child("users").child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val isFirstTime = snapshot.child("isFirstTime").getValue(Boolean::class.java) ?: false
                            val username = snapshot.child("username").getValue(String::class.java) ?: ""

                            if (isFirstTime || username.isEmpty()) {
                                startActivity(Intent(this@SplashActivity, EditProfileActivity::class.java))
                            } else {
                                startActivity(Intent(this@SplashActivity, FeedActivity::class.java))
                            }
                            finish()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            startActivity(Intent(this@SplashActivity, FeedActivity::class.java))
                            finish()
                        }
                    })
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }, 5000) // 5 seconds as specified
    }
}
