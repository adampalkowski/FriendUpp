package com.palkowski.friendupp.ChatUi

import com.palkowski.friendupp.Login.TextFieldState
import com.palkowski.friendupp.Login.textFieldStateSaver

class MessageState : TextFieldState(validator = ::isMessageValid, errorFor = ::messageValidationError)

private fun messageValidationError(message: String): String {
    return when {
        message.isEmpty() -> "Message cannot be empty"
        message.length > 300 -> "Message should be shorter than 300 characters"
        else -> ""
    }
}

private fun isMessageValid(message: String): Boolean {
    return message.isNotEmpty() && message.length <= 300
}

val MessageStateSaver = textFieldStateSaver(MessageState())