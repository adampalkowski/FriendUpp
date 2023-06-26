package com.example.friendupp.Components.TimePicker

import androidx.compose.runtime.saveable.Saver


import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.text.intl.Locale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class TimeState(hours: Int, minutes: Int) {
    private var _hours by mutableStateOf(hours, structuralEqualityPolicy())
    private var _minutes by mutableStateOf(minutes, structuralEqualityPolicy())

    var hours: Int
        get() = _hours
        set(value) {
            _hours = value
        }

    var minutes: Int
        get() = _minutes
        set(value) {
            _minutes = value
        }

    companion object {
        val Saver: Saver<TimeState, *> = listSaver(
            save = {
                listOf(
                    it.hours,
                    it.minutes
                )
            },
            restore = {
                TimeState(
                    hours = it[0],
                    minutes = it[1]
                )
            }
        )
    }
}

@Composable
fun rememberTimeState(initialHours: Int, initialMinutes: Int): TimeState =
    rememberSaveable(saver = TimeState.Saver) {
        TimeState(
            hours = initialHours,
            minutes = initialMinutes
        )
    }