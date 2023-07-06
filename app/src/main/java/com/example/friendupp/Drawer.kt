package com.example.friendupp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.Pacifico
import com.example.friendupp.ui.theme.SocialTheme
import com.example.friendupp.Settings.SettingsItem


sealed class DrawerEvents{
    object GoToSettings:DrawerEvents()
    object GoToInbox:DrawerEvents()
    object GoToTrending:DrawerEvents()
    object GoToJoined:DrawerEvents()
    object GoToCreated:DrawerEvents()
    object GoToBookmarked:DrawerEvents()
    object GoToForYou:DrawerEvents()
    object GoToGroups:DrawerEvents()
    object GoToRate:DrawerEvents()
    object GoToSearch:DrawerEvents()
}

@Composable
fun DrawerContent(onEvent: (DrawerEvents)->Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .background(SocialTheme.colors.uiBackground)
            .padding(vertical = 24.dp)
            .padding(start = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
    ) {
        androidx.compose.material3.Text(modifier=Modifier.padding(start=24.dp),
            text = "FriendUpp",
            style = TextStyle(
                fontFamily = Pacifico,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                color = SocialTheme.colors.textPrimary.copy(0.8f)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        //DRAWER Content
        SettingsItem(label = "Search", icon = R.drawable.ic_search) {
            onEvent(DrawerEvents.GoToSearch)
        }
       /* SettingsItem(label = "Inbox", icon = R.drawable.ic_notify) {
            onEvent(DrawerEvents.GoToInbox)
        }*/
        SettingsItem(label = "Trending", icon = R.drawable.ic_trending,disabled=true) {
            onEvent(DrawerEvents.GoToTrending)
        }
        SettingsItem(label = "Joined", icon = R.drawable.ic_calendar_upcoming) {
            onEvent(DrawerEvents.GoToJoined)
        }
        SettingsItem(label = "Created", icon = R.drawable.ic_history) {
            onEvent(DrawerEvents.GoToCreated)
        }
        SettingsItem(label = "Bookmarked", icon = R.drawable.ic_bookmark_300) {
            onEvent(DrawerEvents.GoToBookmarked)
        }
     /*   SettingsItem(label = "For you", icon = R.drawable.ic_recommend) {
            onEvent(DrawerEvents.GoToForYou)
        }*/
        SettingsItem(label = "Groups", icon = R.drawable.ic_group) {
            onEvent(DrawerEvents.GoToGroups)
        }
    /*    SettingsItem(label = "Rate app", icon = R.drawable.ic_rate) {
            onEvent(DrawerEvents.GoToRate)
        }*/
        SettingsItem(label = "Settings", icon = R.drawable.ic_settings) {
            onEvent(DrawerEvents.GoToSettings)
        }

        Spacer(Modifier.weight(1f))
        androidx.compose.material3.Text(modifier=Modifier.padding(start=32.dp),
            text = "Version 1.0",
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                color = SocialTheme.colors.textPrimary.copy(0.5f)
            )
        )

    }
}

@Composable
fun DrawerField(
    modifier: Modifier = Modifier,
    title: String,
    icon: Int,
    subtitle: String? = null,
    onClick: () -> Unit,
    content:@Composable () -> Unit ={}
) {


    Box(modifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(vertical = 16.dp)) {
        Column() {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    painter = painterResource(id = icon),
                    tint = SocialTheme.colors.iconPrimary,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(text =title, style = TextStyle(color=SocialTheme.colors.textPrimary,
                    fontFamily = Lexend, fontWeight = FontWeight.Medium, fontSize = 16.sp))

                Spacer(modifier = Modifier.width(24.dp))
            }
            content()
        }

    }
}
