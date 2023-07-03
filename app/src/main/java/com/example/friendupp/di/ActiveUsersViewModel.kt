package com.example.friendupp.di

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.model.ActiveUser
import com.example.friendupp.model.Response
import com.example.friendupp.model.SocialException
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class ActiveUsersViewModel @Inject constructor(
    private val repo: ActivityRepository
) : ViewModel() {

    private val _activeUsersListState = mutableStateOf<Response<List<ActiveUser>>>(Response.Loading)
    val activeUsersListState: State<Response<List<ActiveUser>>> = _activeUsersListState
    private val _moreActiveUsers = mutableStateOf<Response<List<ActiveUser>>>(Response.Loading)
    val moreActiveUsers: State<Response<List<ActiveUser>>> = _moreActiveUsers


    private val _isActiveUsersAddedState= MutableStateFlow<Response<Boolean>?>(null)
    val isActiveUsersAddedState: StateFlow<Response<Boolean>?> = _isActiveUsersAddedState
    private val _isActiveUsersDeletedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isActiveUsersDeletedState: State<Response<Void?>> = _isActiveUsersDeletedState

    private val _isUserAddedToLiveActivityState = mutableStateOf<Response<Void?>>(Response.Loading)
    val isUserAddedToLiveActivityState: State<Response<Void?>> = _isUserAddedToLiveActivityState

    private val _isLiveActivityLeft = mutableStateOf<Response<Void?>>(Response.Loading)
    val isLiveActivityLeft: State<Response<Void?>> = _isLiveActivityLeft


    private val _granted_permission = MutableStateFlow<Boolean>(false)
    val granted_permission: StateFlow<Boolean> = _granted_permission

    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location
    fun setLocation(location: LatLng){
        _location.value=location
    }
    fun joinActiveUser(live_activity_id:String,user:String,profile_url:String,username:String){
        viewModelScope.launch {
            _isUserAddedToLiveActivityState.value=Response.Loading
            val result= repo.joinActiveUser(live_activity_id,user, profile_url ,username).collect{
                response->
                _isUserAddedToLiveActivityState.value=response
            }
        }


    }
    fun leaveLiveActivity( activity_id:String, user_id:String){

        viewModelScope.launch {
                repo.leaveLiveActivity( activity_id,user_id).collect(){

                }
         }
    }
    fun permissionGranted(){
        _granted_permission.value=true
    }
    fun getActiveUsersForUser(id: String?) {
        if (id == null) {
            _activeUsersListState.value = Response.Failure(
                SocialException(
                    "getActiveUserForUser id passed is null",
                    Exception()
                )
            )
        } else {
            viewModelScope.launch {
                val list_without_removed_activites: ArrayList<ActiveUser> = ArrayList()
                repo.getActiveUsers(id).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data.forEach {
                                list_without_removed_activites.add(it)
                                Log.d("ActiveUsersViewModel_CHECKING",list_without_removed_activites.toString())
                                if(checkIfEnded(it.destroy_time)){
                                    Log.d("ActiveUsersViewModel_CHECKING","NOT EDNED")
                                }else{
                                    Log.d("ActiveUsersViewModel_CHECKING","Ended")
                                    deleteActiveUser(it.creator_id)
                                    list_without_removed_activites.remove(it)
                                    Log.d("ActiveUsersViewModel_CHECKING",list_without_removed_activites.toString())

                                    Log.d("ActiveUsersViewModel_CHECKING", "delete users")
                                }
                                if(list_without_removed_activites.isEmpty()){
                                    Log.d("ActiveUsersViewModel_CHECKING", "Empty")

                                    _activeUsersListState.value =
                                        Response.Success(emptyList())
                                }else{
                                    Log.d("ActiveUsersViewModel_CHECKING",list_without_removed_activites.toString())


                                    _activeUsersListState.value =
                                        Response.Success(list_without_removed_activites as List<ActiveUser>)
                                }


                            }
                        }
                        else->{}
                    }

                }
            }
        }

    }
    fun getMoreActiveUsers(id:String){
        viewModelScope.launch {
            val list_without_removed_activites: ArrayList<ActiveUser> = ArrayList()
            repo.getMoreActiveUsers(id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        response.data.forEach {
                            list_without_removed_activites.add(it)
                            Log.d("ActiveUsersViewModel_CHECKING",list_without_removed_activites.toString())
                            if(checkIfEnded(it.destroy_time)){
                                Log.d("ActiveUsersViewModel_CHECKING","NOT EDNED")
                            }else{
                                Log.d("ActiveUsersViewModel_CHECKING","Ended")
                                deleteActiveUser(it.creator_id)
                                list_without_removed_activites.remove(it)
                                Log.d("ActiveUsersViewModel_CHECKING",list_without_removed_activites.toString())

                                Log.d("ActiveUsersViewModel_CHECKING", "delete users")
                            }
                            if(list_without_removed_activites.isEmpty()){
                                Log.d("ActiveUsersViewModel_CHECKING", "Empty")

                                _moreActiveUsers.value =
                                    Response.Success(emptyList())
                            }else{
                                Log.d("ActiveUsersViewModel_CHECKING",list_without_removed_activites.toString())


                                _moreActiveUsers.value =
                                    Response.Success(list_without_removed_activites as List<ActiveUser>)
                            }


                        }
                    }
                    else->{}
                }

            }
        }
    }
    fun compareDates(date1: String, date2: String, deleteActivity: () -> Unit): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val d1 = format.parse(date1)
        val d2 = format.parse(date2)
        val diffInMilliseconds = d2.time - d1.time
        if (diffInMilliseconds < 0) {
            deleteActivity()
        }

        return TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS)
    }
    fun checkIfEnded(
        destroy_time: String,
    ): Boolean{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val date1Str = getTimeNoSeconds()
        val date2Str = destroy_time
        val date1 = LocalDateTime.parse(date1Str, formatter)
        val date2 = LocalDateTime.parse(date2Str, formatter)
       return date2.isEqual(date1) || date2.isAfter(date1)

    }
    fun calculateTimeLeft(
        date: String,
        time_start: String,
        deleteActivity: (ActivityEvent) -> Unit
    ): String {
        //time

        val difference = compareDates(
            LocalDate.now().toString(),
            date,
            deleteActivity = { deleteActivity(ActivityEvent.DeleteActivity) })
        if (difference.toString().equals("0")) {

            return compareTimes(
                LocalTime.now().toString(),
                time_start,
                deleteActivity = { deleteActivity(ActivityEvent.DeleteActivity) })
        } else {
            return "$difference days"
        }

    }

    fun compareTimes(time1: String, time2: String, deleteActivity: () -> Unit): String {
        val format = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val t1 = format.parse(time1)
        val t2 = format.parse(time2)
        val diffInMilliseconds = t2.time - t1.time
        val hours = TimeUnit.HOURS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS)
        val minutes = TimeUnit.MINUTES.convert(diffInMilliseconds, TimeUnit.MILLISECONDS) -
                TimeUnit.HOURS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS) * 60
        if (minutes < 0 || hours < 0) {
            deleteActivity()
        }


        if (hours > 0) {
            return "$hours hours $minutes minutes"
        } else {
            return "$minutes minutes"

        }
    }

    fun addActiveUser(activeUser: ActiveUser) {
        viewModelScope.launch {
            _isActiveUsersAddedState.value=Response.Loading
            val result= repo.addActiveUser(activeUser)
            _isActiveUsersAddedState.value=result
        }
    }

    fun activeUserAdded() {
        _isActiveUsersAddedState.value = null
    }

    fun deleteActiveUser(id: String) {
        viewModelScope.launch {
            repo.deleteActiveUser(id).collect { response ->
                _isActiveUsersDeletedState.value = response
            }
        }
    }
}
fun getTimeNoSeconds():String{
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formatted = current.format(formatter)

    return convertTimeZoneNoSeconds(formatted)
}
fun convertTimeZoneNoSeconds(dateTimeString: String): String {
    val timeZoneId = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
    val fromZone = ZoneId.of(timeZoneId.toString())
    val toZone = ZoneId.of("Europe/Warsaw")
    val zonedDateTime = ZonedDateTime.of(localDateTime, fromZone)
        .withZoneSameInstant(toZone)
    return formatter.format(zonedDateTime)
}