package com.example.friendupp.ActivityUi

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
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
import com.example.friendupp.ChatUi.convertUTCtoLocal
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.example.friendupp.Home.buttonsRow
import com.example.friendupp.Profile.TagDivider
import com.example.friendupp.TimeFormat.getFormattedDate
import com.example.friendupp.TimeFormat.getFormattedDateNoSeconds
import com.example.friendupp.model.Activity
import com.example.friendupp.model.UserData

sealed class ActivityEvents{
    class Expand(val activity:Activity):ActivityEvents()
    class Join(val id :String):ActivityEvents()
    class Leave(val id :String):ActivityEvents()
    class OpenChat(val id :String):ActivityEvents()
    class GoToProfile(val id :String):ActivityEvents()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun activityCard(
    title: String,
    description: String,
    creatorUsername: String,
    creatorFullName: String,
    profilePictureUrl: String,
    creatorId: String,
    expandButton:Boolean=true,
    onExpand:()->Unit,
    goToProfile:(String)->Unit,

) {

        Column(Modifier.background(SocialTheme.colors.uiBackground)) {
            Box(modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp, top = 6.dp)) {
                Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier =  Modifier.weight(1f).clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = Color.White),
                            onClick ={goToProfile(creatorId.toString()) })){
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(profilePictureUrl)
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(R.drawable.ic_launcher_background),
                                    contentDescription = "stringResource(R.string.description)",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column() {
                                    Text(
                                        text = creatorFullName,
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = Lexend,
                                            color = SocialTheme.colors.textPrimary
                                        )
                                    )
                                    Text(
                                        text = creatorUsername,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraLight,
                                            fontFamily = Lexend,
                                            color = SocialTheme.colors.textSecondary
                                        )
                                    )
                                }
                            }
                        }

                        if(expandButton){
                            IconButton(onClick = onExpand) {
                                Icon(painter = painterResource(id = R.drawable.ic_expand), contentDescription =null,tint=SocialTheme.colors.iconPrimary.copy(0.5f))

                            }
                        }

                        Spacer(modifier = Modifier.width(24.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Lexend,
                            color = SocialTheme.colors.textPrimary
                        )
                    )
                    Text(
                        text = description,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            fontFamily = Lexend,
                            color = SocialTheme.colors.textSecondary
                        )
                    )
                }

            }
        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun activityItem(
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

                if(!activity.image.isNullOrEmpty()){
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
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
                activityCard(
                    title = activity.title,
                    description = activity.description,
                    creatorUsername = activity.creator_username,
                    creatorFullName = activity.creator_name,
                    creatorId=activity.creator_id,
                    profilePictureUrl = activity.creator_profile_picture,
                    goToProfile = {onEvent(ActivityEvents.GoToProfile(it))},
                    onExpand= {

                        Log.d("ACTIVITYDEBUG","LAUNCH ")
                       onEvent( ActivityEvents.Expand(activity)) }
                )
                var joined=activity.participants_ids.contains(UserData.user!!.id)
                var switch by remember { mutableStateOf(joined) }

                buttonsRow(modifier = Modifier,onEvent=onEvent,id=activity.id,joined=switch,joinChanged={it->
                    switch=it
                },activity.participants_profile_pictures)

            }
        }
}





@Composable
fun TimeIndicator(time: String,tags:ArrayList<String>, color: Color = SocialTheme.colors.uiBorder,Divider:Boolean=true) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if(Divider) {
            Box(
                modifier = Modifier
                    .height(0.5.dp)
                    .width(36.dp)
                    .background(color)
            )
       }else{
            Spacer(modifier = Modifier.width(16.dp))

        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = convertUTCtoLocal(time, outputFormat = "yyyy-MM-dd HH:mm"),
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = SocialTheme.colors.uiBorder
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        TagDivider(tags=tags)
        Spacer(modifier = Modifier.width(12.dp))
    }
}
