package com.example.friendupp.Map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.friendupp.ActivityUi.TimeIndicator
import com.example.friendupp.ActivityUi.activityCard
import com.example.friendupp.Home.buttonsRow
import com.example.friendupp.MapEvent
import com.example.friendupp.Profile.ProfileInfo
import com.example.friendupp.R
import com.example.friendupp.model.Activity
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapActivityItem(onClick: () -> Unit, activity: Activity, onEvent: (MapEvent) -> Unit) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .clickable(onClick = onClick)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                contentColor = SocialTheme.colors.uiBackground,
                containerColor = SocialTheme.colors.uiBackground
            ),
            onClick = onClick,
            modifier = Modifier.widthIn(min = 150.dp, max = 350.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column() {
                Spacer(modifier = Modifier.height(8.dp))
                TimeIndicator(time = activity.start_time, tags = activity.tags,Divider=false)

                if (activity.image != null) {
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
                activityCard(
                    title = activity.title,
                    description = activity.description,
                    creatorUsername = activity.creator_username,
                    creatorFullName = activity.creator_name,expandButton=false,
                    profilePictureUrl = activity.creator_profile_picture, onExpand = {
                        onEvent(MapEvent.PreviewActivity(activity))
                    })

            }
        }

    }


}
