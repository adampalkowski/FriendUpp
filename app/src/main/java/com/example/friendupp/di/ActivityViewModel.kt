package com.example.friendupp.di

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.Navigation.getCurrentUTCTime
import com.example.friendupp.Navigation.sendNotification
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.SocialException
import com.example.friendupp.model.User
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.ln

sealed class ActivityEvent {
    object DeleteActivity : ActivityEvent()
}

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repo: ActivityRepository,
    private val chatRepo: ChatRepository,

) : ViewModel() {
    var openDialogState = mutableStateOf(false)
    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location

    var currentlyCreatedActivity = mutableStateOf<Activity?>(null)


    private val _isImageAddedToStorageState = MutableStateFlow<Response<String>?>(null)
    val isImageAddedToStorageFlow: StateFlow<Response<String>?> = _isImageAddedToStorageState

    private val _isImageDeletedFromStorage = MutableStateFlow<Response<String>?>(null)
    val isImageDeletedFromStorage: StateFlow<Response<String>?> = _isImageDeletedFromStorage

    private val _trendingActivitiesListState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val trendingActivitiesListState: State<Response<List<Activity>>> = _trendingActivitiesListState

    private val _closestActivitiesListState = mutableStateOf<Response<List<Activity>>?>(null)
    val closestActivitiesListState: State<Response<List<Activity>>?> = _closestActivitiesListState
    private val _closestActivitiesDateListState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val closestActivitiesDateListState: State<Response<List<Activity>>> = _closestActivitiesDateListState

    private val _closestFilteredActivitiesListState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val closestFilteredActivitiesListState: State<Response<List<Activity>>> = _closestFilteredActivitiesListState

    private val _closestMoreFilteredActivitiesListState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val closestMoreFilteredActivitiesListState: State<Response<List<Activity>>> = _closestMoreFilteredActivitiesListState

    private val _moreclosestActivitiesListState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val moreclosestActivitiesListState: State<Response<List<Activity>>> = _moreclosestActivitiesListState

    private val _moreclosestActivitiesListStateError = mutableStateOf<Response<Void?>?>(null)
    val moreclosestActivitiesListStateError: State<Response<Void?>?> = _moreclosestActivitiesListStateError

    private val _activitiesListState = mutableStateOf<Response<List<Activity>>?>(null)
    val activitiesListState: State<Response<List<Activity>>?> = _activitiesListState

    private val _bookmarkedActivitiesState = mutableStateOf<Response<List<Activity>>?>(null)
    val bookmarkedActivitiesState: State<Response<List<Activity>>?> = _bookmarkedActivitiesState
    private val _moreBookmarkedActivitiesState = mutableStateOf<Response<List<Activity>>?>(null)
    val moreBookmarkedActivitiesState: State<Response<List<Activity>>?> = _moreBookmarkedActivitiesState

    private val _moreActivitiesListState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val moreActivitiesListState: State<Response<List<Activity>>> = _moreActivitiesListState

    private val _userActivitiesState = MutableStateFlow<Response<List<Activity>>?>(null)
    val userActivitiesState = _userActivitiesState

    private val _joinedActivitiesState = MutableStateFlow<Response<List<Activity>>?>(null)
    val joinedActivitiesState = _joinedActivitiesState

    private val _userMoreActivitiesState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val userMoreActivitiesState: State<Response<List<Activity>>> = _userMoreActivitiesState
    private val _moreJoinedActivitiesState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val moreJoinedActivitiesState: State<Response<List<Activity>>> = _moreJoinedActivitiesState

    private val _activityState = mutableStateOf<Response<Activity>>(Response.Loading)
    val activityState: State<Response<Activity>> = _activityState

    private val _isActivityAddedState = mutableStateOf<Response<Void?>?>(null)
    val isActivityAddedState: State<Response<Void?>?> = _isActivityAddedState

    private val _addImageToActivityState = MutableStateFlow<Response<String>?>(null)
    val addImageToActivityState: MutableStateFlow<Response<String>?> = _addImageToActivityState

    private val _isImageRemoveFromActivityState = MutableStateFlow<Response<String>?>(null)
    val isImageRemoveFromActivityState: MutableStateFlow<Response<String>?> = _isImageRemoveFromActivityState

    private val _isActivityDeletedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isActivityDeletedState: State<Response<Void?>> = _isActivityDeletedState


    private val _isActivityUnliked = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isActivityUnliked: State<Response<Void?>> = _isActivityUnliked

    private val _isActivityLiked = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isActivityLiked: State<Response<Void?>> = _isActivityLiked
    private val _isActivityBookmarked = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isActivityBookmarked: State<Response<Void?>> = _isActivityBookmarked

    private val _isActivityUnBookmarked = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isActivityUnBookmarked: State<Response<Void?>> = _isActivityUnBookmarked
    private val _isChatDeleted = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isChatDeleted: State<Response<Void?>> = _isChatDeleted

    private val _isInviteAddedToActivity = mutableStateOf<Response<Void?>?>(Response.Success(null))
    val isInviteAddedToActivity: State<Response<Void?>?> = _isInviteAddedToActivity
    private val _tags = MutableStateFlow<ArrayList<String>?>(null)
    val tags: StateFlow<ArrayList<String>?> = _tags
    private val _isRequestAddedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isRequestAddedState: State<Response<Void?>> =_isRequestAddedState

    private val _isRequestRemovedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isRequestRemovedState: State<Response<Void?>> =_isRequestRemovedState
    private val _isInviteRemovedFromActivity =
        mutableStateOf<Response<Void?>?>(Response.Success(null))
    val isInviteRemovedFromActivity: State<Response<Void?>?> = _isInviteRemovedFromActivity
    private val _isActivityInvitesUpdated =
        mutableStateOf<Response<Void?>?>(Response.Success(null))
    val isActivityInvitesUpdated: State<Response<Void?>?> = _isActivityInvitesUpdated

    private val _friendsActivitiesByTagsState = mutableStateOf<Response<List<Activity>>?>(null)
    val friendsActivitiesByTagsState: State<Response<List<Activity>>?> = _friendsActivitiesByTagsState


    fun resetClosestActivites(){
        _closestActivitiesListState.value=null
    }
    init {
        // getActivities()
    }
    fun setTags(tags: ArrayList<String>?){
        _tags.value=tags
    }
    fun setLocation(location: LatLng){
        _location.value=location
    }
    fun getClosestActivities(lat:Double,lng:Double,radius:Double){
        Log.d("getClosestActimivities","CALL")
        Log.d("getClosestActimivities",lat.toString())
        Log.d("getClosestActimivities", lng.toString())

        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            repo.getClosestActivities(lat,lng,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        Log.d("getClosestActimivities","SUCESS")
                        Log.d("getClosestActimivities",response.data.toString())
                        response.data.forEach {
                            Log.d("getClosestActimivities",it.toString())
                            list_without_removed_activites.add(it)
                            checkIfDelete(
                                it.end_time,
                                deleteActivity = {
                                    Log.d("getActivitiesForUser", "delete activity")
                                    deleteActivity(it)
                                    list_without_removed_activites.remove(it)
                                })



                            Log.d("getClosestActivities","list"+list_without_removed_activites.toString())
                            _closestActivitiesListState.value =
                                Response.Success(list_without_removed_activites as List<Activity>)
                        }
                    }
                    is Response.Failure -> {
                        Log.d("getClosestActimivities","Failure")
                        _closestActivitiesListState.value = response
                    }
                    is Response.Loading -> {
                        Log.d("getClosestActimivities","vLoading")
                        _closestActivitiesListState.value = response
                    }
                }


            }
        }
    }
    fun getMoreFilteredClosestActivities(lat:Double,lng:Double,tags:ArrayList<String>,radius:Double){
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            repo.getMoreFilteredClosestActivities(lat,lng,tags,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        response.data.forEach {
                            Log.d("getClosestActimivities",it.toString())
                            list_without_removed_activites.add(it)
                            checkIfDelete(
                                it.end_time,
                                deleteActivity = {
                                    Log.d("getActivitiesForUser", "delete activity")
                                    deleteActivity(it)
                                    list_without_removed_activites.remove(it)
                                })



                            Log.d("getClosestActivities","list"+list_without_removed_activites.toString())
                            _closestActivitiesListState.value =
                                Response.Success(list_without_removed_activites as List<Activity>)
                        }
                    }
                    is Response.Failure -> {
                        _closestActivitiesListState.value = response
                    }
                    is Response.Loading -> {
                        _closestActivitiesListState.value = response
                    }
                }


            }
        }
    }
    fun getClosestFilteredActivities(lat:Double,lng:Double,tags:ArrayList<String>,radius:Double){
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            repo.getClosestFilteredActivities(lat,lng,tags,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        response.data.forEach {
                            Log.d("getClosestActimivities",it.toString())
                            list_without_removed_activites.add(it)
                            checkIfDelete(
                                it.end_time,
                                deleteActivity = {
                                    Log.d("getActivitiesForUser", "delete activity")
                                    deleteActivity(it)
                                    list_without_removed_activites.remove(it)
                                })


                            Log.d("getClosestActivities","list"+list_without_removed_activites.toString())
                            _closestActivitiesListState.value =
                                Response.Success(list_without_removed_activites as List<Activity>)
                        }
                    }
                    is Response.Failure -> {
                        _closestActivitiesListState.value = response
                    }
                    is Response.Loading -> {
                        _closestActivitiesListState.value = response
                    }
                }


            }
        }
    }
    private fun deleteActivityFromUser(userId: String, activity_id: String) {
        viewModelScope.launch {
            repo.deleteActivityFromUser(userId,activity_id).collect { response ->
                _isActivityInvitesUpdated.value = response
            }
        }
    }
     fun updateActivityCustomization(activityId:String,activitySharing:Boolean,disableChat:Boolean,participantConfirmation:Boolean) {
        viewModelScope.launch {
            repo.updateActivityCustomization(activityId,activitySharing,disableChat,participantConfirmation).collect { response ->
            }
        }
    }

    fun getMoreClosestActivities(lat:Double,lng:Double,radius:Double){
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            repo.getMoreClosestActivities(lat,lng,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        response.data.forEach {
                            Log.d("getClosestActivities",it.toString())
                            list_without_removed_activites.add(it)
                            checkIfDelete(
                                it.end_time,
                                deleteActivity = {
                                    Log.d("getActivitiesForUser", "delete activity")
                                    deleteActivity(it)
                                    list_without_removed_activites.remove(it)
                                })


                            Log.d("getClosestActivities","list"+list_without_removed_activites.toString())
                            _moreclosestActivitiesListState.value =
                                Response.Success(list_without_removed_activites as List<Activity>)
                        }
                    }
                    is Response.Failure -> {
                        _moreclosestActivitiesListStateError.value = response
                    }
                    is Response.Loading -> {
                        _moreclosestActivitiesListState.value = response
                    }
                }


            }
        }
    }
    fun getActivity(id: String) {
        viewModelScope.launch {
            repo.getActivity(id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        checkIfDelete(
                            response.data
                                .end_time,
                            deleteActivity = {
                                Log.d("getActivitiesForUser", "delete activity")
                                deleteActivity(response.data)

                            })


                        _activityState.value = response
                        }   else->{}

                }


            }
        }
    }

    fun getMoreActivitiesForUser(id: String?) {
        Log.d("ActivityLoadInDebug", "Get more activites called")

        if (id == null) {
            _moreActivitiesListState.value = Response.Failure(
                SocialException(
                    "getActivitiesForUser error id is null",
                    Exception()
                )
            )
        } else {
            viewModelScope.launch {
                val list_without_removed_activites: ArrayList<Activity> = ArrayList()
                repo.getMoreActivitiesForUser(id).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data.forEach {
                                list_without_removed_activites.add(it)
                                checkIfDelete(
                                    it.end_time,
                                    deleteActivity = {
                                        Log.d("getActivitiesForUser", "delete activity")
                                        deleteActivity(it)
                                        list_without_removed_activites.remove(it)
                                    })


                                _moreActivitiesListState.value =
                                    Response.Success(list_without_removed_activites as List<Activity>)
                            }
                        }
                        is Response.Failure -> {
                            _moreActivitiesListState.value = response
                        }
                        is Response.Loading -> {
                            _moreActivitiesListState.value = response
                        }
                    }


                }
            }
        }

    }
    fun getBookmarkedActivities(id: String?) {
        _bookmarkedActivitiesState.value=Response.Loading
        Log.d("ActivityLoadInDebug", "First get bookmarked call")
        if (id == null) {
            _bookmarkedActivitiesState.value = Response.Failure(
                SocialException(
                    "getBookmarkedActivities error id is null",
                    Exception()
                )
            )
        } else {
            viewModelScope.launch {
                val list_without_removed_activites: ArrayList<Activity> = ArrayList()
                repo.getBookmarkedActivities(id).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data.forEach {
                                list_without_removed_activites.add(it)
                                /*todo if it.date is not a date there is error what then*/

                                checkIfDelete(
                                    it.end_time,
                                    deleteActivity = {
                                        Log.d("getActivitiesForUser", "delete activity")
                                        deleteActivity(it)
                                        list_without_removed_activites.remove(it)
                                    })

                                _bookmarkedActivitiesState.value =
                                    Response.Success(list_without_removed_activites as List<Activity>)
                            }
                        }
                        is Response.Failure -> {
                            _bookmarkedActivitiesState.value = response
                        }
                        is Response.Loading -> {
                            _bookmarkedActivitiesState.value = response
                        }
                    }


                }
            }
        }

    }

    fun getMoreBookmarkedActivities(id: String?) {

        if (id == null) {
            _moreBookmarkedActivitiesState.value = Response.Failure(
                SocialException(
                    "getMoreBookmarkedActivities error id is null",
                    Exception()
                )
            )
        } else {
            viewModelScope.launch {
                val list_without_removed_activites: ArrayList<Activity> = ArrayList()
                repo.getMoreBookmarkedActivities(id).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data.forEach {
                                list_without_removed_activites.add(it)
                                checkIfDelete(
                                    it.end_time,
                                    deleteActivity = {
                                        Log.d("getActivitiesForUser", "delete activity")
                                        deleteActivity(it)
                                        list_without_removed_activites.remove(it)
                                    })


                                _moreBookmarkedActivitiesState.value =
                                    Response.Success(list_without_removed_activites as List<Activity>)
                            }
                        }
                        is Response.Failure -> {
                            _moreBookmarkedActivitiesState.value = response
                        }
                        is Response.Loading -> {
                            _moreBookmarkedActivitiesState.value = response
                        }
                    }


                }
            }
        }

    }
    fun getActivitiesForUser(id: String?) {
        _activitiesListState.value=Response.Loading
        Log.d("ActivityLoadInDebug", "First get activity call")
        if (id == null) {
            _activitiesListState.value = Response.Failure(
                SocialException(
                    "getActivitiesForUser error id is null",
                    Exception()
                )
            )
        } else {
            viewModelScope.launch {
                val list_without_removed_activites: ArrayList<Activity> = ArrayList()
                repo.getActivitiesForUser(id).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data.forEach {
                                list_without_removed_activites.add(it)
                                /*todo if it.date is not a date there is error what then*/

                                 checkIfDelete(
                                    it.end_time,
                                    deleteActivity = {
                                        Log.d("getActivitiesForUser", "delete activity")
                                        deleteActivity(it)
                                        list_without_removed_activites.remove(it)
                                    })

                                _activitiesListState.value =
                                    Response.Success(list_without_removed_activites as List<Activity>)
                            }
                        }
                        is Response.Failure -> {
                            _activitiesListState.value = response
                        }
                        is Response.Loading -> {
                            _activitiesListState.value = response
                        }
                    }


                }
            }
        }

    }


    fun addTimes(time1: String, time2: String): String {
        val (hours1, minutes1) = time1.split(":").map { it.toInt() }
        val (hours2, minutes2) = time2.split(":").map { it.toInt() }
        val totalMinutes = (hours1 * 60 + minutes1) + (hours2 * 60 + minutes2)
        val totalHours = totalMinutes / 60
        val finalMinutes = totalMinutes % 60
        return String.format("%02d:%02d", totalHours, finalMinutes)
    }

    fun addActivity(activity: Activity) {
        viewModelScope.launch {
                repo.addActivity(activity).collect { responseAddActivity ->
                    _isActivityAddedState.value = responseAddActivity
                }

           /* }else{
                repo.addImageFromGalleryToStorage(activity.id, activity.image.toUri()).collect{ response ->
                    _isImageAddedToStorageState.value=response
                    when(response){
                        is Response.Success->{
                            val new_url:String=response.data
                            val final_activity=activity.copy(image = new_url)

                            repo.addActivity(final_activity).collect { responseAddActivity ->
                                _isActivityAddedState.value = responseAddActivity
                            }

                        }
                        is Response.Loading->{

                        }
                        is Response.Failure->{
                            _addImageToActivityState.value=Response.Failure(e = SocialException(
                                "image not found in directory",
                                Exception()))
                        }
                    }

                }
            }*/


        }
    }
    fun updateActivityInvites(activity_id: String,invites:ArrayList<String>) {
        viewModelScope.launch {
            repo.updateActivityInvites(activity_id,invites).collect { response ->
                _isActivityInvitesUpdated.value = response
            }
        }
    }

    fun resetActivity() {
        viewModelScope.launch {
            _isActivityAddedState.value = Response.Loading
        }
    }

    fun resetActivityState() {
        viewModelScope.launch {
            _activityState.value = Response.Loading
        }
    }

    fun addUserToActivityInvites(activity: Activity, user_id: String) {
        viewModelScope.launch {
            repo.addUserToActivityInvites(activity, user_id).collect { response ->
                _isInviteAddedToActivity.value = response
            }
        }
    }

    fun removeUserFromActivityInvites(activity: Activity, user_id: String) {
        viewModelScope.launch {
            repo.removeUserFromActivityInvites(activity, user_id).collect { response ->
                _isInviteRemovedFromActivity.value = response
            }
        }
    }
    fun hideActivity(activity_id: String, user_id: String) {
        viewModelScope.launch {
            repo.hideActivity(activity_id, user_id).collect { response ->
                _isInviteRemovedFromActivity.value = response
            }
        }
    }
    fun reportActivity(activity_id: String ){
        viewModelScope.launch {
            repo.reportActivity(activity_id).collect { response ->
                //todo reponse of report
            }
        }
    }
    fun activityAdded() {
        _isActivityAddedState.value = null
    }

    fun likeActivity(id: String, user: User) {
        viewModelScope.launch {
            repo.likeActivity(id, user).collect { response ->
                _isActivityDeletedState.value = response
            }

        }
    }
    fun likeActivityOnlyId(id: String, user: User) {
        viewModelScope.launch {
            repo.likeActivityOnlyId(id, user).collect { response ->
                _isActivityLiked.value = response
            }
        }
    }

    fun bookMarkActivity(activity_id: String, user_id:String) {
        viewModelScope.launch {
            repo.bookMarkActivity(activity_id, user_id).collect { response ->
                _isActivityBookmarked.value = response
            }
        }
    }
    fun unBookMarkActivity(activity_id: String, user_id:String) {
        viewModelScope.launch {
            repo.unBookMarkActivity(activity_id, user_id).collect { response ->
                _isActivityUnBookmarked.value = response
            }
        }
    }
    fun addActivityParticipant(id: String, user: User) {
        viewModelScope.launch {
            repo.addActivityParticipant(id, user).collect { response ->

            }
        }
    }

    fun unlikeActivity(id: String, user_id: String) {
        viewModelScope.launch {
            repo.unlikeActivity(id, user_id).collect { response ->
                _isActivityDeletedState.value = response
            }
        }

    }
    fun unlikeActivityOnlyId(id: String, user_id: String) {
        viewModelScope.launch {
            repo.unlikeActivityOnlyId(id, user_id).collect { response ->
                _isActivityUnliked.value = response
            }
        }

    }
    fun removeRequestFromActivity(activity_id: String,user_id: String) {
        viewModelScope.launch {
            repo.removeRequestFromActivity(activity_id,user_id).collect { response ->
                _isRequestRemovedState.value=response
            }

        }
    }
    fun addRequestToActivity(activity_id: String,user_id: String) {
        viewModelScope.launch {
            repo.addRequestToActivity(activity_id,user_id).collect { response ->
                _isRequestAddedState.value=response

            }

        }
    }

    fun deleteActivity(activity: Activity,manualyDeleted:Boolean=false) {
        val TAG="DELETEACTIVITYDEBUG"
        viewModelScope.launch {
            repo.deleteActivity(activity.id).collect { response ->
                when(response){
                    is Response.Success->{
                        Log.d(TAG,"DB ACTIVITY DELETE")
                    }
                    else->{}
                }
                _isActivityDeletedState.value = response
            }

            chatRepo.deleteChatCollection(activity.id).collect { response ->
                when(response){
                    is Response.Success->{
                        Log.d(TAG,"Chat  DELETE")
                    }
                    else->{}
                }
                _isChatDeleted.value = response
            }
            if(activity.image!=null){
                repo.removeActivityImage(activity.image!!).collect(){response->
                    when(response){
                        is Response.Success->{
                            Log.d(TAG,"removed image")
                        }
                        else->{}
                    }
                }

            }
            if(!manualyDeleted){
                Log.d(TAG,"INCERASE STATS")
                repo.increaseUserStats(activity.creator_id,activity.participants_ids.size).collect(){
                    response->
                }
            }
        }
    }
    fun deleteActivityWithImage(id: String) {
        viewModelScope.launch {
            repo.deleteActivity(id).collect { response ->
                _isActivityDeletedState.value = response
            }
            chatRepo.deleteChatCollection(id).collect { response ->
                _isChatDeleted.value = response
            }
        }
    }
    fun getUserActivities(id: String) {
        viewModelScope.launch {
            repo.getUserActivities(id).collect { response ->
                _userActivitiesState.value = response
            }
        }
    }
    fun getJoinedActivities(id: String) {
        viewModelScope.launch {
            repo.getJoinedActivities(id).collect { response ->
                _joinedActivitiesState.value = response
            }
        }
    }
    fun getMoreJoinedActivities(id: String) {
        viewModelScope.launch {
            repo.getMoreJoinedActivities(id).collect { response ->
                _moreJoinedActivitiesState.value = response
            }
        }
    }
    fun getMoreUserActivities(id: String) {
        viewModelScope.launch {
            repo.getMoreUserActivities(id).collect { response ->
                _userMoreActivitiesState.value = response
            }
        }
    }



    fun setParticipantImage(activity_id: String, user_id:String,uri: Uri) {
        viewModelScope.launch {
            _addImageToActivityState.value= Response.Loading
            repo.addImageFromGalleryToStorage(activity_id+user_id, uri).collect{ response ->
                _isImageAddedToStorageState.value=response
                when(response){
                    is Response.Success->{
                        val new_url:String=response.data
                        repo.addParticipantImageToActivity(activity_id,user_id,new_url).collect{
                                response->
                            _addImageToActivityState.value=Response.Success(new_url)
                        }

                    }
                    is Response.Loading->{

                    }
                    is Response.Failure->{
                        _addImageToActivityState.value=Response.Failure(e = SocialException(
                            "image not found in directory",
                            Exception()))
                    }
                }

            }



        }
    }
    fun removeParticipantImage(activity_id: String, user_id:String) {
        viewModelScope.launch {
            isImageRemoveFromActivityState.value= Response.Loading
            repo.deleteImageFromHighResStorage(activity_id+user_id).collect{ response ->
                _isImageDeletedFromStorage.value=response
                when(response){
                    is Response.Success->{

                    }
                    is Response.Loading->{

                    }
                    is Response.Failure->{

                        _isImageRemoveFromActivityState.value=Response.Failure(e = SocialException(
                            "exception while removing from storage",
                            Exception()))
                    }
                }

            }
            repo.deleteActivityImageFromFirestoreActivity(activity_id,user_id).collect{
                    response->
                _isImageRemoveFromActivityState.value=Response.Success("removed image from activity and storage")
            }



        }
    }


    fun getClosestFilteredDateActivities(lat:Double,lng:Double,date:String,radius:Double){
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            repo.getClosestFilteredDateActivities(lat,lng,date,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        Log.d(com.example.friendupp.Home.TAG,response.data.size.toString())

                        response.data.forEach {
                            list_without_removed_activites.add(it)
                            checkIfDelete(
                                it.end_time,
                                deleteActivity = {
                                    Log.d("getActivitiesForUser", "delete activity")
                                    deleteActivity(it)
                                    list_without_removed_activites.remove(it)
                                })


                            Log.d("getClosestActivities","getClosestActivities"+"list"+list_without_removed_activites.toString())
                            _closestActivitiesListState.value =
                                Response.Success(list_without_removed_activites as List<Activity>)
                        }
                    }
                    is Response.Failure -> {
                        _closestActivitiesListState.value = response
                    }
                    is Response.Loading -> {
                        _closestActivitiesListState.value = response
                    }
                }


            }
        }
    }
    fun getMoreClosestFilteredDateActivities(lat:Double,lng:Double,date:String,radius:Double){
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            repo.getMoreFilteredDateClosestActivities(lat,lng,date,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        response.data.forEach {
                            Log.d("getClosestActimivities",it.toString())
                            list_without_removed_activites.add(it)
                            checkIfDelete(
                                it.end_time,
                                deleteActivity = {
                                    Log.d("getActivitiesForUser", "delete activity")
                                    deleteActivity(it)

                                    list_without_removed_activites.remove(it)
                                })


                            Log.d("getClosestActivities","list"+list_without_removed_activites.toString())
                            _moreclosestActivitiesListState.value =
                                Response.Success(list_without_removed_activites as List<Activity>)
                        }
                    }
                    is Response.Failure -> {
                        _moreclosestActivitiesListState.value = response
                    }
                    is Response.Loading -> {
                        _moreclosestActivitiesListState.value = response
                    }
                }


            }
        }
    }
}
fun checkIfDelete(endTime: String, deleteActivity: () -> Unit) {
    val currentUtcTime = getCurrentUTCTime()
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

    try {
        val endDate = inputDateFormat.parse(endTime)
        val currentDate = inputDateFormat.parse(currentUtcTime)

        if (currentDate != null && endDate != null && currentDate.after(endDate)) {
            // Delete activity if the current UTC time is after the end time
            deleteActivity()
        }
    } catch (e: Exception) {
        // Handle parsing or comparison errors
        e.printStackTrace()
    }
}