package com.example.friendupp.GroupParticipants

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.model.Participant
import com.example.friendupp.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GroupParticipantsViewModel @Inject constructor(
    private val groupParticipantsRepository: GroupParticipantsRepository
) : ViewModel() {
    private val _participantsList = mutableStateOf<List<Participant>>(emptyList())
    val participantsListState: MutableState<List<Participant>> = _participantsList
    private val _participantsLoading = mutableStateOf(false)
    val participantsLoading: State<Boolean> = _participantsLoading

    fun getParticipantsList(): List<Participant> {
        return participantsListState.value
    }

    // Function to fetch participants for an activity
    fun getParticipants(activityId: String) {
        viewModelScope.launch {
            _participantsLoading.value=true
            groupParticipantsRepository.getParticipants(activityId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _participantsList.value = response.data ?: emptyList()
                        Log.d("ParticipantsViewModel", "Participants fetched successfully for activity: $activityId")
                    }
                    is Response.Failure -> {
                        _participantsList.value = emptyList()
                        Log.d("ParticipantsViewModel", "Failed to fetch participants for activity: $activityId. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("ParticipantsViewModel", "Fetching participants in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
            _participantsLoading.value=false

        }
    }

    // Function to fetch more participants for an activity
    fun getMoreParticipants(activityId: String) {
        viewModelScope.launch {
            groupParticipantsRepository.getMoreParticipants(activityId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _participantsList.value = _participantsList.value + (response.data ?: emptyList())
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

    // Function to add a participant
    fun addParticipant(activityId: String, participant: Participant) {
        viewModelScope.launch {
            groupParticipantsRepository.addParticipant(activityId, participant).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _participantsList.value = _participantsList.value + participant
                        Log.d("ParticipantsViewModel", "Participant added successfully to activity: $activityId")
                    }
                    is Response.Failure -> {
                        Log.d("ParticipantsViewModel", "Failed to add participant to activity: $activityId. Error: ${response.e.message}")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to remove a participant
    fun removeParticipant(activityId: String, participant: Participant) {
        viewModelScope.launch {
            groupParticipantsRepository.removeParticipant(activityId, participant.id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _participantsList.value = _participantsList.value - participant
                        Log.d("ParticipantsViewModel", "Participant removed successfully from activity: $activityId")
                    }
                    is Response.Failure -> {
                        Log.d("ParticipantsViewModel", "Failed to remove participant from activity: $activityId. Error: ${response.e.message}")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
}
