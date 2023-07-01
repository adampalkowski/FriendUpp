package com.example.friendupp.model

data class ChatMessage (
    var text: String,
    var sender_picture_url:String,
    var sent_time:String,
    val sender_id:String,
    var message_type:String="text",
    var id :String,
    val collectionId:String,
    var replyTo:String?
){

    constructor(): this("", "","", "","","","","")
}
