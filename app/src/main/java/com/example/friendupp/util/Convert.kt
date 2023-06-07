package com.example.friendupp.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun getTime():String{
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formatted = current.format(formatter)

    return convertTimeZone(formatted)
}

fun convertTimeZone(dateTimeString: String): String {
    val timeZoneId = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
    val fromZone = ZoneId.of(timeZoneId.toString())
    val toZone = ZoneId.of("Europe/Warsaw")
    val zonedDateTime = ZonedDateTime.of(localDateTime, fromZone)
        .withZoneSameInstant(toZone)
    return formatter.format(zonedDateTime)
}
fun getTimeNoSeconds():String{
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formatted = current.format(formatter)

    return convertTimeZoneNoSeconds(formatted)
}
fun convertTimeZoneNoSeconds(dateTimeString: String): String {
    val timeZoneId = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
    val fromZone = ZoneId.of(timeZoneId.toString())
    val toZone = ZoneId.of("Europe/Warsaw")
    val zonedDateTime = ZonedDateTime.of(localDateTime, fromZone)
        .withZoneSameInstant(toZone)
    return formatter.format(zonedDateTime)
}