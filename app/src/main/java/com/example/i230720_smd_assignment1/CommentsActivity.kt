package com.example.i230720_smd_assignment1

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CommentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        Toast.makeText(this, "Comments support replies, comment likes, and image comments", Toast.LENGTH_SHORT).show()
    }
}
