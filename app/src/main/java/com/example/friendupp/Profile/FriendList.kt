package com.example.friendupp.Profile

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.R
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.User
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
    object GetMoreFriends : FriendListEvents()
}

@Composable
fun FriendListScreen(
    modifier: Modifier = Modifier,
    onEvent: (FriendListEvents) -> Unit,
    friendList:List<User>,
    isLoading:Boolean
) {
    Column(modifier = modifier) {
        ScreenHeading(
            title = "Friend list",
            backButton = true,
            onBack = { onEvent(FriendListEvents.GoBack) })
        {}
        Modifier.height(32.dp)
        if(isLoading){
            CircularProgressIndicator()
        }else{
            LazyColumn {
                items(friendList) { user ->
                    FriendItem(
                        username = user.username.toString(),
                        name = user.name.toString(),
                        pictureUrl = user.pictureUrl.toString(),
                        onEvent = {onEvent(FriendListEvents.ProfileDisplay(it))},
                        user = user
                    )
                }
                item{
                    Spacer(modifier = Modifier.height(64.dp))
                }
                item{
                    onEvent(FriendListEvents.GetMoreFriends)
                }
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
    val moreGroupsFlow = chatViewModel.moreGroupsState.collectAsState()
    groupsFlow.value.let { response ->
        when (response) {
            is com.example.friendupp.model.Response.Success -> {
                groupList.clear()
                Log.d("GROUPDEBUG",response.data.size.toString())
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
                Log.d("GROUPDEBUG","2 "+response.data.size.toString())

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
    onEvent: (String) -> Unit,
) {

    Column() {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .clickable(
                    onClick = { onEvent(user.id) },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = Color.Black)
                )
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

                Icon(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = null,
                    tint = SocialTheme.colors.textPrimary.copy(0.8f)
                )

        }


    }

}
