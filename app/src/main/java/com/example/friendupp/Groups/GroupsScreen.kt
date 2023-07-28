package com.example.friendupp.Groups

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
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
import com.example.friendupp.ChatUi.ChatSettingItem
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.CreateHeading
import com.example.friendupp.Profile.*
import com.example.friendupp.R
import com.example.friendupp.model.*
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class GroupsEvents {
    object CreateGroup : GroupsEvents()
    object GetMoreGroupInvites : GroupsEvents()
    object GetMoreGroups : GroupsEvents()
    object GoBack : GroupsEvents()
    object GoToFriendPicker : GroupsEvents()
    class GoToGroupDisplay(val groupId: String) : GroupsEvents()
    class AcceptGroupInvite(val group: Chat) : GroupsEvents()
    class RemoveGroupInvite(val group: Chat) : GroupsEvents()
}


@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    onEvent: (GroupsEvents) -> Unit,
    groups: List<Chat>,
    groupsInvites: List<Chat>,
) {
    val focusRequester = remember { FocusRequester() }
    val usernameState by rememberSaveable(stateSaver = UsernameStateSaver) {
        mutableStateOf(UsernameState())
    }
    var displaySettings by remember { mutableStateOf(false) }
    BackHandler(true) {
        onEvent(GroupsEvents.GoBack)
    }
    val context = LocalContext.current
    LazyColumn(modifier = modifier) {

        item {
            ScreenHeading(
                title = "Groups",
                backButton = true,
                onBack = { onEvent(GroupsEvents.GoBack) }) {
                Row(Modifier) {
                    ButtonAdd(
                        onClick = { onEvent(GroupsEvents.CreateGroup) },
                        icon = R.drawable.ic_add
                    )
                }
            }
            CreateHeading(text = "Your groups", icon = R.drawable.ic_group, tip = false)
        }


        items(groups) { group ->
            GroupItem(
                groupname = group.name.toString(),
                description = group.description.toString(),
                groupPicture = group.imageUrl.toString(),
                numberOfUsers = group.members.size.toString(),
                isCreator = group.owner_id == UserData.user!!.id,
                onEvent = { event ->
                    when (event) {
                        is GroupItemEvent.GoToGroup -> {
                            Toast.makeText(context, group.id.toString(), Toast.LENGTH_SHORT)
                                .show()
                            onEvent(GroupsEvents.GoToGroupDisplay(group.id.toString()))
                        }
                        is GroupItemEvent.OpenGroupItemSettings -> {
                            displaySettings = true

                        }
                        else -> {}
                    }


                }, groupOwner = group.owner_id
            )
        }

        item {
            LaunchedEffect(true) {
                /*called on init*/
                onEvent(GroupsEvents.GetMoreGroups)
            }
        }
        item {
            CreateHeading(text = "Group invites", icon = R.drawable.ic_invites, tip = false)

        }
        items(groupsInvites) { group ->
            GroupInvitesItem(groupname = group.name.toString(),
                description = group.description.toString(),
                groupPicture = group.imageUrl.toString(),
                numberOfUsers = group.members.size.toString(),
                isCreator = group.owner_id == UserData.user!!.id,
                onEvent = { event ->
                    when (event) {
                        is GroupItemEvent.GoToGroup -> {
                            Toast.makeText(context, group.id.toString(), Toast.LENGTH_SHORT)
                                .show()
                            onEvent(GroupsEvents.GoToGroupDisplay(group.id.toString()))
                        }
                        is GroupItemEvent.OpenGroupItemSettings -> {
                            displaySettings = true

                        }
                        else -> {}
                    }


                }, onAccept = { onEvent(GroupsEvents.AcceptGroupInvite(group)) }, onRemove = {
                    onEvent(GroupsEvents.RemoveGroupInvite(group))

                })
        }

        item {
            LaunchedEffect(true) {
                /*called on init*/
                onEvent(GroupsEvents.GetMoreGroupInvites)

            }
        }
    }
    AnimatedVisibility(visible = displaySettings) {
        Dialog(onDismissRequest = { displaySettings = false }) {
            GroupDisplaySettingContent(onCancel = { displaySettings = false }, addUsers = {

                displaySettings = false
            })
        }
    }
}


sealed class GroupItemEvent {
    object GoBack : GroupItemEvent()
    object GoToAddFriends : GroupItemEvent()
    object GoToChat : GroupItemEvent()
    object Share : GroupItemEvent()
    object RemoveFriend : GroupItemEvent()
    object BlockFriend : GroupItemEvent()
    object GoToGroup : GroupItemEvent()
    object OpenGroupItemSettings : GroupItemEvent()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupItem(
    groupname: String,
    description: String,
    numberOfUsers: String,
    groupPicture: String,
    isCreator: Boolean,
    onEvent: (GroupItemEvent) -> Unit,
    groupOwner: String,
) {
    var expand by remember { mutableStateOf(false) }

    Column() {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .combinedClickable(
                    onClick = { onEvent(GroupItemEvent.GoToGroup) },
                    onLongClick = { onEvent(GroupItemEvent.OpenGroupItemSettings) },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = Color.Black)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            if (groupPicture.isEmpty()) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_group),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(32.dp),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconPrimary
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(groupPicture)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_group),
                    contentDescription = "stringResource(R.string.description)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = groupname,
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = SocialTheme.colors.textPrimary
                    )
                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            style = TextStyle(
                                fontFamily = Lexend,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            ),
                            color = SocialTheme.colors.textPrimary.copy(0.6f)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (groupOwner == UserData.user!!.id) {
                        Text(
                            text = "Creator",
                            style = TextStyle(
                                fontFamily = Lexend,
                                fontWeight = FontWeight.Light,
                                fontSize = 12.sp
                            ),
                            color = SocialTheme.colors.textPrimary
                        )

                    }
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_right),
                        contentDescription = null,
                        tint = SocialTheme.colors.textPrimary.copy(0.8f)
                    )
                }


            }

            AnimatedVisibility(visible = expand) {
                Column() {

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (isCreator) {
                            ChatSettingItem(
                                label = "Chat",
                                icon = R.drawable.ic_chat_300,
                                onClick = {
                                })
                            ChatSettingItem(label = "Share", icon = R.drawable.ic_share, onClick = {
                            })

                            ChatSettingItem(
                                label = "Leave",
                                icon = R.drawable.ic_logout,
                                onClick = {
                                })
                            ChatSettingItem(
                                label = "Delete",
                                icon = R.drawable.ic_delete,
                                onClick = {
                                })
                            ChatSettingItem(
                                label = "Add users",
                                icon = R.drawable.ic_group_add,
                                onClick = {
                                })

                        } else {
                            ChatSettingItem(
                                label = "Chat",
                                icon = R.drawable.ic_chat_300,
                                onClick = {
                                })
                            ChatSettingItem(label = "Share", icon = R.drawable.ic_share, onClick = {
                            })

                            ChatSettingItem(
                                label = "Leave",
                                icon = R.drawable.ic_logout,
                                onClick = {
                                })

                        }

                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(SocialTheme.colors.uiBorder)
                    )
                }

            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupInvitesItem(
    groupname: String,
    description: String,
    numberOfUsers: String,
    groupPicture: String,
    isCreator: Boolean,
    onEvent: (GroupItemEvent) -> Unit,
    onAccept: () -> Unit,
    onRemove: () -> Unit,
) {
    var expand by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column() {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .combinedClickable(
                    onClick = { onEvent(GroupItemEvent.GoToGroup) },
                    onLongClick = { onEvent(GroupItemEvent.OpenGroupItemSettings) },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = Color.Black)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            if (groupPicture.isEmpty()) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_group),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(32.dp),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconPrimary
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(groupPicture)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_group),
                    contentDescription = "stringResource(R.string.description)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = groupname,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = SocialTheme.colors.textPrimary
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        color = SocialTheme.colors.textPrimary.copy(0.6f)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onAccept)
                    .background(SocialTheme.colors.textInteractive)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = context.getString(R.string.accept),
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_x),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconPrimary
                )
            }
        }

        AnimatedVisibility(visible = expand) {
            Column() {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (isCreator) {
                        ChatSettingItem(
                            label = "Chat",
                            icon = R.drawable.ic_chat_300,
                            onClick = {
                            })
                        ChatSettingItem(label = "Share", icon = R.drawable.ic_share, onClick = {
                        })

                        ChatSettingItem(
                            label = "Leave",
                            icon = R.drawable.ic_logout,
                            onClick = {
                            })
                        ChatSettingItem(
                            label = "Delete",
                            icon = R.drawable.ic_delete,
                            onClick = {
                            })
                        ChatSettingItem(
                            label = "Add users",
                            icon = R.drawable.ic_group_add,
                            onClick = {
                            })

                    } else {
                        ChatSettingItem(
                            label = "Chat",
                            icon = R.drawable.ic_chat_300,
                            onClick = {
                            })
                        ChatSettingItem(label = "Share", icon = R.drawable.ic_share, onClick = {
                        })

                        ChatSettingItem(
                            label = "Leave",
                            icon = R.drawable.ic_logout,
                            onClick = {
                            })

                    }

                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(SocialTheme.colors.uiBorder)
                )
            }

        }
    }

}
