package com.example.friendupp.FriendPicker

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.friendupp.Categories.Category
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Create.CreateButton
import com.example.friendupp.Create.FriendPickerItem
import com.example.friendupp.Groups.SelectedUsersState
import com.example.friendupp.Profile.friendsLoading
import com.example.friendupp.Profile.groupsLoading
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Response
import com.example.friendupp.model.User
import com.example.friendupp.model.UserData

@Composable
fun FriendPickerScreen(
    modifier: Modifier,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    goBack: () -> Unit,
    selectedUsers: List<String>,
    onUserSelected: (String) -> Unit,
    onUserDeselected: (String) -> Unit,
    createActivity: () -> Unit,
) {
    val friendsList = remember { mutableStateListOf<User>() }
    val moreFriendsList = remember { mutableStateListOf<User>() }
    val groupList = remember { mutableStateListOf<Chat>() }
    val moreGroupList = remember { mutableStateListOf<Chat>() }
    val selectedList = rememberSaveable(saver = SelectedUsersState.Saver) {
        SelectedUsersState(mutableStateListOf())
    }
    val IconTint = SocialTheme.colors.textPrimary.copy(0.8f)
    friendsLoading(
        userViewModel = userViewModel,
        friendsList = friendsList,
        moreFriendsList = moreFriendsList
    )
    groupsLoading(
        chatViewModel = chatViewModel,
        groupList = groupList,
        moreGroupList = moreGroupList,
        id = UserData.user!!.id
    )
    var grayColor = SocialTheme.colors.uiBorder.copy(0.3f)
    val usersExist = remember { mutableStateOf(false) }

    var allFriends by remember { mutableStateOf(false) }
    BackHandler(true) {
        goBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            item {
                ScreenHeading(title = "Select users", backButton = true, onBack = goBack) {
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                pickerDivider(
                    title = "Groups",
                    icon = com.example.friendupp.R.drawable.ic_group_add,
                    iconTint = IconTint
                )
                GroupPicker(
                    selectedUsers = selectedUsers,
                    onUserSelected = onUserSelected,
                    onUserDeselected = onUserDeselected,
                    addGroupName = { selectedList.list.add(it) },
                    removeGroupName = {
                        selectedList.list
                            .remove(it)
                    },
                    groupList, moreGroupList,
                    chatViewModel = chatViewModel
                )
            }
            item {

            }
            item {
                pickerDivider(
                    title = "Friends",
                    icon = com.example.friendupp.R.drawable.ic_person_add,
                    iconTint = IconTint
                ) {
                    Text(
                        text = "All friends",
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp
                        ),
                        color = SocialTheme.colors.textPrimary.copy(0.8f)
                    )
                    Switch(
                        checked = allFriends,
                        onCheckedChange = { allFriends = !allFriends },
                        modifier = Modifier.padding(start = 16.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SocialTheme.colors.textInteractive,
                            checkedTrackColor = grayColor, uncheckedTrackColor = grayColor,
                            checkedIconColor = SocialTheme.colors.textInteractive,
                            uncheckedThumbColor = Color.White,
                            uncheckedIconColor = Color.White,
                            uncheckedBorderColor = grayColor,
                            checkedBorderColor = grayColor
                        ), thumbContent = {
                            AnimatedVisibility(visible = allFriends) {

                                Icon(
                                    painter = painterResource(id = com.example.friendupp.R.drawable.ic_done),
                                    tint = Color.White,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }

            }
            items(friendsList) { user ->
                FriendPickerItem(
                    id = user.id, username = user.username ?: "", onClick = {
                    },
                    imageUrl = user.pictureUrl ?: "",
                    onUserSelected = onUserSelected, onUserDeselected = onUserDeselected,
                    addUserName = {
                        selectedList.list.add(it)
                    }, removeUsername = { selectedList.list.remove(it) }
                )
            }
            items(moreFriendsList) { user ->
                FriendPickerItem(
                    id = user.id,
                    username = user.username ?: "",
                    onClick = {
                    },
                    imageUrl = user.pictureUrl ?: "",
                    onUserSelected = onUserSelected,
                    onUserDeselected = onUserDeselected,
                    addUserName = { selectedList.list.add(it) },
                    removeUsername = { selectedList.list.remove(it) }
                )

            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                LaunchedEffect(true) {
                    if (usersExist.value) {
                        userViewModel.getMoreFriends(UserData.user!!.id)
                    }
                }
            }
        }



        SelectedUsers(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedUsers = selectedList
        )
        Box(
            modifier = Modifier
                .padding(bottom = 48.dp, end = 24.dp)
                .align(Alignment.BottomEnd), contentAlignment = Alignment.Center
        ) {
            CreateButton("Create", createClicked = {
                createActivity()
            }, disabled = false)
        }
    }


}


@Composable
fun SelectedUsers(modifier: Modifier, selectedUsers: SelectedUsersState) {
    val list = remember { selectedUsers.list } // Make the list mutable using remember

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SocialTheme.colors.textInteractive)
            .padding(start = 24.dp)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row (verticalAlignment = Alignment.CenterVertically){

        LazyRow {
            items(list) { user ->
                val truncatedUsername = if (user.length > 15) {
                    user.take(15) + "..."
                } else {
                    user
                } // Limit the username to 15 letters

                Text(text = "$truncatedUsername, ", style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 12.sp), color = Color.White)
            }
        }
            Spacer(modifier = Modifier.weight(1f))
            ButtonAdd(onClick = { /*TODO*/ }, icon =R.drawable.ic_checkl)
        }


    }
}

@Composable
fun GroupPicker(
    selectedUsers: List<String>,
    onUserSelected: (String) -> Unit,
    onUserDeselected: (String) -> Unit,
    addGroupName: (String) -> Unit, removeGroupName: (String) -> Unit,
    groupList: MutableList<Chat>,
    moreGroupList: MutableList<Chat>,
    chatViewModel: ChatViewModel,
) {

    LazyRow {
        item {
            Spacer(modifier = Modifier.width(24.dp))
        }
        items(groupList) { group ->
            GroupPickerItem(
                onClick = {},
                groupName = group.name.toString(),
                groupPic = group.imageUrl.toString(),
                onUserDeselected = onUserDeselected,
                onUserSelected = onUserSelected,
                addGroupName = addGroupName,
                removeGroupName = removeGroupName

            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        items(moreGroupList) { group ->
            GroupPickerItem(
                onClick = {},
                groupName = group.name.toString(),
                groupPic = group.imageUrl.toString(),
                onUserDeselected = onUserDeselected, onUserSelected = onUserSelected,
                addGroupName = addGroupName, removeGroupName = removeGroupName

            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        item {
            Spacer(modifier = Modifier.width(24.dp))
        }
        item {
            LaunchedEffect(true) {
                /*called on init*/
                chatViewModel.getMoreGroups(UserData.user!!.id)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPickerItem(
    onClick: () -> Unit,
    groupPic: String,
    groupName: String,
    onUserSelected: (String) -> Unit,
    onUserDeselected: (String) -> Unit,
    addGroupName: (String) -> Unit,
    removeGroupName: (String) -> Unit,
) {
    var selected by rememberSaveable {
        mutableStateOf(false)
    }
    var textColor = if (selected) {
        Color.White
    } else {
        SocialTheme.colors.textPrimary.copy(0.8f)
    }
    val truncatedgroupName = if (groupName.length > 15) {
        groupName.take(15) + "..."
    } else {
        groupName
    }  // Limit the username to 30 letters
    var backgroundColor = if (selected) {
        SocialTheme.colors.textInteractive
    } else {
        SocialTheme.colors.uiBorder.copy(0.3f)
    }
    Card(
        shape = RoundedCornerShape(24.dp),
        onClick = {
            if (selected) {
                selected = !selected
                onUserDeselected(groupName)
                removeGroupName(groupName)
            } else {

                selected = !selected
                onUserSelected(groupName)
                addGroupName(groupName)
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = backgroundColor
        )
    ) {
        Row(
            Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(groupPic.isNullOrEmpty()){

            }else{
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(groupPic)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_group),
                    contentDescription = "stringResource(R.string.description)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = truncatedgroupName,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = textColor
            )
        }


    }
}

@Composable
fun pickerDivider(title: String, icon: Int, iconTint: Color, content: @Composable () -> Unit = {}) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(SocialTheme.colors.uiBorder)
        )
        Icon(painter = painterResource(id = icon), contentDescription = null, tint = iconTint)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(SocialTheme.colors.uiBorder)
        )
        Text(
            text = title,
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ),
            color = iconTint
        )
        Spacer(
            modifier = Modifier
                .width(64.dp)
                .height(1.dp)
                .background(SocialTheme.colors.uiBorder)
        )
        Spacer(Modifier.weight(1f))

        content()
    }
}