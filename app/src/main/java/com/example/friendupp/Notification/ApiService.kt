package com.example.friendupp.Notification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers("Authorization: key=YOUR_SERVER_KEY", "Content-Type:application/json")

    @POST("fcm/send")
    fun sendNotification(@Body body: Sender?): Call<MyResponse?>?
}
class MyResponse {
    var success = 0
}
class Sender(data: Data, to: String) {
    var data: Data
    var to: String

    init {
        this.data = data
        this.to = to
    }
}


class Data {
    var user: String? = null
    var icon = 0
    var body: String? = null
    var title: String? = null
    var sent: String? = null

    constructor(user: String?, icon: Int, body: String?, title: String?, sent: String?) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sent = sent
    }

    override fun toString(): String {
        return "Data{" +
                "user='" + user + '\'' +
                ", icon=" + icon +
                ", body='" + body + '\'' +
                ", title='" + title + '\'' +
                ", sent='" + sent + '\'' +
                '}'
    }

}
