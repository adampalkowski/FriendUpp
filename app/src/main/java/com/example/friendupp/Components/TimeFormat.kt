package com.example.friendupp.Components

import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter

fun connectTimeAndDate(year: Int,month: Int,day:Int,hour:Int,minute:Int):String{
    // Create a LocalDate object from the selected year, month, and day.
    val date = LocalDate.of(year ,month, day)

// Create a LocalTime object from the hour and minute.
    val time = LocalTime.of(hour, minute)

// Combine the date and time into a LocalDateTime object.
    val dateTime = date.atTime(time)

// Define the desired format for the string representation.
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

// Format the LocalDateTime object to a string.
    val formattedDateTime = dateTime.format(formatter)

    return formattedDateTime
}