package com.example.friendupp.bottomBar.ActivityUi

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.ChatUi.convertUTCtoLocal
import com.example.friendupp.Components.FriendUppDialog
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.example.friendupp.Home.buttonsRow
import com.example.friendupp.Profile.TagDivider
import com.example.friendupp.TimeFormat.getFormattedDate
import com.example.friendupp.TimeFormat.getFormattedDateNoSeconds
import com.example.friendupp.model.Activity
import com.example.friendupp.model.UserData
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

sealed class ActivityEvents {
    object GoBack : ActivityEvents()
    class Expand(val activity: Activity) : ActivityEvents()
    class Join(val activity: Activity) : ActivityEvents()
    class RemoveRequest(val activity: Activity) : ActivityEvents()
    class CreateRequest(val activity: Activity) : ActivityEvents()
    class Leave(val activity: Activity) : ActivityEvents()
    class Bookmark(val id: String) : ActivityEvents()
    class UnBookmark(val id: String) : ActivityEvents()
    class OpenChat(val id: String) : ActivityEvents()
    class GoToProfile(val id: String) : ActivityEvents()
    class OpenSettings(val id: String) : ActivityEvents()
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
    expandButton: Boolean = true,
    onExpand: () -> Unit,
    OpenSettings: () -> Unit = {},
    goToProfile: (String) -> Unit,
    confirmParticipation: Boolean = true,
) {
    Column(Modifier.background(SocialTheme.colors.uiBackground)) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp, top = 6.dp)
        ) {
            Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = Color.White),
                                onClick = { goToProfile(creatorId.toString()) })
                        ) {
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
                    if (confirmParticipation) {
                        IconButton(onClick = onExpand) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_more),
                                contentDescription = null,
                                tint = SocialTheme.colors.iconPrimary.copy(0.5f)
                            )

                        }
                    } else {
                        if (expandButton) {
                            IconButton(onClick = onExpand) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_expand),
                                    contentDescription = null,
                                    tint = SocialTheme.colors.iconPrimary.copy(0.5f)
                                )

                            }
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
    onClick: () -> Unit,
    onEvent: (ActivityEvents) -> Unit,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp)
    ) {
        Column() {
            TimeIndicator(
                time = activity.start_time,
                tags = activity.tags,
                requests = activity.requests_ids.size,
                participantConfirmation = activity.participantConfirmation,
                isCreator = activity.creator_id==UserData.user!!.id

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
            activityCard(
                title = activity.title,
                description = activity.description,
                creatorUsername = activity.creator_username,
                creatorFullName = activity.creator_name,
                creatorId = activity.creator_id,
                profilePictureUrl = activity.creator_profile_picture,
                goToProfile = { onEvent(ActivityEvents.GoToProfile(it)) },
                onExpand = {
                    Log.d("ACTIVITYDEBUG", "LAUNCH ")
                    onEvent(ActivityEvents.Expand(activity))
                },
                OpenSettings = {
                    Log.d("ACTIVITYDEBUG", "LAUNCH ")
                },
                confirmParticipation = activity.participantConfirmation && activity.creator_id != UserData.user!!.id
            )
            var joined =
                activity.participants_ids.contains(UserData.user!!.id) || activity.requests_ids.contains(
                    UserData.user!!.id
                )
            var switch by remember { mutableStateOf(joined) }
            var bookmarked = activity.bookmarked.contains(UserData.user!!.id)
            var bookmark by remember { mutableStateOf(bookmarked) }
            Log.d("Clickedc",activity.participantConfirmation.toString())
            Log.d("Clickedc",UserData.user!!.id.toString())
            Log.d("Clickedc",activity.creator_id)
            val cgeck=activity.participantConfirmation && activity.creator_id != UserData.user!!.id
            Log.d("Clickedc",cgeck.toString())

            buttonsRow(
                modifier = Modifier,
                onEvent = onEvent,
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


fun convertUTCtoLocal2(utcDate: String, outputFormat: String): String {
    val utcDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val utcDateWithoutSecondsFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    utcDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    utcDateWithoutSecondsFormat.timeZone = TimeZone.getTimeZone("UTC")

    val localDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
    localDateFormat.timeZone = TimeZone.getDefault()

    val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    try {
        utcCalendar.time = utcDateFormat.parse(utcDate)!!
    } catch (e: ParseException) {
        try {
            utcCalendar.time = utcDateWithoutSecondsFormat.parse(utcDate)!!
        } catch (e: ParseException) {
            return "Invalid date format"
        }
    }

    val currentCalendar = Calendar.getInstance()

    // Check if the date is today
    if (isSameDay(utcCalendar, currentCalendar)) {
        return localDateFormat.format(utcCalendar.time) // Display only time for today
    }

    // Check if the date is tomorrow
    val tomorrowCalendar = Calendar.getInstance()
    tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 1)
    if (isSameDay(utcCalendar, tomorrowCalendar)) {
        return "Tomorrow, " + localDateFormat.format(utcCalendar.time) // Display "Tomorrow" and time
    }

    // Check if the date is within the same month and year
    if (isSameMonthAndYear(utcCalendar, currentCalendar)) {
        return SimpleDateFormat(
            "d MMM, HH:mm",
            Locale.getDefault()
        ).format(utcCalendar.time) // Display day, month, and time
    }

    // Check if the date is within the same year
    if (isSameYear(utcCalendar, currentCalendar)) {
        return SimpleDateFormat(
            "d MMM",
            Locale.getDefault()
        ).format(utcCalendar.time) // Display day and month
    }

    return localDateFormat.format(utcCalendar.time) // Default case: display full date and time
}

// Helper function to check if two calendars represent the same day
private fun isSameDay(calendar1: Calendar, calendar2: Calendar): Boolean {
    return calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR) &&
            calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
}

// Helper function to check if two calendars represent the same month and year
private fun isSameMonthAndYear(calendar1: Calendar, calendar2: Calendar): Boolean {
    return calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
            calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
}

// Helper function to check if two calendars represent the same year
private fun isSameYear(calendar1: Calendar, calendar2: Calendar): Boolean {
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
}

@Composable
fun TimeIndicator(
    time: String,
    tags: ArrayList<String>,
    color: Color = SocialTheme.colors.uiBorder,
    Divider: Boolean = true,
    participantConfirmation: Boolean,
    isCreator: Boolean,
    requests: Int,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (Divider) {
            Box(
                modifier = Modifier
                    .height(0.5.dp)
                    .width(36.dp)
                    .background(color)
            )
        } else {
            Spacer(modifier = Modifier.width(16.dp))

        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = convertUTCtoLocal2(time, outputFormat = "yyyy-MM-dd HH:mm"),
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = SocialTheme.colors.iconPrimary.copy(0.7f)
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        TagDivider(tags = tags)
        if (participantConfirmation) {
            if (isCreator) {
                if (requests > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SocialTheme.colors.textInteractive)
                            .padding(4.dp), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = requests.toString(),
                            style = TextStyle(
                                fontFamily = Lexend,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
            }


        }

        Spacer(modifier = Modifier.width(12.dp))
    }
}
