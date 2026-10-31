package com.example.i230720_smd_assignment1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvPasswordStrength = findViewById<TextView>(R.id.tvPasswordStrength)

        etPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pass = s?.toString() ?: ""
                when {
                    pass.isEmpty() -> {
                        tvPasswordStrength?.text = "Password Strength: Weak"
                        tvPasswordStrength?.setTextColor(Color.parseColor("#E53935"))
                    }
                    pass.length < 6 -> {
                        tvPasswordStrength?.text = "Password Strength: Weak"
                        tvPasswordStrength?.setTextColor(Color.parseColor("#E53935"))
                    }
                    pass.length < 10 || !pass.any { it.isDigit() } -> {
                        tvPasswordStrength?.text = "Password Strength: Medium"
                        tvPasswordStrength?.setTextColor(Color.parseColor("#FB8C00"))
                    }
                    else -> {
                        tvPasswordStrength?.text = "Password Strength: Strong"
                        tvPasswordStrength?.setTextColor(Color.parseColor("#43A047"))
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnCreateAccountSubmit)?.setOnClickListener {
            val email = etEmail?.text?.toString()?.trim() ?: ""
            val pass = etPassword?.text?.toString()?.trim() ?: ""

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""
                        val dbRef = FirebaseDatabase.getInstance().reference

                        val userMap = mapOf(
                            "uid" to uid,
                            "email" to email,
                            "status" to "online",
                            "isFirstTime" to true,
                            "username" to "",
                            "bio" to ""
                        )

                        dbRef.child("users").child(uid).setValue(userMap)

                        val prefs = getSharedPreferences("KinnectPrefs", MODE_PRIVATE)
                        prefs.edit()
                            .putBoolean("is_logged_in", true)
                            .putString("last_email", email)
                            .putBoolean("is_first_login", true)
                            .apply()

                        // Direct first-time user to Profile Setup (EditProfileActivity, screen 16)
                        val intent = Intent(this, EditProfileActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Sign up failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        findViewById<View>(R.id.tvLogin)?.setOnClickListener {
            finish()
        }
    }
}
