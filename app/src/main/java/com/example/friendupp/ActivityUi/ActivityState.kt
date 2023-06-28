package com.example.friendupp.ActivityUi

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.friendupp.Components.Calendar.HorizontalDateState2
import com.example.friendupp.Components.TimePicker.TimeState
import com.example.friendupp.Components.TimePicker.rememberTimeState
import com.example.friendupp.Create.*
import com.example.friendupp.Login.TextFieldState
import com.google.android.gms.maps.model.LatLng

class ActivityState(
    val titleState: TextFieldState,
    val descriptionState: TextFieldState,
    val timeStartState: TimeState,
    val timeEndState: TimeState,
    val startDateState: HorizontalDateState2,
    val endDateState: HorizontalDateState2,
    val selectedOptionState: SelectedOptionState,
    val tags: ArrayList<String>,
    var imageUrl: String,
    var location:LatLng

) {
    companion object {
        val Saver: Saver<ActivityState, *> = Saver<ActivityState, String>(
            save = { state ->
                listOf(
                    state.titleState.text,
                    state.timeStartState.hours,
                    state.timeStartState.minutes,
                    state.timeEndState.hours,
                    state.timeEndState.minutes,
                    state.startDateState.selectedDay,
                    state.startDateState.selectedMonth,
                    state.startDateState.selectedYear,
                    state.endDateState.selectedDay,
                    state.endDateState.selectedMonth,
                    state.endDateState.selectedYear,
                    state.selectedOptionState.option,
                    state.descriptionState.text,
                    state.imageUrl,
                    state.location.latitude,
                    state.location.longitude,
                    state.tags.joinToString(",")
                ).joinToString(",")
            },
            restore = { savedStateString ->
                val savedStates = savedStateString.split(",")
                ActivityState(
                    titleState = TitleState().apply { text=savedStates[0] },
                    timeStartState = TimeState(
                        hours = savedStates[1].toInt(),
                        minutes = savedStates[2].toInt()
                    ),
                    timeEndState = TimeState(
                        hours = savedStates[3].toInt(),
                        minutes = savedStates[4].toInt()
                    ),
                    startDateState = HorizontalDateState2(
                        selectedDay = savedStates[5].toInt(),
                        selectedMonth = savedStates[6].toInt(),
                        selectedYear = savedStates[7].toInt()
                    ),
                    endDateState = HorizontalDateState2(
                        selectedDay = savedStates[8].toInt(),
                        selectedMonth = savedStates[9].toInt(),
                        selectedYear = savedStates[10].toInt()
                    ),
                    selectedOptionState = SelectedOptionState(
                        initialOption = Option.valueOf(savedStates[11])
                    ),
                    descriptionState = DescriptionState().apply { text=savedStates[12] },
                    imageUrl = savedStates[13],
                    location =  LatLng(savedStates[14].toDouble(),savedStates[15].toDouble()),
                    tags = ArrayList( savedStates.drop(16)) // Retrieve the tags from the saved state
                )
            }
        )
    }
}
@Composable
fun rememberActivityState(
    initialTitle: String,
    initialStartHours: Int,
    initialStartMinutes: Int,
    initialEndHours: Int,
    initialEndMinutes: Int,
    initialStartDay: Int,
    initialStartMonth: Int,
    initialStartYear: Int,
    initialEndDay: Int,
    initialEndMonth: Int,
    initialEndYear: Int,
    initialOption: Option,
    initialDescription:String,
    initialImageUrl:String,
    initialLocation:LatLng,
    initialTags: ArrayList<String>

): ActivityState {
    val titleState = rememberSaveable(saver = TitleStateSaver){ TitleState() }

    val timeStartState = rememberTimeState(initialStartHours, initialStartMinutes)
    val timeEndState = rememberTimeState(initialEndHours, initialEndMinutes)

    val startDateState = remember {
        HorizontalDateState2(
            selectedDay = initialStartDay,
            selectedMonth = initialStartMonth,
            selectedYear = initialStartYear
        )
    }
    val descriptionState = rememberSaveable(saver = DescriptionStateSaver){DescriptionState()}

    val endDateState = remember {
        HorizontalDateState2(
            selectedDay = initialEndDay,
            selectedMonth = initialEndMonth,
            selectedYear = initialEndYear
        )
    }

    val selectedOptionState = rememberSelectedOptionState(initialOption)

    val activityState = rememberSaveable(saver = ActivityState.Saver) {
        ActivityState(
            titleState = titleState,
            timeStartState = timeStartState,
            timeEndState = timeEndState,
            startDateState = startDateState,
            endDateState = endDateState,
            selectedOptionState = selectedOptionState,
            descriptionState = descriptionState,
            imageUrl = initialImageUrl,
            tags = initialTags,
            location = initialLocation
        )
    }

    return activityState
}