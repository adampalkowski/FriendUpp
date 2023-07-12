package com.example.friendupp.Notification

import android.Manifest
import android.app.Notification

import android.app.PendingIntent

import android.content.Intent

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Paint.Style

import android.media.RingtoneManager

import android.net.Uri

import android.os.Build

import android.os.Bundle
import android.util.Log


import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat

import androidx.core.app.NotificationCompat

import androidx.core.app.NotificationManagerCompat
import com.example.friendupp.MainActivity
import com.example.friendupp.R
import com.example.friendupp.model.UserData


import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.FirebaseUser

import com.google.firebase.database.DatabaseReference

import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.messaging.FirebaseMessagingService

import com.google.firebase.messaging.RemoteMessage


const val channelId = "notification"
const val channelName = "com.example.Friendupp"
const val CHANNEL_1_ID = "channel1"
const val CHANNEL_2_ID = "channel2"

class MyFirebaseMessaging : FirebaseMessagingService() {
    private var notificationManager: NotificationManagerCompat? = null
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        updateToken(s)
    }

    private fun updateToken(s: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token = Token(s)
        reference.child(UserData.user!!.id.toString()).setValue(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("MESSAGERECEIVER", "asdasdasdasd")
        val sent = remoteMessage.data["sent"]
        val user = remoteMessage.data["user"]
        assert(sent != null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendOreoNotification(remoteMessage)
        } else {
            sendNotifcation(remoteMessage)
        }
    }

    private fun sendOreoNotification(remoteMessage: RemoteMessage) {
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val picture = remoteMessage.data["picture"]
        assert(user != null)
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val oreoNotification = OreoNotification(this)
        val builder = oreoNotification.getOreoNotification(
            title, body, pendingIntent, defaultSound,
            icon!!,picture
        )
        var i = 0
        if (j > 0) {
            i = j
        }
        oreoNotification.manager!!.notify(i, builder.build())
    }

    private fun sendNotifcation(remoteMessage: RemoteMessage) {
        val sent = remoteMessage.data["sent"]
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val notification = remoteMessage.notification
        notificationManager = NotificationManagerCompat.from(this)
        sendOnChannel2(title, body)
    }

    fun sendOnChannel1(tit: String, msg: String) {
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_profile)
            .setContentTitle(tit)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager!!.notify(1, notification)
    }

    fun sendOnChannel2(tit: String?, msg: String?) {
        val activityIntent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean("Chatting_Start_key", true)
        activityIntent.putExtras(bundle)
        val contentIntent = PendingIntent.getActivity(
            this,
            1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_wave)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_2_ID)
            .setSmallIcon(R.drawable.ic_profile)
            .setContentTitle(tit)
            .setLargeIcon(largeIcon)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Yoo")
                    .setBigContentTitle("Big Content Title")
            )
            .setAutoCancel(true)
            .setContentText(msg)
            .setColor(Color.BLUE)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager!!.notify(2, notification)
    }
}