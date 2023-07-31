package com.palkowski.friendupp.Invites

import com.palkowski.friendupp.await1
import com.palkowski.friendupp.model.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalCoroutinesApi
class InviteRepositoryImpl @Inject constructor(
    private val invitesRef: CollectionReference,
    private val usersRef: CollectionReference,
    // Add other dependencies as needed
) : InviteRepository {
    private var loaded_invites: ArrayList<Invite> = ArrayList()
    private var lastVisibleInviteTimestamp: DocumentSnapshot? = null

    // Implement the functions in InviteRepository interface here
    // For example, to get invites for a user:
    override suspend fun getInvites(userId: String): Flow<Response<List<Invite>>> =
        callbackFlow {
            loaded_invites.clear()

            invitesRef.whereEqualTo("receiverId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newInvites = ArrayList<Invite>()
                            for (document in documents) {
                                val invite = document.toObject<Invite>()
                                if (invite != null) {
                                    newInvites.add(invite)
                                }
                            }
                            lastVisibleInviteTimestamp = documents[documents.size - 1]
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
        }

    override suspend fun getMoreInvites(userId: String): Flow<Response<List<Invite>>> =
        callbackFlow {
            val snapshotListener = invitesRef.whereEqualTo("receiverId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisibleInviteTimestamp?.get("timestamp")) // Assuming you have a variable to keep track of the last visible invite timestamp
                .limit(10)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newInvites = documents.mapNotNull { document ->
                                document.toObject<Invite>()
                            }
                            loaded_invites.addAll(newInvites)
                            lastVisibleInviteTimestamp = documents[documents.size - 1] // Update the last visible invite timestamp for pagination
                            trySend(Response.Success(loaded_invites))
                        } else {
                            // No more invites found
                            trySend(Response.Failure(SocialException("No more invites found",e=java.lang.Exception())))
                        }
                    } else {
                        // Task failed
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    message = "Failed to get more invites",
                                    e =java.lang.Exception()
                                )
                            )
                        )
                    }
                }
            awaitClose { /* Cleanup, if needed */ }
        }

    override suspend fun addInvite(invite: Invite): Flow<Response<Void?>> = flow {
        try {
            val addition = invitesRef.document(invite.id).set(invite).await1()
            val update= usersRef.document(invite.senderId).update("invited_ids",FieldValue.arrayUnion(invite.receiverId)).await1()
            emit(Response.Success(addition))
        } catch (e: Exception) {
            // Adding invite failed
            emit(Response.Failure(e = SocialException("addInvite exception", e)))
        }
    }

    override suspend fun removeInvite(invite: Invite): Flow<Response<Void?>> = flow {
        try {
            val addition = invitesRef.document(invite.id).delete().await1()
            val update= usersRef.document(invite.senderId).update("invited_ids",FieldValue.arrayRemove(invite.receiverId)).await1()
            emit(Response.Success(addition))
        } catch (e: Exception) {
            // Removing invite failed
            emit(Response.Failure(e = SocialException("removeInvite exception", e)))
        }
    }
}