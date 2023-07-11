package com.example.friendupp.Groups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.friendupp.bottomBar.ActivityUi.ActivityState
import com.example.friendupp.Components.Calendar.HorizontalDateState2
import com.example.friendupp.Components.TimePicker.TimeState
import com.example.friendupp.Components.TimePicker.rememberTimeState
import com.example.friendupp.Create.*
import com.google.android.gms.maps.model.LatLng

class SelectedUsersState(
    val list: MutableList<String>,
) {
    companion object {
        val Saver: Saver<SelectedUsersState, List<String>> = Saver(
            save = { state ->
                state.list.toList()
            },
            restore = { savedState ->
                SelectedUsersState(mutableStateListOf(*savedState.toTypedArray()))
            }
        )
    }
}