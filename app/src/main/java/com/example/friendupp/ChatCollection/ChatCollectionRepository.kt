package com.example.friendupp.ChatCollection

import com.example.friendupp.model.Chat
import com.example.friendupp.model.Response
import kotlinx.coroutines.flow.Flow

interface ChatCollectionRepository {
    suspend fun getChatCollections(user_id: String): Flow<Response<ArrayList<Chat>>>
    suspend fun getMoreChatCollections(user_id: String): Flow<Response<ArrayList<Chat>>>
}