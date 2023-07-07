package com.example.friendupp.Drawer

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.friendupp.ActivityUi.ActivityEvents
import com.example.friendupp.ActivityUi.activityItem
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Home.HomeEvents
import com.example.friendupp.Profile.ProfileEvents
import com.example.friendupp.Profile.loadActivitiesHistory
import com.example.friendupp.Profile.loadJoinedActivities
import com.example.friendupp.Profile.loadMoreActivitiesHistory
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.UserData


sealed class CreatedActivitiesEvents{
    object GoBack:CreatedActivitiesEvents()
    class ExpandActivity(val activityData: Activity) : CreatedActivitiesEvents()
    class JoinActivity(val activity: Activity) : CreatedActivitiesEvents()
    class UnBookmark(val id: String) : CreatedActivitiesEvents()
    class Bookmark(val id: String) : CreatedActivitiesEvents()
    class GoToProfile(val id: String) : CreatedActivitiesEvents()
    class LeaveActivity(val activity: Activity) : CreatedActivitiesEvents()
    class OpenChat(val id: String) : CreatedActivitiesEvents()
}

@Composable
fun CreatedActivitiesScreen(onEvent:(CreatedActivitiesEvents)->Unit,activityViewModel: ActivityViewModel){
    //LOAD IN PROFILE ACTIVITIES
    var historyActivitiesExist= remember { mutableStateOf(false) }
    val activitiesHistory = remember { mutableStateListOf<Activity>() }
    val moreHistoryActivities = remember { mutableStateListOf<Activity>() }

    loadActivitiesHistory(activityViewModel,activitiesHistory,UserData.user!!.id)
    loadMoreActivitiesHistory(activityViewModel,moreHistoryActivities)

    Column() {

        ScreenHeading(title = "Created activities", backButton = true, onBack = {onEvent(CreatedActivitiesEvents.GoBack)}) {}
            LazyColumn{
                items(activitiesHistory) { activity ->
                    activityItem(
                        activity,
                        onClick = {
                            // Handle click event
                        },
                        onEvent = { event->

                                handleActivityEvent(event,onEvent)
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
                            handleActivityEvent(event,onEvent)
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

    }

}

fun handleActivityEvent(event: ActivityEvents,    onEvent: (CreatedActivitiesEvents) -> Unit) {
    when (event) {
        is ActivityEvents.Expand -> {
            onEvent(CreatedActivitiesEvents.ExpandActivity(event.activity))
        }
        is ActivityEvents.Join -> {
            onEvent(CreatedActivitiesEvents.JoinActivity(event.activity))
        }
        is ActivityEvents.Leave -> {
            onEvent(CreatedActivitiesEvents.LeaveActivity(event.activity))
        }
        is ActivityEvents.OpenChat -> {
            onEvent(CreatedActivitiesEvents.OpenChat(event.id))
        }
        is ActivityEvents.GoToProfile->{
            onEvent(CreatedActivitiesEvents.GoToProfile(event.id))
        }
        is ActivityEvents.Bookmark->{
            onEvent(CreatedActivitiesEvents.Bookmark(event.id))
        }
        is ActivityEvents.UnBookmark->{
            onEvent(CreatedActivitiesEvents.UnBookmark(event.id))
        }
    }
}