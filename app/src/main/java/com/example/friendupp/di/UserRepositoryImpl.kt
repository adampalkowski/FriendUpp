package com.example.friendupp.di

import android.net.Uri
import android.util.Log
import com.example.friendupp.await1
import com.example.friendupp.model.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
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

@Singleton
@ExperimentalCoroutinesApi
class UserRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
    private val chatCollectionsRef: CollectionReference,
    private val storageRef: StorageReference,
    private val resStorage: StorageReference,
) : UserRepository {
    private var lastVisibleDataFriends: DocumentSnapshot? = null
    private var lastVisibleInvite: DocumentSnapshot? = null
    private var lastVisibleUserActivityData: DocumentSnapshot? = null
    private var loaded_invites: ArrayList<User> = ArrayList()

    override suspend fun getUser(id: String): Flow<Response<User>> = callbackFlow {
        val registration =
            usersRef.document(id).get().addOnSuccessListener { documents ->

                val response = if (documents != null) {
                    val user: User? = documents!!.toObject<User>()
                    if (user != null) {
                        trySend(Response.Success(user))

                    } else {
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    "getUser by name document null",
                                    Exception()
                                )
                            )
                        )

                    }
                } else {
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                "getUser by name document null",
                                Exception()
                            )
                        )
                    )
                }

            }.addOnFailureListener { exception ->
                channel.close(exception)
                trySend(
                    Response.Failure(
                        e = SocialException(
                            "get user by name document doesnt exist",
                            Exception()
                        )
                    )
                )
            }

        awaitClose() {
        }
    }

    // Function to listen for user data changes
    override suspend fun getUserListener(id: String): Flow<Response<User>> = callbackFlow {
        val registration = usersRef.document(id).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                channel.close(exception)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val user: User? = snapshot.toObject<User>()
                if (user != null) {
                    trySend(Response.Success(user))
                }
            } else {
                // User data not found
                trySend(Response.Failure(SocialException("User data not found",e=java.lang.Exception())))
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    override suspend fun getUserByUsername(username: String): Flow<Response<User>> = callbackFlow {
        val registration =
            usersRef.whereEqualTo("username", username).get().addOnSuccessListener { documents ->
                var userList: List<User> = mutableListOf()

                //list should always be the size of 1
                if (userList.size > 1) {
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                "more than one of the usernames exist",
                                Exception()
                            )
                        )
                    )
                }

                val response = if (documents != null) {

                    userList = documents.map { it.toObject<User>() }
                    if (userList.size == 1) {
                        trySend(Response.Success(userList[0]))
                    } else {
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    "user found and is not 1",
                                    Exception()
                                )
                            )
                        )
                    }
                } else {
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                "getUser by name document null",
                                Exception()
                            )
                        )
                    )
                }

            }.addOnFailureListener { exception ->
                channel.close(exception)
                trySend(
                    Response.Failure(
                        e = SocialException(
                            "get user by name document doesnt exist",
                            Exception()
                        )
                    )
                )
            }

        awaitClose() {
        }

    }

    override suspend fun addUser(user: User): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val userId = user.id
            val addition = usersRef.document(userId).set(user).await1()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addUser exception", Exception())))
        }
    }

    override suspend fun addRequestToUser(
        activity_id: String,
        user_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update = usersRef.document(user_id)
                .update("user_requests", FieldValue.arrayUnion(activity_id)).await()
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

    override suspend fun removeRequestFromUser(
        activity_id: String,
        user_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val update = usersRef.document(user_id)
                .update("user_requests", FieldValue.arrayUnion(activity_id)).await()
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

    override suspend fun deleteUser(id: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion = usersRef.document(id).delete().await1()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("deleteUser exception", Exception())))
        }
    }



    override suspend fun addActivityToUser(activity_id: String, user: User): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)
                val update = usersRef.document(user.id)
                    .update("activities", FieldValue.arrayUnion(activity_id)).await1()
                emit(Response.Success(update))
            } catch (e: Exception) {
                emit(Response.Failure(e = SocialException("deleteUser exception", Exception())))
            }
        }
    override suspend fun removeActivityFromUser(activity_id: String, user_id: String): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)
                val update = usersRef.document(user_id)
                    .update("activities", FieldValue.arrayRemove(activity_id)).await1()
                emit(Response.Success(update))
            } catch (e: Exception) {
                emit(Response.Failure(e = SocialException("deleteUser exception", Exception())))
            }
        }

    override suspend fun getActivityUsers(activity_id: String): Flow<Response<List<User>>> =
        callbackFlow {

            usersRef.whereArrayContains("activities", activity_id)
                .orderBy("name", Query.Direction.DESCENDING).limit(10).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newUsers = ArrayList<User>()
                            for (document in documents) {
                                val user = document.toObject<User>()

                                Log.d("PARTICINAPTSDEBUG","GOT USER"+user.toString())

                                if (user != null) {
                                    newUsers.add(user)
                                }
                            }
                            lastVisibleUserActivityData = documents[documents.size - 1]
                            trySend(Response.Success(newUsers))

                        }
                    } else {
                        // There are no more messages to load
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    message = "failed to get users first",
                                    e = Exception()
                                )
                            )
                        )
                    }
                }
            awaitClose {

            }
        }

    override suspend fun getMoreActivityUsers(activity_id: String): Flow<Response<List<User>>> =
        callbackFlow {
           usersRef.whereArrayContains("activities", activity_id)
                .orderBy("name", Query.Direction.DESCENDING)  .startAfter(lastVisibleUserActivityData?.data?.get("name")) .limit(10).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newUsers = ArrayList<User>()
                            for (document in documents) {
                                val activity = document.toObject<User>()
                                Log.d("PARTICINAPTSDEBUG","GOT USER2"+activity.toString())

                                if (activity != null) {
                                    newUsers.add(activity)
                                }
                            }
                            lastVisibleUserActivityData = documents[documents.size - 1]
                            trySend(Response.Success(newUsers))

                        }
                    } else {
                        // There are no more messages to load
                        trySend(
                            Response.Failure(
                                e = SocialException(
                                    message = "failed to get more users",
                                    e = Exception()
                                )
                            )
                        )
                    }

                }
            awaitClose {
            }
        }

    override suspend fun updateUser(
        id: String,
        firstAndLastName: String,
        description: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)

            if (firstAndLastName.isEmpty()) {
                var deletion = usersRef.document(id).update("description", description).await1()
                emit(Response.Success(deletion))
            } else if (description.isEmpty()) {
                var deletion = usersRef.document(id).update("name", firstAndLastName).await1()
                emit(Response.Success(deletion))
            } else {
                var deletion = usersRef.document(id)
                    .update("name", firstAndLastName, "description", description).await1()
                emit(Response.Success(deletion))
            }

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "    override suspend fun updateUser(id: String,firstAndLastName:String,description:String): Flow<Response<Void?>> = flow {\n exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun addUsernameToUser(id: String, username: String): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)


                val addition = usersRef.document(id).update("username", username).await1()
                emit(Response.Success(addition))

            } catch (e: Exception) {
                emit(
                    Response.Failure(
                        e = SocialException(
                            "addUsernameToUser exception",
                            Exception()
                        )
                    )
                )
            }
        }

    override suspend fun setUserTags(id: String, tags: List<String>): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)
                val update = usersRef.document(id).update("tags", tags).await1()
                emit(Response.Success(update))
            } catch (e: Exception) {
                emit(Response.Failure(e = SocialException("update tags excpetion", Exception())))
            }
        }

    override suspend fun changeUserProfilePicture(
        user_id: String,
        picture_url: String,
    ): Flow<Response<Void?>> = flow {
        try {
            Log.d("ImagePicker", "changeUserProfilePicture called")
            emit(Response.Loading)
            val addition =
                usersRef.document(user_id).update("pictureUrl", picture_url)
                    .await1()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "changeUserProfilePicture exception",
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

    override suspend fun addProfilePictureToStorage(
        user_id: String,
        imageUri: Uri,
    ): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)

            if (imageUri != null) {
                val fileName = user_id
                try {
                    storageRef.child("images/images/$fileName" + "_320x320").delete().await1()
                } catch (e: StorageException) {

                }
                val imageRef = storageRef.child("images/$fileName")
                imageRef.putFile(imageUri).await1()
                val reference = storageRef.child("images/images/$fileName" + "_320x320")
                val url = keepTrying(5, reference)
                emit(Response.Success(url.toString()))
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
        imageUri: Uri,
    ): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            if (imageUri != null) {
                val fileName = id
                val imageRef = resStorage.child("images/$fileName")
                imageRef.putFile(imageUri).await1()
                val url = resStorage.child("images/$fileName" + "_600x600").downloadUrl.await1()
                emit(Response.Success(url.toString()))
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

    override suspend fun deleteProfilePictureFromStorage(
        user_id: String,
        picture_url: String,
    ): Flow<Response<Void?>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProfilePictureFromStorage(
        user_id: String,
        picture_url: String,
    ): Flow<Response<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMoreFriends(id: String): Flow<Response<ArrayList<User>>> =
        callbackFlow {
            Log.d("GEETINGUSERS", "getmorefriends")

            usersRef.whereArrayContains("friends_ids_list", id).orderBy("accountCreateTime")
                .startAfter(lastVisibleDataFriends?.get("accountCreateTime")).limit(5).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (documents != null && documents.isNotEmpty()) {
                            val newActivities = ArrayList<User>()
                            for (document in documents) {
                                val activity = document.toObject<User>()
                                if (activity != null) {
                                    newActivities.add(activity)
                                }
                            }
                            lastVisibleDataFriends = documents[documents.size - 1]
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

    override suspend fun getFriends(id: String): Flow<Response<ArrayList<User>>> = callbackFlow {
        trySend(Response.Loading)
        usersRef.whereArrayContains("friends_ids_list", id).orderBy("accountCreateTime").limit(5).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (documents != null && documents.isNotEmpty()) {
                        val newActivities = ArrayList<User>()
                        for (document in documents) {
                            val activity = document.toObject<User>()
                            Log.d("GEETINGUSERS", activity.toString())


                            if (activity != null) {
                                newActivities.add(activity)
                            }
                        }
                        lastVisibleDataFriends = documents[documents.size - 1]
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

    override suspend fun addInvitedIDs(
        my_id: String,
        invited_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val addition =
                usersRef.document(my_id).update("invited_ids", FieldValue.arrayUnion(invited_id))
                    .await1()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addInvitedIDs exception", Exception())))
        }
    }


    override suspend fun acceptInvite(
        current_user: User,
        senderId: String,
        chat: Chat,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val list_for_user_two = ArrayList<String>()
            val list_for_user_one = ArrayList<String>()
            list_for_user_two.add(senderId)
            list_for_user_one.add(current_user.id)
            val one = chatCollectionsRef.document(chat.id!!).set(chat).await1()
            val two = usersRef.document(senderId).update(
                "friends_ids" + "." + current_user.id,
                chat.id, "friends_ids_list", list_for_user_one,
                "invited_ids",
                FieldValue.arrayRemove(current_user.id)
            ).await1()

            val three =
                usersRef.document(current_user.id).update(
                    "friends_ids" + "." + senderId,
                    chat.id,
                    "friends_ids_list",
                    list_for_user_two
                )
                    .await1()
            emit(Response.Success(three))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addInvitedIDs exception", Exception())))
        }
    }

    override suspend fun recreateChatCollection(
        current_user_id: String,
        user_id: String,
        chat: Chat,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val one = chatCollectionsRef.document(chat.id!!).set(chat).await1()
            val two = usersRef.document(user_id).update(
                "friends_ids" + "." + current_user_id,
                chat.id,
            ).await1()
            val three =
                usersRef.document(current_user_id).update("friends_ids" + "." + user_id, chat.id)
                    .await1()
            emit(Response.Success(three))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addInvitedIDs exception", Exception())))
        }
    }

    override suspend fun removeInvitedIDs(
        my_id: String,
        invited_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion =
                usersRef.document(my_id).update("invited_ids", FieldValue.arrayRemove(invited_id))
                    .await1()
            emit(Response.Success(deletion))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("removeInvitedIDs exception", Exception())))
        }
    }

    override suspend fun addBlockedIDs(
        my_id: String,
        blocked_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val addition =
                usersRef.document(my_id).update("blocked_ids", FieldValue.arrayUnion(blocked_id))
                    .await1()
            emit(Response.Success(addition))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("addBlockedIDs exception", Exception())))
        }
    }

    override suspend fun removeBlockedIDs(
        my_id: String,
        blocked_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion =
                usersRef.document(my_id).update("blocked_ids", FieldValue.arrayRemove(blocked_id))
                    .await1()
            emit(Response.Success(deletion))

        } catch (e: Exception) {
            emit(Response.Failure(e = SocialException("removeBlockedIDs exception", Exception())))
        }
    }


    override suspend fun addFriendsIDs(my_id: String, friend_id: String): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)
                val addition =
                    usersRef.document(my_id).update("friends_ids" + "." + friend_id, "").await1()
                emit(Response.Success(addition))

            } catch (e: Exception) {
                emit(Response.Failure(e = SocialException("addFriendsIDs exception", Exception())))
            }
        }

    override suspend fun removeFriendsIDs(my_id: String, friend_id: String): Flow<Response<Void?>> =
        flow {
            try {
                emit(Response.Loading)
                val deletion = usersRef.document(my_id)
                    .update("friends_ids" + "." + friend_id, FieldValue.delete()).await1()
                emit(Response.Success(deletion))

            } catch (e: Exception) {
                emit(
                    Response.Failure(
                        e = SocialException(
                            "removeFriendsIDs exception",
                            Exception()
                        )
                    )
                )
            }
        }

    override suspend fun addFriendToBothUsers(
        my_id: String,
        friend_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val one = usersRef.document(my_id).update("friends_ids" + "." + friend_id, "").await1()
            val two = usersRef.document(friend_id).update("friends_ids" + "." + my_id, "").await1()

            emit(Response.Success(two))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "addFriendToBothUsers exception",
                        Exception()
                    )
                )
            )
        }
    }


    override suspend fun removeFriendFromBothUsers(
        my_id: String,
        friend_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val one = usersRef.document(my_id)
                .update(
                    "friends_ids" + "." + friend_id, FieldValue.delete(),
                    "friends_ids_list", FieldValue.arrayRemove(friend_id)
                ).await1()
            val two = usersRef.document(friend_id)
                .update(
                    "friends_ids" + "." + my_id, FieldValue.delete(),
                    "friends_ids_list", FieldValue.arrayRemove(my_id)
                ).await1()
            emit(Response.Success(two))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "removeFriendFromBothUsers exception",
                        Exception()
                    )
                )
            )
        }
    }

    override suspend fun addChatCollectionToUsers(
        id: String,
        friend_id: String,
        chat_id: String,
    ): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val one =
                usersRef.document(id).update("friends_ids" + "." + friend_id, chat_id).await1()
            val two =
                usersRef.document(friend_id).update("friends_ids" + "." + id, chat_id).await1()
            emit(Response.Success(two))

        } catch (e: Exception) {
            emit(
                Response.Failure(
                    e = SocialException(
                        "removeFriendFromBothUsers exception",
                        Exception()
                    )
                )
            )
        }
    }

    //todo paginate the daataaaaa
    override suspend fun getInvites(id: String): Flow<Response<ArrayList<User>>> = callbackFlow {
        Log.d("INVITESDEBUG","get invites cfalled"+id)

      usersRef.whereArrayContains("invited_ids", id)  .orderBy("invited_ids", Query.Direction.DESCENDING).limit(5).get()
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (documents != null && documents.isNotEmpty()) {
                        val newInvites = ArrayList<User>()
                        for (document in documents) {
                            val user = document.toObject<User>()
                            Log.d("INVITESDEBUG",user.toString())
                            if (user != null) {
                                newInvites.add(user)
                            }
                        }



                        lastVisibleInvite = documents[documents.size - 1]
                        trySend(Response.Success(newInvites))

                    }
                } else {
                    // There are no more messages to load
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                message = "failed to get inivties",
                                e = Exception()
                            )
                        )
                    )
                }
            }

        awaitClose() {

        }

    }
    override suspend fun getMoreInvites(id: String): Flow<Response<ArrayList<User>>> = callbackFlow {
        Log.d("invite","ger more invited Db: "+lastVisibleInvite?.data.toString())
        usersRef.whereArrayContains("invited_ids", id) .orderBy("invited_ids", Query.Direction.DESCENDING) .startAfter(lastVisibleInvite).limit(5).get()
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (documents != null && documents.isNotEmpty()) {
                        val newInvites = ArrayList<User>()
                        for (document in documents) {
                            val activity = document.toObject<User>()
                            if (activity != null) {
                                newInvites.add(activity)
                            }
                        }
                        Log.d("HOMESCREENTEST", "ROzmuiar")
                        Log.d("HOMESCREENTEST", documents.size.toString())
                        loaded_invites.addAll(newInvites)
                        val new_instance = ArrayList<User>()
                        new_instance.addAll(loaded_invites)
                        Log.d("HOMESCREENTEST", new_instance.size.toString())

                        lastVisibleInvite = documents[documents.size - 1]
                        trySend(Response.Success(new_instance))

                    }
                } else {
                    // There are no more messages to load
                    trySend(
                        Response.Failure(
                            e = SocialException(
                                message = "failed to get more invites",
                                e = Exception()
                            )
                        )
                    )
                }

            }
        awaitClose {
        }
    }
    override suspend fun checkIfChatCollectionExists(
        id: String,
        chatter_id: String,
    ): Flow<Response<User>> = callbackFlow {
        usersRef.document(id).get().addOnSuccessListener { documentSnapshot ->
            val response = if (documentSnapshot != null) {
                val activity = documentSnapshot.toObject<User>()
                activity?.friends_ids?.forEach { (key, value) ->

                    if (key == chatter_id) {

                    }

                }


                Response.Success(activity)
            } else {
                Response.Failure(e = SocialException("getMessage document null", Exception()))
            }
            trySend(response as Response<User>).isSuccess
        }

    }

}