package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class FeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed)

        TabNavigation.setupTabs(this)

        findViewById<View>(R.id.btnSearch)?.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<View>(R.id.btnChats)?.setOnClickListener {
            startActivity(Intent(this, ChatsActivity::class.java))
        }

        findViewById<View>(R.id.btnCreatePost)?.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }

        findViewById<View>(R.id.btnCreateStory)?.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        findViewById<View>(R.id.btnViewStory)?.setOnClickListener {
            startActivity(Intent(this, ViewStoryActivity::class.java))
        }

        findViewById<View>(R.id.btnYourProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<View>(R.id.btnComments)?.setOnClickListener {
            startActivity(Intent(this, CommentsActivity::class.java))
        }

        findViewById<View>(R.id.btnReactions)?.setOnClickListener {
            startActivity(Intent(this, ReactionPickerActivity::class.java))
        }
    }
}
