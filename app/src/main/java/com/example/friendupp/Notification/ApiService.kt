package com.example.friendupp.Notification

import android.content.res.Resources
import com.example.friendupp.R
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
interface APIService {
    @Headers("Authorization: key=AAAA3EPZfu8:APA91bE9U5uVjVgNNmCHp5U_0wIegIuHWYo3VCBV2D-_6T2PDxUX7Fr7ClqQn0-7x178twbaSnubAt7FWJReP55_s2j503plAxnZhA09hHozq-ce2k6ubvk8PnSZ4QH_FY9Kb_xt59m_",
        "Content-Type:application/json")
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
    var picture:String?=null
    var type:String?=null
    var id:String?=null

    constructor(user: String?, icon: Int, body: String?
                , title: String?, sent: String?,
                picture:String?,type:String?,id:String?) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sent = sent
        this.picture= picture
        this.type=type
        this.id=id
    }

    override fun toString(): String {
        return "Data{" +
                "user='" + user + '\'' +
                ", icon=" + icon +
                ", body='" + body + '\'' +
                ", title='" + title + '\'' +
                ", sent='" + sent + '\'' +
                ", picture='" + picture + '\'' +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                '}'
    }

}
