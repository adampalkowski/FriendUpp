package com.palkowski.friendupp.ChatCollection

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkowski.friendupp.model.Chat
import com.palkowski.friendupp.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatCollectionsViewModel @Inject constructor(
    private val collectionsRepo: ChatCollectionRepository,
) : ViewModel() {
    private val _chatCollectionsListState = mutableStateOf<List<Chat>>(emptyList())
    val chatCollectionsListState: MutableState<List<Chat>> = _chatCollectionsListState

    private val _chatCollectionsResponse = mutableStateOf<Response<List<Chat>>>(
        Response.Success(
            emptyList()
        )
    )
    val chatCollectionsResponse: MutableState<Response<List<Chat>>> = _chatCollectionsResponse

    // Function to fetch chat collections for a chat
    fun getChatCollections(chatId: String) {
        viewModelScope.launch {
            collectionsRepo.getChatCollections(chatId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _chatCollectionsListState.value = response.data ?: emptyList()
                        _chatCollectionsResponse.value = Response.Success(_chatCollectionsListState.value)

                        Log.d("ChatCollectionsViewModel", "Chat collections fetched successfully: $chatId")
                    }
                    is Response.Failure -> {
                        _chatCollectionsListState.value = emptyList()
                        _chatCollectionsResponse.value = Response.Success(emptyList())
                        Log.d("ChatCollectionsViewModel", "Failed to fetch chat collections: $chatId. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        _chatCollectionsResponse.value = response

                        Log.d("ChatCollectionsViewModel", "Fetching chat collections in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }

    // Function to fetch more chat collections for a chat
    fun getMoreChatCollections(chatId: String) {
        viewModelScope.launch {
            Log.d("ChatCollectionsViewModel", "getMoreChatCollections callled: $chatId")

            collectionsRepo.getMoreChatCollections(chatId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _chatCollectionsListState.value = _chatCollectionsListState.value + (response.data ?: emptyList())
                        _chatCollectionsResponse.value = Response.Success(_chatCollectionsListState.value)

                        Log.d("ChatCollectionsViewModel", "More chat collections fetched successfully: $chatId")
                    }
                    is Response.Failure -> {
                        Log.d("ChatCollectionsViewModel", "Failed to fetch more chat collections: $chatId. Error: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("ChatCollectionsViewModel", "Fetching more chat collections in progress...")
                    }
                    else -> { /* Handle other response cases if needed */ }
                }
            }
        }
    }
}