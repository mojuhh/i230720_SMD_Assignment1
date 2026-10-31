package com.example.i230720_smd_assignment1

data class ChatItem(
    val chatId: String = "",
    val otherUserId: String = "",
    val otherUserName: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val lastMessageSenderId: String = "",
    val isUnread: Boolean = false
)
