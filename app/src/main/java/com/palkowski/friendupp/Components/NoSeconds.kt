package com.palkowski.friendupp.Components

import java.time.LocalTime

/**
 * Constructs instance of [LocalTime] with hours and minutes only.
 */
public fun LocalTime.noSeconds(): LocalTime {
    return LocalTime.of(hour, minute)
}
