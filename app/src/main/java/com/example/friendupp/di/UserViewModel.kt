package com.example.friendupp.di

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.Notification.MyFirebaseMessaging
import com.example.friendupp.Notification.Token
import com.example.friendupp.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UserRepository,
) : ViewModel() {
    val userProfileId = MutableLiveData<String>()
    val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: StateFlow<User?> = _currentUserProfile
    private val _userValidation = MutableStateFlow<Response<Boolean>>(Response.Loading)
    val userValidation: StateFlow<Response<Boolean>> = _userValidation
    fun onUriProcessed() {
        _uriReceived.value = false
    }
    fun clearInvites() {
        _invitesStateFlow.value=null
    }

    private val _currentUserState = MutableStateFlow<Response<User?>?>(null)
    val currentUserState: StateFlow<Response<User?>?> = _currentUserState


    private val _userState = MutableStateFlow<Response<User?>?>(null)
    val userState: StateFlow<Response<User?>?> = _userState
    private val _userListenerState = MutableStateFlow<Response<User?>?>(null)
    val userListenerState: StateFlow<Response<User?>?> = _userListenerState

    private val _friendsList = mutableStateOf<List<User>>(emptyList())
    val friendsList: MutableState<List<User>> = _friendsList
    private val _friendsLoading = mutableStateOf(false)
    val friendsLoading: State<Boolean> = _friendsLoading

    // Function to cancel the listener and flow
    fun cancelCurrentUserListener() {
        viewModelScope.coroutineContext.cancelChildren()
    }
    fun resetMoreFriends() {
        _friendMoreState.value=null
    }

    private val _friendMoreState = MutableStateFlow<Response<ArrayList<User>>?>(null)
    val friendMoreState: StateFlow<Response<ArrayList<User>>?> = _friendMoreState


    private val _isUserAddedState = mutableStateOf<Response<Void?>?>(Response.Success(null))
    val isUserAddedState: State<Response<Void?>?> = _isUserAddedState

    private val _isUsernameAddedFlow = MutableStateFlow<Response<Void?>>(Response.Loading)
    val isUsernameAddedFlow: StateFlow<Response<Void?>> = _isUsernameAddedFlow

    private val _loginFlow = MutableStateFlow<Response<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Response<FirebaseUser>?> = _loginFlow

    private val _isUserDeletedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isUserDeletedState: State<Response<Void?>> = _isUserDeletedState
    private val _isUserTagsAdded = mutableStateOf<Response<Void?>?>(null)
    val isUserTagsAdded: State<Response<Void?>?> = _isUserTagsAdded
    private val _isUserUpdated = mutableStateOf<Response<Void?>?>(null)
    val isUserUpdated: State<Response<Void?>?> = _isUserUpdated
    private val _isActivityAddedToUser = mutableStateOf<Response<Void?>?>(null)
    val isActivityAddedToUser: State<Response<Void?>?> = _isActivityAddedToUser
    private val _isActivityRemovedFromUser = mutableStateOf<Response<Void?>?>(null)
    val isActivityRemovedFromUser: State<Response<Void?>?> = _isActivityRemovedFromUser

    private val _isInviteAddedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isInviteAddedState: State<Response<Void?>> = _isInviteAddedState

    private val _isInviteRemovedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isInviteRemovedState: State<Response<Void?>> = _isInviteRemovedState

    private val _isBlockedRemovedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isBlockedRemovedState: State<Response<Void?>> = _isBlockedRemovedState

    private val _isBlockedAddedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isBlockedAddedState: State<Response<Void?>> = _isBlockedAddedState

    private val _isFriendAddedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isFriendAddedState: State<Response<Void?>> = _isFriendAddedState

    private val _isFriendRemovedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isFriendRemovedState: State<Response<Void?>> = _isFriendRemovedState

    private val _isFriendAddedToBothUsersState =
        mutableStateOf<Response<Void?>>(Response.Success(null))
    val isFriendAddedToBothUsersState: State<Response<Void?>> = _isFriendAddedToBothUsersState

    private val _isFriendRemovedFromBothUsersState =
        mutableStateOf<Response<Void?>>(Response.Success(null))
    val isFriendRemovedFromBothUsersState: State<Response<Void?>> =
        _isFriendRemovedFromBothUsersState

    private val _isChatCollectionAddedToUsersState =
        mutableStateOf<Response<Void?>>(Response.Success(null))
    val isChatCollectionAddedToUsersState: State<Response<Void?>> =
        _isChatCollectionAddedToUsersState
    private val _isChatAddedToUsersState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isChatAddedToUsersState: State<Response<Void?>> = _isChatAddedToUsersState

    private val _invitesStateFlow = mutableStateOf<Response<ArrayList<User>>?>(null)
    val invitesStateFlow: State<Response<ArrayList<User>>?> = _invitesStateFlow
    private val _moreInvitesState = mutableStateOf<Response<ArrayList<User>>?>(null)
    val moreInvitesState: State<Response<ArrayList<User>>?> = _moreInvitesState

    private val _pictureAddedStateFlow = mutableStateOf<Response<User>>(Response.Loading)
    val pictureAddedStateFlow: State<Response<User>> = _pictureAddedStateFlow

    private val _isInviteAcceptedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isInviteAcceptedState: State<Response<Void?>> = _isInviteAcceptedState
    private val _isChatCollectionRecreatedState =
        mutableStateOf<Response<Void?>>(Response.Success(null))
    val isChatCollectionRecreatedState: State<Response<Void?>> = _isChatCollectionRecreatedState

    private val _isImageAddedToStorageState = mutableStateOf<Response<String>>(Response.Loading)
    val isImageAddedToStorageState: State<Response<String>> = _isImageAddedToStorageState

    private val _isUserProfilePictureChangedState =
        mutableStateOf<Response<Void?>>(Response.Success(null))
    val isUserProfilePictureChangedState: State<Response<Void?>> = _isUserProfilePictureChangedState

    private var registration: ListenerRegistration? = null

    private val _isRequestAddedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isRequestAddedState: State<Response<Void?>> = _isRequestAddedState

    private val _isRequestRemovedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isRequestRemovedState: State<Response<Void?>> = _isRequestRemovedState

    private val _activityUsersState = mutableStateOf<Response<List<User>>?>(null)
    val activityUsersState: State<Response<List<User>>?> = _activityUsersState

    private val _moreActivityUsersState = mutableStateOf<Response<List<User>>?>(null)
    val moreActivityUsersState: State<Response<List<User>>?> = _moreActivityUsersState

    private val _uri = MutableStateFlow<Uri?>(null)
    val uri: MutableStateFlow<Uri?> = _uri
    private val _uriReceived = mutableStateOf(false)
    val uriReceived: State<Boolean> = _uriReceived
    fun onUriReceived(uri: Uri?) {
        _uri.value = uri
        _uriReceived.value = true
    }
    fun addChatCollectionToUsers(id: String, friend_id: String, chat_id: String) {
        viewModelScope.launch {
            repo.addChatCollectionToUsers(id, friend_id, chat_id).collect { response ->
                _isChatCollectionAddedToUsersState.value = response
            }
        }
    }

    fun resetUserValidation() {
        _userValidation.value = Response.Loading
    }

    fun getActivityUsers(id: String) {
        viewModelScope.launch {
            repo.getActivityUsers(id).collect { response ->
                _activityUsersState.value = response
            }
        }
    }
    fun clearUsers() {
        _activityUsersState.value=null
    }
    fun clearMoreUsers() {
        _moreActivityUsersState.value=null
    }

    fun getMoreActivityUsers(id: String) {
        viewModelScope.launch {
            repo.getMoreActivityUsers(id).collect { response ->
                _moreActivityUsersState.value = response
            }
        }
    }
    fun getFriendsList(): List<User> {
        return friendsList.value
    }
    fun getFriends(id: String) {
        viewModelScope.launch {
            repo.getFriends(id).collect { response ->
                _friendsLoading.value = true // Set the loading state to true before fetching data

                when (response) {
                    is Response.Success -> {
                        _friendsList.value = response.data ?: emptyList()
                        Log.d("FriendsViewModel", "Friends fetched successfully for user: $id")
                    }
                    is Response.Failure -> {
                        // Handle the failure case if needed (e.g., show an error message)
                        // For example:
                        _friendsList.value = emptyList()
                        Log.e("FriendsViewModel", "Failed to fetch friends for user: $id")
                    }
                    is Response.Loading -> {
                        Log.d("FriendsViewModel", "Loading friends for user: $id...")
                    }
                    null -> {
                        Log.e("FriendsViewModel", "Null response received for user: $id")
                    }
                }
                _friendsLoading.value = false // Set the loading state to true before fetching data

            }
        }
    }

    fun getMoreFriends(id: String) {
        viewModelScope.launch {
            repo.getMoreFriends(id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        // If the addition was successful, append the new friends to the existing list
                        // This assumes that you have access to the local list of friends in this view model.
                        _friendsList.value = _friendsList.value + (response.data ?: emptyList())
                        Log.d("FriendsViewModel", "More friends fetched successfully for user: $id")
                    }
                    is Response.Failure -> {
                        // Handle the failure case if needed (e.g., show an error message)
                        // For example:
                        Log.e("FriendsViewModel", "Failed to fetch more friends for user: $id")
                    }
                    is Response.Loading -> {
                        Log.d("FriendsViewModel", "Loading more friends for user: $id...")
                    }
                    null -> {
                        Log.e("FriendsViewModel", "Null response received for user: $id")
                    }
                }
            }

        }
    }

    fun acceptInvite(current_user: User, senderId: String, chat: Chat) {
        viewModelScope.launch {
            repo.acceptInvite(current_user, senderId, chat).collect { response ->
                _isInviteAcceptedState.value = response
            }
        }
    }

    fun addRequestToUser(activity_id: String, user_id: String) {
        viewModelScope.launch {
            repo.addRequestToUser(activity_id, user_id).collect { response ->
                _isRequestAddedState.value = response

            }
        }
    }

    fun removeRequestFromUser(activity_id: String, user_id: String) {
        viewModelScope.launch {
            repo.removeRequestFromUser(activity_id, user_id).collect { response ->
                _isRequestRemovedState.value = response

            }
        }
    }

    fun recreateChatCollection(current_user_id: String, user_id: String, chat: Chat) {
        viewModelScope.launch {
            repo.recreateChatCollection(current_user_id, user_id, chat).collect { response ->
                _isChatCollectionRecreatedState.value = response
            }
        }
    }

    fun changeUserProfilePicture(user_id: String, picture_uri: Uri) {
        viewModelScope.launch {
            _userState.value = Response.Loading
            Log.d("ImagePicker", "changeUserProfilePicture called")
            repo.addProfilePictureToStorage(user_id, picture_uri).collect { response ->
                when (response) {
                    is Response.Success -> {
                        val imageUrl = response.data
                        Log.d("UserViewModel", "changeUserProfilePicture response" + response.data)
                        repo.changeUserProfilePicture(user_id, response.data).collect { response ->
                            when (response) {
                                is Response.Success -> {
                                    Log.d(
                                        "UserViewModel",
                                        "changeUserProfilePicture response" + response.data
                                    )
                                    currentUserProfile.value?.pictureUrl = imageUrl
                                    UserData.user!!.pictureUrl = imageUrl
                                    Log.d("Edit_profile_screen", "get user called")
                                    repo.getUser(user_id).collect { response ->
                                        _userState.value = response
                                        when (response) {
                                            is Response.Success -> {
                                                UserData.user = response.data
                                            }
                                            else -> {}
                                        }

                                    }
                                }
                                is Response.Failure -> {
                                    Log.d("ImagePicker", response.e.message)
                                }
                                else -> {}
                            }

                        }
                    }
                    is Response.Failure -> {
                        Log.d("ImagePicker", response.e.message)
                    }
                    else -> {}
                }

            }

        }
    }

    fun addImageToStorage(id: String, picture_uri: Uri) {
        viewModelScope.launch {
            repo.addProfilePictureToStorage(id, picture_uri).collect { response ->
                _isImageAddedToStorageState.value = response
            }
        }
    }

    fun addFriendToBothUsers(my_id: String, friend_id: String) {
        viewModelScope.launch {
            repo.addFriendToBothUsers(my_id, friend_id).collect { response ->
                _isFriendAddedToBothUsersState.value = response
            }
        }
    }

    fun removeFriendFromBothUsers(my_id: String, friend_id: String) {
        viewModelScope.launch {
            repo.removeFriendFromBothUsers(my_id, friend_id).collect { response ->
                _isFriendRemovedFromBothUsersState.value = response
            }
        }
    }

    fun addFriendIdToUser(my_id: String, friend_id: String) {
        viewModelScope.launch {
            repo.addInvitedIDs(my_id, friend_id).collect { response ->
                _isFriendAddedState.value = response
            }
        }
    }

    fun removeFriendIdFromUser(my_id: String, friend_id: String) {
        viewModelScope.launch {
            repo.removeInvitedIDs(my_id, friend_id).collect { response ->
                _isFriendRemovedState.value = response
            }
        }
    }

    fun addBlockedIdToUser(my_id: String, blocked_id: String) {
        viewModelScope.launch {
            repo.addBlockedIDs(my_id, blocked_id).collect { response ->
                _isBlockedAddedState.value = response
            }
        }
    }

    fun removeBlockedIdFromUser(my_id: String, blocked_id: String) {
        viewModelScope.launch {
            repo.removeBlockedIDs(my_id, blocked_id).collect { response ->
                _isBlockedRemovedState.value = response
            }
        }
    }

    fun removeInvitedIdFromUser(my_id: String, invited_id: String) {
        viewModelScope.launch {
            repo.removeInvitedIDs(my_id, invited_id).collect { response ->
                _isInviteRemovedState.value = response
            }
        }
    }

    fun addInvitedIdToUser(my_id: String, invited_id: String) {
        viewModelScope.launch {
            repo.addInvitedIDs(my_id, invited_id).collect { response ->
                _isInviteAddedState.value = response
            }
        }
    }


    fun getInvites(id: String) {
        viewModelScope.launch {
            repo.getInvites(id).collect { response ->
                _invitesStateFlow.value = response
            }
        }
    }
    fun getMoreInvites(id: String) {
        viewModelScope.launch {
            repo.getMoreInvites(id).collect { response ->
                _moreInvitesState.value = response
            }
        }
    }

    fun resetUserValue() {
        viewModelScope.launch {
            _userState.value = null
        }
    }

    fun setCurrentUser(user: User) {
        _currentUserProfile.value = user
    }
    fun setUserData(user: User) {
        UserData.user=user
    }
    fun getUser(id: String) {
        viewModelScope.launch {
            repo.getUser(id).collect { response ->
                _userState.value = response
            }
        }
    }
    private val _user= mutableStateOf<User?> (null)
    val user: MutableState<User?> = _user
    fun getUserProfile(): User? {
        return user.value
    }

    fun getUserListener(id: String) {
        viewModelScope.launch {
            repo.getUserListener(id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        val user: User = response.data
                        _user.value = user
                        Log.d("UserViewModel", "User data fetched successfully")
                    }
                    is Response.Failure -> {
                        _user.value = null
                        Log.e("UserViewModel", "Failed to fetch user data: ${response.e.message}")
                    }
                    is Response.Loading -> {
                        Log.d("UserViewModel", "Loading user data...")
                    }
                    null -> {
                        _user.value = null
                        Log.e("UserViewModel", "Null response received")
                    }
                }

            }
        }
    }

    //eror prone ? uses _userState same as get user above
    fun getUserByUsername(username: String) {
        viewModelScope.launch {
            repo.getUserByUsername(username).collect { response ->
                _userState.value = response
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            repo.addUser(user).collect { response ->
                _isUserAddedState.value = response
            }
        }
    }

    fun userAdded() {
        _isUserAddedState.value = null
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            repo.deleteUser(id).collect { response ->
                _isUserDeletedState.value = response
            }
        }
    }
    fun addUsernameToUser(id: String, username: String) {
        viewModelScope.launch {
            repo.getUserByUsername(username).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _isUsernameAddedFlow.value =
                            Response.Failure(
                                e = SocialException(
                                    "addUsernameToUser erro user with same username has been found",
                                    Exception()
                                )
                            )
                    }
                    is Response.Failure -> {
                        repo.addUsernameToUser(id = id, username = username).collect { response ->
                            _isUsernameAddedFlow.value = response
                        }
                    }
                    else -> {}
                }
            }

        }
    }

    fun checkIfUsernameExists( username: String) {
        viewModelScope.launch {
            repo.getUserByUsername(username).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _isUsernameAddedFlow.value =
                            Response.Failure(
                                e = SocialException(
                                    "addUsernameToUser error user with same username has been found",
                                    Exception()
                                )
                            )
                    }
                    is Response.Failure -> {
                        _isUsernameAddedFlow.value =Response.Success(null)
                    }
                    else -> {}
                }
            }

        }
    }
     fun updateToken(task: Task<String>,user_id:String) {
        val reference = FirebaseDatabase.getInstance("https://friendupp-3ecc2-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tokens")
         Log.d("TOKEN","UPDATE TOKEN CALLED")
        task.addOnCompleteListener { task ->
            val token1 = Token(task.result)
            reference.child(user_id).setValue(token1).addOnCompleteListener { task->
                Log.d("TOKEN",task.toString())
            }
        }
    }
    fun validateUser(firebaseUser: FirebaseUser) {
        val id: String = firebaseUser.uid
        viewModelScope.launch {
            repo.getUser(id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        //get user from database and check if
                        val user: User = response.data
                        FirebaseMessaging.getInstance().deleteToken()
                        // Get token
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                            //On token fetch fail
                            if (!task.isSuccessful) {
                                //msg_token_failed
                                return@OnCompleteListener
                            }

                            // Get new Instance ID token
                            val newDeviceToken = task.result
                                MyFirebaseMessaging.getInstance().updateToken(task.result)
                        })


                        _currentUserState.value = response
                        Log.d("LOGINGRAPHDEBUG", user.toString())
                        //emails don't match
                        //TODO SHOW CORRECT EXCEPTION
                        if (user.email != firebaseUser.email) {
                            _userValidation.value = Response.Failure(
                                SocialException(
                                    message = "validate user error emails dont match",
                                    Exception()
                                )
                            )
                        }
                        if (user.username == null || user.username.toString().isEmpty()) {
                            //this needs a username to be set aswell but for safety set it right now without username
                            UserData.user = user
                            Log.d("TAG", "succes")
                            _userValidation.value = Response.Success(false)
                        } else {
                            //SET THE GLOBAL USER
                            UserData.user = user
                            Log.d("TAG", "succes")
                            _userValidation.value = Response.Success(true)
                        }

                    }
                    is Response.Failure -> {
                        //issue with retreiving user from database
                        _userValidation.value = Response.Failure(
                            SocialException(
                                "User not in database",
                                Exception()
                            )
                        )
                    }
                    else -> {}
                }


            }
        }

    }

    fun profileChanges(id: String, firstAndLastName: String, description: String) {
        viewModelScope.launch {
            repo.updateUser(id, firstAndLastName, description = description).collect { response ->
                _isUserUpdated.value = response
            }
        }
    }

    fun setUserTags(id: String, tags: List<String>) {
        viewModelScope.launch {
            repo.setUserTags(id, tags).collect { response ->
                _isUserTagsAdded.value = response
            }
        }
    }

    fun addActivityToUser(id: String, user: User) {
        viewModelScope.launch {
            repo.addActivityToUser(id, user).collect { response ->
                _isActivityAddedToUser.value=response
            }
        }
    }
    fun removeActivityFromUser(id: String, user_id: String) {
        viewModelScope.launch {
            repo.removeActivityFromUser(id, user_id).collect { response ->
                _isActivityRemovedFromUser.value=response

            }
        }
    }
    fun resetIsUsernameAdded() {
        _isUsernameAddedFlow.value = Response.Loading
    }

}