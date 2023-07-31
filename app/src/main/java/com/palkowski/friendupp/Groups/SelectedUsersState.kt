package com.palkowski.friendupp.Groups

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver

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