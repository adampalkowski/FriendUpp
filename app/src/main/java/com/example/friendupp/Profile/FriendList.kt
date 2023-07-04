package com.example.friendupp.Profile

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.camera.core.ImageProcessor.Response
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.R
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.User
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

sealed class FriendListEvents {
    object GoBack : FriendListEvents()
    object GoToAddFriends : FriendListEvents()
    object GoToChat : FriendListEvents()
    object Share : FriendListEvents()
    object RemoveFriend : FriendListEvents()
    class ProfileDisplay(val userId: String) : FriendListEvents()
    object BlockFriend : FriendListEvents()
}

@Composable
fun FriendListScreen(
    modifier: Modifier = Modifier,
    onEvent: (FriendListEvents) -> Unit,
    userViewModel: UserViewModel,
) {
    val friendsList = remember { mutableStateListOf<User>() }
    val moreFriendsList = remember { mutableStateListOf<User>() }
    friendsLoading(userViewModel, friendsList, moreFriendsList)
    Column(modifier = Modifier) {
        ScreenHeading(
            title = "Friend list",
            backButton = true,
            onBack = { onEvent(FriendListEvents.GoBack) })
        {}
        Modifier.height(32.dp)

        LazyColumn {
            items(friendsList) { user ->
                FriendItem(
                    username = user.username.toString(),
                    name = user.name.toString(),
                    pictureUrl = user.pictureUrl.toString(),
                    onEvent = onEvent,
                    user = user
                )
            }
            items(moreFriendsList) { user ->
                FriendItem(
                    username = user.username.toString(),
                    name = user.name.toString(),
                    pictureUrl = user.pictureUrl.toString(),
                    onEvent = onEvent,
                    user = user
                )
            }
        }

    }

}

@Composable
fun friendsLoading(
    userViewModel: UserViewModel,
    friendsList: MutableList<User>,
    moreFriendsList: MutableList<User>,
    ) {

    //call get activities only once
    val activitiesFetched = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = activitiesFetched.value) {
        if (!activitiesFetched.value) {
            userViewModel.getFriends(UserData.user!!.id)

            activitiesFetched.value = true
        }
    }
    val friendsFlow = userViewModel.friendState.collectAsState()
    val moreFriendsFlow = userViewModel.friendMoreState.collectAsState()
    friendsFlow.value.let { response ->
        when (response) {
            is com.example.friendupp.model.Response.Success -> {
                friendsList.clear()
                friendsList.addAll(response.data)
                userViewModel.resetFriendState()
            }
            is com.example.friendupp.model.Response.Loading -> {
                friendsList.clear()
                CircularProgressIndicator()
            }
            is com.example.friendupp.model.Response.Failure -> {
                friendsList.clear()
            }
            else -> {

            }
        }
    }
    moreFriendsFlow.value.let { response ->
        when (response) {
            is com.example.friendupp.model.Response.Success -> {
                moreFriendsList.clear()
                moreFriendsList.addAll(response.data)
                userViewModel.resetMoreFriends()

            }

            is com.example.friendupp.model.Response.Loading -> {
                moreFriendsList.clear()
                CircularProgressIndicator()
            }
            is com.example.friendupp.model.Response.Failure -> {
                moreFriendsList.clear()
            }
            else -> {

            }
        }
    }


}

@Composable
fun groupsLoading(
    chatViewModel: ChatViewModel,
    groupList: MutableList<Chat>,
    moreGroupList: MutableList<Chat>,
    id: String,
) {

    //call get activities only once
    val activitiesFetched = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = activitiesFetched.value) {
        if (!activitiesFetched.value) {
            chatViewModel.getGroups(id)
            activitiesFetched.value = true
        }
    }
    val groupsFlow = chatViewModel.groupsState.collectAsState()
    val moreGroupsFlow = chatViewModel.groupsState.collectAsState()
    groupsFlow.value.let { response ->
        when (response) {
            is com.example.friendupp.model.Response.Success -> {
                groupList.clear()
                groupList.addAll(response.data)
                chatViewModel.resetGroups()
            }
            is com.example.friendupp.model.Response.Loading -> {
                groupList.clear()
                CircularProgressIndicator()
            }
            is com.example.friendupp.model.Response.Failure -> {
                groupList.clear()
            }
            else -> {

            }
        }
    }
    moreGroupsFlow.value.let { response ->
        when (response) {
            is com.example.friendupp.model.Response.Success -> {
                moreGroupList.clear()
                moreGroupList.addAll(response.data)
                chatViewModel.resetMoreGroups()

            }

            is com.example.friendupp.model.Response.Loading -> {
                moreGroupList.clear()
                CircularProgressIndicator()
            }
            is com.example.friendupp.model.Response.Failure -> {
                moreGroupList.clear()
            }
            else -> {

            }
        }
    }
}

@Composable
fun FriendItem(
    username: String,
    name: String,
    pictureUrl: String,
    user: User,
    onEvent: (FriendListEvents) -> Unit,
) {
    var expand by remember { mutableStateOf(false) }
    BackHandler(true) {
        onEvent(FriendListEvents.GoBack)
    }
    Column() {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .clickable(onClick = { onEvent(FriendListEvents.ProfileDisplay(user.id)) })
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_profile_300),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = SocialTheme.colors.textPrimary
                )
                Text(
                    text = username,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = SocialTheme.colors.textPrimary.copy(0.6f)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .size(36.dp)
                    .background(SocialTheme.colors.uiBorder.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = null,
                    tint = SocialTheme.colors.textPrimary.copy(0.8f)
                )
            }

        }


    }

}
