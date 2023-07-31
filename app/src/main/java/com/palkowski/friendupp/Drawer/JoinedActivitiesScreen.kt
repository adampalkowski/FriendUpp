package com.palkowski.friendupp.Drawer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkowski.friendupp.bottomBar.ActivityUi.ActivityEvents
import com.palkowski.friendupp.bottomBar.ActivityUi.activityItem
import com.palkowski.friendupp.Components.ScreenHeading
import com.palkowski.friendupp.model.Activity
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.model.UserData


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
