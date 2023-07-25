package com.example.friendupp.Groups

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.bottomBar.ActivityUi.activityItem
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Profile.*
import com.example.friendupp.R
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Chat

import com.example.friendupp.model.User
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class GroupDisplayEvents {
    object GoToSearch : GroupDisplayEvents()
    object GoBack : GroupDisplayEvents()
    object GoToSettings : GroupDisplayEvents()
    object GoToEditProfile : GroupDisplayEvents()
    object AddUsers : GroupDisplayEvents()
    class ShareGroupLink(val id: String) : GroupDisplayEvents()
    class LeaveGroup(val id: String) : GroupDisplayEvents()

    /*todo*/
    object GoToFriendList : GroupDisplayEvents()
    object GoToGroupCreate : GroupDisplayEvents()
    object GetProfileLink : GroupDisplayEvents()
}


@Composable
fun GroupDisplayScreen(
    modifier: Modifier,
    onEvent: (GroupDisplayEvents) -> Unit,
    onClick: () -> Unit = {},
    activityViewModel: ActivityViewModel,
    group: Chat,
) {
    /*var selectedItem by remember { mutableStateOf("Upcoming") }
    var ifCalendar by remember { mutableStateOf(true) }
    var ifHistory by remember { mutableStateOf(false) }
    var joinedActivitiesExist= remember { mutableStateOf(false) }
    var historyActivitiesExist= remember { mutableStateOf(false) }

    //LOAD IN PROFILE ACTIVITIES

    val activitiesHistory = remember { mutableStateListOf<Activity>() }
    val moreHistoryActivities = remember { mutableStateListOf<Activity>() }


    val joinedActivities = remember { mutableStateListOf<Activity>() }
    val moreJoinedActivities = remember { mutableStateListOf<Activity>() }

    if(selectedItem=="Upcoming"){

        loadJoinedActivities(activityViewModel,joinedActivities,chat.id)
        loadMoreJoinedActivities(activityViewModel,moreJoinedActivities)


    }else{
        loadActivitiesHistory(activityViewModel,activitiesHistory,chat.id)
        loadMoreActivitiesHistory(activityViewModel,moreHistoryActivities)
    }
*/
    var displaySettings by rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(true) {
        onEvent(GroupDisplayEvents.GoBack)
    }

    LazyColumn {
        item {
            Column(modifier) {
                ScreenHeading(
                    title = "Group",
                    backButton = true,
                    onBack = { onEvent(GroupDisplayEvents.GoBack) }) {
                    Spacer(modifier = Modifier.weight(1f))
                    ButtonAdd(icon = R.drawable.ic_more, onClick = {
                        displaySettings = true
                    })
                }
                Spacer(modifier = Modifier.height(12.dp))
                GroupInfo(
                    name = group.name ?: "",
                    imageUrl = group.imageUrl ?: "",
                    description = group.description ?: ""
                )
                Spacer(modifier = Modifier.height(12.dp))
                GroupStats(
                    modifier = Modifier.fillMaxWidth(),
                    group.numberOfUsers,
                    group.numberOfActivities,
                )

            }
        }
        /* item {      Column (Modifier.fillMaxSize()){
             Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                 ProfilePickerItem(
                     label = "Upcoming",
                     icon = R.drawable.ic_calendar_upcoming,
                     selected = selectedItem == "Upcoming",
                     onItemSelected = {
                         selectedItem = "Upcoming"
                         ifCalendar = true
                         ifHistory = false
                     }
                 )
                 ProfilePickerItem(
                     label = "History",
                     icon = R.drawable.ic_history,
                     selected = selectedItem == "History",
                     onItemSelected = {
                         selectedItem = "History"
                         ifCalendar = false
                         ifHistory = true
                     }
                 )
             }
             Spacer(modifier = Modifier
                 .fillMaxWidth()
                 .height(1.dp)
                 .background(SocialTheme.colors.uiBorder))

         }
         }
         if(ifCalendar){
             items(joinedActivities) { activity ->
                 activityItem(
                     activity,
                     onClick = {
                         // Handle click event
                     },
                     onEvent = { event->
                         handleActivityEvent(event, onEvent = onEvent)
                     }
                 )
             }
             items(moreJoinedActivities) { activity ->
                 activityItem(
                     activity,
                     onClick = {
                         // Handle click event
                     },
                     onEvent = { event->
                         handleActivityEvent(event, onEvent = onEvent)
                     }
                 )
             }
             item {
                 Spacer(modifier = Modifier.height(64.dp))

             }
             item {
                 LaunchedEffect(true) {
                     if (joinedActivitiesExist.value) {
                         activityViewModel.getMoreJoinedActivities(UserData.user!!.id)
                     }
                 }
             }
         }
         if(ifHistory){
             items(activitiesHistory) { activity ->
                 activityItem(
                     activity,
                     onClick = {
                         // Handle click event
                     },
                     onEvent = { event->
                         handleActivityEvent(event, onEvent = onEvent)
                     }
                 )
             }

             items(moreHistoryActivities) { activity ->
                 activityItem(
                     activity,
                     onClick = {
                         // Handle click event
                     },
                     onEvent = { event->
                         handleActivityEvent(event, onEvent = onEvent)
                     }
                 )
             }
             item {
                 Spacer(modifier = Modifier.height(64.dp))

             }
             item {
                 LaunchedEffect(true) {
                     if (historyActivitiesExist.value) {
                         activityViewModel.getMoreUserActivities(UserData.user!!.id)
                     }
                 }
             }
         }
 */


    }

    AnimatedVisibility(visible = displaySettings) {
        Dialog(onDismissRequest = { displaySettings = false }) {
            GroupDisplaySettingContent(onCancel = { displaySettings = false }, addUsers = {
                onEvent(GroupDisplayEvents.AddUsers)
                displaySettings = false
            }, leaveGroup = {
                            onEvent(GroupDisplayEvents.LeaveGroup(UserData.user!!.id))
                displaySettings = false
            }, deleteGroup = {}, shareGroup = {
                onEvent(GroupDisplayEvents.ShareGroupLink(group.id.toString()))
                displaySettings = false
            }

            )
        }
    }

}

@Composable
fun GroupStats(modifier: Modifier, numberOfUsers: Int, numberOfActivities: Int) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        Stat(value = numberOfUsers.toString(), label = "Users", onClick = {})
        Stat(value = numberOfActivities.toString(), label = "Activities", onClick = {})
    }
}

@Composable
fun GroupInfo(name: String, imageUrl: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageUrl.isNullOrEmpty() ){
            Box(modifier = Modifier
                .size(90.dp)
                .clip(CircleShape).background(SocialTheme.colors.textPrimary.copy(0.8f)), contentAlignment = Alignment.Center){

                Icon(painter = painterResource(id = R.drawable.ic_add_image), contentDescription =null, tint = SocialTheme.colors.textSecondary )
            }
        }else{
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_group),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
        }


        val truncname = if (name.length > 30) name.substring(0, 30) + "..." else name
        Text(
            text = truncname,
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            color = SocialTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        val truncatedDescription =
            if (description.length > 500) description.substring(0, 500) + "..." else description
        Text(
            text = truncatedDescription,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold
            ),
            color = SocialTheme.colors.textPrimary
        )
    }
}

@Composable
fun GroupDisplaySettingContent(
    onCancel: () -> Unit = {},
    addUsers: () -> Unit = {},
    creator: Boolean = false,
    shareGroup: () -> Unit = {},
    deleteGroup: () -> Unit = {},
    leaveGroup: () -> Unit = {},
) {
    Column(Modifier.clip(RoundedCornerShape(24.dp))) {
        ProfileDisplaySettingsItem(
            label = "Share",
            icon = R.drawable.ic_share,
            textColor = SocialTheme.colors.textPrimary,
            onClick = shareGroup
        )
        ProfileDisplaySettingsItem(
            label = "Add users",
            icon = R.drawable.ic_person_add,
            textColor = SocialTheme.colors.textPrimary,
            onClick = addUsers
        )
        if (creator) {
            ProfileDisplaySettingsItem(
                label = "Delete",
                icon = R.drawable.ic_delete,
                textColor = SocialTheme.colors.error,
                onClick = deleteGroup
            )
        } else {
            ProfileDisplaySettingsItem(
                label = "Leave",
                icon = R.drawable.ic_logout,
                textColor = SocialTheme.colors.error,
                onClick = leaveGroup
            )

        }
        ProfileDisplaySettingsItem(
            label = "Cancel",
            turnOffIcon = true,
            textColor = SocialTheme.colors.textPrimary.copy(0.5f),
            onClick = onCancel
        )
    }
}