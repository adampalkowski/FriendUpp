package com.example.friendupp.Groups

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Components.FriendUppDialog
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Profile.ProfileDisplaySettingsItem
import com.example.friendupp.Profile.Stat
import com.example.friendupp.R
import com.example.friendupp.Settings.SettingsItem
import com.example.friendupp.bottomBar.ActivityUi.ChangeDescriptionDialog
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class GroupDisplayEvents {
    object GoToSearch : GroupDisplayEvents()
    class GoToMembers(val id :String) : GroupDisplayEvents()
    object GoBack : GroupDisplayEvents()
    object GoToSettings : GroupDisplayEvents()
    object GoToEditProfile : GroupDisplayEvents()
    class AddUsers(val id: String): GroupDisplayEvents()
    class ShareGroupLink(val id: String) : GroupDisplayEvents()
    class ChangeImage(val id: String) : GroupDisplayEvents()
    class LeaveGroup(val chat: Chat) : GroupDisplayEvents()
    class DeleteGroup(val chat: Chat) : GroupDisplayEvents()
    class ChangeGroupName(val chat: Chat,val name:String) : GroupDisplayEvents()
    class ReportGroup(val chat: Chat) : GroupDisplayEvents()

    /*todo*/
    object GoToFriendList : GroupDisplayEvents()
    object GoToGroupCreate : GroupDisplayEvents()
    object GetProfileLink : GroupDisplayEvents()
}

@Composable
fun GroupDisplayScreen(
    modifier: Modifier = Modifier,
    onEvent: (GroupDisplayEvents) -> Unit,
    chatResponse:Response<Chat>?,
    context: Context
) {

    // Handle null safety and loading state
    when(chatResponse) {
        is Response.Success->{

            // The data has been successfully fetched, and group is not null
            GroupDisplayContent(modifier=modifier,group = chatResponse.data, onEvent = onEvent)
        }
        is Response.Loading->{
            // Show a loading indicator while data is being fetched
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is Response.Failure->{
            // Show an error message or navigate back on failure
            Toast.makeText(
                context,
                "Failed to load chat. Please try again later.",
                Toast.LENGTH_LONG
            ).show()
            // You can also navigate back using the onEvent callback
            onEvent(GroupDisplayEvents.GoBack)
        }
        else->{
            // Show a loading indicator or some placeholder content
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

    }
}
@Composable
fun GroupDisplayContent(
    modifier: Modifier,
    onEvent: (GroupDisplayEvents) -> Unit,
    group: Chat,
) {

    var openReportDialog by remember { mutableStateOf(false) }
    var openLeaveDialog by remember { mutableStateOf(false) }
    var openDeleteDialog by remember { mutableStateOf(false) }
    var openChangeNameDialog by remember { mutableStateOf(false) }
    LazyColumn {
        item {
            Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                ScreenHeading(
                    title = "Group",
                    backButton = true,
                    onBack = { onEvent(GroupDisplayEvents.GoBack) }) {
                    Spacer(modifier = Modifier.weight(1f))
                    /*
                    ButtonAdd(icon = R.drawable.ic_more, onClick = {
                        displaySettings = true
                    })*/
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
                    onEvent(GroupDisplayEvents.AddUsers(group.id.toString()))
                }

                SettingsItem(label = "Change group image", icon = R.drawable.ic_add_image) {
                    onEvent(GroupDisplayEvents.ChangeImage(group.id.toString()))
                }
                SettingsItem(label = "Change group name", icon = R.drawable.ic_edit) {
                    openChangeNameDialog = true

                }

                SettingsItem(label = "Share group", icon = R.drawable.ic_share) {
                    onEvent(GroupDisplayEvents.ShareGroupLink(group.id.toString()))
                }

                if (group.owner_id==UserData.user!!.id) {
                    SettingsItem(label = "Delete group", icon = R.drawable.ic_delete) {
                        openLeaveDialog = true

                    }
                }else{
                    SettingsItem(label = "Leave group", icon = R.drawable.ic_logout) {
                    }
                }

                SettingsItem(label = "Report", icon = R.drawable.ic_flag) {
                    openReportDialog = true

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

    if (openDeleteDialog) {
        FriendUppDialog(
            label = "Delete the group? Group chat will also be deleted. This action is irreversible and will lose all the group information. ",
            icon = R.drawable.ic_delete,
            onCancel = { openDeleteDialog = false },
            onConfirm = {
                onEvent(GroupDisplayEvents.DeleteGroup(group))
                openDeleteDialog=false
            }, confirmLabel = "Delete"
        )
    }
    if (openChangeNameDialog) {
        ChangeDescriptionDialog(
            label = "Change group name.",
            icon = R.drawable.ic_edit,
            onCancel = { openChangeNameDialog = false },
            onConfirm = { name ->
                onEvent(GroupDisplayEvents.ChangeGroupName(group,name))
                openChangeNameDialog=false

            },
            confirmTextColor = SocialTheme.colors.textInteractive,
            disableConfirmButton = false, editTextLabel = "Group name"
        )
    }
    if (openReportDialog) {
        FriendUppDialog(
            label = "If the group contains any violations, inappropriate content, or any other concerns that violate community guidelines, please report it.",
            icon = R.drawable.ic_flag,
            onCancel = { openReportDialog = false },
            onConfirm = {
                onEvent(GroupDisplayEvents.ReportGroup(group))
                openReportDialog=false

            }, confirmLabel = "Report"
        )
    }
    if (openLeaveDialog) {
        FriendUppDialog(
            label = "Leave group? You will be able to rejoin it if somone invites you again.",
            icon = R.drawable.ic_logout,
            onCancel = { openDeleteDialog = false },
            onConfirm = {
                onEvent(GroupDisplayEvents.LeaveGroup(chat = group))

                openLeaveDialog=false

            }, confirmLabel = "Leave group", confirmTextColor = SocialTheme.colors.error
        )
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