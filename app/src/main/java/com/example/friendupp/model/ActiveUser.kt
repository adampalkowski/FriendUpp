package com.example.friendupp.model

data class ActiveUser(
    val id: String,
    val creator_id:String,
    val participants_profile_pictures:HashMap<String,String>,
    val participants_usernames:HashMap<String,String>,
    val latLng: String?,
    val time_length: String,
    val time_start: String,
    val time_end: String,
    val create_time:String,
    val destroy_time:String,
    val invited_users:ArrayList<String>,
){
    constructor(): this(id="",creator_id="",participants_profile_pictures= HashMap(),participants_usernames=HashMap(),latLng ="",time_length="",time_start="",time_end="",create_time="",destroy_time="",ArrayList())

}