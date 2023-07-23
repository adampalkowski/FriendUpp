package com.example.friendupp.Participants

import com.example.friendupp.model.Invite
import com.example.friendupp.model.Participant
import com.example.friendupp.model.Response
import kotlinx.coroutines.flow.Flow

interface ParticipantsRepository{
    suspend fun getParticipants(activityId: String): Flow<Response<List<Participant>>>
    suspend fun getMoreParticipants(activityId: String): Flow<Response<List<Participant>>>
    suspend fun addParticipant(activityId:String,participant: Participant): Flow<Response<Void?>>
    suspend fun removeParticipant(activityId:String,participant: Participant): Flow<Response<Void?>>
    // Add other functions for handling invites, such as creating, deleting, etc.
}