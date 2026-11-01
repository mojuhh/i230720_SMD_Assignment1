package com.example.i230720_smd_assignment1

data class Story(
    var storyId: String = "",
    var userId: String = "",
    var username: String = "",
    var userAvatar: String = "",
    var mediaUri: String = "",
    var mediaType: String = "image", // "image" or "video"
    var textLabel: String = "",
    var audience: String = "your_story", // "your_story" or "close_friends"
    var timestamp: Long = 0L,
    var expiresAt: Long = 0L
)
