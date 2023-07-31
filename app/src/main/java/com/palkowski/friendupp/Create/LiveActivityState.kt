package com.palkowski.friendupp.Create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.palkowski.friendupp.Components.Calendar.HorizontalDateState2
import com.palkowski.friendupp.Components.TimePicker.TimeState
import com.palkowski.friendupp.Components.TimePicker.rememberTimeState
import com.palkowski.friendupp.Login.TextFieldState
import com.google.android.gms.maps.model.LatLng

class LiveActivityState(
    val note: TextFieldState,
    val timeEndState: TimeState,
    val endDateState: HorizontalDateState2,
    val timeStartState: TimeState,
    val startDateState: HorizontalDateState2,
    var location: LatLng


) {
    companion object {
        val Saver: Saver<LiveActivityState, *> = Saver<LiveActivityState, String>(
            save = { state ->
                listOf(
                    state.note.text,
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
                    state.location.latitude,
                    state.location.longitude,

                ).joinToString(",")
            },
            restore = { savedStateString ->
                val savedStates = savedStateString.split(",")
                LiveActivityState(
                    note =  NoteState().apply { text=savedStates[0] },
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
                    location =  LatLng(savedStates[11].toDouble(),savedStates[12].toDouble()),

                    )
            }
        )
    }
}
@Composable
fun rememberLiveActivityState(
    initialNote: String,
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
    initialLocation:LatLng,
): LiveActivityState {
    // Existing code...
    val noteState = rememberSaveable(saver = NoteStateSaver){ NoteState() }
    val timeStartState = rememberTimeState(initialStartHours, initialStartMinutes)
    val timeEndState = rememberTimeState(initialEndHours, initialEndMinutes)

    val endDateState = remember {
        HorizontalDateState2(
            selectedDay = initialEndDay,
            selectedMonth = initialEndMonth,
            selectedYear = initialEndYear
        )
    }


    val startDateState = remember {
        HorizontalDateState2(
            selectedDay = initialStartDay,
            selectedMonth = initialStartMonth,
            selectedYear = initialStartYear
        )
    }

    val liveActivityState = rememberSaveable(saver = LiveActivityState.Saver) {
        LiveActivityState(
            note = noteState,
            timeStartState = timeStartState,
            timeEndState = timeEndState,
            startDateState = startDateState,
            endDateState = endDateState,
            location = initialLocation
        )
    }

    return liveActivityState
}