package com.example.friendupp.Settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.friendupp.Components.ScreenHeading

sealed class EmailEvents{
    object GoBack:EmailEvents()
}

@Composable
fun EmailScreen(onEvent:(EmailEvents)->Unit){
    Column() {
        ScreenHeading(title = "Email", backButton = true, onBack = {onEvent(EmailEvents.GoBack)}) {

        }
    }
}