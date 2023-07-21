package com.example.friendupp.Home

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.rangeTo
import com.example.friendupp.Settings.getSavedRangeValue
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
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
                    50.0 * 10000.0f
                )
            }
        } else {
            if (currentLocation != null) {
                activityViewModel.getClosestActivities(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    50.0 * 10000.0f
                )
                activitiesFetched.value = true
            }
        }
    }

    LaunchedEffect(date) {
            if(date!=null){
                if(currentLocation!=null){
                    Log.d(TAG,"CALLED FOR CLOSEST WITH DATE"+date.toString())
                    activities.clear()
                    activityViewModel.getClosestFilteredDateActivities(  currentLocation.latitude
                        , currentLocation.longitude,date,
                        50.0 * 10000.0f)
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