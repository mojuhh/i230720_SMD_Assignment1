package com.example.i230720_smd_assignment1

data class MessageItem(
    val messageId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val type: String = "text", // "text", "image", "post"
    val content: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val replyToId: String = "",
    val replyToText: String = "",
    val reactions: Map<String, String> = emptyMap(),
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false
)
