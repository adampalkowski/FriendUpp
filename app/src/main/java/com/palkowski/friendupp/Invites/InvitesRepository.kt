package com.palkowski.friendupp.Invites

import com.palkowski.friendupp.model.Invite
import com.palkowski.friendupp.model.Response
import kotlinx.coroutines.flow.Flow

interface InviteRepository {
    suspend fun getInvites(userId: String): Flow<Response<List<Invite>>>
    suspend fun getMoreInvites(userId: String): Flow<Response<List<Invite>>>
    suspend fun addInvite(invite: Invite):  Flow<Response<Void?>>
    suspend fun removeInvite(invite: Invite):  Flow<Response<Void?>>
    // Add other functions for handling invites, such as creating, deleting, etc.
}