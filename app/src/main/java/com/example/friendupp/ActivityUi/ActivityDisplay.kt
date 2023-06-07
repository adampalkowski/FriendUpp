package com.example.friendupp.ActivityUi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.example.friendupp.Home.buttonsRow
import com.example.friendupp.model.Activity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun activityCard(
    title: String,
    description: String,
    creatorUsername: String,
    creatorFullName: String,
    profilePictureUrl: String,onExpand:()->Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            contentColor = Color.Transparent,
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(0.dp),

    ) {
        Column() {
            Box(modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp, top = 6.dp)) {
                Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
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
                        Column(Modifier.weight(1f)) {
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
                        IconButton(onClick = onExpand) {
                            Icon(painter = painterResource(id = R.drawable.ic_expand), contentDescription =null,tint=SocialTheme.colors.iconPrimary.copy(0.5f))

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun activityItem(
    activity: Activity,
    onClick:()->Unit,
    onExpand:(Activity)->Unit,
) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)

        ) {
            Column() {
                TimeIndicator(time = activity.start_time)

                if(activity.image!=null){
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
                    profilePictureUrl = activity.creator_profile_picture,
                    onExpand= { onExpand(activity) }
                )
                buttonsRow(modifier = Modifier)

            }
        }
    }





@Composable
fun TimeIndicator(time: String, color: Color = Color(0xFFA0A0A0)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .height(0.5.dp)
                .width(36.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = time,
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = color
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {}) {
            Icon(
                modifier=Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_expand),
                contentDescription = null,
                tint =SocialTheme.colors.uiBorder.copy(0.5f)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))
    }
}
