package com.example.friendupp.ActivityUi

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.friendupp.Components.Calendar.HorizontalDateState2
import com.example.friendupp.Components.TimePicker.TimeState
import com.example.friendupp.Components.TimePicker.rememberTimeState
import com.example.friendupp.Create.*

class ActivityState(
    val titleState: TitleState,
    val descriptionState: DescriptionState,
    val timeStartState: TimeState,
    val timeEndState: TimeState,
    val startDateState: HorizontalDateState2,
    val endDateState: HorizontalDateState2,
    val selectedOptionState: SelectedOptionState
) {
    companion object {
        val Saver: Saver<ActivityState, *> = Saver<ActivityState, String>(
            save = { state ->
                listOf(
                    state.titleState,
                    state.timeStartState,
                    state.timeEndState,
                    state.startDateState,
                    state.endDateState,
                    state.selectedOptionState,
                    state.descriptionState
                ).joinToString(",")
            },
            restore = { savedStateString ->
                val savedStates = savedStateString.split(",")
                ActivityState(
                    titleState = TitleState(),
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
                    descriptionState = DescriptionState()
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
    initialDescription:String
): ActivityState {
    val titleState = remember { TitleState() }
    titleState.text = initialTitle

    val timeStartState = rememberTimeState(initialStartHours, initialStartMinutes)
    val timeEndState = rememberTimeState(initialEndHours, initialEndMinutes)

    val startDateState = remember {
        HorizontalDateState2(
            selectedDay = initialStartDay,
            selectedMonth = initialStartMonth,
            selectedYear = initialStartYear
        )
    }
    val descriptionState = remember{DescriptionState()}

    val endDateState = remember {
        HorizontalDateState2(
            selectedDay = initialEndDay,
            selectedMonth = initialEndMonth,
            selectedYear = initialEndYear
        )
    }

    val selectedOptionState = rememberSelectedOptionState(initialOption)

    return ActivityState(
        titleState = titleState,
        timeStartState = timeStartState,
        timeEndState = timeEndState,
        startDateState = startDateState,
        endDateState = endDateState,
        selectedOptionState = selectedOptionState,
        descriptionState=descriptionState
    )
}