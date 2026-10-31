package com.example.i230720_smd_assignment1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig

class VoiceCallActivity : AppCompatActivity() {

    private var rtcEngine: RtcEngine? = null
    private var isMuted = false
    private var isSpeakerOn = false
    private var secondsElapsed = 0
    private val handler = Handler(Looper.getMainLooper())
    private var callTimerRunnable: Runnable? = null

    private lateinit var tvCallStatus: TextView
    private lateinit var tvCallerName: TextView
    private lateinit var btnMute: ImageView
    private lateinit var btnSpeaker: ImageView

    private val PERMISSION_REQ_ID = 22
    private val channelName = AgoraConfig.DEFAULT_CHANNEL

    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread {
                tvCallStatus.text = "Calling..."
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                tvCallStatus.text = "Connected"
                startCallTimer()
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                tvCallStatus.text = "Call Ended"
                Toast.makeText(this@VoiceCallActivity, "User disconnected", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voicecall)

        tvCallerName = findViewById(R.id.tvCallerName)
        tvCallStatus = findViewById(R.id.tvCallStatus)
        btnMute = findViewById(R.id.btnMute)
        btnSpeaker = findViewById(R.id.btnSpeaker)

        val nameExtra = intent.getStringExtra("caller_name") ?: "Aisha Khan"
        tvCallerName.text = nameExtra

        findViewById<View>(R.id.btnEndCall)?.setOnClickListener {
            endCall()
        }

        btnMute.setOnClickListener {
            isMuted = !isMuted
            rtcEngine?.muteLocalAudioStream(isMuted)
            btnMute.alpha = if (isMuted) 0.5f else 1.0f
            Toast.makeText(this, if (isMuted) "Microphone Muted" else "Microphone Unmuted", Toast.LENGTH_SHORT).show()
        }

        btnSpeaker.setOnClickListener {
            isSpeakerOn = !isSpeakerOn
            rtcEngine?.setEnableSpeakerphone(isSpeakerOn)
            btnSpeaker.alpha = if (isSpeakerOn) 1.0f else 0.5f
            Toast.makeText(this, if (isSpeakerOn) "Speakerphone On" else "Speakerphone Off", Toast.LENGTH_SHORT).show()
        }

        if (checkPermissions()) {
            initAgoraEngineAndJoin()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQ_ID
            )
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initAgoraEngineAndJoin()
            } else {
                Toast.makeText(this, "Audio permission required for Voice Call", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun initAgoraEngineAndJoin() {
        try {
            val config = RtcEngineConfig()
            config.mContext = applicationContext
            config.mAppId = AgoraConfig.AGORA_APP_ID
            config.mEventHandler = rtcEventHandler

            rtcEngine = RtcEngine.create(config)
            rtcEngine?.enableAudio()
            rtcEngine?.joinChannel(null, channelName, "", 0)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to start call: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun startCallTimer() {
        callTimerRunnable = object : Runnable {
            override fun run() {
                secondsElapsed++
                val mins = secondsElapsed / 60
                val secs = secondsElapsed % 60
                tvCallStatus.text = String.format(java.util.Locale.getDefault(), "%02d:%02d", mins, secs)
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(callTimerRunnable!!, 1000)
    }

    private fun endCall() {
        callTimerRunnable?.let { handler.removeCallbacks(it) }
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        callTimerRunnable?.let { handler.removeCallbacks(it) }
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
    }
}
