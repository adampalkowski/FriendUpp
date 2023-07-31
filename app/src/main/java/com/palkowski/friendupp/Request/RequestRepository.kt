package com.palkowski.friendupp.Request

import com.palkowski.friendupp.model.Response
import kotlinx.coroutines.flow.Flow


interface RequestRepository{
    suspend fun getRequests(activityId: String): Flow<Response<List<Request>>>
    suspend fun getMoreRequests(activityId: String): Flow<Response<List<Request>>>
    suspend fun createRequest(activityId:String,request: Request): Flow<Response<Void?>>
    suspend fun removeRequest(activityId:String,request: Request): Flow<Response<Void?>>
    // Add other functions for handling invites, such as creating, deleting, etc.
}