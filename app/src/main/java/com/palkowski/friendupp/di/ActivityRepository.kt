package com.palkowski.friendupp.di

import android.net.Uri
import com.palkowski.friendupp.model.ActiveUser
import com.palkowski.friendupp.model.Activity
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.model.User
import kotlinx.coroutines.flow.Flow


interface ActivityRepository {

    suspend fun getActivity(id:String) : Flow<Response<Activity>>
    suspend fun getUserActivities(id: String): Flow<Response<List<Activity>>>
    suspend fun watchCurrentUserActive(id:String): Flow<Response<List<ActiveUser>>>
    suspend fun increaseUserStats(user_id:String,numberOfParticipants:Int): Flow<Response<Void?>>
    suspend fun updateDescription(id:String,description:String): Flow<Response<Void?>>

    suspend fun updateActivityCustomization(activityId: String,activitySharing:Boolean,disableChat:Boolean,participantConfirmation:Boolean):Flow<Response<Boolean>>
    suspend fun getJoinedActivities(id: String): Flow<Response<List<Activity>>>
    suspend fun getClosestActivities(lat: Double,lng:Double,radius:Double): Flow<Response<List<Activity>>>
    suspend fun getClosestFilteredActivities(lat: Double,lng:Double,tags:ArrayList<String>,radius:Double): Flow<Response<List<Activity>>>
    suspend fun getClosestFilteredDateActivities(lat: Double,lng:Double,date:String,radius:Double): Flow<Response<List<Activity>>>
    suspend fun getMoreFilteredDateClosestActivities(lat: Double,lng:Double,date:String,radius:Double): Flow<Response<List<Activity>>>
    suspend fun getMoreFilteredClosestActivities(lat: Double,lng:Double,tags:ArrayList<String>,radius:Double): Flow<Response<List<Activity>>>
    suspend fun getMoreClosestActivities(lat: Double,lng:Double,radius:Double): Flow<Response<List<Activity>>>
    suspend fun getMoreUserActivities(id: String): Flow<Response<List<Activity>>>
    suspend fun getMoreBookmarkedActivities(id: String): Flow<Response<List<Activity>>>
    suspend fun getBookmarkedActivities(id: String): Flow<Response<List<Activity>>>
    suspend fun getMoreJoinedActivities(id: String): Flow<Response<List<Activity>>>
    suspend fun addImageFromGalleryToStorage(id: String,uri: Uri): Flow<Response<String>>
    suspend fun removeActivityImage(url: String): Flow<Response<Boolean>>
    suspend fun deleteImageFromHighResStorage(id: String): Flow<Response<String>>
    suspend fun deleteActivityImageFromFirestoreActivity(activity_id: String,user_id:String): Flow<Response<String>>
    suspend fun likeActivity(id:String,user: User) : Flow<Response<Void?>>
    suspend fun likeActivityOnlyId(id:String,user: User) : Flow<Response<Void?>>
    suspend fun bookMarkActivity(activity_id:String,user_id: String) : Flow<Response<Void?>>
    suspend fun unBookMarkActivity(activity_id:String,user_id: String) : Flow<Response<Void?>>
    suspend fun addActivityParticipant(id:String,user: User) : Flow<Response<Void?>>
    suspend fun addParticipantImageToActivity(activity_id:String,user_id:String,picture_url: String) : Flow<Response<Void?>>
    suspend fun setParticipantPicture(id:String,user: User) : Flow<Response<Void?>>
    suspend fun unlikeActivity(id:String,user_id:String) : Flow<Response<Void?>>
    suspend fun unlikeActivityOnlyId(id:String,user_id:String) : Flow<Response<Void?>>
    suspend fun addRequestToActivity(activity_id:String,user_id:String) : Flow<Response<Void?>>
    suspend fun removeRequestFromActivity(activity_id:String,user_id:String) : Flow<Response<Void?>>
    suspend fun reportActivity(activity_id:String) : Flow<Response<Void?>>
    suspend fun deleteActivityFromUser(user_id:String,activity_id:String) :  Flow<Response<Void?>>

    suspend fun addActivity(activity:Activity) : Flow<Response<Void?>>
    suspend fun updateActivityInvites(activity_id: String,invites:ArrayList<String>) : Flow<Response<Void?>>
    suspend fun addUserToActivityInvites(activity: Activity,user_id:String) : Flow<Response<Void?>>
    suspend fun leaveLiveActivity(activity_id: String,user_id:String) : Flow<Response<Void?>>
    suspend fun removeUserFromActivityInvites(activity: Activity,user_id:String) : Flow<Response<Void?>>
    suspend fun hideActivity(activity_id: String,user_id:String) : Flow<Response<Void?>>
    suspend fun deleteActivity(id:String) : Flow<Response<Void?>>
    suspend fun joinActiveUser(live_activity_id:String,user_id:String,profile_url:String,username:String) : Flow<Response<Void?>>

    suspend fun getActivitiesForUser(id:String) : Flow<Response<List<Activity>>>
    suspend fun getMoreActivitiesForUser(id:String) : Flow<Response<List<Activity>>>

    suspend fun getActiveUsers(id:String) : Flow<Response<List<ActiveUser>>>
    suspend fun getMoreActiveUsers(id:String) : Flow<Response<List<ActiveUser>>>

    suspend fun addActiveUser(activeUser: ActiveUser) : Response<Boolean>
    suspend fun deleteActiveUser(id: String) :  Flow<Response<Void?>>


}