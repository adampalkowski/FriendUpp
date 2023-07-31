package com.palkowski.friendupp.Participants

import com.palkowski.friendupp.await1
import com.palkowski.friendupp.model.Participant
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.model.SocialException
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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalCoroutinesApi
class ParticipantsRepositoryImpl @Inject constructor(
    private val activitiesRef: CollectionReference,
    // Add other dependencies as needed
) : ParticipantsRepository {
    private var loaded_participants: ArrayList<Participant> = ArrayList()
    private var lastVisibleParticipant: DocumentSnapshot? = null

    // Implement the functions in InviteRepository interface here
    // For example, to get invites for a user:
    override suspend fun getParticipants(activityId: String): Flow<Response<List<Participant>>> =
        callbackFlow {
            loaded_participants.clear()

            activitiesRef.document(activityId).collection("participants")
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
        }

    override suspend fun getMoreParticipants(activityId:  String): Flow<Response<List<Participant>>> =
        callbackFlow {
            activitiesRef.document(activityId).collection("participants")
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

    override suspend fun addParticipant(activityId:String,participant: Participant): Flow<Response<Void?>> = flow {
        try {
            val addition = activitiesRef.document(activityId).collection("participants").document(participant.id).set(participant).await1()
            val update = activitiesRef.document(activityId).update(
                "participants_ids",
                FieldValue.arrayUnion(participant.id)
            ).await()
            emit(Response.Success(addition))
        } catch (e: Exception) {
            // Adding invite failed
            emit(Response.Failure(e = SocialException("addParticipant exception", e)))
        }
    }

    override suspend fun removeParticipant(activityId:String,participant: Participant) :Flow<Response<Void?>> = flow {
        try {
            val deletion = activitiesRef.document(activityId).collection("participants").document(participant.id).delete().await1()
            val update = activitiesRef.document(activityId).update(
                "participants_ids",
                FieldValue.arrayRemove(participant.id)
            ).await()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            // Removing invite failed
            emit(Response.Failure(e = SocialException("removeParticipant exception", e)))
        }
    }
}