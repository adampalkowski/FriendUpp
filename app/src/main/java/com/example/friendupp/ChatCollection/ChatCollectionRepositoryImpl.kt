package com.example.friendupp.ChatCollection

import android.util.Log
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Response
import com.example.friendupp.model.SocialException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


@ExperimentalCoroutinesApi
class ChatCollectionRepositoryImpl @Inject constructor(
    private val chatCollectionsRef: CollectionReference,
) : ChatCollectionRepository {
    private var lastVisibleChat: DocumentSnapshot? = null

    override suspend fun getChatCollections(user_id: String): Flow<Response<ArrayList<Chat>>>  =
        callbackFlow {

            val snapshotListener = chatCollectionsRef.whereArrayContains("members", user_id)
                .orderBy("recent_message_time", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newChat = ArrayList<Chat>()
                            for (document in documents) {
                                val activity = document.toObject<Chat>()
                                Log.d("PROFILESCREEN", activity.toString())


                                if (activity != null) {
                                    newChat.add(activity)
                                }
                            }
                            lastVisibleChat = documents[documents.size - 1]
                            trySend(Response.Success(newChat))

                        } else {
                            // Send a failure response indicating that no activities are found
                            trySend(
                                Response.Failure(
                                    e = SocialException(
                                        message = "No activities found.",
                                        e = Exception()
                                    )
                                )
                            )
                        }
                    } else {
                        // There are no more messages to load
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    message = "failed to get more activities",
                                    e = Exception()
                                )
                            )
                        )
                    }

                }.addOnFailureListener {
                    // There was an exception in the operation
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                message = it.message.toString(),
                                e = it
                            )
                        )
                    )
                }
            awaitClose {

            }

        }


    override suspend fun getMoreChatCollections(user_id: String): Flow<Response<ArrayList<Chat>>>  =
        callbackFlow {

            chatCollectionsRef.whereArrayContains("members", user_id)
                .orderBy("recent_message_time", Query.Direction.DESCENDING)
                .startAfter(lastVisibleChat?.data?.get("recent_message_time"))  .limit(10)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newChats = ArrayList<Chat>()
                            for (document in documents) {
                                val activity = document.toObject<Chat>()
                                if (activity != null) {
                                    newChats.add(activity)
                                }
                            }


                            lastVisibleChat = documents[documents.size - 1]
                            trySend(Response.Success(newChats))

                        } else {
                            // Send a failure response indicating that no activities are found
                            trySend(
                                Response.Failure(
                                    e = SocialException(
                                        message = "No activities found.",
                                        e = Exception()
                                    )
                                )
                            )
                        }
                    } else {
                        // There are no more messages to load
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    message = "failed to get more activities",
                                    e = Exception()
                                )
                            )
                        )
                    }

                }.addOnFailureListener {
                    // There was an exception in the operation
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                message = it.message.toString(),
                                e = it
                            )
                        )
                    )
                }
            awaitClose {
            }
        }


}