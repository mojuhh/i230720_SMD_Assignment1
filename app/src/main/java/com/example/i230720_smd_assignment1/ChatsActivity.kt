package com.example.i230720_smd_assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatsActivity : AppCompatActivity() {

    private lateinit var rvChats: RecyclerView
    private lateinit var adapter: ChatsAdapter
    private val chatList = mutableListOf<ChatItem>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chats)

        auth = FirebaseAuth.getInstance()
        val currentUid = auth.currentUser?.uid ?: ""

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        rvChats = findViewById(R.id.rvChats)
        rvChats.layoutManager = LinearLayoutManager(this)
        adapter = ChatsAdapter(chatList) { chat ->
            val intent = Intent(this, DmActivity::class.java)
            intent.putExtra("chatId", chat.chatId)
            intent.putExtra("receiverUid", chat.otherUserId)
            intent.putExtra("receiverName", chat.otherUserName)
            startActivity(intent)
        }
        rvChats.adapter = adapter

        if (currentUid.isNotEmpty()) {
            loadUserChats(currentUid)
        }
    }

    private fun loadUserChats(currentUid: String) {
        val dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("chats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()

                for (chatSnap in snapshot.children) {
                    val chatId = chatSnap.key ?: continue
                    if (chatId.contains(currentUid) || chatSnap.child("participants").hasChild(currentUid)) {
                        val lastMessage = chatSnap.child("lastMessage").getValue(String::class.java) ?: ""
                        val lastMessageTime = chatSnap.child("lastMessageTime").getValue(Long::class.java) ?: 0L
                        val lastSenderId = chatSnap.child("lastMessageSenderId").getValue(String::class.java) ?: ""
                        val unreadBy = chatSnap.child("unreadBy").child(currentUid).getValue(Boolean::class.java) ?: false

                        // Find other user name
                        var otherName = "Aisha Khan"
                        var otherUid = "demo_aisha"

                        if (chatSnap.hasChild("otherUserName")) {
                            otherName = chatSnap.child("otherUserName").getValue(String::class.java) ?: "Aisha Khan"
                            otherUid = chatSnap.child("otherUserId").getValue(String::class.java) ?: "demo_aisha"
                        }

                        val item = ChatItem(
                            chatId = chatId,
                            otherUserId = otherUid,
                            otherUserName = otherName,
                            lastMessage = lastMessage,
                            lastMessageTime = lastMessageTime,
                            lastMessageSenderId = lastSenderId,
                            isUnread = unreadBy
                        )
                        chatList.add(item)
                    }
                }

                // If no chats exist in Firebase yet, populate default demo chats for smooth testing
                if (chatList.isEmpty()) {
                    chatList.add(
                        ChatItem(
                            chatId = "${currentUid}_aisha",
                            otherUserId = "aisha_uid",
                            otherUserName = "Aisha Khan",
                            lastMessage = "Sending the photos tonight",
                            lastMessageTime = System.currentTimeMillis() - (2 * 60 * 1000),
                            lastMessageSenderId = "aisha_uid",
                            isUnread = true
                        )
                    )
                    chatList.add(
                        ChatItem(
                            chatId = "${currentUid}_lina",
                            otherUserId = "lina_uid",
                            otherUserName = "Lina Marsh",
                            lastMessage = "You: See you at the pier",
                            lastMessageTime = System.currentTimeMillis() - (60 * 60 * 1000),
                            lastMessageSenderId = currentUid,
                            isUnread = false
                        )
                    )
                    chatList.add(
                        ChatItem(
                            chatId = "${currentUid}_omar",
                            otherUserId = "omar_uid",
                            otherUserName = "Omar Farooq",
                            lastMessage = "Check out this sunset shot!",
                            lastMessageTime = System.currentTimeMillis() - (3 * 60 * 60 * 1000),
                            lastMessageSenderId = "omar_uid",
                            isUnread = false
                        )
                    )
                }

                chatList.sortByDescending { it.lastMessageTime }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
