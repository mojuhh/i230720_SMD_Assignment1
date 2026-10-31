package com.example.i230720_smd_assignment1

import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessagesAdapter(
    private val messages: List<MessageItem>,
    private val currentUserId: String,
    private val onMessageClick: (MessageItem) -> Unit,
    private val onMessageLongClick: (MessageItem) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    companion object {
        private const val TYPE_SENT = 1
        private const val TYPE_RECEIVED = 2
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessageContent: TextView = itemView.findViewById(R.id.tvMessageContent)
        val ivMessageImage: ImageView = itemView.findViewById(R.id.ivMessageImage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        val layoutReplyBox: LinearLayout = itemView.findViewById(R.id.layoutReplyBox)
        val tvReplyToText: TextView = itemView.findViewById(R.id.tvReplyToText)
        val tvHeartReaction: TextView = itemView.findViewById(R.id.tvHeartReaction)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) TYPE_SENT else TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutRes = if (viewType == TYPE_SENT) R.layout.item_message_sent else R.layout.item_message_received
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        // Reply Box
        if (message.replyToText.isNotEmpty()) {
            holder.layoutReplyBox.visibility = View.VISIBLE
            holder.tvReplyToText.text = "Replying: ${message.replyToText}"
        } else {
            holder.layoutReplyBox.visibility = View.GONE
        }

        // Message Content / Deleted / Edited
        if (message.isDeleted) {
            holder.tvMessageContent.text = "This message was deleted"
            holder.tvMessageContent.setTypeface(null, Typeface.ITALIC)
            holder.ivMessageImage.visibility = View.GONE
        } else {
            holder.tvMessageContent.setTypeface(null, Typeface.NORMAL)
            var text = message.content
            if (message.isEdited) {
                text += " (Edited)"
            }
            holder.tvMessageContent.text = text

            // Handle Image Media
            if (message.type == "image" && message.imageUrl.isNotEmpty()) {
                holder.ivMessageImage.visibility = View.VISIBLE
                try {
                    val cleanBase64 = message.imageUrl.substringAfter(",")
                    val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    if (bitmap != null) {
                        holder.ivMessageImage.setImageBitmap(bitmap)
                    } else {
                        holder.ivMessageImage.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    holder.ivMessageImage.visibility = View.GONE
                }
            } else {
                holder.ivMessageImage.visibility = View.GONE
            }
        }

        // Timestamp
        val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))
        holder.tvTimestamp.text = timeString

        // Heart Reaction
        val hasHeart = message.reactions.containsValue("❤️") || message.reactions.isNotEmpty()
        if (hasHeart) {
            holder.tvHeartReaction.visibility = View.VISIBLE
        } else {
            holder.tvHeartReaction.visibility = View.GONE
        }

        // Long click for Edit / Delete / Reply / React options
        holder.itemView.setOnLongClickListener {
            onMessageLongClick(message)
            true
        }

        // Click to toggle Heart Reaction
        holder.itemView.setOnClickListener {
            onMessageClick(message)
        }
    }

    override fun getItemCount(): Int = messages.size
}
