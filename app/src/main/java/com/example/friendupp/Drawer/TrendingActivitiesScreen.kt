package com.example.friendupp.Drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.friendupp.Components.ScreenHeading


sealed class TrendingActivitiesEvents{
    object GoBack:TrendingActivitiesEvents()
}

@Composable
fun TrendingActivitiesScreen(onEvent:(TrendingActivitiesEvents)->Unit){
    Column() {
        ScreenHeading(title = "Joined activities", backButton = true, onBack = {onEvent(TrendingActivitiesEvents.GoBack)}) {}


    }

}