package com.example.friendupp.Settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.friendupp.Components.ScreenHeading

sealed class RangeEvents{
    object GoBack:RangeEvents()
}

@Composable
fun RangeScreen(onEvent:(RangeEvents)->Unit){
    Column() {
        ScreenHeading(title = "Range", backButton = true, onBack = {onEvent(RangeEvents.GoBack)}) {

        }
    }
}