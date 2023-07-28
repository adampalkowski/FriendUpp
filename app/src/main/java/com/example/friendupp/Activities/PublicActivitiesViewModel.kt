package com.example.friendupp.Activities

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.di.ActivityRepository
import com.example.friendupp.di.ChatRepository
import com.example.friendupp.di.checkIfDelete
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.SocialException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject



@HiltViewModel
class PublicActivitiesViewModel @Inject constructor(
    private val activitiesRepo: ActivityRepository,
    private val chatRepo: ChatRepository,
    ) : ViewModel() {
    private val _publicActivitiesListState = mutableStateOf<List<Activity>>(emptyList())
    val publicActivitiesListState: MutableState<List<Activity>> = _publicActivitiesListState

    private val _publicActivitiesListResponse = mutableStateOf<Response<List<Activity>>>(Response.Success(emptyList()))
    val publicActivitiesListResponse: State<Response<List<Activity>>> = _publicActivitiesListResponse


    fun getPublicActivities(lat:Double,lng:Double,radius:Double) {
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            activitiesRepo.getClosestActivities(lat,lng,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        response.data.forEach {
                            list_without_removed_activites.add(it)
                            checkIfDelete(
                                it.end_time,
                                deleteActivity = {
                                    Log.d("getActivitiesForUser", "delete activity")
                                    deleteActivity(it)
                                    list_without_removed_activites.remove(it)
                                })

                            _publicActivitiesListState.value =list_without_removed_activites?: emptyList()
                            _publicActivitiesListResponse.value =
                                Response.Success(list_without_removed_activites as List<Activity>)
                        }
                        Log.d("PublicActivitiesViewModel", "Activities fetched successfully: ")

                    }
                    is Response.Failure -> {
                        _publicActivitiesListState.value = emptyList()
                        _publicActivitiesListResponse.value = Response.Failure(
                            e = SocialException(message = "Failed to fetch public activities.", e = response.e)
                        )
                        Log.d(
                            "PublicActivitiesViewModel",
                            "Failed to fetch activities:  Error: ${response.e.message}"
                        )
                    }
                    is Response.Loading -> {
                        _publicActivitiesListResponse.value = response
                        Log.d("PublicActivitiesViewModel", "Fetching activities in progress...")

                    }
                }

            }

        }
    }

    fun getMorePublicActivities(lat:Double,lng:Double,radius:Double){
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            activitiesRepo.getMoreClosestActivities(lat,lng,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        response.data.forEach {
                            Log.d("getClosestActivities",it.toString())
                            list_without_removed_activites.add(it)
                            checkIfDelete(
                                it.end_time,
                                deleteActivity = {
                                    Log.d("getActivitiesForUser", "delete activity")
                                    deleteActivity(it)
                                    list_without_removed_activites.remove(it)
                                })

                            _publicActivitiesListState.value =_publicActivitiesListState.value + list_without_removed_activites?: emptyList()
                            _publicActivitiesListResponse.value = Response.Success(_publicActivitiesListState.value)
                        }
                        Log.d(
                            "PublicActivitiesViewModel",
                            "More activities fetched successfully:"
                        )
                    }
                    is Response.Failure -> {
                        _publicActivitiesListResponse.value = response
                        Log.d(
                            "PublicActivitiesViewModel",
                            "Failed to fetch more activities:. Error: ${response.e.message}"
                        )
                    }
                    is Response.Loading -> {
                        Log.d("PublicActivitiesViewModel", "Fetching more activities in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Additional logging functionality can be added here
    // For example:
    fun logEvent(eventName: String) {
        Log.d("PublicActivitiesViewModel", "Event logged: $eventName")
        // Implement the logic to log the event
    }
    private val _isActivityDeletedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isActivityDeletedState: State<Response<Void?>> = _isActivityDeletedState
    private val _isChatDeleted = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isChatDeleted: State<Response<Void?>> = _isChatDeleted
    fun deleteActivity(activity: Activity,manualyDeleted:Boolean=false) {
        val TAG="DELETEACTIVITYDEBUG"
        viewModelScope.launch {
            activitiesRepo.deleteActivity(activity.id).collect { response ->
                when(response){
                    is Response.Success->{
                        Log.d(TAG,"DB ACTIVITY DELETE")
                    }
                    else->{}
                }
                _isActivityDeletedState.value = response
            }

            chatRepo.deleteChatCollection(activity.id).collect { response ->
                when(response){
                    is Response.Success->{
                        Log.d(TAG,"Chat  DELETE")
                    }
                    else->{}
                }
                _isChatDeleted.value = response
            }
            if(activity.image!=null){
                activitiesRepo.removeActivityImage(activity.image!!).collect(){response->
                    when(response){
                        is Response.Success->{
                            Log.d(TAG,"removed image")
                        }
                        else->{}
                    }
                }
            }
            if(!manualyDeleted){
                Log.d(TAG,"INCERASE STATS")
                activitiesRepo.increaseUserStats(activity.creator_id,activity.participants_ids.size).collect(){
                        response->
                }
            }
        }
    }
}