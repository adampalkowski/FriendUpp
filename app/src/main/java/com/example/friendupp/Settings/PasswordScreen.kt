package com.example.friendupp.Settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.friendupp.Components.ScreenHeading


sealed class PasswordEvents{
    object GoBack:PasswordEvents()
}

@Composable
fun PasswordScreen(onEvent:(PasswordEvents)->Unit){
    Column() {
        ScreenHeading(title = "Password", backButton = true, onBack = {onEvent(PasswordEvents.GoBack)}) {

        }
    }
}