package com.example.i230720_smd_assignment1

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class PickPhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pick_photo)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnNext)?.setOnClickListener {
            finish()
        }
    }
}
