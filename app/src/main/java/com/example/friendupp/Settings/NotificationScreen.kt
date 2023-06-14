package com.example.friendupp.Settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


sealed class NotificationEvents{
    object GoBack:NotificationEvents()
}

@Composable
fun NotificationScreen(onEvent:(NotificationEvents)->Unit){
    var grayColor = SocialTheme.colors.uiBorder.copy(0.6f)
    var allNotification by remember { mutableStateOf(false) }
    var activityNotification by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeading(title = "Notification", backButton = true, onBack = {onEvent(NotificationEvents.GoBack)}) {

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
                onCheckedChange = { allNotification=it },
                modifier = Modifier.padding(start = 16.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SocialTheme.colors.textInteractive,
                    checkedTrackColor = grayColor, uncheckedTrackColor = grayColor,
                    checkedIconColor = SocialTheme.colors.textInteractive,
                    uncheckedThumbColor = Color.White,
                    uncheckedIconColor = Color.White,
                    uncheckedBorderColor = grayColor,
                    checkedBorderColor = grayColor
                )
                ,thumbContent={
                    AnimatedVisibility(visible = allNotification) {
                        Icon(painter = painterResource(id = R.drawable.ic_done),tint= Color.White, contentDescription =null )
                    } }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedVisibility(visible = !allNotification, enter = slideInVertically(), exit = slideOutVertically()) {
            CustomizeItem(
                title = "Activity notification", info = "Get notifications when users invite you to their activities.",
                switchValue = activityNotification,
                onSwitchValueChanged = { activityNotification=it }
            )

        }




    }
}