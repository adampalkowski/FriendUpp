package com.example.friendupp.Home

import androidx.lifecycle.ViewModel
import com.example.friendupp.model.Activity
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel:ViewModel() {
    private val _expandedActivity = MutableStateFlow<Activity?>(null)
    val expandedActivity = _expandedActivity

    fun setExpandedActivity(activityData: Activity){
        _expandedActivity.value=activityData
    }
    fun resetExpandedActivity(){
        _expandedActivity.value=null
    }
}