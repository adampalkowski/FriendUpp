package com.palkowski.friendupp.model

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
    var friends_ids_list: ArrayList<String>,
    var invited_ids: ArrayList<String>,
    var user_requests: ArrayList<String>,
    var activities:ArrayList<String>,
    val activitiesCreated:Int,
    val usersReached:Int,
    val tags:ArrayList<String>,
    val accountCreateTime:String,
    val geoHash:String
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
        friends_ids_list = ArrayList(),
        biography = "",
        user_requests= ArrayList(),
        activities= ArrayList(),
        activitiesCreated = 0,
        usersReached =0,
        tags= arrayListOf(),
        accountCreateTime="",
        geoHash=""
    )
}