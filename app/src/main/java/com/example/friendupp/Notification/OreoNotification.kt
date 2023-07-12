package com.example.friendupp.Notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.friendupp.R
import retrofit2.http.Url
import java.net.URL

class OreoNotification(base: Context?) : ContextWrapper(base) {
    private var notificationManager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableLights(false)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager!!.createNotificationChannel(channel)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun getOreoNotification(
        title: String?,
        body: String?,
        pendingIntent: PendingIntent?,
        soundUri: Uri?,
        icon: String,
        picture:String?
    ): Notification.Builder {
        val contentView = RemoteViews(applicationContext.packageName, R.layout.notification)
        contentView.setTextViewText(R.id.title, title)
        contentView.setTextViewText(R.id.description, body)
        contentView.setTextViewText(R.id.app_logo, picture)
        // Set other views and customize the layout as needed
        if(!picture.isNullOrEmpty()){
            val url: URL = URL(picture)
            return Notification.Builder(applicationContext, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setLargeIcon(BitmapFactory.decodeStream(url.openConnection().getInputStream()))
                .setContentText(body)
                .setSmallIcon(icon.toInt())
                .setSound(soundUri)
                .setAutoCancel(true)
        }

        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(icon.toInt())
            .setSound(soundUri)
            .setAutoCancel(true)

    }

    val manager: NotificationManager?
        get() {
            if (notificationManager == null) {
                notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return notificationManager
        }

    companion object {
        const val CHANNEL_ID = "com.example.FriendUpp"
        const val CHANNEL_NAME = "notification"
    }
}
