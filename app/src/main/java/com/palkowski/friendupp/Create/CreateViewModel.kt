package com.palkowski.friendupp.Create

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.palkowski.friendupp.Components.Calendar.HorizontalDateState2
import com.palkowski.friendupp.Components.TimePicker.TimeState
import com.palkowski.friendupp.Login.TextFieldState


class CreateViewModel(private val appContext: Context): ViewModel(){
    val titleState = mutableStateOf<TextFieldState?>(null)
    val descriptionState = mutableStateOf<TextFieldState?>(null)
    val selectedOption = mutableStateOf<SelectedOptionState?>(null)
    val timeStartState = mutableStateOf<TimeState?>(null)
    val timeEndState = mutableStateOf<TimeState?>(null)
    val startDateState = mutableStateOf<HorizontalDateState2?>(null)
    val endDateState = mutableStateOf<HorizontalDateState2?>(null)

    fun updateTitleState(titleState: TextFieldState) {
        this.titleState.value = titleState
    }

    fun updateDescriptionState(descriptionState: TextFieldState) {
        this.descriptionState.value = descriptionState
    }

    fun updateSelectedOption(option: SelectedOptionState) {
        this.selectedOption.value = option
    }

    fun updateTimeStartState(timeState: TimeState) {
        this.timeStartState.value = timeState
    }

    fun updateTimeEndState(timeState: TimeState) {
        this.timeEndState.value = timeState
    }

    fun updateStartDateState(dateState: HorizontalDateState2) {
        this.startDateState.value = dateState
    }

    fun updateEndDateState(dateState: HorizontalDateState2) {
        this.endDateState.value = dateState
    }
}