package com.palkowski.friendupp.GroupParticipants

import com.palkowski.friendupp.model.Chat
import com.palkowski.friendupp.model.Participant
import com.palkowski.friendupp.model.Response
import kotlinx.coroutines.flow.Flow

interface GroupParticipantsRepository{
    suspend fun getParticipants(activityId: String): Flow<Response<List<Participant>>>
    suspend fun getMoreParticipants(activityId: String): Flow<Response<List<Participant>>>
    suspend fun addParticipant(activityId:String,participant: Participant): Flow<Response<Void?>>
    suspend fun removeParticipant(activityId:String,participant_id: String): Flow<Response<Void?>>
    suspend fun removeInvite(activityId:String, user_id: String): Flow<Response<Void?>>
    suspend fun removeGroup(group_id:String): Flow<Response<Void?>>
    suspend fun getGroupsInvites(user_id:String): Flow<Response<List<Chat>>>
    suspend fun getMoreGroupsInvites(user_id:String): Flow<Response<List<Chat>>>

    suspend fun getGroups(user_id:String): Flow<Response<List<Chat>>>
    suspend fun getMoreGroups(user_id:String): Flow<Response<List<Chat>>>
    suspend fun removeGroupImage(url: String): Flow<Response<Boolean>>


    // Add other functions for handling invites, such as creating, deleting, etc.
}