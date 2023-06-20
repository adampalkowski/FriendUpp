package com.example.friendupp.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.friendupp.Home.Live.CreateLive
import com.example.friendupp.Home.Live.LiveUserItem
import com.example.friendupp.R
import com.example.friendupp.ui.theme.SocialTheme


enum class Option(val label: String, val icon: Int) {
    FRIENDS("Friends", R.drawable.ic_hand_300),
    PUBLIC("Public", R.drawable.ic_public_300)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionPicker(onEvent: (HomeEvents) -> Unit,selectedOption:Option,onOptionSelected:(Option)->Unit) {
    val context = LocalContext.current
    val dividerColor = SocialTheme.colors.uiBorder
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            )
    ) {
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(option = Option.FRIENDS,
            isSelected = selectedOption == Option.FRIENDS,
            onClick = {onOptionSelected(Option.FRIENDS)})
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(option = Option.PUBLIC,
            isSelected = selectedOption == Option.PUBLIC,
            onClick = {onOptionSelected(Option.PUBLIC)})
        Spacer(
            modifier = Modifier
                .width(64.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        CreateLive(
            onClick = { onEvent(HomeEvents.CreateLive) },
            imageUrl =   "https://images.unsplash.com/photo-1587691592099-24045742c181?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxw" +
                    "aG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2073&q=80"
        )
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        LiveUserItem(
            text = "Quick trip?",
            imageUrl ="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcScGQQPJTeRXYxfbXVhLLXPl4aCJCexZ4dS7Q&usqp=CAU"

        )
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        LiveUserItem(imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSdMOgc-WqbgagnyjIGnPOvsxypn_bNVODFaQ&usqp=CAU")
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        LiveUserItem(imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSf686xJRtWDvGxXHISwA9QBWLPi-EVW3PFIw&usqp=CAU")
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(dividerColor)
        )
    }


}
