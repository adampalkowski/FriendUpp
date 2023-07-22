package com.example.friendupp.model

import com.google.firebase.Timestamp

data class Invite(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val timestamp: String,
    val senderName: String,
    val senderProfilePictureUrl: String
){
    constructor() : this(
        id = "",
        senderId = "",
        receiverId = "",
        timestamp = "",
        senderName = "",
        senderProfilePictureUrl = ""
    )
}