package com.example.friendupp.Message

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.Participants.ParticipantsRepository
import com.example.friendupp.di.ChatRepository
import com.example.friendupp.model.ChatMessage
import com.example.friendupp.model.Participant
import com.example.friendupp.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val chatRepo: ChatRepository
) : ViewModel() {

    private val _messagesList = mutableStateOf<List<ChatMessage>>(emptyList())
    val messagesListState: MutableState<List<ChatMessage>> = _messagesList

    private val _messagesLoading = mutableStateOf(false)
    val messagesLoading: State<Boolean> = _messagesLoading

    fun getMessagesList(): List<ChatMessage> {
        return messagesListState.value
    }

    // Function to fetch messages for a chat
    fun getMessages(chatId: String,current_time:String) {
        viewModelScope.launch {
            _messagesLoading.value = true
            chatRepo.getMessages(chatId, current_time ).collect { response ->
                when (response) {
                    is Response.Success -> {
                        val newMessage = response.data // Assuming response.data is of type ChatMessage
                        _messagesList.value = listOf(newMessage) + _messagesList.value
                        Log.d("MessagesViewModel", "Messages fetched successfully for chat: $chatId")
                    }
                    is Response.Failure -> {
                        _messagesList.value = emptyList()
                        Log.d("MessagesViewModel", "Failed to fetch messages for chat: $chatId. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("MessagesViewModel", "Fetching messages in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
                _messagesLoading.value = false
            }
        }
    }

    // Other properties and functions remain the same

    // Function to fetch the first batch of messages for a chat
    fun getFirstMessages(chatId: String, current_time: String) {
        viewModelScope.launch {
            _messagesLoading.value = true
            chatRepo.getFirstMessages(chatId, current_time).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _messagesList.value = response.data ?: emptyList()
                        Log.d("MessagesViewModel", "First batch of messages fetched successfully for chat: $chatId")
                    }
                    is Response.Failure -> {
                        _messagesList.value = emptyList()
                        Log.d("MessagesViewModel", "Failed to fetch first batch of messages for chat: $chatId. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("MessagesViewModel", "Fetching first batch of messages in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
                _messagesLoading.value = false
            }
        }
    }

    // Function to fetch more messages for a chat
    fun getMoreMessages(chatId: String) {
        viewModelScope.launch {
            chatRepo.getMoreMessages(chatId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        // Append the new batch of messages to the existing list
                        _messagesList.value = _messagesList.value + (response.data ?: emptyList())
                        Log.d("MessagesViewModel", "More messages fetched successfully for chat: $chatId")
                    }
                    is Response.Failure -> {
                        Log.d("MessagesViewModel", "Failed to fetch more messages for chat: $chatId. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("MessagesViewModel", "Fetching more messages in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
    // Function to add a new message to the chat
    fun addMessage(chatId: String, message: ChatMessage) {
        viewModelScope.launch {
            chatRepo.addMessage(chatId, message).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _messagesList.value = _messagesList.value + message
                        Log.d("MessagesViewModel", "Message added successfully to chat: $chatId")
                    }
                    is Response.Failure -> {
                        Log.d("MessagesViewModel", "Failed to add message to chat: $chatId. Error: ${response.e.message}")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to delete a message from the chat
    fun deleteMessage(chatId: String, messageId: String) {
        viewModelScope.launch {
            chatRepo.deleteMessage(chatId, messageId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _messagesList.value = _messagesList.value.filterNot { it.id == messageId }
                        Log.d("MessagesViewModel", "Message deleted successfully from chat: $chatId")
                    }
                    is Response.Failure -> {
                        Log.d("MessagesViewModel", "Failed to delete message from chat: $chatId. Error: ${response.e.message}")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
}