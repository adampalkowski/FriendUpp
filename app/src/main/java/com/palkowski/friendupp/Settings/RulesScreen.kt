package com.palkowski.friendupp.Settings


import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.sp
import com.palkowski.friendupp.Components.PasswordEditText
import com.palkowski.friendupp.Components.ScreenHeading



sealed class RulesEvents{
    object GoBack:RulesEvents()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(onEvent:(RulesEvents)->Unit){

    Column(modifier=Modifier.safeDrawingPadding(),horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeading(title = "Privacy policy", backButton = true, onBack = {onEvent(RulesEvents.GoBack)}) { }


    }
}