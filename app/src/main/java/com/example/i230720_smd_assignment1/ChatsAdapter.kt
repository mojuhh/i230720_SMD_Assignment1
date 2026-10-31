package com.example.i230720_smd_assignment1

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatsAdapter(
    private val chats: List<ChatItem>,
    private val onChatClick: (ChatItem) -> Unit
) : RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvLastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        val viewUnreadDot: View = itemView.findViewById(R.id.viewUnreadDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]

        val name = chat.otherUserName.ifEmpty { "User" }
        holder.tvName.text = name

        val initials = if (name.length >= 2) name.substring(0, 2).uppercase() else name.uppercase()
        holder.tvAvatar.text = initials

        val timeAgo = if (chat.lastMessageTime > 0) {
            DateUtils.getRelativeTimeSpanString(
                chat.lastMessageTime,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
            ).toString()
        } else ""

        val lastText = if (timeAgo.isNotEmpty()) "${chat.lastMessage} · $timeAgo" else chat.lastMessage
        holder.tvLastMessage.text = lastText

        holder.viewUnreadDot.visibility = if (chat.isUnread) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            onChatClick(chat)
        }
    }

    override fun getItemCount(): Int = chats.size
}
