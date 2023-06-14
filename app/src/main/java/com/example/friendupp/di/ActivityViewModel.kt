package com.example.friendupp.di

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.SocialException
import com.example.friendupp.model.User
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class ActivityEvent {
    object DeleteActivity : ActivityEvent()
}

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repo: ActivityRepository
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

    private val _closestActivitiesListState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val closestActivitiesListState: State<Response<List<Activity>>> = _closestActivitiesListState
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

    private val _moreActivitiesListState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val moreActivitiesListState: State<Response<List<Activity>>> = _moreActivitiesListState

    private val _userActivitiesState = MutableStateFlow<Response<List<Activity>>>(Response.Loading)
    val userActivitiesState = _userActivitiesState

    private val _joinedActivitiesState = MutableStateFlow<Response<List<Activity>>?>(Response.Loading)
    val joinedActivitiesState = _joinedActivitiesState

    private val _userMoreActivitiesState = mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val userMoreActivitiesState: State<Response<List<Activity>>> = _userMoreActivitiesState

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
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            repo.getClosestActivities(lat,lng,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        response.data.forEach {
                            Log.d("getClosestActimivities",it.toString())
                            list_without_removed_activites.add(it)
                           /*val time_left: String = calculateTimeLeft(
                                it.date,
                                it.start_time,
                                deleteActivity = { event ->
                                    Log.d("getClosestActivities", "delete activity")
                                    deleteActivity(it.id)
                                    it.participants_ids.forEach{user_id->
                                        deleteActivityFromUser(user_id,it.id)
                                    }
                                    list_without_removed_activites.remove(it)
                                })
                            it.time_left = time_left*/

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
    fun getMoreFilteredClosestActivities(lat:Double,lng:Double,tags:ArrayList<String>,radius:Double){
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            repo.getMoreFilteredClosestActivities(lat,lng,tags,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        response.data.forEach {
                            Log.d("getClosestActimivities",it.toString())
                            list_without_removed_activites.add(it)
                           /* val time_left: String = calculateTimeLeft(
                                it.date,
                                it.start_time,
                                deleteActivity = { event ->
                                    Log.d("getClosestActivities", "delete activity")
                                    deleteActivity(it.id)
                                    it.participants_ids.forEach{user_id->
                                        deleteActivityFromUser(user_id,it.id)
                                    }
                                    list_without_removed_activites.remove(it)
                                })
                            it.time_left = time_left*/

                            Log.d("getClosestActivities","list"+list_without_removed_activites.toString())
                            _closestMoreFilteredActivitiesListState.value =
                                Response.Success(list_without_removed_activites as List<Activity>)
                        }
                    }
                    is Response.Failure -> {
                        _closestMoreFilteredActivitiesListState.value = response
                    }
                    is Response.Loading -> {
                        _closestMoreFilteredActivitiesListState.value = response
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
                           /* val time_left: String = calculateTimeLeft(
                                it.date,
                                it.start_time,
                                deleteActivity = { event ->
                                    Log.d("getClosestActivities", "delete activity")
                                    deleteActivity(it.id)
                                    it.participants_ids.forEach{user_id->
                                        deleteActivityFromUser(user_id,it.id)
                                    }
                                    list_without_removed_activites.remove(it)
                                })
                            it.time_left = time_left*/

                            Log.d("getClosestActivities","list"+list_without_removed_activites.toString())
                            _closestFilteredActivitiesListState.value =
                                Response.Success(list_without_removed_activites as List<Activity>)
                        }
                    }
                    is Response.Failure -> {
                        _closestFilteredActivitiesListState.value = response
                    }
                    is Response.Loading -> {
                        _closestFilteredActivitiesListState.value = response
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

    fun getMoreClosestActivities(lat:Double,lng:Double,radius:Double){
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<Activity> = ArrayList()
            repo.getMoreClosestActivities(lat,lng,radius).collect { response ->
                when (response) {
                    is Response.Success -> {
                        response.data.forEach {
                            Log.d("getClosestActivities",it.toString())
                            list_without_removed_activites.add(it)
                           /* val time_left: String = calculateTimeLeft(
                                it.date,
                                it.start_time,
                                deleteActivity = { event ->
                                    Log.d("getClosestActivities", "delete activity")
                                    deleteActivity(it.id)
                                    it.participants_ids.forEach{user_id->
                                        deleteActivityFromUser(user_id,it.id)
                                    }
                                    list_without_removed_activites.remove(it)
                                })
                            it.time_left = time_left*/

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
                           /* val time_left: String = calculateTimeLeft(
                                response.data.date,
                                response.data.start_time,
                                deleteActivity = { event ->
                                    deleteActivity(response.data.id)
                                })
                            response.data.time_left = time_left*/
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
                                list_without_removed_activites.add(it)/*
                                val time_left: String = calculateTimeLeft(
                                    it.date,
                                    it.start_time,
                                    deleteActivity = { event ->
                                        Log.d("activityViewModel", "delete activity")
                                        deleteActivity(it.id)
                                        it.participants_ids.forEach{user_id->
                                            deleteActivityFromUser(user_id,it.id)
                                        }
                                        list_without_removed_activites.remove(it)
                                    })
                                it.time_left = time_left*/


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

                            /*    val time_left: String = calculateTimeLeft(
                                    LocalTime.now().toString(),
                                    it.start_time,
                                    deleteActivity = { event ->
                                        Log.d("getActivitiesForUser", "delete activity")
                                        deleteActivity(it.id)
                                        it.participants_ids.forEach{user_id->
                                            deleteActivityFromUser(user_id,it.id)
                                        }
                                        list_without_removed_activites.remove(it)
                                    })
                                it.time_left = time_left*/

                                Log.d("getActivitiesForUser","list"+list_without_removed_activites.toString())
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
    fun deleteActivity(id: String) {
        viewModelScope.launch {
            repo.deleteActivity(id).collect { response ->
                _isActivityDeletedState.value = response
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



}
