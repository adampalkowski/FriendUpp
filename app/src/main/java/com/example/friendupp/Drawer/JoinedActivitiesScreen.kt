package com.example.friendupp.Drawer

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.friendupp.bottomBar.ActivityUi.ActivityEvents
import com.example.friendupp.bottomBar.ActivityUi.activityItem
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Profile.*
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData


sealed class JoinedActivitiesScreenEvents{
    class GetMoreJoinedActivities(val id :String):JoinedActivitiesScreenEvents()
}


@Composable
fun JoinedActivitiesScreen(modifier:Modifier,activitiesEvents:(ActivityEvents)->Unit,onEvent:(JoinedActivitiesScreenEvents)->Unit,joinedActivitiesResponse: Response<List<Activity>>){

    var joinedActivitiesExist= remember { mutableStateOf(false) }

    Column(modifier) {
        ScreenHeading(title = "Joined activities", backButton = true, onBack = {activitiesEvents(ActivityEvents.GoBack)}) {}
        LazyColumn{
            when(joinedActivitiesResponse){
                is Response.Success->{
                    items(joinedActivitiesResponse.data) { activity ->
                        activityItem(
                            activity,
                            onClick = {
                                // Handle click event
                            },
                            onEvent = activitiesEvents
                        )
                    }
                }
                is Response.Loading->{
                    item{
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                }
                is Response.Failure->{
                        /*todo handle joined activities load failure*/
                }
                else->{}
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))

            }
            item {
                LaunchedEffect(true) {
                    if (joinedActivitiesExist.value) {
                        onEvent(JoinedActivitiesScreenEvents.GetMoreJoinedActivities(UserData.user!!.id))
                    }
                }
            }
        }

    }

}
