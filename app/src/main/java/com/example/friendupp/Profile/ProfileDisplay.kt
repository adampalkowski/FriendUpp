package com.example.friendupp.Profile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.R
import com.example.friendupp.di.DEFAULT_PROFILE_PICTURE_URL
import com.example.friendupp.model.User
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class ProfileDisplayEvents {
    object GoToSearch : ProfileDisplayEvents()
    object GoBack : ProfileDisplayEvents()
    object GoToSettings : ProfileDisplayEvents()
    object GoToEditProfile : ProfileDisplayEvents()

    /*todo*/
    object GoToFriendList : ProfileDisplayEvents()
    object GetProfileLink : ProfileDisplayEvents()
}


@Composable
fun ProfileDisplayScreen(
    modifier: Modifier,
    onEvent: (ProfileDisplayEvents) -> Unit,
    user: User,
    onClick: () -> Unit,
) {
    var displaySettings by remember {
        mutableStateOf(false)
    }
    BackHandler(true) {
        onEvent(ProfileDisplayEvents.GoBack)
    }

    LazyColumn {
        item {
            Column(modifier) {
                ScreenHeading(
                    title = "User",
                    backButton = true,
                    onBack = { onEvent(ProfileDisplayEvents.GoBack) }) {
                        Spacer(modifier = Modifier.weight(1f))
                        ButtonAdd(icon = R.drawable.ic_more, onClick = {
                            displaySettings=true
                        })
                }
                Spacer(modifier = Modifier.height(12.dp))
                ProfileInfoDisplay(
                    name = user.name ?:"",
                    username = user.username?:"",
                    profilePictureUrl = user.pictureUrl?:"",
                    location = user.location,
                    description = user.biography
                )
                TagDivider(user.tags)
                ProfileStats(
                    modifier = Modifier.fillMaxWidth(),
                    user.activitiesCreated,
                    user.friends_ids.size,
                    user.usersReached
                )

            }
        }

        item { ProfileDisplayPicker() }
    }

    AnimatedVisibility(visible = displaySettings) {
        Dialog(onDismissRequest = { displaySettings=false }) {
            ProfileDisplaySettingContent(onCancel={displaySettings=false})
        }
    }

}

@Composable
fun ProfileInfoDisplay(name:String,username:String,profilePictureUrl:String,location:String,description:String){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)) {
        Row (verticalAlignment = Alignment.CenterVertically){
            Box(modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)){
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


            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val truncatedName = if (name.length > 30) name.substring(0, 30)+"..." else name
                androidx.compose.material.Text(
                    text = truncatedName,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = SocialTheme.colors.textPrimary
                )
                val truncatedUsername = if (username.length > 30) username.substring(0, 30)+"..." else username
                androidx.compose.material.Text(
                    text = truncatedUsername,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp
                    ),
                    color = SocialTheme.colors.textPrimary
                )
                val truncatedLocation = if (location.length > 50) location.substring(0, 50)+"..." else location
                androidx.compose.material.Text(
                    text = truncatedLocation,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp
                    ),
                    color = SocialTheme.colors.textPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

            val truncatedDescription = if (description.length > 500) description.substring(0, 500)+"..." else description
            androidx.compose.material.Text(
                text = truncatedDescription,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold
                ),
                color = SocialTheme.colors.textPrimary
            )

    }
}



@Composable
fun ProfileDisplaySettingContent(onCancel: () -> Unit={}) {
    Column(Modifier.clip(RoundedCornerShape(24.dp))) {
        ProfileDisplaySettingsItem(label="Share",icon=R.drawable.ic_share, textColor = SocialTheme.colors.textPrimary)
        ProfileDisplaySettingsItem(label="Remove",icon=R.drawable.ic_delete , textColor = SocialTheme.colors.error)
        ProfileDisplaySettingsItem(label="Block",icon=R.drawable.ic_block , textColor = SocialTheme.colors.error)
        ProfileDisplaySettingsItem(label="Cancel" , turnOffIcon = true, textColor = SocialTheme.colors.textPrimary.copy(0.5f), onClick = onCancel)
    }
}

@Composable
fun ProfileDisplaySettingsItem(turnOffIcon:Boolean=false,icon:Int=R.drawable.ic_x,label:String,textColor:androidx.compose.ui.graphics.Color=androidx.compose.ui.graphics.Color.Black,onClick: () -> Unit={}) {
    Column(Modifier.background(SocialTheme.colors.uiBackground)) {

        
    Row(modifier = Modifier
        .fillMaxWidth().clickable(onClick=onClick)
        .padding(vertical = 16.dp, horizontal = 12.dp), horizontalArrangement =Arrangement.Center){
        if(!turnOffIcon){
            Icon(painter = painterResource(id = icon), contentDescription =null,tint=textColor )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(text =label, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, fontFamily = Lexend), color = textColor )
        if(!turnOffIcon){
            Spacer(modifier = Modifier.width(32.dp))

        }
    }
    Box(modifier = Modifier
        .height(0.5.dp)
        .fillMaxWidth()
        .background(SocialTheme.colors.uiBorder))
    }
}

