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
import com.example.friendupp.Profile.loadJoinedActivities
import com.example.friendupp.Profile.loadMoreJoinedActivities
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.UserData

sealed class JoinedActivitiesEvents{
    object GoBack:JoinedActivitiesEvents()
}

@Composable
fun JoinedActivitiesScreen(onEvent:(JoinedActivitiesEvents)->Unit,activityViewModel: ActivityViewModel){
    val joinedActivities = remember { mutableStateListOf<Activity>() }

    var joinedActivitiesExist= remember { mutableStateOf(false) }
    val moreJoinedActivities = remember { mutableStateListOf<Activity>() }
    loadJoinedActivities(activityViewModel = activityViewModel, joinedActivities =joinedActivities,UserData.user!!.id )
    loadMoreJoinedActivities(activityViewModel,moreJoinedActivities)
    Column() {
        ScreenHeading(title = "Joined activities", backButton = true, onBack = {onEvent(JoinedActivitiesEvents.GoBack)}) {}
        LazyColumn{
            items(joinedActivities) { activity ->
                activityItem(
                    activity,
                    onClick = {
                        // Handle click event
                    },
                    onEvent = { event->
                        when(event){
                            is ActivityEvents.Expand->{
                                Log.d("ACTIVITYDEBUG","LAUNCH PREIVEW2 ")

                            }
                            is ActivityEvents.Leave->{ }

                            is ActivityEvents.Join->{  }
                            is ActivityEvents.OpenChat->{ }
                        }
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
                        when(event){
                            is ActivityEvents.Expand->{
                            }
                            is ActivityEvents.Leave->{ }

                            is ActivityEvents.Join->{  }
                            is ActivityEvents.OpenChat->{  }
                        }
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

    }

}