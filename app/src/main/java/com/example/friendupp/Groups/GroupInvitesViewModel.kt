package com.example.friendupp.Groups

import com.example.friendupp.GroupParticipants.GroupParticipantsRepository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Participant
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupInvitesViewModel @Inject constructor(
    private val groupParticipantsRepository: GroupParticipantsRepository
) : ViewModel() {
    private val _participantsList = mutableStateOf<List<Participant>>(emptyList())
    val participantsListState: MutableState<List<Participant>> = _participantsList

    private val _groupsList = mutableStateOf<List<Chat>>(emptyList())
    val groupsListState: MutableState<List<Chat>> = _groupsList

    private val _groupListLoading = mutableStateOf(false)
    val groupListLoading: State<Boolean> = _groupListLoading
    private val _groupListInvitesLoading = mutableStateOf(false)
    val groupListInvitesLoading: State<Boolean> = _groupListInvitesLoading

    fun getGroupsList(): List<Chat> {
        return groupsListState.value
    }


    private val _groupsInvitesList = mutableStateOf<List<Chat>>(emptyList())
    val groupsInvitesListState: MutableState<List<Chat>> = _groupsInvitesList

    fun getGroupInvites(): List<Chat> {
        return groupsInvitesListState.value
    }
    // Function to fetch participants for an activity
    fun getGroupInvites(user_id: String) {
        viewModelScope.launch {
            _groupListInvitesLoading.value = true
            groupParticipantsRepository.getGroupsInvites(user_id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _groupsInvitesList.value = response.data ?: emptyList()
                        Log.d("GroupInvitesViewModel", "Groups invites fetched successfully for user: $user_id")
                    }
                    is Response.Failure -> {
                        _groupsInvitesList.value = emptyList()
                        Log.d("GroupInvitesViewModel", "Failed to fetch group invites for user: $user_id. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("GroupInvitesViewModel", "Fetching group invites in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
            _groupListInvitesLoading.value = false
        }
    }

    // Function to fetch more participants for an activity
    fun getMoreGroupsInvites(user_id: String) {
        viewModelScope.launch {
            groupParticipantsRepository.getMoreGroupsInvites(user_id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _groupsInvitesList.value = _groupsInvitesList.value + (response.data ?: emptyList())
                        Log.d("GroupInvitesViewModel", "More group invites fetched successfully for user: $user_id")
                    }
                    is Response.Failure -> {
                        Log.d("GroupInvitesViewModel", "Failed to fetch more group invites for user: $user_id. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("GroupInvitesViewModel", "Fetching more group invites in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
    // Function to fetch groups for a user
    fun getGroups(user_id: String) {
        viewModelScope.launch {
            _groupListLoading.value = true
            groupParticipantsRepository.getGroups(user_id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _groupsList.value = response.data ?: emptyList()
                        Log.d("GroupInvitesViewModel", "Groups fetched successfully for user: $user_id")
                    }
                    is Response.Failure -> {
                        _groupsList.value = emptyList()
                        Log.d("GroupInvitesViewModel", "Failed to fetch groups for user: $user_id. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("GroupInvitesViewModel", "Fetching groups in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
            _groupListLoading.value = false
        }
    }

    // Function to fetch more groups for a user
    fun getMoreGroups(user_id: String) {
        viewModelScope.launch {
            groupParticipantsRepository.getMoreGroups(user_id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _groupsList.value = _groupsList.value + (response.data ?: emptyList())
                        Log.d("GroupInvitesViewModel", "More groups fetched successfully for user: $user_id")
                    }
                    is Response.Failure -> {
                        Log.d("GroupInvitesViewModel", "Failed to fetch more groups for user: $user_id. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("GroupInvitesViewModel", "Fetching more groups in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to add a participant
    fun addParticipantToGroup(group: Chat, participant: Participant) {
        viewModelScope.launch {
            groupParticipantsRepository.addParticipant(group.id!!, participant).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _groupsInvitesList.value = _groupsInvitesList.value -group
                        _groupsList.value = _groupsList.value +group
                        _participantsList.value = _participantsList.value + participant
                        Log.d("GroupInvitesViewModel", "Participant added successfully to activity: ${group.id}")
                    }
                    is Response.Failure -> {
                        Log.d("GroupInvitesViewModel", "Failed to add participant to activity: ${group.id}. Error: ${response.e.message}")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to remove a participant
    fun removeParticipantFromGroup(activityId: String, participant: Participant) {
        viewModelScope.launch {
            groupParticipantsRepository.removeParticipant(activityId, participant.id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _participantsList.value = _participantsList.value - participant
                        Log.d("GroupInvitesViewModel", "Participant removed successfully from activity: $activityId")
                    }
                    is Response.Failure -> {
                        Log.d("GroupInvitesViewModel", "Failed to remove participant from activity: $activityId. Error: ${response.e.message}")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
    // Function to remove a participant
    fun removeParticipantFromGroupOnlyId(activityId: String, participant_id: String) {
        viewModelScope.launch {
            groupParticipantsRepository.removeParticipant(activityId,participant_id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        Log.d("GroupInvitesViewModel", "Participant removed successfully from activity: $activityId")
                    }
                    is Response.Failure -> {
                        Log.d("GroupInvitesViewModel", "Failed to remove participant from activity: $activityId. Error: ${response.e.message}")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to remove a participant
    fun removeInvite(chat: Chat, user_id: String) {
        viewModelScope.launch {
            groupParticipantsRepository.removeInvite(chat.id!!, user_id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _groupsInvitesList.value = _groupsInvitesList.value - chat
                        Log.d("GroupInvitesViewModel", "Invite removed successfully from activity: ${chat.id!!}")
                    }
                    is Response.Failure -> {
                        Log.d("GroupInvitesViewModel", "Failed to remove ivnite from group: ${chat.id!!}. Error: ${response.e.message}")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
}
