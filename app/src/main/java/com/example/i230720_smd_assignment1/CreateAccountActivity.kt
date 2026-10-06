package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnCreateAccountSubmit)?.setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.tvLogin)?.setOnClickListener {
            finish()
        }
    }
}
