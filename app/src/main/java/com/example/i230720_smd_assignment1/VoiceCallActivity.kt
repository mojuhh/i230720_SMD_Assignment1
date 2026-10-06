package com.example.i230720_smd_assignment1

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class VoiceCallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voicecall)

        findViewById<View>(R.id.btnEndCall)?.setOnClickListener {
            finish()
        }
    }
}
