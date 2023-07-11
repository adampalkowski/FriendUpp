package com.example.friendupp.Drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.friendupp.bottomBar.ActivityUi.activityItem
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Profile.loadActivitiesHistory
import com.example.friendupp.Profile.loadMoreActivitiesHistory
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData

@Composable
fun BookmarkedScreen(onEvent:(CreatedActivitiesEvents)->Unit,activityViewModel: ActivityViewModel){
    //LOAD IN PROFILE ACTIVITIES
    var bookmarkedActivitiesExist= remember { mutableStateOf(false) }
    val bookmarked = remember { mutableStateListOf<Activity>() }
    val moreBookmarked = remember { mutableStateListOf<Activity>() }

    loadBookmarkedActivites(activityViewModel,bookmarked,moreBookmarked, UserData.user!!.id)
    loadMoreActivitiesHistory(activityViewModel,moreBookmarked)

    Column() {

        ScreenHeading(title = "Bookmarked activities", backButton = true, onBack = {onEvent(CreatedActivitiesEvents.GoBack)}) {}
        LazyColumn{
            items(bookmarked) { activity ->
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

            items(moreBookmarked) { activity ->
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
                    if (bookmarkedActivitiesExist.value) {
                        activityViewModel.getMoreBookmarkedActivities(UserData.user!!.id)
                    }
                }
            }
        }

    }

}
@Composable
fun loadBookmarkedActivites(activityViewModel: ActivityViewModel, activities: MutableList<Activity>,moreActivities: MutableList<Activity>,userId:String) {
    activityViewModel.bookmarkedActivitiesState.value.let {
        when (it) {
            is Response.Success -> {
                activities.clear()
                activities.addAll(it.data)
            }
            else -> {}
        }
    }

    activityViewModel.moreBookmarkedActivitiesState.value.let {
        when (it) {
            is Response.Success -> {
                moreActivities.clear()
                moreActivities.addAll(it.data)
            }
            else -> {}
        }
    }


}