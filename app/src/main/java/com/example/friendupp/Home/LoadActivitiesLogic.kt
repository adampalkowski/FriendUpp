package com.example.friendupp.Home

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData

@Composable
fun loadMoreFriendsActivities(activityViewModel: ActivityViewModel, activities: MutableList<Activity>) {
    activityViewModel.moreActivitiesListState.value.let {
        when (it) {
            is Response.Success -> {

                activities.clear()
                activities.addAll(it.data)
            }
            else -> {}
        }
    }

}

@Composable
fun loadFriendsActivities(activityViewModel: ActivityViewModel, activities: MutableList<Activity>, activitiesExist: MutableState<Boolean>) {
    //call get activities only once
    val activitiesFetched = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = activitiesFetched.value) {
        if (!activitiesFetched.value) {
            activityViewModel.getActivitiesForUser(UserData.user!!.id)
            activitiesFetched.value = true
        }
    }
    activityViewModel.activitiesListState.value.let { response ->
        when (response) {
            is Response.Success -> {

                activities.clear()
                activities.addAll(response.data)
                activitiesExist.value = true

            }
            is Response.Failure -> {

                Toast.makeText(LocalContext.current, "FAiled", Toast.LENGTH_SHORT).show()

            }
            is Response.Loading -> {
            }
            null->{

            }
        }
    }
}


@Composable
fun loadMorePublicActivities(activityViewModel: ActivityViewModel, activities: MutableList<Activity>) {
    activityViewModel.moreclosestActivitiesListState.value.let {
        when (it) {
            is Response.Success -> {

                activities.clear()
                activities.addAll(it.data)
            }
            else -> {}
        }
    }

}

@Composable
fun loadPublicActivities(activityViewModel: ActivityViewModel, activities: MutableList<Activity>, activitiesExist: MutableState<Boolean>) {

    //call get activities only once
    val activitiesFetched = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = activitiesFetched.value) {
        if (!activitiesFetched.value) {
            activityViewModel.location.value.let {location->
                if(location!=null){
                    activityViewModel.getClosestActivities(location.latitude,location.longitude,  50.0*1000.0)
                    activitiesFetched.value = true
                }
            }

        }
    }

    activityViewModel.closestActivitiesListState.value.let { response ->
        when (response) {
            is Response.Success -> {
                activities.clear()
                activities.addAll(response.data)
                activitiesExist.value = true

            }
            is Response.Failure -> {

                Toast.makeText(LocalContext.current, "FAiled", Toast.LENGTH_SHORT).show()

            }
            is Response.Loading -> {
            }
            null->{

            }
        }
    }
}