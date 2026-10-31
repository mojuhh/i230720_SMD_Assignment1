package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        val shareAction = View.OnClickListener {
            val prefs = getSharedPreferences("KinnectPrefs", MODE_PRIVATE)
            prefs.edit()
                .putLong("story_timestamp", System.currentTimeMillis())
                .putBoolean("has_active_story", true)
                .apply()

            Toast.makeText(this, "Story shared successfully! (Valid for 24h)", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, YourStoryActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.btnYourStory)?.setOnClickListener(shareAction)
        findViewById<View>(R.id.btnShareYourStory)?.setOnClickListener(shareAction)
    }
}
