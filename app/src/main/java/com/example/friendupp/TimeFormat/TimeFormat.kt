package com.example.friendupp.TimeFormat

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

fun getFormattedDate(date: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateTime = dateFormat.parse(date)
    val now = Calendar.getInstance()

    val formatter = if (isSameDay(dateTime, now)) {
        // Today, display only hour and minute
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    } else if (isYesterday(dateTime, now)) {
        // Yesterday, display day of the week and hour and minute
        SimpleDateFormat("EEEE hh:mm a", Locale.getDefault())
    } else {
        // Display month, day, and year
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    }
    Log.d("GETFORMATTEDDATE",formatter.format(dateTime))

    return formatter.format(dateTime)
}

fun isSameDay(dateTime: Date?, now: Calendar): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = dateTime
    return calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
            calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)
}

fun isYesterday(dateTime: Date?, now: Calendar): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = dateTime
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    return calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
            calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)
}
fun getFormattedDateNoSeconds(date: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val dateTime = dateFormat.parse(date)
    val now = Calendar.getInstance()

    val formatter = if (isSameDay(dateTime, now)) {
        // Today, display only hour and minute
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    } else if (isYesterday(dateTime, now)) {
        // Yesterday, display day of the week and hour and minute
        SimpleDateFormat("EEEE hh:mm a", Locale.getDefault())
    } else {
        // Display month, day, and year
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    }

    return formatter.format(dateTime)
}