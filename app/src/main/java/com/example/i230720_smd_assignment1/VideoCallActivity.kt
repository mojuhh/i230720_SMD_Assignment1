package com.example.i230720_smd_assignment1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class VideoCallActivity : AppCompatActivity() {

    private var rtcEngine: RtcEngine? = null
    private var isMuted = false
    private var isVideoMuted = false

    private lateinit var localVideoContainer: FrameLayout
    private lateinit var remoteVideoContainer: FrameLayout
    private lateinit var tvCallerName: TextView
    private lateinit var tvCallStatus: TextView
    private lateinit var btnMute: ImageView
    private lateinit var btnMuteVideo: ImageView
    private lateinit var btnSwitchCamera: ImageView

    private val PERMISSION_REQ_ID = 23
    private val channelName = AgoraConfig.DEFAULT_CHANNEL

    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread {
                tvCallStatus.text = "Calling..."
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                setupRemoteVideo(uid)
                tvCallStatus.text = "Connected"
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                remoteVideoContainer.removeAllViews()
                tvCallStatus.text = "User Disconnected"
                Toast.makeText(this@VideoCallActivity, "User left call", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.videocall)

        localVideoContainer = findViewById(R.id.localVideoContainer)
        remoteVideoContainer = findViewById(R.id.remoteVideoContainer)
        tvCallerName = findViewById(R.id.tvCallerName)
        tvCallStatus = findViewById(R.id.tvCallStatus)
        btnMute = findViewById(R.id.btnMute)
        btnMuteVideo = findViewById(R.id.btnMuteVideo)
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera)

        val callerName = intent.getStringExtra("caller_name") ?: "Aisha Khan"
        tvCallerName.text = callerName

        findViewById<View>(R.id.btnEndCall)?.setOnClickListener {
            endCall()
        }

        btnMute.setOnClickListener {
            isMuted = !isMuted
            rtcEngine?.muteLocalAudioStream(isMuted)
            btnMute.alpha = if (isMuted) 0.5f else 1.0f
        }

        btnMuteVideo.setOnClickListener {
            isVideoMuted = !isVideoMuted
            rtcEngine?.muteLocalVideoStream(isVideoMuted)
            localVideoContainer.visibility = if (isVideoMuted) View.GONE else View.VISIBLE
            btnMuteVideo.alpha = if (isVideoMuted) 0.5f else 1.0f
        }

        btnSwitchCamera.setOnClickListener {
            rtcEngine?.switchCamera()
        }

        if (checkPermissions()) {
            initAgoraEngineAndJoin()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA),
                PERMISSION_REQ_ID
            )
        }
    }

    private fun checkPermissions(): Boolean {
        val audioGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val cameraGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        return audioGranted && cameraGranted
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initAgoraEngineAndJoin()
            } else {
                Toast.makeText(this, "Camera and Audio permissions required for Video Call", Toast.LENGTH_LONG).show()
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
            rtcEngine?.enableVideo()
            rtcEngine?.startPreview()

            setupLocalVideo()

            rtcEngine?.joinChannel(null, channelName, "", 0)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to start video call: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupLocalVideo() {
        val surfaceView = SurfaceView(this)
        surfaceView.setZOrderMediaOverlay(true)
        localVideoContainer.addView(surfaceView)
        rtcEngine?.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteVideoContainer.removeAllViews()
        val surfaceView = SurfaceView(this)
        remoteVideoContainer.addView(surfaceView)
        rtcEngine?.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
    }

    private fun endCall() {
        rtcEngine?.stopPreview()
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        rtcEngine?.stopPreview()
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
    }
}
