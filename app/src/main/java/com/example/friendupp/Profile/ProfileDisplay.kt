package com.example.friendupp.Profile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.R
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
                ProfileInfo(
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

