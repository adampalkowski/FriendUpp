package com.palkowski.friendupp.Home

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.palkowski.friendupp.Settings.getSavedRangeValue
import com.palkowski.friendupp.di.ActivityViewModel
import com.palkowski.friendupp.model.Activity
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.model.UserData
import com.google.android.gms.maps.model.LatLng



@Composable
fun loadMoreFriendsActivities(
    activityViewModel: ActivityViewModel,
    activities: MutableList<Activity>,
) {
    activityViewModel.moreActivitiesListState.value.let {
        when (it) {
            is Response.Success -> {

                activities.clear()
                activities.addAll(it.data)
            }
            is Response.Failure -> {

                activities.clear()
            }
            is Response.Loading -> {

                activities.clear()
            }
            else -> {}
        }
    }

}

@Composable
fun loadFriendsActivities(
    activityViewModel: ActivityViewModel,
    activities: MutableList<Activity>,
    activitiesExist: MutableState<Boolean>,
) {
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

                activities.clear()
            }
            is Response.Loading -> {

                activities.clear()
            }
            null -> {

            }
        }
    }
}




@Composable
fun loadPublicActivities(
    activityViewModel: ActivityViewModel,
    activities: MutableList<Activity>,
    activitiesExist: MutableState<Boolean>,
    currentLocation: LatLng?,
    selectedTags: MutableList<String>,
    date:String?,
    moreActivities: MutableList<Activity>,
) {

    val context= LocalContext.current
    //call get activities only once
    val activitiesFetched = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = activitiesFetched.value) {

        if (!activitiesFetched.value) {
            if (currentLocation != null) {
                val range = getSavedRangeValue(context)
                activityViewModel.getClosestActivities(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    range.toDouble() * 10000.0f
                )
                activitiesFetched.value = true
            }
        }
    }
    val radius = getSavedRangeValue(context)
    LaunchedEffect(selectedTags.toList()) {
        if (selectedTags.isNotEmpty()) {
            val tags: ArrayList<String> = arrayListOf()
            tags.addAll(selectedTags)
            Log.d("HOMESCREEN", "get friends by tags")
            Log.d("HOMESCREEN", tags.toString())
            if (currentLocation != null) {
                Log.d(TAG,"CALLED FOR CLOSEST WITH TAGS")
                activityViewModel.getClosestFilteredActivities(
                    currentLocation.latitude, currentLocation.longitude, tags,
                    radius * 1000.0
                )
            }
        } else {
            if (currentLocation != null) {
                activityViewModel.getClosestActivities(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    radius * 1000.0
                )
                activitiesFetched.value = true
            }
        }
    }
    Log.d("LOADPUBLIC",date.toString())
    LaunchedEffect(date) {
            if(date!=null){
                if(currentLocation!=null){
                    val radius= getSavedRangeValue(context)
                    Log.d(TAG,"CALLED FOR CLOSEST WITH DATE"+date.toString())
                    activities.clear()
                    activityViewModel.getClosestFilteredDateActivities(  currentLocation.latitude
                        , currentLocation.longitude,date,
                        radius * 1000.0)
                }

            }
    }

        activityViewModel.closestActivitiesListState.value.let { response ->
            when (response) {
                is Response.Success -> {
                    Log.d(TAG,"GOT CLOSEST")
                    Log.d(TAG,"GOT CLOSEST act")
                    Log.d(TAG,response.data.size.toString())

                    activities.clear()
                    activities.addAll(response.data)
                    activitiesExist.value = true
                }
                is Response.Failure -> {
                    Log.d(TAG,"Failure"+response.e.message)
                    activities.clear()
                }
                is Response.Loading -> {
                    activities.clear()
                }
                null -> {

                }
            }
        }
    activityViewModel.moreclosestActivitiesListState.value.let {
        when (it) {
            is Response.Success -> {

                moreActivities.clear()
                moreActivities.addAll(it.data)
            }
            is Response.Failure->{

                moreActivities.clear()
            }
            is Response.Loading -> {
                moreActivities.clear()
            }
            else -> {}
        }
    }


}