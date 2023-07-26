package com.example.friendupp.di

import android.net.Uri
import android.util.Log
import com.example.friendupp.await1
import com.example.friendupp.model.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Singleton


@Suppress("IMPLICIT_CAST_TO_ANY")
@Singleton
@ExperimentalCoroutinesApi
class ChatRepositoryImpl @Inject constructor(
    private val messagesRef: CollectionReference,
    private val chatCollectionsRef: CollectionReference,
    private val activitiesCollectionRef: CollectionReference,
    private val resStorage: StorageReference,
    private val lowResStorage: StorageReference,
) : ChatRepository {
    private var lastVisibleData: DocumentSnapshot? = null
    private var lastVisibleDataGroup: DocumentSnapshot? = null
    private var lastVisibleChatCollectionData: DocumentSnapshot? = null
    private  var loaded_messages: ArrayList<ChatMessage> = ArrayList()
    override suspend fun getChatCollection(id: String): Flow<Response<Chat>> = flow {
        try {
            emit(  Response.Loading)
            val documentSnapshot = chatCollectionsRef.document(id).get().await()
            val response = if (documentSnapshot != null && documentSnapshot.exists()) {
                val activity = documentSnapshot.toObject<Chat>()
                if (activity != null) {
                    Response.Success(activity)
                } else {
                    Response.Failure(
                        e = SocialException(
                            "document_null",
                            Exception()
                        )
                    )
                }
            } else {
                Response.Failure(
                    e = SocialException(
                        "getChatCollection exception",
                        Exception()
                    )
                )
            }
            emit(response)
        } catch (e: Exception) {
            emit(Response.Failure(
                e = SocialException(
                    "getChatCollection exception",
                    e
                )
            ))
        }
    }

    override suspend fun reportChat(id: String): Flow<Response<Boolean>> = flow {
        try {
            val response = chatCollectionsRef.document(id).update("reports",FieldValue.increment(1)).await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Failure(
                e = SocialException(
                    "getChatCollection exception",
                    e
                )
            ))
        }
    }
    override suspend fun blockChat(id: String): Flow<Response<Boolean>> = flow {
        try {
            val response = chatCollectionsRef.document(id).update("blocked",true).await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Failure(
                e = SocialException(
                    "getChatCollection exception",
                    e
                )
            ))
        }
    }


    suspend fun keepTrying(triesRemaining: Int, storageRef: StorageReference): String {
        if (triesRemaining < 0) {
            throw TimeoutException("out of tries")
        }

        return try {
            val url = storageRef.downloadUrl.await()
            url.toString()
        } catch (error: Exception) {
            when (error) {
                is StorageException -> {
                    if (error.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                        delay(1000)
                        keepTrying(triesRemaining - 1, storageRef)
                    } else {
                        println(error)
                        throw error
                    }
                }
                else -> {
                    println(error)
                    throw error
                }
            }
        }
    }
    override suspend fun updateActivityImage(
        id: String,
        imageUri: Uri
    ): Flow<Response<String>> = flow {
        try {
            if (imageUri != null) {
                Log.d("Createdebug",id)

                val fileName = id
                try {
                    resStorage.child("images/$fileName" + "_1080x1080").delete().await1()
                } catch (e: StorageException) {

                }
                val imageRef = resStorage.child("images/$fileName")
                imageRef.putFile(imageUri).await1()
                val reference = resStorage.child("images/$fileName" + "_1080x1080")
                val url = keepTrying(6, reference)

                chatCollectionsRef.document(id).update("imageUrl",url).await()
                activitiesCollectionRef.document(id).update("image",url).await()


                emit(Response.Success(url))

            }
        } catch (e: Exception) {
            Log.d("ImagePicker", "try addProfilePictureToStorage EXCEPTION")
            emit(
                Response.Failure(
                    e = SocialException(
                        "addProfilePictureToStorage exception",
                        Exception()
                    )
                )
            )
        }
    }
    override suspend fun updateGroupImage(
        id: String,
        imageUri: Uri
    ): Flow<Response<String>> = flow {
        try {
            if (imageUri != null) {
                Log.d("Createdebug",id)

                val fileName = id
                try {
                    resStorage.child("images/$fileName" + "_1080x1080").delete().await1()
                } catch (e: StorageException) {

                }
                val imageRef = resStorage.child("images/$fileName")
                imageRef.putFile(imageUri).await1()
                val reference = resStorage.child("images/$fileName" + "_1080x1080")
                val url = keepTrying(6, reference)

                chatCollectionsRef.document(id).update("imageUrl",url).await()


                emit(Response.Success(url))

            }
        } catch (e: Exception) {
            Log.d("ImagePicker", "try addProfilePictureToStorage EXCEPTION")
            emit(
                Response.Failure(
                    e = SocialException(
                        "addProfilePictureToStorage exception",
                        Exception()
                    )
                )
            )
        }
    }
    override suspend fun addImageFromGalleryToStorage(
        id: String,
        imageUri: Uri
    ): Flow<Response<String>> = flow {
        try {
            if (imageUri != null) {
                Log.d("Createdebug",id)

                val fileName = id
                try {
                    resStorage.child("images/$fileName" + "_1080x1080").delete().await1()
                } catch (e: StorageException) {

                }
                val imageRef = resStorage.child("images/$fileName")
                imageRef.putFile(imageUri).await1()
                val reference = resStorage.child("images/$fileName" + "_1080x1080")
                val url = keepTrying(6, reference)
                emit(Response.Success(url))
            }
        } catch (e: Exception) {
            Log.d("ImagePicker", "try addProfilePictureToStorage EXCEPTION")
            emit(
                Response.Failure(
                    e = SocialException(
                        "addProfilePictureToStorage exception",
                        Exception()
                    )
                )
            )
        }
    }
    override suspend fun addLoweResImageFromGalleryToStorage(
        id: String,
        imageUri: Uri
    ): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            if (imageUri != null) {
                val fileName = id
                val imageRef = lowResStorage.child("images/$fileName")
                imageRef.putFile(imageUri).await1()
                val reference = lowResStorage.child("images/images/$fileName" + "_320x320")
                val url = keepTrying(6, reference)
                emit(Response.Success(url))
            }
        } catch (e: Exception) {
            Log.d("ImagePicker", "try addLoweResImageFromGalleryToStorage EXCEPTION")
            emit(
                Response.Failure(
                    e = SocialException(
                        "addProfilePictureToStorage exception",
                        Exception()
                    )
                )
            )
        }
    }
    override suspend fun addChatCollection(chatCollection: Chat): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val chatCollectionId = chatCollection.id
            val addition =
                chatCollectionsRef.document(chatCollectionId!!).set(chatCollection).await()
            emit(Response.Success(addition))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addChatCollection exception", Exception())))
        }
    }

    override suspend fun deleteChatCollection(id: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion = chatCollectionsRef.document(id).delete().await()

            emit(Response.Success(deletion))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "deleteChatCollection exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun addGroupHighlight(
        group_id: String,
        text_message: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion =
                chatCollectionsRef.document(group_id).update("highlited_message", text_message)
                    .await()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addGroupHighlight exception", Exception())))
        }
    }

    override suspend fun removeGroupHighlight(group_id: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion = chatCollectionsRef.document(group_id)
                .update("highlited_message", FieldValue.delete()).await()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "removeGroupHighlight exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun updateChatCollectionRecentMessage(
        id: String,
        recentMessage: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update =
                chatCollectionsRef.document(id).update("recentMessage", recentMessage).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "updateChatCollectionRecentMessage exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun updateChatCollectionRecentMessage(
        id: String,
        recent_message_time: String,
        recent_message: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update = chatCollectionsRef.document(id).update(
                "recent_message", recent_message,
                "recent_message_time", recent_message_time
            ).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "updateChatCollectionRecentMessage exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun updateChatCollectionMembers(
        members_list: List<String>,
        id: String
    ): Flow<Response<Void?>> = flow {
        Log.d("updateChatCollectionMembers",members_list.toString())
        try {
            emit(Response.Loading)
            val membersArray = members_list.toTypedArray() // Convert list to array
            val update = chatCollectionsRef.document(id).update("members", FieldValue.arrayUnion(*membersArray)).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "updateChatCollectionMembers exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun updateChatCollectionInvites(
        members_list: List<String>,
        id: String
    ): Flow<Response<Void?>> = flow {
        Log.d("updateChatCollectionMembers",members_list.toString())
        try {
            emit(Response.Loading)
            val membersArray = members_list.toTypedArray() // Convert list to array
            val update = chatCollectionsRef.document(id).update("invites", FieldValue.arrayUnion(*membersArray)).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "updateChatCollectionMembers exception",
                        Exception()
                    )
                )
            )
        }
    }
    override suspend fun updateChatCollectionName(
        chatCollectionName: String,
        id: String
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update = chatCollectionsRef.document(id).update("name", chatCollectionName).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "updateChatCollectionName exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun getMessage(
        chat_collection_id: String,
        message_id: String
    ): Flow<Response<ChatMessage>> = callbackFlow {
        messagesRef.document(chat_collection_id).collection("messages").document(message_id)
            .get().addOnSuccessListener { documentSnapshot ->
                val response = if (documentSnapshot != null) {
                    val activity = documentSnapshot.toObject<ChatMessage>()
                    Response.Success(activity)
                } else {
                    Response.Failure(e = SocialException("getMessage document null", Exception()))
                }
                trySend(response as Response<ChatMessage>).isSuccess
            }
    }



    override suspend fun getMessages(chatCollectionId: String, currentTime: String): Flow<Response<ChatMessage>> =
        callbackFlow {
            Log.d("ChatDebug", "getMessages called")
            val registration = messagesRef.document(chatCollectionId)
                .collection("messages")
                .orderBy("sent_time", Query.Direction.DESCENDING)
                .endAt(currentTime)
                .addSnapshotListener { snapshots, exception ->
                    if (exception != null) {
                        channel.close(exception)
                        return@addSnapshotListener
                    }

                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val message = dc.document.toObject(ChatMessage::class.java)
                                trySend(Response.Success(message))
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val message = dc.document.toObject(ChatMessage::class.java)
                                trySend(Response.Success(message))
                            }
                            DocumentChange.Type.REMOVED -> {
                                val message = dc.document.toObject(ChatMessage::class.java)
                                trySend(Response.Success(message))
                            }
                        }
                    }

                }

            awaitClose {
                registration.remove()
            }
        }

    //todo ::PAGINATION
    override suspend fun getFirstMessages(chatCollectionId: String, currentTime: String): Flow<Response<ArrayList<ChatMessage>>> =

        callbackFlow {
            trySend(Response.Loading)
            Log.d("CHATDEBUG", "GET FIRST MESSAGES")
            Log.d("CHATDEBUG", currentTime)
            messagesRef.document(chatCollectionId)
                .collection("messages")
                .orderBy("sent_time", Query.Direction.DESCENDING)
                .startAfter(currentTime)
                .limit(25)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newMessages = ArrayList<ChatMessage>()
                            for (document in documents) {
                                val message = document.toObject<ChatMessage>()
                                Log.d(
                                    "CHATDEBUG",
                                  message.toString()
                                )
                                if (message != null) {
                                    newMessages.add(message)
                                }
                            }

                            lastVisibleData = documents[documents.size - 1]
                            trySend(Response.Success(ArrayList(newMessages)))
                        } else {
                            // No more messages to load
                            trySend(Response.Failure(SocialException("No more messages to load", Exception())))
                        }
                    } else {
                        val exception = task.exception
                        trySend(Response.Failure(SocialException("Failed to get more messages", exception ?: Exception())))
                    }
                }

            awaitClose {}
        }
    //todo ::PAGINATION

    override suspend fun getMoreMessages(chatCollectionId: String): Flow<Response<ArrayList<ChatMessage>>> =
        callbackFlow {
            Log.d("CHATDEBUG", "getMoreMessages repo" + lastVisibleData?.toString())

            messagesRef.document(chatCollectionId)
                .collection("messages")
                .orderBy("sent_time", Query.Direction.DESCENDING)
                .startAfter(lastVisibleData?.data?.get("sent_time"))
                .limit(15)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("CHATDEBUG", "success repo")
                        val documents = task.result?.documents
                        Log.d("CHATDEBUG", "docs" + documents.toString())
                        if (documents != null && documents.isNotEmpty()) {
                            val newMessages = ArrayList<ChatMessage>()
                            for (document in documents) {
                                val message = document.toObject<ChatMessage>()
                                if (message != null) {
                                    newMessages.add(message)
                                    Log.d("CHATDEBUG", message.toString())
                                }
                            }
                            lastVisibleData = documents[documents.size - 1]
                            val newInstances = ArrayList(newMessages)
                            trySend(Response.Success(newInstances))
                        } else {
                            // No more messages to load
                            trySend(Response.Failure(SocialException("No more messages to load", Exception())))
                        }
                    } else {
                        Log.d("CHATDEBUG", "failed repo")
                        val exception = task.exception
                        trySend(Response.Failure(SocialException("Failed to get more messages", exception ?: Exception())))
                    }
                }

            awaitClose {}
        }
    override suspend fun getGroups(id: String): Flow<Response<ArrayList<Chat>>> =
        callbackFlow {
            lastVisibleDataGroup=null
            chatCollectionsRef.whereEqualTo("type","group").whereArrayContains("members",id)
                .orderBy("recent_message_time", Query.Direction.DESCENDING).limit(5)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newMessages = ArrayList<Chat>()
                            for (document in documents) {
                                val message = document.toObject<Chat>()
                                if (message!=null){
                                    newMessages.add(message)
                                }
                            }
                            lastVisibleDataGroup= documents[documents.size - 1]
                            trySend(Response.Success(newMessages))

                        }
                    } else {
                        // There are no more messages to load
                        trySend(Response.Failure(e=SocialException(message="failed to get more messages",e=Exception())))
                    }

                }

            awaitClose{
            }
        }

    override suspend fun getMoreGroups(id: String): Flow<Response<ArrayList<Chat>>> =
        callbackFlow {

            chatCollectionsRef.whereEqualTo("type","group").whereArrayContains("members",id)   .orderBy("recent_message_time", Query.Direction.DESCENDING)
                .startAfter(lastVisibleDataGroup?.data?.get("recent_message_time")).limit(6)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newMessages = ArrayList<Chat>()
                            for (document in documents) {
                                val message = document.toObject<Chat>()
                                if (message!=null){
                                    newMessages.add(message)
                                }
                            }
                            lastVisibleDataGroup= documents[documents.size - 1]
                            trySend(Response.Success(newMessages))

                        }
                    } else {
                        // There are no more messages to load
                        trySend(Response.Failure(e=SocialException(message="failed to get more groups",e=Exception())))
                    }

                }

            awaitClose{
            }
        }
    override suspend fun getChatCollections(user_id: String): Flow<Response<ArrayList<Chat>>> =
        callbackFlow {

            var messages: ArrayList<Chat> = ArrayList()

            lastVisibleChatCollectionData = null
            val registration = chatCollectionsRef.whereArrayContains("members", user_id)
                .orderBy("recent_message_time", Query.Direction.DESCENDING).limit(10)
                .addSnapshotListener { snapshots, exception ->
                    if (exception != null) {
                        channel.close(exception)
                        return@addSnapshotListener
                    }
                    var new_messages = ArrayList<Chat>()
                    new_messages.addAll(messages)
                    if (snapshots == null || snapshots.isEmpty()) {
                        lastVisibleChatCollectionData = null
                    } else {
                        lastVisibleChatCollectionData = snapshots.getDocuments()
                            .get(snapshots.size() - 1)
                    }
                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val message = dc.document.toObject(Chat::class.java)
                                new_messages.reverse()
                                new_messages.add(message)
                                new_messages.reverse()
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val message = dc.document.toObject(Chat::class.java)
                                new_messages.add(message)
                            }
                            DocumentChange.Type.REMOVED -> {
                                val message = dc.document.toObject(Chat::class.java)
                                new_messages.remove(message)
                                messages.remove(message)
                            }
                        }

                    }
                    messages.clear()
                    messages.addAll(new_messages)
                    trySend(Response.Success(new_messages))

                }
            awaitClose() {
                registration.remove()
            }
        }


    override suspend fun addMessage(
        chat_collection_id: String,
        message: ChatMessage
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val addition = messagesRef.document(chat_collection_id).collection("messages")
                .document(message.id!!).set(message).await()
            emit(Response.Success(addition))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addMessage exception", Exception())))
        }
    }

    override suspend fun deleteMessage(
        chat_collection_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            Log.d("ProfileDisplay","delete messages")
            emit(Response.Loading)
            val collectionRef = messagesRef.document(chat_collection_id).collection("messages")
            val ref = collectionRef.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                }
            }
            ref.await()
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("deleteMessage exception", Exception())))
        }
    }


}