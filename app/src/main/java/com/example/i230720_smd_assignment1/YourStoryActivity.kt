package com.example.i230720_smd_assignment1

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class YourStoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.your_story)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }
    }
}
