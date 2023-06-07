package com.example.friendupp.Components.Calendar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import java.time.LocalDate


@Composable
fun rememberHorizontalDatePickerState(initialDate: LocalDate = LocalDate.now()) =
    rememberSaveable(saver = HorizontalDateState.Saver) { HorizontalDateState(initialDate) }

class HorizontalDateState(
    selectedDate: LocalDate,
    shouldScrollToSelectedDate: Boolean = true,
) {
    private var _initialDate by mutableStateOf(selectedDate, structuralEqualityPolicy())
    private var _shouldScrollToSelectedDate by mutableStateOf(
        shouldScrollToSelectedDate,
        structuralEqualityPolicy()
    )

    internal fun onScrollCompleted() {
        _shouldScrollToSelectedDate = false
    }

    val shouldScrollToSelectedDate: Boolean
        get() = _shouldScrollToSelectedDate

    val initialDate: LocalDate
        get() = _initialDate

    fun smoothScrollToDate(date: LocalDate) {
        _shouldScrollToSelectedDate = true
        _initialDate = date
    }

    fun setVisibleDates(firstDate: LocalDate?, lastDate: LocalDate?) {
        _firstVisibleDate = firstDate
        _lastVisibleDate = lastDate
    }

    private var _firstVisibleDate by mutableStateOf<LocalDate?>(null)
    val firstVisibleDate get() = _firstVisibleDate

    private var _lastVisibleDate by mutableStateOf<LocalDate?>(null)
    val lastVisibleDate get() = _lastVisibleDate

    companion object {
        val Saver: Saver<HorizontalDateState, *> = listSaver(
            save = {
                listOf(
                    it.initialDate.year,
                    it.initialDate.monthValue,
                    it.initialDate.dayOfMonth,
                    it.shouldScrollToSelectedDate.toString()
                )
            },
            restore = {
                HorizontalDateState(
                    selectedDate = LocalDate.of(
                        it[0].toString().toInt(), // year
                        it[1].toString().toInt(), // month
                        it[2].toString().toInt(), // day
                    ),
                    shouldScrollToSelectedDate = it[3].toString()
                        .toBoolean() // shouldScrollToSelectedDate
                )
            }
        )
    }
}