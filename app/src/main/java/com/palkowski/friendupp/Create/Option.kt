package com.palkowski.friendupp.Create

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.palkowski.friendupp.R


enum class Option(val label: String, val icon: Int) {
    FRIENDS("Friends", R.drawable.ic_hand_300),
    PUBLIC("Public", R.drawable.ic_public_300)
}

class SelectedOptionState(initialOption: Option) {
    private var _option by mutableStateOf(initialOption, structuralEqualityPolicy())

    var option: Option
        get() = _option
        set(value) {
            _option = value
        }

    companion object {
        val Saver: Saver<SelectedOptionState, *> = listSaver(
            save = {   listOf(
                it.option,
            )
            },
            restore = {
                SelectedOptionState(
                    initialOption = it[0]
                )
            }
        )
    }
}

@Composable
fun rememberSelectedOptionState(initialOption: Option): SelectedOptionState {
    val savedState = rememberSaveable(saver = SelectedOptionState.Saver) {
        SelectedOptionState(initialOption)
    }

    val state = remember { mutableStateOf(savedState) }

    return state.value
}