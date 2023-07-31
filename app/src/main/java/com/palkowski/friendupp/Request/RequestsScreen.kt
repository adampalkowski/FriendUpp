package com.palkowski.friendupp.Request

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.palkowski.friendupp.Components.ScreenHeading
import com.palkowski.friendupp.R
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme


sealed class RequestsEvents{
    object GoBack:RequestsEvents()
    class GoToUserProfile(val id:String):RequestsEvents()
    object GetMoreParticipants:RequestsEvents()
    class AcceptRequest(val request: Request):RequestsEvents()
}


@Composable
fun RequestsScreen(modifier: Modifier, onEvent:(RequestsEvents)->Unit, participantsList:List<Request>, isLoading:Boolean){
    BackHandler(true) {
        onEvent(RequestsEvents.GoBack)
    }


    Column(modifier=modifier) {
        ScreenHeading(title = "Requests", onBack = {onEvent(RequestsEvents.GoBack)}, backButton = true) {}
        LazyColumn{
            items(participantsList){
                    request ->
                RequestItem(
                    username = request.username.toString(),
                    pictureUrl = request.profile_picture.toString(),
                    onEvent = { onEvent(RequestsEvents.GoToUserProfile(request.id)) },
                    name=request.name,
                    onAccept = {onEvent(RequestsEvents.AcceptRequest(request))}
                )
            }
            item {
                Spacer(modifier = Modifier.height(58.dp))

            }
            item {
                LaunchedEffect(Unit){
                    onEvent(RequestsEvents.GetMoreParticipants)
                }
            }
        }
    }




}

@Composable
fun RequestItem(
    username: String,
    name: String,
    pictureUrl: String,
    onEvent: () -> Unit,
    onAccept: () -> Unit,
) {
    val context = LocalContext.current
    Column() {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .clickable(
                    onClick = { onEvent() },
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

            Box(modifier = Modifier
                .clip(RoundedCornerShape(12.dp)).clickable(onClick = onAccept)
                .background(SocialTheme.colors.textInteractive).padding(horizontal = 16.dp, vertical = 8.dp)){
                Text(text = context.getString(R.string.accept), color = Color.White   , style = TextStyle(
                        fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ))
            }


        }


    }

}
