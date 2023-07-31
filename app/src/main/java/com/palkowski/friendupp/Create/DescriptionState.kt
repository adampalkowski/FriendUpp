package com.palkowski.friendupp.Create

import com.palkowski.friendupp.Login.TextFieldState
import com.palkowski.friendupp.Login.textFieldStateSaver

class DescriptionState() : TextFieldState(validator = ::isDescriptionValid, errorFor = ::descriptionValidationError)

private fun descriptionValidationError(description: String): String {
    return if (description.length > 500) {
        "Description should be shorter than 500 characters"
    } else {
        ""
    }
}

private fun isDescriptionValid(description: String): Boolean {
    return description.length <= 500
}
val DescriptionStateSaver = textFieldStateSaver(DescriptionState())