package com.palkowski.friendupp.Map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.palkowski.friendupp.bottomBar.ActivityUi.TimeIndicator
import com.palkowski.friendupp.bottomBar.ActivityUi.activityCard
import com.palkowski.friendupp.Home.buttonsRow
import com.palkowski.friendupp.MapEvent
import com.palkowski.friendupp.bottomBar.ActivityUi.ActivityEvents
import com.palkowski.friendupp.model.Activity
import com.palkowski.friendupp.model.UserData
import com.palkowski.friendupp.ui.theme.SocialTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MapActivityItem(onClick: () -> Unit, activity: Activity, onEvent: (MapEvent) -> Unit, activityEvents: (ActivityEvents) -> Unit) {

    val context = LocalContext.current
    androidx.compose.material.Card(
        elevation = 10.dp,
        modifier = Modifier
            .widthIn(min = 150.dp, max = 350.dp)
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        onClick = {}, backgroundColor = SocialTheme.colors.uiBackground) {
        Column() {
            Spacer(modifier = Modifier.height(8.dp))
            TimeIndicator(
                time = activity.start_time,
                tags = activity.tags,
                requests = activity.requests.size,
                participantConfirmation = activity.participantConfirmation,
                isCreator = activity.creator_id==UserData.user!!.id,
                Divider=false
            )

            if (!activity.image.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(activity.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
                            .heightIn(48.dp, 100.dp)
                    )
                }
            }
            var switch by remember { mutableStateOf(false) }
            var bookmarked = activity.bookmarked.contains(UserData.user!!.id)
            var bookmark by remember { mutableStateOf(bookmarked) }

            activityCard(
                title = activity.title,
                description = activity.description,
                creatorUsername = activity.creator_username,
                creatorFullName = activity.creator_name,
                creatorId = activity.creator_id,
                profilePictureUrl = activity.creator_profile_picture,
                goToProfile = { onEvent(MapEvent.GoToProfile(it)) },
                onExpand = {

                    onEvent(MapEvent.PreviewActivity(activity))
                }
            )
            buttonsRow(
                modifier = Modifier,
                onEvent =activityEvents,
                id = activity.id,
                joined = switch,
                joinChanged = { it ->
                    switch = it
                },
                activity.participants_profile_pictures,
                bookmarked = bookmark,
                bookmarkedChanged = { bookmark = it },
                activity = activity,
                chatDisabled = activity.disableChat,
                confirmParticipation = activity.participantConfirmation && activity.creator_id != UserData.user!!.id
            )


        }
    }


}
