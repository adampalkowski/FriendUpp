package com.example.friendupp.Settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.friendupp.Components.ScreenHeading

sealed class LanguageEvents{
    object GoBack:LanguageEvents()
}


@Composable
fun LanguageScreen(onEvent:(LanguageEvents)->Unit){
    Column() {
        ScreenHeading(title = "Language", backButton = true, onBack = {onEvent(LanguageEvents.GoBack)}) {

        }
    }
}