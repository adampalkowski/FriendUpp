package com.example.friendupp.di

import android.net.Uri
import android.util.Log
import com.example.friendupp.await1
import com.example.friendupp.model.*
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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

val TAG = "ActivityRepositoryImpl"

@Singleton
@ExperimentalCoroutinesApi
class ActivityRepositoryImpl @Inject constructor(
    private val activitiesRef: CollectionReference,
    private val activeUsersRef: CollectionReference,
    private val usersRef: CollectionReference,
    private val chatCollectionsRef: CollectionReference,
    private val messagessRef: CollectionReference,
    private val resStorage: StorageReference,
    private val lowResStorage: StorageReference,
) : ActivityRepository {
    private var lastVisibleData: DocumentSnapshot? = null
    private var lastVisibleActiveUserData: DocumentSnapshot? = null
    private var lastVisibleUserData: DocumentSnapshot? = null
    private var lastVisibleJoinedData: DocumentSnapshot? = null
    private var lastVisibleDataForUserProfile: DocumentSnapshot? = null
    private var lastVisibleClosestData: DocumentSnapshot? = null
    private var lastVisibleFilteredClosestData: DocumentSnapshot? = null
    private var loaded_public_activities: ArrayList<Activity> = ArrayList()
    private var loaded_user_activities: ArrayList<Activity> = ArrayList()
    private var loaded_active_users: ArrayList<ActiveUser> = ArrayList()
    override suspend fun getClosestFilteredActivities(
        lat: Double,
        lng: Double,
        tags: ArrayList<String>,
        radius: Double,
    ): Flow<Response<List<Activity>>> = callbackFlow {
        Log.d(TAG, "getClosestFilteredActivities")
        lastVisibleFilteredClosestData = null
        val center = GeoLocation(lat, lng)
        val radiusInM = radius
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q = activitiesRef.whereEqualTo("public", true).whereArrayContainsAny("tags", tags)
                .orderBy("geoHash")
                .startAt(b.startHash)
                .endAt(b.endHash)
                .limit(2)
            tasks.add(q.get())
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result



                    for (doc in snap!!.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }
                }

                if (matchingDocs != null && matchingDocs.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in matchingDocs) {
                        val activity = document.toObject<Activity>()

                        if (activity != null) {
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleFilteredClosestData = matchingDocs[matchingDocs.size - 1]

                    trySend(Response.Success(newActivities))

                }
            }.addOnFailureListener() {
                trySend(
                    Response.Failure(
                        e = SocialException(
                            message = "Nearby activity download failure",
                            e = Exception()
                        )
                    )
                )

            }

        awaitClose {
        }
    }

    override suspend fun getMoreFilteredClosestActivities(
        lat: Double,
        lng: Double,
        tags: ArrayList<String>,
        radius: Double,
    ): Flow<Response<List<Activity>>> = callbackFlow {
        val center = GeoLocation(lat, lng)
        val radiusInM = radius
        Log.d("HOMESCREENTEST", "DB getMoreFilteredClosestActivities")


        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q = activitiesRef.whereEqualTo("public", true)
                .orderBy("geoHash")
                .startAfter(lastVisibleFilteredClosestData?.data?.get("geoHash"))
                .endAt(b.endHash)
                .limit(2)
            tasks.add(q.get())
        }
        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result


                    for (doc in snap!!.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }
                }

                if (matchingDocs != null && matchingDocs.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in matchingDocs) {
                        val activity = document.toObject<Activity>()
                        Log.d("getMoreClosestActivities", activity.toString())

                        if (activity != null) {
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleFilteredClosestData = matchingDocs[matchingDocs.size - 1]
                    loaded_public_activities.addAll(newActivities)
                    val new_instance = ArrayList<Activity>()
                    Log.d("ActivityRepositoryImpl", loaded_public_activities.toString())
                    new_instance.addAll(loaded_public_activities)
                    trySend(Response.Success(new_instance))

                }
            }.addOnFailureListener() {
                trySend(
                    Response.Failure(
                        e = SocialException(
                            message = "Nearby activity download failure",
                            e = Exception()
                        )
                    )
                )

            }

        awaitClose {
        }
    }

    override suspend fun getClosestActivities(
        lat: Double,
        lng: Double,
        radius: Double,
    ): Flow<Response<List<Activity>>> = callbackFlow {
        Log.d(TAG, "getClosestActivities")

        lastVisibleClosestData = null
        val center = GeoLocation(lat, lng)
        val radiusInM = radius
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q = activitiesRef.whereEqualTo("public", true)
                .orderBy("geoHash")
                .startAt(b.startHash)
                .endAt(b.endHash)
                .limit(2)
            tasks.add(q.get())
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result

                    for (doc in snap!!.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }
                }

                if (matchingDocs.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in matchingDocs) {
                        val activity = document.toObject<Activity>()

                        if (activity != null) {
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleClosestData = matchingDocs[matchingDocs.size - 1]

                    trySend(Response.Success(newActivities))

                }
            }.addOnFailureListener() {
                trySend(
                    Response.Failure(
                        e = SocialException(
                            message = "Nearby activity download failure",
                            e = Exception()
                        )
                    )
                )

            }

        awaitClose {
        }
    }

    override suspend fun getMoreClosestActivities(
        lat: Double,
        lng: Double,
        radius: Double,
    ): Flow<Response<List<Activity>>> = callbackFlow {
        val center = GeoLocation(lat, lng)
        val radiusInM = radius
        Log.d("getMoreClosestActivities", "DB getMoreClosestActivities")
        Log.d("getMoreClosestActivities", center.toString())

        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q = activitiesRef.whereEqualTo("public", true)
                .orderBy("geoHash")
                .startAfter(lastVisibleClosestData?.data?.get("geoHash"))
                .endAt(b.endHash)
                .limit(2)
            tasks.add(q.get())
        }
        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result


                    for (doc in snap!!.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }
                }

                if (matchingDocs != null && matchingDocs.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in matchingDocs) {
                        val activity = document.toObject<Activity>()
                        Log.d("getMoreClosestActivities", activity.toString())

                        if (activity != null) {
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleClosestData = matchingDocs[matchingDocs.size - 1]
                    loaded_public_activities.addAll(newActivities)
                    val new_instance = ArrayList<Activity>()
                    Log.d("getMoreClosestActivities", loaded_public_activities.toString())
                    new_instance.addAll(loaded_public_activities)
                    trySend(Response.Success(new_instance))

                } else if (matchingDocs != null && matchingDocs.isEmpty()) {
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                message = "No more nearby activities",
                                e = Exception()
                            )
                        )
                    )
                }
            }.addOnFailureListener() {
                trySend(
                    Response.Failure(
                        e = SocialException(
                            message = "Nearby activity download failure",
                            e = Exception()
                        )
                    )
                )

            }

        awaitClose {
        }
    }

    override suspend fun getActivity(id: String): Flow<Response<Activity>> = callbackFlow {
        activitiesRef.document(id).get().addOnSuccessListener { documentSnapshot ->
            val response = if (documentSnapshot != null) {
                val activity = documentSnapshot.toObject<Activity>()
                Response.Success(activity)
            } else {
                Response.Failure(e = SocialException("getActivty document null", Exception()))
            }
            trySend(response as Response<Activity>).isSuccess
        }
        awaitClose() {
        }
    }

    override suspend fun setParticipantPicture(id: String, user: User): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)
                val update = activitiesRef.document(id).update(
                    "participants_profile_pictures" + "." + user.id,
                    FieldValue.delete(),
                    "participants_usernames" + "." + user.id,
                    FieldValue.delete()
                ).await()
                emit(Response.Success(update))
            } catch (e: Exception) {
                emit(Response.Failure(e = SocialException("unlikeActivity exception", Exception())))
            }
        }

    override suspend fun likeActivity(id: String, user: User): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update = activitiesRef.document(id).update(
                "participants_profile_pictures" + "." + user.id,
                user.pictureUrl,
                "participants_usernames" + "." + user.id,
                user.username,
                "participants_ids",
                FieldValue.arrayUnion(user.id)
            ).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("likeActivity exception", Exception())))
        }
    }

    override suspend fun addActivityParticipant(id: String, user: User): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)
                val update = activitiesRef.document(id)
                    .update("participants_ids", FieldValue.arrayUnion(user.id)).await()
                emit(Response.Success(update))
            } catch (e: Exception) {
                emit(
                    Response.Failure(
                        e = SocialException(
                            "addActivityParticipant exception",
                            Exception()
                        )
                    )
                )
            }
        }

    override suspend fun addParticipantImageToActivity(
        activity_id: String,
        user_id: String,
        picture_url: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update =
                activitiesRef.document(activity_id).update("pictures" + "." + user_id, picture_url)
                    .await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("likeActivity exception", Exception())))
        }
    }

    override suspend fun unlikeActivity(id: String, user_id: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update = activitiesRef.document(id).update(
                "participants_profile_pictures" + "." + user_id,
                FieldValue.delete(),
                "participants_usernames" + "." + user_id,
                FieldValue.delete(),
                "participants_ids",
                FieldValue.arrayRemove(user_id)
            ).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("unlikeActivity exception", Exception())))
        }
    }

    override suspend fun addRequestToActivity(
        activity_id: String,
        user_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update = activitiesRef.document(activity_id)
                .update("requests", FieldValue.arrayUnion(user_id)).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "addRequestToActivity exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun removeRequestFromActivity(
        activity_id: String,
        user_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update = activitiesRef.document(activity_id)
                .update("requests", FieldValue.arrayRemove(user_id)).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "removeRequestFromActivity exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun reportActivity(activity_id: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update =
                activitiesRef.document(activity_id).update("reports", FieldValue.increment(1))
                    .await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "removeRequestFromActivity exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun deleteActivityFromUser(
        user_id: String,
        activities_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val remove = usersRef.document(user_id)
                .update("activities", FieldValue.arrayRemove(activities_id)).await()
            emit(Response.Success(remove))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "deleteActivityFromUser exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun addActivity(activity: Activity): Flow<Response<Void?>> = flow {
        try {
            val addition = activitiesRef.document(activity.id).set(activity).await1()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("AddActivity exception", Exception())))
        }
    }

    override suspend fun updateActivityInvites(
        activity_id: String,
        invites: ArrayList<String>,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val addition =
                activitiesRef.document(activity_id).update("invited_users", invites).await()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("AddActivity exception", Exception())))
        }
    }

    override suspend fun addUserToActivityInvites(
        activity: Activity,
        user_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val activityId = activity.id
            val addition = activitiesRef.document(activityId)
                .update("invited_users", FieldValue.arrayUnion(user_id)).await()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "addUserToActivityInvites exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun leaveLiveActivity(
        activity_id: String,
        user_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            Log.d("HOMESCREEN", "LEAVEACTIVITY")
            Log.d("HOMESCREEN", activity_id + user_id)
            val addition = activeUsersRef.document(activity_id).update(
                "participants_profile_pictures" + "." + user_id,
                FieldValue.delete(),
                "participants_usernames" + "." + user_id,
                FieldValue.delete()
            ).await()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "addUserToActivityInvites exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun removeUserFromActivityInvites(
        activity: Activity,
        user_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val activityId = activity.id
            val addition = activitiesRef.document(activityId)
                .update("invited_users", FieldValue.arrayRemove(user_id)).await()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "removeUserFromActivityInvites exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun hideActivity(activity_id: String, user_id: String): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)
                val addition = activitiesRef.document(activity_id)
                    .update("invited_users", FieldValue.arrayRemove(user_id)).await()
                emit(Response.Success(addition))

            } catch (e: Exception) {
                emit(Response.Failure(e = SocialException("hideActivity exception", Exception())))
            }
        }

    override suspend fun deleteActivity(id: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val collectionRef = messagessRef.document(id).collection("messages")
            val ref = collectionRef.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                }
            }
            ref.await()
            val deletion1 = activitiesRef.document(id).delete().await()
            val deletion3 = chatCollectionsRef.document(id).delete().await()
            emit(Response.Success(deletion3))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("deleteActivity exception", Exception())))
        }
    }

    override suspend fun joinActiveUser(
        live_activity_id: String,
        user_id: String,
        profile_url: String,
        username: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update = activeUsersRef.document(live_activity_id).update(
                "participants_profile_pictures" + "." + user_id,
                profile_url,
                "participants_usernames" + "." + user_id,
                username
            ).await()
            emit(Response.Success(update))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("joinActiveUser exception", Exception())))
        }
    }

    override suspend fun getUserActivities(id: String): Flow<Response<List<Activity>>> =
        callbackFlow {
            Log.d("HOMESCREENTEST", "DB getUserActivities")

            val snapshotListener = activitiesRef.whereEqualTo("creator_id", id)
                .orderBy("creation_time", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener { task ->
                    var activitiesList: List<Activity> = mutableListOf()
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newActivities = ArrayList<Activity>()
                            for (document in documents) {
                                val activity = document.toObject<Activity>()
                                Log.d("PROFILESCREEN", activity.toString())


                                if (activity != null) {
                                    newActivities.add(activity)
                                }
                            }
                            lastVisibleUserData = documents[documents.size - 1]
                            trySend(Response.Success(newActivities))

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

                }
            awaitClose {

            }

        }

    override suspend fun getJoinedActivities(id: String): Flow<Response<List<Activity>>> =
        callbackFlow {
            Log.d("GETJOINEDACTIVITIES", "CALL")
            val snapshotListener = activitiesRef.whereArrayContains("participants_ids", id)
                .orderBy("creation_time", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newActivities = ArrayList<Activity>()
                            for (document in documents) {
                                val activity = document.toObject<Activity>()
                                Log.d("GETJOINEDACTIVITIES", activity.toString())


                                if (activity != null) {
                                    newActivities.add(activity)
                                }
                            }
                            lastVisibleJoinedData = documents[documents.size - 1]
                            trySend(Response.Success(newActivities))

                        }
                    } else {
                        // There are no more messages to load
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    message = "failed to get activities",
                                    e = Exception()
                                )
                            )
                        )
                    }

                }
            awaitClose {

            }

        }

    override suspend fun getMoreUserActivities(id: String): Flow<Response<List<Activity>>> =
        callbackFlow {
            Log.d("HOMESCREENTEST", "getMoreUserActivities")

            val snapshotListener = activitiesRef.whereEqualTo("creator_id", id)
                .orderBy("creation_time", Query.Direction.DESCENDING)
                .startAfter(lastVisibleUserData?.data?.get("creation_time")).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newActivities = ArrayList<Activity>()
                            for (document in documents) {
                                val activity = document.toObject<Activity>()
                                if (activity != null) {
                                    newActivities.add(activity)
                                }
                            }


                            lastVisibleUserData = documents[documents.size - 1]
                            trySend(Response.Success(newActivities))

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

                }
            awaitClose {
            }
        }

    override suspend fun getMoreJoinedActivities(id: String): Flow<Response<List<Activity>>> =
        callbackFlow {
            Log.d("HOMESCREENTEST", "getMoreUserActivities")

            activitiesRef.whereArrayContains("participants_ids", id)
                .orderBy("creation_time", Query.Direction.DESCENDING)
                .startAfter(lastVisibleJoinedData?.data?.get("creation_time")).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newActivities = ArrayList<Activity>()
                            for (document in documents) {
                                val activity = document.toObject<Activity>()
                                if (activity != null) {
                                    newActivities.add(activity)
                                }
                            }


                            lastVisibleJoinedData = documents[documents.size - 1]
                            trySend(Response.Success(newActivities))

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

                }
            awaitClose {
            }
        }

    override suspend fun addImageFromGalleryToStorage(
        id: String,
        imageUri: Uri,
    ): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            if (imageUri != null) {
                val fileName = id
                try {
                    resStorage.child("high_res_images/$fileName" + "_1080x1920").delete().await1()
                } catch (e: StorageException) {

                }
                val imageRef = resStorage.child("high_res_images/$fileName")
                imageRef.putFile(imageUri).await1()
                val reference = resStorage.child("high_res_images/$fileName" + "_1080x1920")
                val url = keepTrying(8, reference)
                emit(Response.Success(url))
            }
        } catch (e: Exception) {
            Log.d("ActivityRepositoryImpl", "try addProfilePictureToStorage EXCEPTION")
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

    override suspend fun deleteImageFromHighResStorage(
        id: String,
    ): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val fileName = id
            try {
                val deletion =
                    resStorage.child("high_res_images/$fileName" + "_1080x1920").delete().await1()
            } catch (e: StorageException) {

            }
            emit(Response.Success("succesfully deleted from storage"))

        } catch (e: Exception) {
            Log.d("deleteImageFromHighResStorage", "deletion from storage exception")
            emit(
                Response.Failure(
                    e = SocialException(
                        "deleteImageFromHighResStorage exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun deleteActivityImageFromFirestoreActivity(
        activity_id: String,
        user_id: String,
    ): Flow<Response<String>> = flow {

        try {
            emit(Response.Loading)
            val deletion = activitiesRef.document(activity_id)
                .update("pictures" + "." + user_id, FieldValue.delete()).await()
            emit(Response.Success("deletion"))
        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "deleteActivityImageFromFirestoreActivity",
                        Exception()
                    )
                )
            )
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

    override suspend fun getActivitiesForUser(id: String): Flow<Response<List<Activity>>> =
        callbackFlow {

            activitiesRef.whereArrayContains("invited_users", id)
                .orderBy("creation_time", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newActivities = ArrayList<Activity>()
                            for (document in documents) {
                                val activity = document.toObject<Activity>()
                                if (activity != null) {
                                    newActivities.add(activity)
                                }
                            }
                            Log.d("HOMESCREENTEST", documents.size.toString())

                            lastVisibleData = documents[documents.size - 1]
                            trySend(Response.Success(newActivities))

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

                }
            awaitClose {
            }
        }

    override suspend fun getMoreActivitiesForUser(id: String): Flow<Response<List<Activity>>> =
        callbackFlow {
            val snapshotListener = activitiesRef.whereArrayContains("invited_users", id)
                .orderBy("creation_time", Query.Direction.DESCENDING)
                .startAfter(lastVisibleData?.data?.get("creation_time")).limit(3).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newActivities = ArrayList<Activity>()
                            for (document in documents) {
                                val activity = document.toObject<Activity>()
                                if (activity != null) {
                                    newActivities.add(activity)
                                }
                            }
                            Log.d("HOMESCREENTEST", "ROzmuiar")
                            Log.d("HOMESCREENTEST", documents.size.toString())
                            loaded_user_activities.addAll(newActivities)
                            val new_instance = ArrayList<Activity>()
                            new_instance.addAll(loaded_user_activities)
                            Log.d("HOMESCREENTEST", new_instance.size.toString())

                            lastVisibleData = documents[documents.size - 1]
                            trySend(Response.Success(new_instance))

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

                }
            awaitClose {
            }
        }


    //TODO CHANGE WHERE_EQUAL_TO
    override suspend fun getActiveUsers(id: String): Flow<Response<List<ActiveUser>>> =
        callbackFlow {
            activeUsersRef.whereArrayContains("invited_users", id)
                .orderBy("create_time", Query.Direction.DESCENDING)
                .limit(5).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {

                            val newActiviteUsers = ArrayList<ActiveUser>()
                            for (document in documents) {
                                val activity = document.toObject<ActiveUser>()
                                if (activity != null) {
                                    newActiviteUsers.add(activity)
                                }
                            }
                            Log.d("HOMESCREENTEST", documents.size.toString())

                            lastVisibleActiveUserData = documents[documents.size - 1]
                            trySend(Response.Success(newActiviteUsers))


                        } else {
                            // There are no more messages to load
                            trySend(
                                Response.Failure(
                                    e = SocialException(
                                        message = "failed to get more active users",
                                        e = Exception()
                                    )
                                )
                            )
                        }

                    }


                }
            awaitClose {
            }
        }

    override suspend fun getMoreActiveUsers(id: String): Flow<Response<List<ActiveUser>>> =
        callbackFlow {
           activeUsersRef.whereArrayContains("invited_users", id)  .orderBy("create_time", Query.Direction.DESCENDING)
               .startAfter(lastVisibleData?.data?.get("create_time")).limit(5).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newActivities = ArrayList<ActiveUser>()
                            for (document in documents) {
                                val activity = document.toObject<ActiveUser>()
                                if (activity != null) {
                                    newActivities.add(activity)
                                }
                            }
                            loaded_active_users.addAll(newActivities)
                            val new_instance = ArrayList<ActiveUser>()
                            new_instance.addAll(loaded_active_users)
                            lastVisibleActiveUserData= documents[documents.size - 1]
                            trySend(Response.Success(new_instance))

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

                }
            awaitClose {
            }
        }

    override suspend fun addActiveUser(activeUser: ActiveUser): Response<Boolean> {
        return try {
            val result = activeUsersRef.document(activeUser.creator_id).set(activeUser).await()
            com.example.friendupp.model.Response.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            com.example.friendupp.model.Response.Failure(
                com.example.friendupp.model.SocialException(
                    "signIn error",
                    e
                )
            )
        }
    }

    override suspend fun deleteActiveUser(id: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion = activeUsersRef.document(id).delete().await()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("deleteActiveUser", Exception())))
        }
    }

    override suspend fun getClosestFilteredDateActivities(
        lat: Double,
        lng: Double,
        date: String,
        radius: Double,
    ): Flow<Response<List<Activity>>> = callbackFlow {

        Log.d(TAG, "getClosestFilteredDateActivities")
        Log.d(TAG, date.toString())

        lastVisibleFilteredClosestData = null
        val center = GeoLocation(lat, lng)
        val radiusInM = radius
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q = activitiesRef.whereEqualTo("public", true).whereEqualTo("date", date)
                .orderBy("geoHash")
                .startAt(b.startHash)
                .endAt(b.endHash)
                .limit(2)
            tasks.add(q.get())
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result

                    for (doc in snap!!.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }
                }

                if (matchingDocs != null && matchingDocs.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in matchingDocs) {
                        val activity = document.toObject<Activity>()
                        Log.d(TAG, activity.toString())

                        if (activity != null) {
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleFilteredClosestData = matchingDocs[matchingDocs.size - 1]

                    trySend(Response.Success(newActivities))

                }
            }.addOnFailureListener() {
                trySend(
                    Response.Failure(
                        e = SocialException(
                            message = "Nearby activity download failure",
                            e = Exception()
                        )
                    )
                )

            }


        awaitClose {
        }
    }

    override suspend fun getMoreFilteredDateClosestActivities(
        lat: Double,
        lng: Double,
        date: String,
        radius: Double,
    ): Flow<Response<List<Activity>>> = callbackFlow {
        val center = GeoLocation(lat, lng)
        val radiusInM = radius
        Log.d("HOMESCREENTEST", "DB getMoreFilteredClosestActivities")


        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q = activitiesRef.whereEqualTo("public", true)
                .whereGreaterThan("start_time", date)
                .orderBy("start_time", Query.Direction.ASCENDING)
                .orderBy("geoHash")
                .startAfter(lastVisibleFilteredClosestData?.data?.get("geoHash"))
                .endAt(b.endHash)
                .limit(2)
            tasks.add(q.get())
        }
        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result


                    for (doc in snap!!.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }
                }

                if (matchingDocs != null && matchingDocs.isNotEmpty()) {
                    val newActivities = ArrayList<Activity>()
                    for (document in matchingDocs) {
                        val activity = document.toObject<Activity>()
                        Log.d("getMoreClosestActivities", activity.toString())

                        if (activity != null) {
                            newActivities.add(activity)
                        }
                    }
                    lastVisibleFilteredClosestData = matchingDocs[matchingDocs.size - 1]
                    loaded_public_activities.addAll(newActivities)
                    val new_instance = ArrayList<Activity>()
                    Log.d("ActivityRepositoryImpl", loaded_public_activities.toString())
                    new_instance.addAll(loaded_public_activities)
                    trySend(Response.Success(new_instance))

                }
            }.addOnFailureListener() {
                trySend(
                    Response.Failure(
                        e = SocialException(
                            message = "Nearby activity download failure",
                            e = Exception()
                        )
                    )
                )

            }

        awaitClose {
        }
    }

    override suspend fun watchCurrentUserActive(id:String): Flow<Response<List<ActiveUser>>> = callbackFlow {

        val activeUsersQuery = activeUsersRef
            .whereEqualTo("creator_id", id)
        val registration = activeUsersQuery.addSnapshotListener { snapshot, exception ->
            // Handle exceptions and send responses accordingly
            if (exception != null) {
                trySend(
                    Response.Failure(
                        e = SocialException(
                            message = "failed to get more active users",
                            e = exception
                        )
                    )
                )
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {

                val documents = snapshot.documents
                val newActiveUsers = ArrayList<ActiveUser>()
                for (document in documents) {
                    val activity = document.toObject<ActiveUser>()
                    Log.d("ACTIVEUSERDEBUG","got"+activity.toString())
                    if (activity != null) {
                        newActiveUsers.add(activity)
                    }
                }
                trySend(Response.Success(newActiveUsers))
            } else {
                // There are no more messages to load
                trySend(
                    Response.Failure(
                        e = SocialException(
                            message = "failed to get more active users",
                            e = Exception()
                        )
                    )
                )
            }
        }
        awaitClose {
            registration.remove() // Remove the snapshot listener when the flow is cancelled
        }


    }
}