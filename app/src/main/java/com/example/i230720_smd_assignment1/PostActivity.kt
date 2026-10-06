package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class PostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnPhotoVideo)?.setOnClickListener {
            startActivity(Intent(this, PickPhotoActivity::class.java))
        }

        findViewById<View>(R.id.btnCamera)?.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }
}
