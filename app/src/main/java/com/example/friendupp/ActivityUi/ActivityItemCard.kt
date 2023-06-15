package com.example.friendupp.ActivityUi

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Home.buttonsRow
import com.example.friendupp.model.Activity
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.SocialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun activityItemCard(
    activity: Activity,
    onClick:()->Unit,
    onEvent:(ActivityEvents)->Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp)
    ) {
        Column() {
            TimeIndicator(time = activity.start_time,tags=activity.tags)

            if(activity.image!=null){
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(activity.image)
                            .crossfade(true)
                            .build(),
                        contentDescription =null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
                            .heightIn(48.dp, 100.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 12.dp).clip(RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp)).border(
                BorderStroke(0.5.dp,SocialTheme.colors.uiBorder.copy(0.5f)), shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp)
            )){
                activityCard(
                    title = activity.title,
                    description = activity.description,
                    creatorUsername = activity.creator_username,
                    creatorFullName = activity.creator_name,
                    profilePictureUrl = activity.creator_profile_picture,
                    onExpand= {

                        Log.d("ACTIVITYDEBUG","LAUNCH ")
                        onEvent( ActivityEvents.Expand(activity)) }
                )
                buttonsRow(modifier = Modifier,onEvent=onEvent,id=activity.id,joined=activity.participants_ids.contains(
                    UserData.user!!.id))
            }


        }
    }
}