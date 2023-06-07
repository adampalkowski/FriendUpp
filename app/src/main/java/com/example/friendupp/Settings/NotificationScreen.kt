package com.example.friendupp.Settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.friendupp.Components.ScreenHeading


sealed class NotificationEvents{
    object GoBack:NotificationEvents()
}

@Composable
fun NotificationScreen(onEvent:(NotificationEvents)->Unit){
    Column() {
        ScreenHeading(title = "Notification", backButton = true, onBack = {onEvent(NotificationEvents.GoBack)}) {

        }
    }
}