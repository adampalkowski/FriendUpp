package com.palkowski.friendupp.Login

import java.util.regex.Pattern

class EmailState : TextFieldState(validator = ::isEmailValid, errorFor = ::emailValidationError)

private const val EMAIL_VALIDATION_REGEX = "^(.+)@(.+)\$"

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun emailValidationError(email: String): String {
    return "Invalid email $email"
}

private fun isEmailValid(email: String): Boolean {
    return Pattern.matches(EMAIL_VALIDATION_REGEX, email)
}

val EmailStateSaver = textFieldStateSaver(EmailState())