package com.example.friendupp.model

data class Participant(
    val id: String,
    val profile_picture: String,
    val username: String,
    val name: String,
    val timestamp:String
){
    constructor() : this("", "", "", "", "")

}