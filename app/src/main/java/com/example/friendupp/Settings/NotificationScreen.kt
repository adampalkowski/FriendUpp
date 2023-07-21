package com.example.friendupp.Settings

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.CustomizeItem
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class NotificationEvents {
    object GoBack : NotificationEvents()
}
// Function to retrieve the notification preferences from SharedPreferences
fun getNotificationPrefs(context: Context): NotificationPreferences {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val allNotificationsKey = "allNotifications"
    val activityNotificationKey = "activityNotification"
    val chatMessageNotificationKey = "chatMessageNotification"
    val userInviteNotificationKey = "userInviteNotification"
    val participantNotificationKey = "participantNotification"

    return NotificationPreferences(
        preferences.getBoolean(allNotificationsKey, false),
        preferences.getBoolean(activityNotificationKey, false),
        preferences.getBoolean(chatMessageNotificationKey, false),
        preferences.getBoolean(userInviteNotificationKey, false),
        preferences.getBoolean(participantNotificationKey, false)
    )
}

// Function to save the notification preferences to SharedPreferences
fun saveNotificationPrefs(
    notificationPrefs: NotificationPreferences,
    context: Context
) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val allNotificationsKey = "allNotifications"
    val activityNotificationKey = "activityNotification"
    val chatMessageNotificationKey = "chatMessageNotification"
    val userInviteNotificationKey = "userInviteNotification"
    val participantNotificationKey = "participantNotification"

    with(preferences.edit()) {
        putBoolean(allNotificationsKey, notificationPrefs.allNotifications)
        putBoolean(activityNotificationKey, notificationPrefs.activityNotification)
        putBoolean(chatMessageNotificationKey, notificationPrefs.chatMessageNotification)
        putBoolean(userInviteNotificationKey, notificationPrefs.userInviteNotification)
        putBoolean(participantNotificationKey, notificationPrefs.participantNotification)
        apply()
    }
}

data class NotificationPreferences(
    val allNotifications: Boolean,
    val activityNotification: Boolean,
    val chatMessageNotification: Boolean,
    val userInviteNotification: Boolean,
    val participantNotification: Boolean
)
@Composable
fun NotificationScreen(modifier: Modifier,onEvent: (NotificationEvents) -> Unit) {
    val context = LocalContext.current

    var grayColor = SocialTheme.colors.uiBorder.copy(0.6f)
    val notificationPreferences=getNotificationPrefs(context =context )
    var allNotification by rememberSaveable { mutableStateOf(notificationPreferences.allNotifications) }
    var activityNotification by rememberSaveable { mutableStateOf(notificationPreferences.activityNotification) }
    var chatMessageNotification by rememberSaveable{ mutableStateOf(notificationPreferences.chatMessageNotification) }
    var userInviteNotification by rememberSaveable { mutableStateOf(notificationPreferences.userInviteNotification) }
    var participantNotification by rememberSaveable { mutableStateOf(notificationPreferences.participantNotification) }
    DisposableEffect(true) {
        onDispose {
            val notificationPrefs= NotificationPreferences(allNotification,activityNotification,chatMessageNotification,userInviteNotification,participantNotification)
            saveNotificationPrefs(notificationPrefs,context=context)
        }
    }


    if(allNotification){
        activityNotification=true
        userInviteNotification=true
        chatMessageNotification=true
        participantNotification=true
    }
    Column(modifier=modifier,horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeading(
            title = "Notification",
            backButton = true,
            onBack = { onEvent(NotificationEvents.GoBack) }) {

        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Turn off all notifications",
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                ),
                color = SocialTheme.colors.textPrimary
            )

            Switch(
                checked = allNotification,
                onCheckedChange = { allNotification = it },
                modifier = Modifier.padding(start = 16.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SocialTheme.colors.textInteractive,
                    checkedTrackColor = grayColor, uncheckedTrackColor = grayColor,
                    checkedIconColor = SocialTheme.colors.textInteractive,
                    uncheckedThumbColor = Color.White,
                    uncheckedIconColor = Color.White,
                    uncheckedBorderColor = grayColor,
                    checkedBorderColor = grayColor
                ), thumbContent = {
                    AnimatedVisibility(visible = allNotification) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_done),
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        CustomizeItem(
            title = "Activity invites",
            info = "Turn off notifications when users invite you to their activities.",
            switchValue = activityNotification,
            onSwitchValueChanged = { activityNotification = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomizeItem(
            title = "Friend requests",
            info = "Turn off notifications when users send you to friend requests.",
            switchValue = userInviteNotification,
            onSwitchValueChanged = { userInviteNotification = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomizeItem(
            title = "Chat messages",
            info = "Turn off notifications when users send you chat messages.",
            switchValue = chatMessageNotification,
            onSwitchValueChanged = { chatMessageNotification = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomizeItem(
            title = "Activity participation",
            info = "Turn off notifications when users join your activity.",
            switchValue = participantNotification,
            onSwitchValueChanged = { participantNotification = it }
        )
    }
}