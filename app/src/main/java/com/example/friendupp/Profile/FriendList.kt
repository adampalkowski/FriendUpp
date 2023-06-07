package com.example.friendupp.Profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
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
fun FriendListScreen(modifier: Modifier=Modifier,onEvent:(FriendListEvents)->Unit){
    Column(modifier=Modifier) {
        ScreenHeading(title = "Friend list", backButton = true, onBack = {onEvent(FriendListEvents.GoBack)})
        {}
        Modifier.height(32.dp)

        LazyColumn {
            items(5){
                FriendItem(username = "adamooo-5", name ="Adam Pałkowski" , pictureUrl = "https://images.unsplash.com/photo-1529665253569-6d01c0eaf7b6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cHJvZml" +
                        "sZXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60",onEvent=onEvent)
            }
        }

    }
}

@Composable
fun FriendItem(username:String,name:String,pictureUrl:String,onEvent: (FriendListEvents) -> Unit){
    var expand by remember { mutableStateOf(false) }
    val userID="123123123"
    BackHandler(true) {
        onEvent(FriendListEvents.GoBack)
    }
    Column() {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .clickable(onClick = { onEvent(FriendListEvents.ProfileDisplay(userID)) })
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
