package com.palkowski.friendupp.Participants

import com.palkowski.friendupp.model.Participant
import com.palkowski.friendupp.model.Response
import kotlinx.coroutines.flow.Flow

interface ParticipantsRepository{
    suspend fun getParticipants(activityId: String): Flow<Response<List<Participant>>>
    suspend fun getMoreParticipants(activityId: String): Flow<Response<List<Participant>>>
    suspend fun addParticipant(activityId:String,participant: Participant): Flow<Response<Void?>>
    suspend fun removeParticipant(activityId:String,participant: Participant): Flow<Response<Void?>>
    // Add other functions for handling invites, such as creating, deleting, etc.
}