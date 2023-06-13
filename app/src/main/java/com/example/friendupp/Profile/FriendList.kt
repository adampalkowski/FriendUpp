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
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.User
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

sealed class FriendListEvents{
    object GoBack:FriendListEvents()
    object GoToAddFriends:FriendListEvents()
    object GoToChat:FriendListEvents()
    object Share:FriendListEvents()
    object RemoveFriend:FriendListEvents()
    class ProfileDisplay(val userId:String):FriendListEvents()
    object BlockFriend:FriendListEvents()
}

@Composable
fun FriendListScreen(modifier: Modifier=Modifier,onEvent:(FriendListEvents)->Unit,userViewModel:UserViewModel){
    val friendsList = remember { mutableStateListOf<User>() }
    friendsLoading(userViewModel,friendsList)
    Column(modifier=Modifier) {
        ScreenHeading(title = "Friend list", backButton = true, onBack = {onEvent(FriendListEvents.GoBack)})
        {}
        Modifier.height(32.dp)

        LazyColumn {
            items(friendsList){user->
                FriendItem(username =user.username.toString()
                    , name =user.name.toString()
                    , pictureUrl = user.pictureUrl.toString()
                    ,onEvent=onEvent,user=user)
            }
        }

    }

}
@Composable
fun friendsLoading(
    userViewModel: UserViewModel,
    friendsList: MutableList<User>
) {
    val friendsFlow =userViewModel.friendState.collectAsState()
    friendsFlow.value.let {
            response -> when(response){
        is com.example.friendupp.model.Response.Success->{
            friendsList.clear()
            friendsList.addAll(response.data)
        }
        is com.example.friendupp.model.Response.Loading->{
            CircularProgressIndicator()
        }
        is com.example.friendupp.model.Response.Failure->{
            Toast.makeText(LocalContext.current,"Failed to load in friends list ", Toast.LENGTH_SHORT).show()
        }
    }
    }
}
@Composable
fun FriendItem(username:String,name:String,pictureUrl:String,user:User,onEvent: (FriendListEvents) -> Unit){
    var expand by remember { mutableStateOf(false) }
    BackHandler(true) {
        onEvent(FriendListEvents.GoBack)
    }
    Column() {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .clickable(onClick = { onEvent(FriendListEvents.ProfileDisplay(user.id)) })
            .padding(horizontal = 24.dp, vertical = 8.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape))
            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 16.sp), color = SocialTheme.colors.textPrimary)
                Text(text = username, style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Normal, fontSize = 14.sp), color = SocialTheme.colors.textPrimary.copy(0.6f))
            }
            Box(modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(36.dp)
                .background(SocialTheme.colors.uiBorder.copy(0.2f)), contentAlignment = Alignment.Center){
                Icon(painter = painterResource(id =R.drawable.arrow_right), contentDescription =null, tint = SocialTheme.colors.textPrimary.copy(0.8f))
            }

        }


    }

}
