package com.palkowski.friendupp.model

import com.google.firebase.firestore.GeoPoint

data class ActiveUser(
    val id: String,
    val note: String,
    val creator_id: String,
    val participants_profile_pictures: HashMap<String, String>,
    val participants_usernames: HashMap<String, String>,
    val location: GeoPoint?,
    val time_start: String,
    val time_end: String,
    val create_time: String,
    val destroy_time: String,
    val invited_users: ArrayList<String>,
) {
    constructor() : this(
        id = "",
        note = "",
        creator_id = "",
        participants_profile_pictures = HashMap(),
        participants_usernames = HashMap(),
        location = null,
        time_start = "",
        time_end = "",
        create_time = "",
        destroy_time = "",
        ArrayList()
    )

}