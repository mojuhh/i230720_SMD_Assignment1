package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        findViewById<View>(R.id.btnLogin)?.setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.btnCreateAccount)?.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }
}
