package com.palkowski.friendupp.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.palkowski.friendupp.ui.theme.SocialTheme

@Composable
fun TimePicker(
    currentHour: Int,
    currentMinute: Int,
    includeAllHours: Boolean = false,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    val timeList = generateTimeList(currentHour, currentMinute, includeAllHours)
    val listState = rememberLazyListState()
    val selectedIndex = remember {
        mutableStateOf(findNextAvailableTimeIndex(currentHour, currentMinute, timeList))
    }

    LaunchedEffect(listState.layoutInfo) {
        if (timeList.isNotEmpty()) {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val middleIndex = visibleItems.size / 2
            val middleItem = visibleItems[middleIndex]
            val middleTime = timeList[middleItem.index]
            val hour = middleTime / 60
            val minute = middleTime % 60
            selectedIndex.value = middleItem.index
            onTimeSelected(hour, minute)
        }
    }

    Box(modifier = Modifier
        .height(250.dp)
        .width(100.dp)) {
        LazyColumn(state = listState) {
            itemsIndexed(timeList) { index, time ->
                val hour = time / 60
                val minute = time % 60

                // Format hour and minute as desired (e.g., add leading zeroes)
                val formattedHour = hour.toString().padStart(2, '0')
                val formattedMinute = minute.toString().padStart(2, '0')
                Column(Modifier.clickable {
                    selectedIndex.value = index
                    onTimeSelected(hour, minute)
                }) {
                    Box(
                        modifier = Modifier.height(0.5.dp).fillMaxWidth()
                            .background(SocialTheme.colors.uiBorder)
                    )
                    Text(
                        text = "$formattedHour:$formattedMinute",
                        modifier = Modifier.padding(16.dp),
                        color = if (index == selectedIndex.value) Color.Blue else Color.Black,
                    )
                }
            }
        }
    }
}
fun findNextAvailableTimeIndex(currentHour: Int, currentMinute: Int, timeList: List<Int>): Int {
    val currentTime = currentHour * 60 + currentMinute

    // Find the index of the next available time in the time list
    for (index in timeList.indices) {
        if (timeList[index] >= currentTime) {
            return index
        }
    }

    // If no next available time found, select the last time in the list
    return timeList.size - 1
}

fun generateTimeList(currentHour: Int, currentMinute: Int, includeAllHours: Boolean): List<Int> {
    val currentTime = currentHour * 60 + currentMinute
    val timeList = mutableListOf<Int>()
    var time = currentTime + 30

    // Generate time slots with a 30-minute difference starting from the next available time
    while (time < 24 * 60) {
        timeList.add(time)
        time += 30
    }

    // Include all hours if the flag is set to true
    if (includeAllHours) {
        for (hour in 0 until currentHour) {
            for (minute in 0 until 60 step 30) {
                timeList.add(hour * 60 + minute)
            }
        }
    }

    return timeList
}
