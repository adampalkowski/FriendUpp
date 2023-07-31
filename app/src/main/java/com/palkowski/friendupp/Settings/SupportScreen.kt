package com.palkowski.friendupp.Settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.palkowski.friendupp.Components.ScreenHeading

sealed class SupportEvents{
    object GoBack:SupportEvents()
}

@Composable
fun SupportScreen(onEvent:(SupportEvents)->Unit){
    Column() {
        ScreenHeading(title = "Support", backButton = true, onBack = {onEvent(SupportEvents.GoBack)}) {

        }
    }
}