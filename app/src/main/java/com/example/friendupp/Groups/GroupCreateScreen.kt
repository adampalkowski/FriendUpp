package com.example.friendupp.Groups

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.friendupp.Components.ScreenHeading

sealed class GroupCreateEvents{
    object GoBack:GroupCreateEvents()
}


@Composable
fun GroupsCreateScreen(modifier: Modifier=Modifier,onEvent:(GroupCreateEvents)->Unit){
    Column(modifier = modifier) {
        ScreenHeading(title = "Create group", backButton = true, onBack = {onEvent(GroupCreateEvents.GoBack)}) {

        }




    }
}