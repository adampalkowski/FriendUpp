package com.palkowski.friendupp.Drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkowski.friendupp.Components.ScreenHeading
import com.palkowski.friendupp.bottomBar.ActivityUi.ActivityEvents
import com.palkowski.friendupp.bottomBar.ActivityUi.activityItem
import com.palkowski.friendupp.model.Activity
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.model.UserData


sealed class CreatedActivitiesScreenEvents{
    class GetMoreCreatedActivities(val id :String):CreatedActivitiesScreenEvents()
    object GoBack:CreatedActivitiesScreenEvents()
}


@Composable
fun CreatedActivitiesScreen(modifier: Modifier,onEvent:(CreatedActivitiesScreenEvents)->Unit,activitiesEvents:(ActivityEvents)->Unit,createdActivitiesResponse: Response<List<Activity>>){
    //LOAD IN PROFILE ACTIVITIES
    var historyActivitiesExist= remember { mutableStateOf(false) }

    Column(modifier=modifier) {

        ScreenHeading(title = "Created activities", backButton = true, onBack = {onEvent(CreatedActivitiesScreenEvents.GoBack)}) {}
            LazyColumn{
                when(createdActivitiesResponse){
                    is Response.Success->{
                        items(createdActivitiesResponse.data) { activity ->
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
                        if (historyActivitiesExist.value) {
                            onEvent(CreatedActivitiesScreenEvents.GetMoreCreatedActivities(UserData.user!!.id))
                        }
                    }
                }
            }

    }

}

