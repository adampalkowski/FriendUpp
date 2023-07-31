package com.palkowski.friendupp.Request

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkowski.friendupp.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(
    private val requestRepo: RequestRepository
) : ViewModel() {
    private val _requestsList = mutableStateOf<List<Request>>(emptyList())
    val requestsListState: MutableState<List<Request>> = _requestsList

    private val _requestsLoading = mutableStateOf(false)
    val requestsLoading: State<Boolean> = _requestsLoading

    fun getRequestsList():List<Request>{
        return requestsListState.value
    }
    fun isRequestLoaded():Boolean{
        return requestsLoading.value
    }

    // Function to fetch requests for an activity
    fun getRequests(activityId: String) {
        viewModelScope.launch {
            _requestsLoading.value = true
            requestRepo.getRequests(activityId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _requestsList.value = response.data ?: emptyList()
                        Log.d("RequestViewModel", "Requests fetched successfully for activity: $activityId")
                    }
                    is Response.Failure -> {
                        _requestsList.value = emptyList()
                        Log.d("RequestViewModel", "Failed to fetch requests for activity: $activityId. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("RequestViewModel", "Fetching requests in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
                _requestsLoading.value = false
            }
        }
    }

    // Function to fetch more participants for an activity
    fun getMoreRequests(activityId: String) {
        viewModelScope.launch {
            requestRepo.getMoreRequests(activityId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _requestsList.value =_requestsList.value + (response.data ?: emptyList())
                        Log.d("ParticipantsViewModel", "More participants fetched successfully for activity: $activityId")
                    }
                    is Response.Failure -> {
                        Log.d("ParticipantsViewModel", "Failed to fetch more participants for activity: $activityId. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("ParticipantsViewModel", "Fetching more participants in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
    // Function to accept a request and add the participant to the activity
    fun createRequest(activityId: String, request: Request) {
        viewModelScope.launch {
            requestRepo.createRequest(activityId, request).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _requestsList.value = _requestsList.value - request
                        Log.d("RequestViewModel", "Request accepted successfully for activity: $activityId")
                    }
                    is Response.Failure -> {
                        Log.d("RequestViewModel", "Failed to accept request for activity: $activityId. Error: ${response.e.message}")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to reject a request
    fun removeRequest(activityId: String, request: Request) {
        viewModelScope.launch {
            requestRepo.removeRequest(activityId, request).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _requestsList.value = _requestsList.value - request
                        Log.d("RequestViewModel", "Request rejected successfully for activity: $activityId")
                    }
                    is Response.Failure -> {
                        Log.d("RequestViewModel", "Failed to reject request for activity: $activityId. Error: ${response.e.message}")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
}
