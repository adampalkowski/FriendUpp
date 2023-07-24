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
import com.example.friendupp.bottomBar.ActivityUi.ActivityEvents
import com.example.friendupp.bottomBar.ActivityUi.activityItem
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Home.HomeEvents
import com.example.friendupp.Profile.ProfileEvents
import com.example.friendupp.Profile.loadActivitiesHistory
import com.example.friendupp.Profile.loadJoinedActivities
import com.example.friendupp.Profile.loadMoreActivitiesHistory
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.UserData




@Composable
fun CreatedActivitiesScreen(modifier: Modifier,onEvent:(ActivityEvents)->Unit,activityViewModel: ActivityViewModel){
    //LOAD IN PROFILE ACTIVITIES
    var historyActivitiesExist= remember { mutableStateOf(false) }
    val activitiesHistory = remember { mutableStateListOf<Activity>() }
    val moreHistoryActivities = remember { mutableStateListOf<Activity>() }

    loadActivitiesHistory(activityViewModel,activitiesHistory,UserData.user!!.id)
    loadMoreActivitiesHistory(activityViewModel,moreHistoryActivities)

    Column(modifier=modifier) {

        ScreenHeading(title = "Created activities", backButton = true, onBack = {onEvent(ActivityEvents.GoBack)}) {}
            LazyColumn{
                items(activitiesHistory) { activity ->
                    activityItem(
                        activity,
                        onClick = {
                            // Handle click event
                        },
                        onEvent = onEvent
                    )
                }

                items(moreHistoryActivities) { activity ->
                    activityItem(
                        activity,
                        onClick = {
                            // Handle click event
                        },
                        onEvent = onEvent
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

