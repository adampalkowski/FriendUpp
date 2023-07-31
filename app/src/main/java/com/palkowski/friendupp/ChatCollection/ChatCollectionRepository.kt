package com.palkowski.friendupp.ChatCollection

import com.palkowski.friendupp.model.Chat
import com.palkowski.friendupp.model.Response
import kotlinx.coroutines.flow.Flow

interface ChatCollectionRepository {
    suspend fun getChatCollections(user_id: String): Flow<Response<ArrayList<Chat>>>
    suspend fun getMoreChatCollections(user_id: String): Flow<Response<ArrayList<Chat>>>
}