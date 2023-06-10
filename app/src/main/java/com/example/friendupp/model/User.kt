package com.example.friendupp.model

import com.example.friendupp.Categories.Category
import java.io.Serializable

data class User(
    var name: String?,
    var username: String?,
    var email: String?,
    var id: String,
    var pictureUrl: String?,
    var biography:String,
    var location:String,
    var blocked_ids: ArrayList<String>,
    var friends_ids: HashMap<String,String>,
    var invited_ids: ArrayList<String>,
    var user_requests: ArrayList<String>,
    var activities:ArrayList<String>,
    val activitiesCreated:Int,
    val usersReached:Int,
    val tags:ArrayList<String>,
) : Serializable {
    constructor() : this(
        name = "",
        username = "",
        email = "",
        id = "",
        location="",
        pictureUrl = "",
        blocked_ids = ArrayList(),
        friends_ids = HashMap(),
        invited_ids = ArrayList(),
        biography = "",
        user_requests= ArrayList(),
        activities= ArrayList(),
        activitiesCreated = 0,
        usersReached =0,
        tags= arrayListOf()
    )
}