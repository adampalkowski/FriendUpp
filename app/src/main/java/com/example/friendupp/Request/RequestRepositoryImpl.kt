package com.example.friendupp.Request

import com.example.friendupp.await1
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
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class RequestRepositoryImpl @Inject constructor(
    private val activitiesRef: CollectionReference,
    // Add other dependencies as needed
) : RequestRepository {

    private var loadedRequests: ArrayList<Request> = ArrayList()
    private var lastVisibleRequest: DocumentSnapshot? = null

    // Function to get requests for an activity
    override suspend fun getRequests(activityId: String): Flow<Response<List<Request>>> = callbackFlow {
        loadedRequests.clear()

        activitiesRef.document(activityId).collection("requests")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (documents != null && documents.isNotEmpty()) {
                        val newRequests = ArrayList<Request>()
                        for (document in documents) {
                            val request = document.toObject<Request>()
                            if (request != null) {
                                newRequests.add(request)
                            }
                        }
                        lastVisibleRequest = documents[documents.size - 1]
                        trySend(Response.Success(newRequests))
                    } else {
                        // No requests found
                        trySend(Response.Failure(SocialException("No requests found", e = Exception())))
                    }
                } else {
                    // Task failed
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                message = "Failed to get requests",
                                e = Exception()
                            )
                        )
                    )
                }
            }
        awaitClose { /* Cleanup, if needed */ }
    }

    // Function to get more requests for an activity (used for pagination, if applicable)
    override suspend fun getMoreRequests(activityId: String): Flow<Response<List<Request>>> = callbackFlow {
        activitiesRef.document(activityId).collection("requests")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .startAfter(lastVisibleRequest?.get("timestamp")) // Assuming you have a variable to keep track of the last visible request timestamp
            .limit(10)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (documents != null && documents.isNotEmpty()) {
                        val newRequests = documents.mapNotNull { document ->
                            document.toObject<Request>()
                        }
                        loadedRequests.addAll(newRequests)
                        lastVisibleRequest = documents[documents.size - 1] // Update the last visible request timestamp for pagination
                        trySend(Response.Success(loadedRequests))
                    } else {
                        // No more requests found
                        trySend(Response.Failure(SocialException("No more requests found", e = Exception())))
                    }
                } else {
                    // Task failed
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                message = "Failed to get more requests",
                                e = Exception()
                            )
                        )
                    )
                }
            }
        awaitClose { /* Cleanup, if needed */ }
    }

    // Function to accept a request and add the participant to the activity
    override suspend fun createRequest(activityId: String, request: Request): Flow<Response<Void?>> = flow {
        try {

            val addition = activitiesRef.document(activityId).collection("requests").document(request.id)
                .set(request).await1()
            emit(Response.Success(addition))
        } catch (e: Exception) {
            // Accepting request failed
            emit(Response.Failure(e = SocialException("acceptRequest exception", e)))
        }
    }

    // Function to reject a request
    override suspend fun removeRequest(activityId: String, request: Request): Flow<Response<Void?>> = flow {
        try {
            val deletion =
                activitiesRef.document(activityId).collection("requests").document(request.id).delete().await1()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            // Rejecting request failed
            emit(Response.Failure(e = SocialException("rejectRequest exception", e)))
        }
    }

}