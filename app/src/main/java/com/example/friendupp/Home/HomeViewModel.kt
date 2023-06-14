package com.example.friendupp.Home

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.friendupp.model.Activity
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel:ViewModel() {
    private val _expandedActivity = MutableStateFlow<Activity?>(null)
    val expandedActivity = _expandedActivity

    private val _user_link = mutableStateOf<String?>(null)
    val user_link: State<String?> = _user_link

    private val _deep_link = mutableStateOf<Uri?>(null)
    val deep_link: State<Uri?> = _deep_link
    fun setExpandedActivity(activityData: Activity){
        _expandedActivity.value=activityData
    }
    fun resetExpandedActivity(){
        _expandedActivity.value=null
    }
    fun setUserLink(link:String){
        _user_link.value=link
    }
    fun resetUserLink() {
        _user_link.value=null
    }

    fun setDeepLink(link: Uri){
        _deep_link.value=link
    }
    fun resetDeepLink() {
        _deep_link.value=null
    }
}