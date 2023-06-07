package com.example.friendupp.Profile

import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.Login.textFieldStateSaver

class NameState : TextFieldState(validator = ::isNameValid, errorFor = ::nameValidationError)

private fun nameValidationError(name: String): String {
    return when {
        name.isEmpty() -> "Name cannot be empty"
        name.length > 30 -> "Name should be shorter than 30 characters"
        else -> ""
    }
}

private fun isNameValid(name: String): Boolean {
    return name.isNotEmpty() && name.length <= 30
}

val NameStateSaver = textFieldStateSaver(NameState())

class UsernameState : TextFieldState(validator = ::isUsernameValid, errorFor = ::usernameValidationError)

private fun usernameValidationError(username: String): String {
    return when {
        username.length < 4 -> "Username should be at least 4 characters"
        username.length > 25 -> "Username should be shorter than 25 characters"
        else -> ""
    }
}

private fun isUsernameValid(username: String): Boolean {
    return username.length >= 4 && username.length <= 25
}

val UsernameStateSaver = textFieldStateSaver(UsernameState())

class BiographyState : TextFieldState(validator = ::isBiographyValid, errorFor = ::biographyValidationError)

private fun biographyValidationError(biography: String): String {
    return if (biography.length > 500) {
        "Biography should be shorter than 500 characters"
    } else {
        ""
    }
}

private fun isBiographyValid(biography: String): Boolean {
    return biography.length <= 500
}

val BiographyStateSaver = textFieldStateSaver(BiographyState())

class LocationState : TextFieldState(validator = ::isLocationValid, errorFor = ::locationValidationError)

private fun locationValidationError(location: String): String {
    return if (location.length > 50) {
        "Location should be shorter than 50 characters"
    } else {
        ""
    }
}

private fun isLocationValid(location: String): Boolean {
    return location.length <= 50
}

val LocationStateSaver = textFieldStateSaver(LocationState())