package com.example.friendupp.ChatUi

import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.Login.textFieldStateSaver

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