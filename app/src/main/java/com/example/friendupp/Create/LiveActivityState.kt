package com.example.friendupp.Create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.friendupp.Components.Calendar.HorizontalDateState2
import com.example.friendupp.Components.TimePicker.TimeState
import com.example.friendupp.Components.TimePicker.rememberTimeState

class LiveActivityState(
    val note: String,
    val timeEndState: TimeState,
    val endDateState: HorizontalDateState2
) {
    companion object {
        val Saver: Saver<LiveActivityState, *> = Saver<LiveActivityState, String>(
            save = { state ->
                listOf(
                    state.note,
                    state.timeEndState.hours,
                    state.timeEndState.minutes,
                    state.endDateState.selectedDay,
                    state.endDateState.selectedMonth,
                    state.endDateState.selectedYear
                ).joinToString(",")
            },
            restore = { savedStateString ->
                val savedStates = savedStateString.split(",")
                LiveActivityState(
                    note = savedStates[0],
                    timeEndState = TimeState(
                        hours = savedStates[1].toInt(),
                        minutes = savedStates[2].toInt()
                    ),
                    endDateState = HorizontalDateState2(
                        selectedDay = savedStates[3].toInt(),
                        selectedMonth = savedStates[4].toInt(),
                        selectedYear = savedStates[5].toInt()
                    )
                )
            }
        )
    }
}
@Composable
fun rememberActivityState(
    initialEndHours: Int,
    initialEndMinutes: Int,
    initialEndDay: Int,
    initialEndMonth: Int,
    initialEndYear: Int,
    initialNote: String
): LiveActivityState {
    // Existing code...

    val timeEndState = rememberTimeState(initialEndHours, initialEndMinutes)

    val endDateState = remember {
        HorizontalDateState2(
            selectedDay = initialEndDay,
            selectedMonth = initialEndMonth,
            selectedYear = initialEndYear
        )
    }

    val liveActivityState = rememberSaveable(saver = LiveActivityState.Saver) {
        LiveActivityState(
            note = initialNote,
            timeEndState = timeEndState,
            endDateState = endDateState
        )
    }

    return liveActivityState
}