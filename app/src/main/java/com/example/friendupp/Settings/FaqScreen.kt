package com.example.friendupp.Settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.friendupp.Components.ScreenHeading

sealed class FAQEvents{
    object GoBack:FAQEvents()
}

@Composable
fun FAQScreen(onEvent:(FAQEvents)->Unit){
    Column() {
        ScreenHeading(title = "FAQ", backButton = true, onBack = {onEvent(FAQEvents.GoBack)}) {

        }
    }
}