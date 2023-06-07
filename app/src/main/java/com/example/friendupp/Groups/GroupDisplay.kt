package com.example.friendupp.Groups

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Profile.*
import com.example.friendupp.R
import com.example.friendupp.model.Chat

import com.example.friendupp.model.User
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class GroupDisplayEvents {
    object GoToSearch : GroupDisplayEvents()
    object GoBack : GroupDisplayEvents()
    object GoToSettings : GroupDisplayEvents()
    object GoToEditProfile : GroupDisplayEvents()

    /*todo*/
    object GoToFriendList : GroupDisplayEvents()
    object GetProfileLink : GroupDisplayEvents()
}


@Composable
fun GroupDisplayScreen(
    modifier: Modifier,
    onEvent: (GroupDisplayEvents) -> Unit,
    group: Chat,
    onClick: () -> Unit={},
) {
    var displaySettings by remember {
        mutableStateOf(false)
    }
    BackHandler(true) {
        onEvent(GroupDisplayEvents.GoBack)
    }

    LazyColumn {
        item {
            Column(modifier) {
                ScreenHeading(
                    title = "Group",
                    backButton = true,
                    onBack = { onEvent(GroupDisplayEvents.GoBack) }) {
                    Spacer(modifier = Modifier.weight(1f))
                    ButtonAdd(icon = R.drawable.ic_more, onClick = {
                        displaySettings=true
                    })
                }
                Spacer(modifier = Modifier.height(12.dp))
                GroupInfo(name=group.name?:"", imageUrl = group.imageUrl?:"", description = group.description?:"")
                Spacer(modifier = Modifier.height(12.dp))
                GroupStats(
                    modifier = Modifier.fillMaxWidth(),
                    group.numberOfUsers,
                    group.numberOfActivities,
                )

            }
        }

        item { ProfileDisplayPicker() }
    }

    AnimatedVisibility(visible = displaySettings) {
        Dialog(onDismissRequest = { displaySettings=false }) {
           GroupDisplaySettingContent(onCancel={displaySettings=false})
        }
    }

}
@Composable
fun GroupStats(modifier: Modifier,numberOfUsers:Int,numberOfActivities:Int,){
    Row(modifier = modifier,horizontalArrangement = Arrangement.SpaceEvenly) {
        Stat(value=numberOfUsers.toString(), label = "Users")
        Stat(value=numberOfActivities.toString(), label = "Activities")
    }
}

@Composable
fun GroupInfo(name:String,imageUrl:String,description:String){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )

        val truncname = if (name.length > 30) name.substring(0, 30)+"..." else name
        Text(
            text = truncname,
            style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Medium, fontSize = 16.sp),
            color = SocialTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        val truncatedDescription = if (description.length > 500) description.substring(0, 500)+"..." else description
        Text(
            text = truncatedDescription,
            style = TextStyle(fontSize = 14.sp, fontFamily = Lexend, fontWeight = FontWeight.SemiBold),
            color = SocialTheme.colors.textPrimary
        )
    }
}
@Composable
fun GroupDisplaySettingContent(onCancel: () -> Unit={}) {
    Column(Modifier.clip(RoundedCornerShape(24.dp))) {
        ProfileDisplaySettingsItem(label="Share",icon=R.drawable.ic_share, textColor = SocialTheme.colors.textPrimary)
        ProfileDisplaySettingsItem(label="Add users",icon=R.drawable.ic_person_add , textColor = SocialTheme.colors.textPrimary)
        ProfileDisplaySettingsItem(label="Delete",icon=R.drawable.ic_delete , textColor = SocialTheme.colors.error)
        ProfileDisplaySettingsItem(label="Leave",icon=R.drawable.ic_logout , textColor = SocialTheme.colors.error)
        ProfileDisplaySettingsItem(label="Cancel" , turnOffIcon = true, textColor = SocialTheme.colors.textPrimary.copy(0.5f), onClick = onCancel)
    }
}