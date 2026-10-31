package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class DmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dm)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnVoiceCall)?.setOnClickListener {
            val intent = Intent(this, VoiceCallActivity::class.java)
            intent.putExtra("caller_name", "Aisha Khan")
            startActivity(intent)
        }

        findViewById<View>(R.id.btnVideoCall)?.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("caller_name", "Aisha Khan")
            startActivity(intent)
        }
    }
}
