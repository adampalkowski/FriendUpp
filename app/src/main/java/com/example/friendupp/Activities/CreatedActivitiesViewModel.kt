package com.example.friendupp.Activities

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.di.ActivityRepository
import com.example.friendupp.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatedActivitiesViewModel @Inject constructor(
    private val activitiesRepo: ActivityRepository,
) : ViewModel() {
    private val _joinedActivities = mutableStateOf<List<Activity>>(emptyList())
    val joinedActivities: State<List<Activity>> = _joinedActivities

    private val _joinedActivitiesResponse = mutableStateOf<Response<List<Activity>>>(Response.Success(emptyList()))
    val joinedActivitiesResponse: State<Response<List<Activity>>> = _joinedActivitiesResponse

    // Function to fetch participants for an activity
    fun fetchJoinedActivities(activityId: String) {
        viewModelScope.launch {
            activitiesRepo.getUserActivities(activityId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _joinedActivities.value = response.data ?: emptyList()
                        _joinedActivitiesResponse.value = Response.Success(_joinedActivities.value)

                        Log.d("CreatedActivitiesViewModel", "Joined activities fetched successfully: $activityId")
                    }
                    is Response.Failure -> {
                        _joinedActivities.value = emptyList()
                        _joinedActivitiesResponse.value = Response.Failure(
                            e = SocialException(message = "Failed to fetch joined activities.", e = response.e)
                        )
                        Log.d(
                            "CreatedActivitiesViewModel",
                            "Failed to fetch joined activities: $activityId. Error: ${response.e.message}"
                        )
                    }
                    is Response.Loading -> {
                        _joinedActivitiesResponse.value = Response.Loading

                        Log.d("CreatedActivitiesViewModel", "Fetching joined activities in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to fetch more participants for an activity
    fun fetchMoreJoinedActivities(activityId: String) {
        viewModelScope.launch {
            activitiesRepo.getMoreUserActivities(activityId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        val currentActivities = _joinedActivities.value
                        _joinedActivities.value = currentActivities + (response.data ?: emptyList())
                        _joinedActivitiesResponse.value = Response.Success(_joinedActivities.value)

                        Log.d(
                            "CreatedActivitiesViewModel",
                            "More joined activities fetched successfully: $activityId"
                        )
                    }
                    is Response.Failure -> {
                        _joinedActivitiesResponse.value = Response.Failure(
                            e = SocialException(message = "Failed to fetch more joined activities.", e = response.e)
                        )
                        Log.d(
                            "CreatedActivitiesViewModel",
                            "Failed to fetch more joined activities: $activityId. Error: ${response.e.message}"
                        )
                    }
                    is Response.Loading -> {
                        Log.d("CreatedActivitiesViewModel", "Fetching more joined activities in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
}