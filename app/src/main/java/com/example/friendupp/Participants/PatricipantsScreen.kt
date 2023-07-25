package com.example.friendupp.Participants

import androidx.activity.compose.BackHandler
import androidx.camera.core.ImageProcessor.Response
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.R
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
import com.example.friendupp.ChatUi.ChatSettingsEvents
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.MapEvent
import com.example.friendupp.Profile.FriendItem
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Participant
import com.example.friendupp.model.User
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

sealed class ParticipantsEvents{
    object GoBack:ParticipantsEvents()
    class GoToUserProfile(val id:String):ParticipantsEvents()
    object GetMoreParticipants:ParticipantsEvents()
}


@Composable
fun ParticipantsScreen(modifier:Modifier,onEvent:(ParticipantsEvents)->Unit ,participantsList:List<Participant>,isLoading:Boolean){
    BackHandler(true) {
        onEvent(ParticipantsEvents.GoBack)
    }


    Column(modifier=modifier) {
        ScreenHeading(title = "Participants", onBack = {onEvent(ParticipantsEvents.GoBack)}, backButton = true) {}
        LazyColumn{
            items(participantsList){
                    participant ->
                ParticipantItem(
                    username = participant.username.toString(),
                    pictureUrl = participant.profile_picture.toString(),
                    onEvent = { onEvent(ParticipantsEvents.GoToUserProfile(participant.id)) },
                    name=participant.name
                )
            }
            item {
                Spacer(modifier = Modifier.height(58.dp))

            }
            item {
                LaunchedEffect(Unit){
                       onEvent(ParticipantsEvents.GetMoreParticipants)
               }
             }
            }
        }

}


@Composable
fun ParticipantItem(
    username: String,
    name: String,
    pictureUrl: String,
    onEvent: () -> Unit,
) {

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
                placeholder = painterResource(com.example.friendupp.R.drawable.ic_profile_300),
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
                painter = painterResource(id = com.example.friendupp.R.drawable.arrow_right),
                contentDescription = null,
                tint = SocialTheme.colors.textPrimary.copy(0.8f)
            )

        }


    }

}
