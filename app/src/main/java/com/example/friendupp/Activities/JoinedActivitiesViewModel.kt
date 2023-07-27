package com.example.friendupp.Activities

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.di.ActivityRepository
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Participant
import com.example.friendupp.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class JoinedActivitiesViewModel @Inject constructor(
    private val activitiesRepo: ActivityRepository,
) : ViewModel() {
    private val _joinedActivitiesListState = mutableStateOf<List<Activity>>(emptyList())
    val joinedActivitiesListState: MutableState<List<Activity>> = _joinedActivitiesListState

    private val _joinedActivitiesReponse = mutableStateOf<Response<List<Activity>>>(Response.Success(
        emptyList()
    ))
    val joinedActivitiesResponse: MutableState<Response<List<Activity>>> = _joinedActivitiesReponse


    // Function to fetch participants for an activity
    fun getJoinedActivities(activityId: String) {
        viewModelScope.launch {
            activitiesRepo.getJoinedActivities(activityId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _joinedActivitiesListState.value = response.data ?: emptyList()
                        _joinedActivitiesReponse.value=Response.Success(_joinedActivitiesListState.value)

                        Log.d("JoinedActivitiesViewModel", "Activities fetched successfully: $activityId")
                    }
                    is Response.Failure -> {
                        _joinedActivitiesListState.value = emptyList()
                        _joinedActivitiesReponse.value=Response.Success(emptyList())
                        Log.d("JoinedActivitiesViewModel", "Failed to fetch Activities : $activityId. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        _joinedActivitiesReponse.value=response


                        Log.d("JoinedActivitiesViewModel", "Fetching participants in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }

        }
    }

    // Function to fetch more participants for an activity
    fun getMoreJoinedActivities(activityId: String) {
        viewModelScope.launch {
            activitiesRepo.getMoreJoinedActivities(activityId).collect { response ->
                when (response) {
                    is Response.Success -> {

                        _joinedActivitiesListState.value = _joinedActivitiesListState.value + (response.data ?: emptyList())
                        _joinedActivitiesReponse.value=Response.Success(_joinedActivitiesListState.value)

                        Log.d("JoinedActivitiesViewModel", "More Activities fetched successfully: $activityId")
                    }
                    is Response.Failure -> {
                        _joinedActivitiesReponse.value=response
                        Log.d("JoinedActivitiesViewModel", "Failed to fetch more Activities: $activityId. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("JoinedActivitiesViewModel", "Fetching more participants in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

}
