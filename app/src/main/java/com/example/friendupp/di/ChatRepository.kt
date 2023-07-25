package com.example.friendupp.di

import android.net.Uri
import com.example.friendupp.model.Chat
import com.example.friendupp.model.ChatMessage
import com.example.friendupp.model.Response
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    //handle groups REPO
    suspend fun getChatCollection(id: String): Flow<Response<Chat>>
    suspend fun reportChat(id: String): Flow<Response<Boolean>>
    suspend fun blockChat(id: String): Flow<Response<Boolean>>
    suspend fun addChatCollection(chatCollection: Chat): Flow<Response<Void?>>
    suspend fun deleteChatCollection(id: String): Flow<Response<Void?>>
    suspend fun addGroupHighlight(group_id:String,text_message:String): Flow<Response<Void?>>
    suspend fun removeGroupHighlight(group_id:String): Flow<Response<Void?>>
    suspend fun addImageFromGalleryToStorage(id:String,imageUri: Uri): Flow<Response<String>>
    suspend fun updateActivityImage(id:String,imageUri: Uri): Flow<Response<String>>
    suspend fun updateGroupImage(id:String,imageUri: Uri): Flow<Response<String>>
    suspend fun addLoweResImageFromGalleryToStorage(id:String,imageUri: Uri): Flow<Response<String>>
    suspend fun updateChatCollectionRecentMessage(
        id: String,
        recentMessage: String
    ): Flow<Response<Void?>>

    suspend fun updateChatCollectionMembers(
        members_list: List<String>,
        id: String
    ): Flow<Response<Void?>>
    suspend fun updateChatCollectionInvites(
        members_list: List<String>,
        id: String
    ): Flow<Response<Void?>>

    suspend fun updateChatCollectionName(
        chatCollectionName: String,
        id: String
    ): Flow<Response<Void?>>
    suspend fun updateChatCollectionRecentMessage(
        id: String,recent_message_time:String,recent_message:String
    ): Flow<Response<Void?>>

    // handle  CHATS REPO
    suspend fun getMessage(
        chat_collection_id: String,
        message_id: String
    ): Flow<Response<ChatMessage>>

    suspend fun getMessages(chat_collection_id: String,current_time:String): Flow<Response<ChatMessage>>
    suspend fun getMoreMessages(chat_collection_id: String): Flow<Response<ArrayList<ChatMessage>>>
    suspend fun getFirstMessages(chat_collection_id: String,current_time:String): Flow<Response<ArrayList<ChatMessage>>>
    suspend fun getGroups(id: String): Flow<Response<ArrayList<Chat>>>
    suspend fun getMoreGroups(id: String): Flow<Response<ArrayList<Chat>>>
    suspend fun getChatCollections(user_id: String): Flow<Response<ArrayList<Chat>>>
    suspend fun addMessage(chat_collection_id: String, message: ChatMessage): Flow<Response<Void?>>
    suspend fun deleteMessage(chat_collection_id: String): Flow<Response<Void?>>
}