package com.example.friendupp.Invites

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.model.Invite
import com.example.friendupp.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitesViewModel @Inject constructor(
    private val inviteRepo: InviteRepository
) : ViewModel() {
    // Define state variables for the invite-related data
    private val _invitesList = mutableStateOf<List<Invite>>(emptyList())
    val invitesListState: MutableState<List<Invite>> = _invitesList

    // Function to access the current list of invites without recomposing the UI
    fun getCurrentInvitesList(): List<Invite> {
        return invitesListState.value
    }

    // Function to fetch invites for a user
    fun getInvites(userId: String) {
        viewModelScope.launch {
            inviteRepo.getInvites(userId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _invitesList.value = response.data ?: emptyList()
                        Log.d("InvitesViewModel", "Invites fetched successfully for user: $userId")
                    }
                    is Response.Failure -> {
                        // Handle the failure case if needed (e.g., show an error message)
                        // For example:
                        _invitesList.value = emptyList()
                        Log.d("InvitesViewModel", "Failed to fetch invites for user: $userId")
                    }
                    is Response.Loading->{

                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to fetch more invites for a user
    fun getMoreInvites(userId: String) {
        viewModelScope.launch {
            inviteRepo.getMoreInvites(userId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        // If the addition was successful, append the new invites to the existing list
                        // This assumes that you have access to the local list of invites in this view model.
                        _invitesList.value = _invitesList.value + (response.data ?: emptyList())
                        Log.d("InvitesViewModel", "More invites fetched successfully for user: $userId")
                    }
                    is Response.Failure -> {
                        // Handle the failure case if needed (e.g., show an error message)
                        // For example:
                        _invitesList.value = _invitesList.value // No changes to the list if there was an error
                        Log.d("InvitesViewModel", "Failed to fetch more invites for user: $userId")
                    }
                    is Response.Loading->{

                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to add an invite
    fun addInvite(invite: Invite) {
        viewModelScope.launch {
            inviteRepo.addInvite(invite).collect { responseInviteAdded ->
                when (responseInviteAdded) {
                    is Response.Success -> {
                        // If the addition was successful, update the local list of invites
                        _invitesList.value = _invitesList.value + invite
                        Log.d("InvitesViewModel", "Invite added successfully")
                    }
                    is Response.Failure -> {
                        // Handle the failure case if needed (e.g., show an error message)
                        // For example:
                        // Handle the failure case if needed (e.g., show an error message)
                        // For example:
                        _invitesList.value = _invitesList.value - invite
                        Log.d("InvitesViewModel", "Failed to add invite")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to remove an invite
    fun removeInvite(invite: Invite) {
        viewModelScope.launch {
            inviteRepo.removeInvite(invite).collect { responseInviteRemoved ->
                when (responseInviteRemoved) {
                    is Response.Success -> {
                        // If the removal was successful, update the local list of invites
                        _invitesList.value = _invitesList.value - invite
                        Log.d("InvitesViewModel", "Invite removed successfully")
                    }
                    is Response.Failure -> {
                        // Handle the failure case if needed (e.g., show an error message)
                        // For example:
                        // Handle the failure case if needed (e.g., show an error message)
                        // For example:
                        _invitesList.value = _invitesList.value + invite
                        Log.d("InvitesViewModel", "Failed to remove invite")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
}