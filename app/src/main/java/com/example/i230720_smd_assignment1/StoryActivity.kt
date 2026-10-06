package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class StoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnYourStory)?.setOnClickListener {
            startActivity(Intent(this, YourStoryActivity::class.java))
        }

        findViewById<View>(R.id.btnShareYourStory)?.setOnClickListener {
            startActivity(Intent(this, YourStoryActivity::class.java))
        }
    }
}
