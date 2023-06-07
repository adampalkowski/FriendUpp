package com.example.friendupp.Login

class PasswordState : TextFieldState(validator = ::isPasswordValid, errorFor = ::passwordValidationError)

private const val MIN_PASSWORD_LENGTH = 4

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun passwordValidationError(password: String): String {
    return if (password.isEmpty()) {
        "Password cannot be empty"
    } else {
        "Password must be at least $MIN_PASSWORD_LENGTH characters long"
    }
}

private fun isPasswordValid(password: String): Boolean {
    return password.length >= MIN_PASSWORD_LENGTH
}

val PasswordStateSaver = textFieldStateSaver(PasswordState())