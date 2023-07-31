package com.palkowski.friendupp.Create

import com.palkowski.friendupp.Login.TextFieldState
import com.palkowski.friendupp.Login.textFieldStateSaver



class CustomLocationState : TextFieldState(validator = ::isCustomLocationValid, errorFor = ::customLocationValidationError)

private fun customLocationValidationError(customLocation: String): String {
    return if (customLocation.length > 100) {
        "Description should be shorter than 500 characters"
    } else {
        ""
    }
}

private fun isCustomLocationValid(customLocation: String): Boolean {
    return customLocation.length <= 500
}
val CustomLocationStateSaver = textFieldStateSaver(CustomLocationState())