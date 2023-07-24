package com.example.friendupp.Request

data class Request(
    val id: String,
    val profile_picture: String,
    val username: String,
    val name: String,
    val timestamp:String
){
    constructor() : this("", "", "", "", "")
}