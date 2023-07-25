package com.example.friendupp.Groups

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.friendupp.bottomBar.ActivityUi.activityItem
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.ChatUi.ChatSettingsEvents
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Profile.*
import com.example.friendupp.R
import com.example.friendupp.Settings.SettingsItem
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Chat

import com.example.friendupp.model.User
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class GroupDisplayEvents {
    object GoToSearch : GroupDisplayEvents()
    class GoToMembers(val id :String) : GroupDisplayEvents()
    object GoBack : GroupDisplayEvents()
    object GoToSettings : GroupDisplayEvents()
    object GoToEditProfile : GroupDisplayEvents()
    object AddUsers : GroupDisplayEvents()
    class ShareGroupLink(val id: String) : GroupDisplayEvents()
    class LeaveGroup(val id: String) : GroupDisplayEvents()
    class DeleteGroup(val id: String) : GroupDisplayEvents()
    class ChangeGroupName(val id: String) : GroupDisplayEvents()
    class ReportGroup(val id: String) : GroupDisplayEvents()

    /*todo*/
    object GoToFriendList : GroupDisplayEvents()
    object GoToGroupCreate : GroupDisplayEvents()
    object GetProfileLink : GroupDisplayEvents()
}


@Composable
fun GroupDisplayScreen(
    modifier: Modifier,
    onEvent: (GroupDisplayEvents) -> Unit,
    onClick: () -> Unit = {},
    group: Chat,
) {

    var displaySettings by rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(true) {
        onEvent(GroupDisplayEvents.GoBack)
    }

    LazyColumn {
        item {
            Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                ScreenHeading(
                    title = "Group",
                    backButton = true,
                    onBack = { onEvent(GroupDisplayEvents.GoBack) }) {
                    Spacer(modifier = Modifier.weight(1f))
                    ButtonAdd(icon = R.drawable.ic_more, onClick = {
                        displaySettings = true
                    })
                }
                Spacer(modifier = Modifier.height(12.dp))
                GroupInfo(
                    name = group.name ?: "",
                    imageUrl = group.imageUrl ?: "",
                    description = group.description ?: "",
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsItem(label = group.members.size.toString() +" users", icon = R.drawable.ic_group) {
                    onEvent(GroupDisplayEvents.GoToMembers(group.id!!))
                }
                SettingsItem(label = "Add users", icon = R.drawable.ic_group_add) {
                    onEvent(GroupDisplayEvents.AddUsers)
                }

                SettingsItem(label = "Change group image", icon = R.drawable.ic_add_image) {

                }
                SettingsItem(label = "Change group name", icon = R.drawable.ic_edit) {
                    onEvent(GroupDisplayEvents.ChangeGroupName(group.id.toString()))
                }

                SettingsItem(label = "Share group", icon = R.drawable.ic_share) {
                    onEvent(GroupDisplayEvents.ShareGroupLink(group.id.toString()))
                }

                if (group.owner_id==UserData.user!!.id) {
                    SettingsItem(label = "Delete group", icon = R.drawable.ic_logout) {
                        onEvent(GroupDisplayEvents.DeleteGroup(group.id!!))
                    }
                }else{
                    SettingsItem(label = "Leave group", icon = R.drawable.ic_logout) {
                        onEvent(GroupDisplayEvents.LeaveGroup(UserData.user!!.id))
                    }
                }

                SettingsItem(label = "Report", icon = R.drawable.ic_flag) {
                    onEvent(GroupDisplayEvents.ReportGroup(UserData.user!!.id))

                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Created on-"+group.create_date,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Light
                    ),
                    color = SocialTheme.colors.iconPrimary
                )
            }


        }
    }


}

@Composable
fun GroupStats(modifier: Modifier, numberOfUsers: Int, numberOfActivities: Int) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        Stat(value = numberOfUsers.toString(), label = "Users", onClick = {})
        Stat(value = numberOfActivities.toString(), label = "Activities", onClick = {})
    }
}

@Composable
fun GroupInfo(name: String, imageUrl: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageUrl.isNullOrEmpty() ){
            Box(modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(SocialTheme.colors.textPrimary.copy(0.8f)), contentAlignment = Alignment.Center){

                Icon(painter = painterResource(id = R.drawable.ic_add_image), contentDescription =null, tint = SocialTheme.colors.textSecondary )
            }
        }else{
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_group),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
        }


        val truncname = if (name.length > 30) name.substring(0, 30) + "..." else name
        Text(
            text = truncname,
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            color = SocialTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        val truncatedDescription =
            if (description.length > 500) description.substring(0, 500) + "..." else description
        Text(
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
fun GroupDisplaySettingContent(
    onCancel: () -> Unit = {},
    addUsers: () -> Unit = {},
    creator: Boolean = false,
    shareGroup: () -> Unit = {},
    deleteGroup: () -> Unit = {},
    leaveGroup: () -> Unit = {},
) {
    Column(Modifier.clip(RoundedCornerShape(24.dp))) {
        ProfileDisplaySettingsItem(
            label = "Share",
            icon = R.drawable.ic_share,
            textColor = SocialTheme.colors.textPrimary,
            onClick = shareGroup
        )
        ProfileDisplaySettingsItem(
            label = "Add users",
            icon = R.drawable.ic_person_add,
            textColor = SocialTheme.colors.textPrimary,
            onClick = addUsers
        )
        if (creator) {
            ProfileDisplaySettingsItem(
                label = "Delete",
                icon = R.drawable.ic_delete,
                textColor = SocialTheme.colors.error,
                onClick = deleteGroup
            )
        } else {
            ProfileDisplaySettingsItem(
                label = "Leave",
                icon = R.drawable.ic_logout,
                textColor = SocialTheme.colors.error,
                onClick = leaveGroup
            )

        }
        ProfileDisplaySettingsItem(
            label = "Cancel",
            turnOffIcon = true,
            textColor = SocialTheme.colors.textPrimary.copy(0.5f),
            onClick = onCancel
        )
    }
}