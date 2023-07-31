package com.palkowski.friendupp.Create

import com.palkowski.friendupp.Login.TextFieldState
import com.palkowski.friendupp.Login.textFieldStateSaver

class NoteState : TextFieldState(validator = ::isTitleValid, errorFor = ::titleValidationError)

private fun titleValidationError(title: String): String {
    return if (title.isBlank()) {
        "Title cannot be empty"
    } else {
        "Title should be shorter than 100 characters"
    }
}

private fun isTitleValid(title: String): Boolean {
    return title.isNotBlank() && title.length <= 100
}

val NoteStateSaver = textFieldStateSaver(NoteState())