package com.example.i230720_smd_assignment1

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream
import java.io.InputStream

class DmActivity : AppCompatActivity() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var adapter: MessagesAdapter
    private val messageList = mutableListOf<MessageItem>()

    private lateinit var etMessageInput: EditText
    private lateinit var btnSendMessage: ImageView
    private lateinit var btnGalleryMedia: ImageView
    private lateinit var btnCameraMedia: ImageView
    private lateinit var btnSharePost: ImageView

    private lateinit var layoutReplyPreview: LinearLayout
    private lateinit var tvReplyPreviewText: TextView
    private lateinit var btnCancelReply: TextView

    private lateinit var tvReceiverName: TextView
    private lateinit var tvAvatar: TextView

    private lateinit var auth: FirebaseAuth
    private var currentUid = ""
    private var receiverUid = "aisha_uid"
    private var receiverName = "Aisha Khan"
    private var chatId = ""

    private var replyToMessageId = ""
    private var replyToMessageText = ""

    // Image Picker Launcher
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { sendImageMessage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dm)

        auth = FirebaseAuth.getInstance()
        currentUid = auth.currentUser?.uid ?: "my_uid"

        receiverUid = intent.getStringExtra("receiverUid") ?: "aisha_uid"
        receiverName = intent.getStringExtra("receiverName") ?: "Aisha Khan"
        chatId = intent.getStringExtra("chatId") ?: getChatId(currentUid, receiverUid)

        tvReceiverName = findViewById(R.id.tvReceiverName)
        tvAvatar = findViewById(R.id.tvAvatar)
        tvReceiverName.text = receiverName
        val initials = if (receiverName.length >= 2) receiverName.substring(0, 2).uppercase() else "AK"
        tvAvatar.text = initials

        rvMessages = findViewById(R.id.rvMessages)
        etMessageInput = findViewById(R.id.etMessageInput)
        btnSendMessage = findViewById(R.id.btnSendMessage)
        btnGalleryMedia = findViewById(R.id.btnGalleryMedia)
        btnCameraMedia = findViewById(R.id.btnCameraMedia)
        btnSharePost = findViewById(R.id.btnSharePost)

        layoutReplyPreview = findViewById(R.id.layoutReplyPreview)
        tvReplyPreviewText = findViewById(R.id.tvReplyPreviewText)
        btnCancelReply = findViewById(R.id.btnCancelReply)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnVoiceCall)?.setOnClickListener {
            val intent = Intent(this, VoiceCallActivity::class.java)
            intent.putExtra("caller_name", receiverName)
            startActivity(intent)
        }

        findViewById<View>(R.id.btnVideoCall)?.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("caller_name", receiverName)
            startActivity(intent)
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rvMessages.layoutManager = layoutManager

        adapter = MessagesAdapter(
            messages = messageList,
            currentUserId = currentUid,
            onMessageClick = { message ->
                toggleHeartReaction(message)
            },
            onMessageLongClick = { message ->
                showMessageOptionsDialog(message)
            }
        )
        rvMessages.adapter = adapter

        btnSendMessage.setOnClickListener {
            sendTextMessage()
        }

        btnGalleryMedia.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnCameraMedia.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSharePost.setOnClickListener {
            sendPostMessage()
        }

        btnCancelReply.setOnClickListener {
            cancelReply()
        }

        loadMessages()
    }

    private fun getChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_${uid2}" else "${uid2}_${uid1}"
    }

    private fun loadMessages() {
        val dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("messages").child(chatId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (msgSnap in snapshot.children) {
                        val msgId = msgSnap.key ?: ""
                        val senderId = msgSnap.child("senderId").getValue(String::class.java) ?: ""
                        val senderName = msgSnap.child("senderName").getValue(String::class.java) ?: ""
                        val type = msgSnap.child("type").getValue(String::class.java) ?: "text"
                        val content = msgSnap.child("content").getValue(String::class.java) ?: ""
                        val imageUrl = msgSnap.child("imageUrl").getValue(String::class.java) ?: ""
                        val timestamp = msgSnap.child("timestamp").getValue(Long::class.java) ?: 0L
                        val replyToId = msgSnap.child("replyToId").getValue(String::class.java) ?: ""
                        val replyToText = msgSnap.child("replyToText").getValue(String::class.java) ?: ""
                        val isEdited = msgSnap.child("isEdited").getValue(Boolean::class.java) ?: false
                        val isDeleted = msgSnap.child("isDeleted").getValue(Boolean::class.java) ?: false

                        val reactionsMap = mutableMapOf<String, String>()
                        val reactionsSnap = msgSnap.child("reactions")
                        for (r in reactionsSnap.children) {
                            val rUid = r.key ?: ""
                            val rVal = r.getValue(String::class.java) ?: ""
                            reactionsMap[rUid] = rVal
                        }

                        val item = MessageItem(
                            messageId = msgId,
                            senderId = senderId,
                            senderName = senderName,
                            type = type,
                            content = content,
                            imageUrl = imageUrl,
                            timestamp = timestamp,
                            replyToId = replyToId,
                            replyToText = replyToText,
                            reactions = reactionsMap,
                            isEdited = isEdited,
                            isDeleted = isDeleted
                        )
                        messageList.add(item)
                    }

                    // Seed default sample messages if conversation is empty
                    if (messageList.isEmpty()) {
                        val now = System.currentTimeMillis()
                        val m1 = MessageItem(
                            messageId = "sample_1",
                            senderId = receiverUid,
                            senderName = receiverName,
                            type = "text",
                            content = "Hey! Are you still coming Saturday?",
                            timestamp = now - 100000
                        )
                        val m2 = MessageItem(
                            messageId = "sample_2",
                            senderId = currentUid,
                            senderName = "You",
                            type = "text",
                            content = "Yes, obviously",
                            timestamp = now - 60000
                        )
                        messageList.add(m1)
                        messageList.add(m2)
                    }

                    adapter.notifyDataSetChanged()
                    if (messageList.isNotEmpty()) {
                        rvMessages.smoothScrollToPosition(messageList.size - 1)
                    }

                    // Clear unread flag for this user
                    dbRef.child("chats").child(chatId).child("unreadBy").child(currentUid).setValue(false)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun sendTextMessage() {
        val text = etMessageInput.text.toString().trim()
        if (text.isEmpty()) return

        val dbRef = FirebaseDatabase.getInstance().reference
        val msgRef = dbRef.child("messages").child(chatId).push()
        val msgId = msgRef.key ?: ""
        val now = System.currentTimeMillis()

        val msgMap = mapOf(
            "messageId" to msgId,
            "senderId" to currentUid,
            "senderName" to "You",
            "type" to "text",
            "content" to text,
            "timestamp" to now,
            "replyToId" to replyToMessageId,
            "replyToText" to replyToMessageText,
            "isEdited" to false,
            "isDeleted" to false
        )

        msgRef.setValue(msgMap)

        // Update chat meta
        updateChatMeta(text, now)

        etMessageInput.setText("")
        cancelReply()
    }

    private fun sendImageMessage(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
            val bytes = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

            val dbRef = FirebaseDatabase.getInstance().reference
            val msgRef = dbRef.child("messages").child(chatId).push()
            val msgId = msgRef.key ?: ""
            val now = System.currentTimeMillis()

            val msgMap = mapOf(
                "messageId" to msgId,
                "senderId" to currentUid,
                "senderName" to "You",
                "type" to "image",
                "content" to "📷 Photo",
                "imageUrl" to base64Image,
                "timestamp" to now,
                "replyToId" to replyToMessageId,
                "replyToText" to replyToMessageText,
                "isEdited" to false,
                "isDeleted" to false
            )

            msgRef.setValue(msgMap)
            updateChatMeta("📷 Photo", now)
            cancelReply()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendPostMessage() {
        val dbRef = FirebaseDatabase.getInstance().reference
        val msgRef = dbRef.child("messages").child(chatId).push()
        val msgId = msgRef.key ?: ""
        val now = System.currentTimeMillis()

        val msgMap = mapOf(
            "messageId" to msgId,
            "senderId" to currentUid,
            "senderName" to "You",
            "type" to "post",
            "content" to "📌 Shared a post: Sunset at Clifton Beach",
            "timestamp" to now,
            "replyToId" to replyToMessageId,
            "replyToText" to replyToMessageText,
            "isEdited" to false,
            "isDeleted" to false
        )

        msgRef.setValue(msgMap)
        updateChatMeta("📌 Shared a post", now)
        cancelReply()
    }

    private fun updateChatMeta(lastMessage: String, timestamp: Long) {
        val dbRef = FirebaseDatabase.getInstance().reference
        val chatMeta = mapOf(
            "lastMessage" to lastMessage,
            "lastMessageTime" to timestamp,
            "lastMessageSenderId" to currentUid,
            "otherUserName" to receiverName,
            "otherUserId" to receiverUid,
            "participants" to mapOf(currentUid to true, receiverUid to true),
            "unreadBy" to mapOf(receiverUid to true)
        )
        dbRef.child("chats").child(chatId).updateChildren(chatMeta)
    }

    private fun toggleHeartReaction(message: MessageItem) {
        val dbRef = FirebaseDatabase.getInstance().reference.child("messages").child(chatId).child(message.messageId).child("reactions")
        val hasHeart = message.reactions[currentUid] == "❤️"

        if (hasHeart) {
            dbRef.child(currentUid).removeValue()
        } else {
            dbRef.child(currentUid).setValue("❤️")
        }
    }

    private fun showMessageOptionsDialog(message: MessageItem) {
        val isOwnMessage = message.senderId == currentUid
        val elapsedMs = System.currentTimeMillis() - message.timestamp
        val isWithin5Min = elapsedMs <= 5 * 60 * 1000 // 5 minutes = 300,000 ms

        val options = mutableListOf<String>()
        options.add("Reply")
        options.add("Heart Reaction ❤️")

        // Edit & Delete option ONLY appear if own message AND sent within 5 minutes
        if (isOwnMessage && isWithin5Min && !message.isDeleted) {
            options.add("Edit Message")
            options.add("Delete Message")
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Message Options")
        builder.setItems(options.toTypedArray()) { _, which ->
            when (options[which]) {
                "Reply" -> {
                    replyToMessageId = message.messageId
                    replyToMessageText = message.content
                    layoutReplyPreview.visibility = View.VISIBLE
                    tvReplyPreviewText.text = "Replying to: ${message.content}"
                    etMessageInput.requestFocus()
                }
                "Heart Reaction ❤️" -> {
                    toggleHeartReaction(message)
                }
                "Edit Message" -> {
                    showEditDialog(message)
                }
                "Delete Message" -> {
                    deleteMessage(message)
                }
            }
        }
        builder.show()
    }

    private fun showEditDialog(message: MessageItem) {
        val input = EditText(this)
        input.setText(message.content)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Message")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newText = input.text.toString().trim()
                if (newText.isNotEmpty()) {
                    val dbRef = FirebaseDatabase.getInstance().reference
                        .child("messages").child(chatId).child(message.messageId)

                    val updates = mapOf<String, Any>(
                        "content" to newText,
                        "isEdited" to true
                    )
                    dbRef.updateChildren(updates)
                    Toast.makeText(this, "Message edited", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun deleteMessage(message: MessageItem) {
        val dbRef = FirebaseDatabase.getInstance().reference
            .child("messages").child(chatId).child(message.messageId)

        val updates = mapOf<String, Any>(
            "content" to "This message was deleted",
            "isDeleted" to true
        )
        dbRef.updateChildren(updates)
        Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show()
    }

    private fun cancelReply() {
        replyToMessageId = ""
        replyToMessageText = ""
        layoutReplyPreview.visibility = View.GONE
    }
}
