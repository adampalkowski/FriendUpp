package com.palkowski.friendupp.model

import java.io.Serializable


data class Chat(
    var create_date: String?,
    val owner_id: String,
    var id: String?,
    var members: List<String>,
    var invites: List<String>,
    var name: String?,
    var imageUrl: String?,
    var recent_message_time: String?,
    var recent_message: String?,
    var type: String = "duo",
    var user_one_username: String?,
    var user_one_profile_pic: String?,
    var user_two_username: String?,
    var user_two_profile_pic: String?,
    var highlited_message: String?,
    var description: String?,
    var numberOfUsers: Int,
    val numberOfActivities: Int,
    var public: Boolean,
    var reports:Int,
    var blocked:Boolean,
    var user_one_id :String?,
    var user_two_id :String?
) : Serializable {
    constructor() : this(
        create_date="",
        owner_id="",
        id="",
        members=    listOf(),
        invites=    listOf(),
        name="",
        imageUrl="",
        recent_message_time="",
        recent_message="",
        type="",
        user_one_username="",
        user_one_profile_pic="",
        user_two_username="",
        user_two_profile_pic="",
        highlited_message="",
        description="",
        numberOfUsers=0,
        numberOfActivities=0,
        public=false,
        reports=0,
        blocked=false,
        user_one_id="",
         user_two_id="",
       )
}