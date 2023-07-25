package com.example.friendupp.GroupParticipants

import android.util.Log
import com.example.friendupp.await1
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Participant
import com.example.friendupp.model.Response
import com.example.friendupp.model.SocialException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalCoroutinesApi
class GroupParticipantsRepositoryImpl @Inject constructor(
    private val chatColletionRef: CollectionReference,
    private val messagesRef: CollectionReference,
    private val resStorage: StorageReference,

    // Add other dependencies as needed
) : GroupParticipantsRepository {
    private var loaded_participants: ArrayList<Participant> = ArrayList()
    private var loaded_groups: ArrayList<Chat> = ArrayList()
    private var lastVisibleParticipant: DocumentSnapshot? = null
    private var lastVisibleGroup: DocumentSnapshot? = null
    private var lastVisibleGroupInvite: DocumentSnapshot? = null

    // Implement the functions in InviteRepository interface here
    // For example, to get invites for a user:
    override suspend fun getParticipants(activityId: String): Flow<Response<List<Participant>>> =
        callbackFlow {
            loaded_participants.clear()

            chatColletionRef.document(activityId).collection("participants")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newInvites = ArrayList<Participant>()
                            for (document in documents) {
                                val invite = document.toObject<Participant>()
                                if (invite != null) {
                                    newInvites.add(invite)
                                }
                            }
                            lastVisibleParticipant = documents[documents.size - 1]
                            trySend(Response.Success(newInvites))
                        } else {
                            // No invites found
                            trySend(Response.Failure(SocialException("No invites found" , e =java.lang.Exception())))
                        }
                    } else {
                        // Task failed
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    message = "Failed to get invites",
                                    e=java.lang.Exception()
                                )
                            )
                        )
                    }
                }
            awaitClose { /* Cleanup, if needed */ }
        }// For example, to get invites for a user:


    override suspend fun getMoreParticipants(activityId:  String): Flow<Response<List<Participant>>> =
        callbackFlow {
            chatColletionRef.document(activityId).collection("participants")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisibleParticipant?.get("timestamp")) // Assuming you have a variable to keep track of the last visible invite timestamp
                .limit(10)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newInvites = documents.mapNotNull { document ->
                                document.toObject<Participant>()
                            }
                            loaded_participants.addAll(newInvites)
                            lastVisibleParticipant = documents[documents.size - 1] // Update the last visible invite timestamp for pagination
                            trySend(Response.Success(loaded_participants))
                        } else {
                            // No more invites found
                            trySend(Response.Failure(SocialException("No more loaded_participants found",e=java.lang.Exception())))
                        }
                    } else {
                        // Task failed
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    message = "Failed to get more particpants",
                                    e =java.lang.Exception()
                                )
                            )
                        )
                    }
                }
            awaitClose { /* Cleanup, if needed */ }
        }
    override suspend fun getGroupsInvites(user_id: String): Flow<Response<List<Chat>>> =
        callbackFlow {
            loaded_groups.clear()

            chatColletionRef.whereArrayContains("invites", user_id)
                .orderBy("create_date", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newInvites = ArrayList<Chat>()
                            for (document in documents) {
                                val chat = document.toObject<Chat>()
                                if (chat != null) {
                                    newInvites.add(chat)
                                }
                            }
                            lastVisibleGroupInvite = documents[documents.size - 1]
                            trySend(Response.Success(newInvites))
                        } else {
                            // No chats found
                            trySend(Response.Failure(SocialException("No chats found", e = java.lang.Exception())))
                        }
                    } else {
                        // Task failed
                        trySend(Response.Failure(SocialException(message = "Failed to get chats", e = java.lang.Exception())))
                    }
                }
            awaitClose { /* Cleanup, if needed */ }
        }

    override suspend fun getMoreGroupsInvites(user_id: String): Flow<Response<List<Chat>>> =
        callbackFlow {
            chatColletionRef.whereArrayContains("invites", user_id)
                .orderBy("create_date", Query.Direction.DESCENDING)
                .startAfter(lastVisibleGroupInvite?.get("create_date")) // Assuming you have a variable to keep track of the last visible chat timestamp
                .limit(10)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newChats = documents.mapNotNull { document ->
                                document.toObject<Chat>()
                            }
                            loaded_groups.addAll(newChats)
                            lastVisibleGroupInvite = documents[documents.size - 1] // Update the last visible chat timestamp for pagination
                            trySend(Response.Success(loaded_groups))
                        } else {
                            // No more chats found
                            trySend(Response.Failure(SocialException("No more chats found", e = java.lang.Exception())))
                        }
                    } else {
                        // Task failed
                        trySend(Response.Failure(SocialException(message = "Failed to get more chats", e = java.lang.Exception())))
                    }
                }
            awaitClose { /* Cleanup, if needed */ }
        }

    override suspend fun getGroups(user_id: String): Flow<Response<List<Chat>>> =
        callbackFlow {
            loaded_groups.clear()

            chatColletionRef.whereEqualTo("type","group").whereArrayContains("members",user_id)
                .orderBy("recent_message_time", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newInvites = ArrayList<Chat>()
                            for (document in documents) {
                                val chat = document.toObject<Chat>()
                                if (chat != null) {
                                    newInvites.add(chat)
                                }
                            }
                            lastVisibleGroup = documents[documents.size - 1]
                            trySend(Response.Success(newInvites))
                        } else {
                            // No chats found
                            trySend(Response.Failure(SocialException("No chats found", e = java.lang.Exception())))
                        }
                    } else {
                        // Task failed
                        trySend(Response.Failure(SocialException(message = "Failed to get chats", e = java.lang.Exception())))
                    }
                }
            awaitClose { /* Cleanup, if needed */ }
        }

    override suspend fun getMoreGroups(user_id: String): Flow<Response<List<Chat>>> =
        callbackFlow {
            chatColletionRef.whereEqualTo("type","group").whereArrayContains("members",user_id)
                .orderBy("recent_message_time", Query.Direction.DESCENDING)
                .startAfter(lastVisibleGroup?.get("recent_message_time")) // Assuming you have a variable to keep track of the last visible chat timestamp
                .limit(10)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newChats = documents.mapNotNull { document ->
                                document.toObject<Chat>()
                            }
                            loaded_groups.addAll(newChats)
                            lastVisibleGroup = documents[documents.size - 1] // Update the last visible chat timestamp for pagination
                            trySend(Response.Success(loaded_groups))
                        } else {
                            // No more chats found
                            trySend(Response.Failure(SocialException("No more chats found", e = java.lang.Exception())))
                        }
                    } else {
                        // Task failed
                        trySend(Response.Failure(SocialException(message = "Failed to get more chats", e = java.lang.Exception())))
                    }
                }
            awaitClose { /* Cleanup, if needed */ }
        }
    override suspend fun addParticipant(activityId:String,participant: Participant): Flow<Response<Void?>> = flow {
        try {
            val addition = chatColletionRef.document(activityId).collection("participants").document(participant.id).set(participant).await1()
            val update = chatColletionRef.document(activityId).update(
                "members",
                FieldValue.arrayUnion(participant.id),
                "invites",FieldValue.arrayRemove(participant.id)
            ).await()
            emit(Response.Success(addition))
        } catch (e: Exception) {
            // Adding invite failed
            emit(Response.Failure(e = SocialException("addParticipant exception", e)))
        }
    }

    override suspend fun removeParticipant(activityId:String,participant_id: String) :Flow<Response<Void?>> = flow {
        try {
            val deletion = chatColletionRef.document(activityId).collection("participants").document(participant_id).delete().await1()
            val update = chatColletionRef.document(activityId).update(
                "members",
                FieldValue.arrayRemove(participant_id)
            ).await()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            // Removing invite failed
            emit(Response.Failure(e = SocialException("removeParticipant exception", e)))
        }
    }

    override suspend fun removeInvite(activityId:String, user_id: String) :Flow<Response<Void?>> = flow {
        try {
            val update = chatColletionRef.document(activityId).update(
                "invites",
                FieldValue.arrayRemove(user_id)
            ).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            // Removing invite failed
            emit(Response.Failure(e = SocialException("removeParticipant exception", e)))
        }
    }
    override suspend fun removeGroup(groupId:String) :Flow<Response<Void?>> = flow {
        try {
            val collectionRef = messagesRef.document(groupId).collection("messages")

            val ref = collectionRef.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                }
            }
            ref.await()

            val participantsRef = chatColletionRef.document(groupId).collection("participants")

            val ref2 = participantsRef.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                }
            }
            ref2.await()


            val update = chatColletionRef.document(groupId).delete( ).await()

            emit(Response.Success(update))
        } catch (e: Exception) {
            // Removing invite failed
            emit(Response.Failure(e = SocialException("removeParticipant exception", e)))
        }
    }
    override suspend fun removeGroupImage(
        url: String,
    ): Flow<Response<Boolean>> = flow {
        try {
            Log.d("Group",url)
            val reference=resStorage.storage.getReferenceFromUrl(url)
            Log.d("Group",reference.toString())

            reference.delete().await()
            emit(Response.Success(true))

        } catch (e: Exception) {
            Log.d("Group", "try addProfilePictureToStorage EXCEPTION"+e.toString())
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


}