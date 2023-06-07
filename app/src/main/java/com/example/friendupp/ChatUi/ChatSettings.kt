package com.example.friendupp.ChatUi

import android.widget.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Profile.TagDivider
import com.example.friendupp.R
import com.example.friendupp.Settings.SettingsItem
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

sealed class TYPE{
    object SINGLE:TYPE()
    object GROUP:TYPE()
}

sealed class ChatSettingsEvents{
    object GoBack:ChatSettingsEvents()
    object Notification:ChatSettingsEvents()
    object Block:ChatSettingsEvents()
    object Limit:ChatSettingsEvents()
    object Report:ChatSettingsEvents()
    object Share:ChatSettingsEvents()
    object CrateGroup:ChatSettingsEvents()
    object ChangeGroupName:ChatSettingsEvents()
    object ChangeGroupImage:ChatSettingsEvents()
    object AddUsers:ChatSettingsEvents()
    object LeaveGroup:ChatSettingsEvents()
    object DisplayUsers:ChatSettingsEvents()
    class GoToUserProfile(val userId:String):ChatSettingsEvents()
}


@Composable
fun ChatSettings(type: TYPE,chatSettingsEvents: (ChatSettingsEvents)->Unit){
    BackHandler(true) {
        chatSettingsEvents(ChatSettingsEvents.GoBack)
    }
    when(type){
        is TYPE.SINGLE->{
            ChatSettingsSingle(name="Agnieszka Tomala", username = "tomala", profilePictureUrl = "https://images.unsplash.com/photo-1529665253569" +
                    "-6d01c0eaf7b6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cHJvZmlsZXxlbnwwfHwwfHw%3D" +
                    "&auto=format&fit=crop&w=500&q=60", chatSettingsEvents = chatSettingsEvents)
        }
        is TYPE.GROUP->{
            ChatSettingsGroup(name="TYPY", profilePictureUrl = "https://images.unsplash.com/photo-1529665253569" +
                    "-6d01c0eaf7b6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cHJvZmlsZXxlbnwwfHwwfHw%3D" +
                    "&auto=format&fit=crop&w=500&q=60", chatSettingsEvents = chatSettingsEvents)
        }
    }
}

@Composable
fun ChatSettingsGroup(profilePictureUrl:String,name:String,chatSettingsEvents: (ChatSettingsEvents)->Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
        rememberScrollState())) {
        ScreenHeading(title ="Chat settings" , backButton = true, onBack ={chatSettingsEvents(ChatSettingsEvents.GoBack)} ) {

        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = {chatSettingsEvents(ChatSettingsEvents.GoToUserProfile(""))})) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text =name, style = TextStyle(fontFamily = Lexend, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),color=SocialTheme.colors.textPrimary)
        }
        TagDivider()
        Spacer(modifier = Modifier.height(24.dp))
        SettingsItem(label = "Display users", icon =R.drawable.ic_group ) {
            chatSettingsEvents(ChatSettingsEvents.DisplayUsers)

        }
        SettingsItem(label = "Add users", icon =R.drawable.ic_group_add ) {
            chatSettingsEvents(ChatSettingsEvents.AddUsers)
        }
        SettingsItem(label = "Notifications", icon = R.drawable.ic_notify) {
            chatSettingsEvents(ChatSettingsEvents.Notification)
        }

        SettingsItem(label = "Change group image", icon =R.drawable.ic_add_image ) {
            chatSettingsEvents(ChatSettingsEvents.ChangeGroupImage)
        }
        SettingsItem(label = "Change group name", icon =R.drawable.ic_edit ) {
            chatSettingsEvents(ChatSettingsEvents.ChangeGroupName)
        }


        SettingsItem(label = "Share group", icon =R.drawable.ic_share ) {
            chatSettingsEvents(ChatSettingsEvents.Share)
        }

        SettingsItem(label = "Leave group", icon =R.drawable.ic_logout ) {
            chatSettingsEvents(ChatSettingsEvents.LeaveGroup)
        }
        SettingsItem(label = "Report", icon =R.drawable.ic_flag ) {
            chatSettingsEvents(ChatSettingsEvents.Report)
        }
    }
}

@Composable
fun ChatSettingsSingle(profilePictureUrl:String,name:String,username:String,chatSettingsEvents: (ChatSettingsEvents)->Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
        rememberScrollState())) {
        ScreenHeading(title ="Chat settings" , backButton = true, onBack ={chatSettingsEvents(ChatSettingsEvents.GoBack)} ) {

        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = {chatSettingsEvents(ChatSettingsEvents.GoToUserProfile(""))})) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text =name, style = TextStyle(fontFamily = Lexend, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),color=SocialTheme.colors.textPrimary)
            Text(text =username, style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Normal),color=SocialTheme.colors.textPrimary)

        }
      TagDivider()
        Spacer(modifier = Modifier.height(24.dp))
        SettingsItem(label = "Notifications", icon = R.drawable.ic_notify) {
            chatSettingsEvents(ChatSettingsEvents.Notification)
        }
        SettingsItem(label = "Create group with user", icon =R.drawable.ic_group_add ) {
            chatSettingsEvents(ChatSettingsEvents.CrateGroup)
        }
        SettingsItem(label = "Share profile", icon =R.drawable.ic_share ) {
            chatSettingsEvents(ChatSettingsEvents.Share)
        }
        SettingsItem(label = "Limit", icon =R.drawable.ic_limit) {
            chatSettingsEvents(ChatSettingsEvents.Limit)
        }
        SettingsItem(label = "Block", icon =R.drawable.ic_block ) {
            chatSettingsEvents(ChatSettingsEvents.Block)
        }
        SettingsItem(label = "Report", icon =R.drawable.ic_flag ) {
            chatSettingsEvents(ChatSettingsEvents.Report)
        }
    }
}
