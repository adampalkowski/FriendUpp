package com.palkowski.friendupp.Settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.palkowski.friendupp.Components.ScreenHeading

sealed class TermsEvents{
    object GoBack:TermsEvents()
}

@Composable
fun TermsScreen(onEvent:(TermsEvents)->Unit){
    Column() {
        ScreenHeading(title = "Terms", backButton = true, onBack = {onEvent(TermsEvents.GoBack)}) {

        }
    }
}