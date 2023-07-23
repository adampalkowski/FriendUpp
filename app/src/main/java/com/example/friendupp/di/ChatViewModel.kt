package com.example.friendupp.di

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.Navigation.getCurrentUTCTime
import com.example.friendupp.model.Chat
import com.example.friendupp.model.ChatMessage
import com.example.friendupp.model.Response
import com.example.friendupp.model.User
import com.example.friendupp.util.getTime
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val repo: ChatRepository
) : ViewModel() {
    private var alreadyRead:Boolean= false

    val _chatCollectionStateFlow = MutableStateFlow<Chat>(Chat())

    private val _chatCollectionState = MutableStateFlow<Response<Chat>?>(null)
    val chatCollectionState: StateFlow<Response<Chat>?> = _chatCollectionState

    private val _uri = MutableStateFlow<Uri?>(null)
    val uri: MutableStateFlow<Uri?> = _uri
    private val _uriReceived = mutableStateOf(false)
    val uriReceived: State<Boolean> = _uriReceived
    private val _granted_permission = MutableStateFlow<Boolean>(false)
    val granted_permission: StateFlow<Boolean> = _granted_permission
    val chat_type = mutableStateOf("")
    val duo_user = mutableStateOf(User())

    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location
    fun setLocation(location: LatLng){
        _location.value=location
    }
    fun permissionGranted(){
        _granted_permission.value=true
    }

    private val _chatCollectionsState = mutableStateOf<Response<ArrayList<Chat>>>(Response.Loading)
    val chatCollectionsState: State<Response<ArrayList<Chat>>> = _chatCollectionsState


    private val _messagesState = mutableStateOf<Response<ArrayList<ChatMessage>>?>(null)
    val messagesState: State<Response<ArrayList<ChatMessage>>?> = _messagesState
    private val _firstMessagesState = mutableStateOf<Response<ArrayList<ChatMessage>>?>(null)
    val firstMessagesState: State<Response<ArrayList<ChatMessage>>?> = _firstMessagesState
    private val _groupsState = MutableStateFlow<Response<ArrayList<Chat>>?>(null)
    val groupsState: StateFlow<Response<ArrayList<Chat>>?> = _groupsState
    private val _moreGroupsState = MutableStateFlow<Response<ArrayList<Chat>>?>(null)
    val moreGroupsState: StateFlow<Response<ArrayList<Chat>>?> = _moreGroupsState
    private val _moreMessagesState = mutableStateOf<Response<ArrayList<ChatMessage>>?>(null)
    val moreMessagesState: State<Response<ArrayList<ChatMessage>>?> = _moreMessagesState
    private val _addedMessagesState = mutableStateOf<Response<ArrayList<ChatMessage>>>(Response.Loading)
    val addedMessagesState: State<Response<ArrayList<ChatMessage>>> = _addedMessagesState

    private val _addChatCollectionState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val addChatCollectionState: State<Response<Void?>> = _addChatCollectionState

    private val _deleteChatCollectionState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val deleteChatCollectionState: State<Response<Void?>> = _deleteChatCollectionState

    private val _updateChatCollectionRecentMessageState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val updateChatCollectionRecentMessageState: State<Response<Void?>> = _updateChatCollectionRecentMessageState

    private val _updateChatCollectionMembersState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val updateChatCollectionMembersState: State<Response<Void?>> = _updateChatCollectionMembersState

    private val _updateChatCollectionNameState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val updateChatCollectionNameState: State<Response<Void?>> = _updateChatCollectionNameState

    private val _messageState = mutableStateOf<Response<ChatMessage>>(Response.Loading)
    val messageState: State<Response<ChatMessage>> = _messageState

    private val _addMessageState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val addMessageState: State<Response<Void?>> = _addMessageState

    private val _isImageAddedToStorageAndFirebaseState = MutableStateFlow<Response<String>?>(null)
    val isImageAddedToStorageAndFirebaseState:MutableStateFlow<Response<String>?> = _isImageAddedToStorageAndFirebaseState

    private val _isImageAddedToStorageState = MutableStateFlow<Response<String>?>(null)
    val isImageAddedToStorageFlow: StateFlow<Response<String>?> = _isImageAddedToStorageState

    private val _deleteMessageState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val deleteMessageState: State<Response<Void?>> = _deleteMessageState

    private val _checkIfChatExistsState = mutableStateOf<Response<Chat>>(Response.Loading)
    val checkIfChatExistsState: State<Response<Chat>> = _checkIfChatExistsState

    private val _highlightAddedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val highlightAddedState: State<Response<Void?>> = _highlightAddedState

    private val _highlightRemovedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val highlightRemovedState: State<Response<Void?>> = _highlightRemovedState


    private val _loadedMessages: ArrayList<ChatMessage> = ArrayList()
     val loadedMessages: ArrayList<ChatMessage> = _loadedMessages
    fun getChatCollections(id:String){
        viewModelScope.launch {
            repo.getChatCollections(id).collect{
                    response->
                _chatCollectionsState.value=response
            }
        }
    }
    fun reportChat(id:String){
        viewModelScope.launch {
            repo.reportChat(id).collect{
                    response->

            }
        }
    }
    fun blockChat(id:String){
        viewModelScope.launch {
            repo.blockChat(id).collect{
                    response->

            }
        }
    }

    fun resetChat(){
        _chatCollectionState.value=null
    }
    fun resetNewMessages(){
        _messagesState.value=null
    }
    fun resetFirstMessages(){
        _firstMessagesState.value=null
    }
    fun resetMoreMessages(){
        _moreMessagesState.value=null
    }

    fun onUriReceived(uri: Uri) {
        _uri.value = uri
        _uriReceived.value = true
    }


    fun onUriProcessed() {
        _uriReceived.value = false
        _uri.value=null
    }
    fun getChatCollection(id: String) {
        viewModelScope.launch {
            repo.getChatCollection(id).collect{ response->
                        _chatCollectionState.value=response
            }
        }
    }

    fun getGroups(id: String) {
        viewModelScope.launch {
            repo.getGroups(id).collect{
                    response->
                _groupsState.value=response
            }
        }
    }
    fun resetGroups(){
        _groupsState.value=null
    }
    fun resetMoreGroups(){
        _moreGroupsState.value=null
    }
    fun getMoreGroups(id: String) {
        viewModelScope.launch {
            repo.getMoreGroups(id).collect{
                    response->
                _moreGroupsState.value=response
            }
        }
    }
    fun addGroupAlone(chatCollection: Chat,url:String?,onFinished:(String)->Unit={}) {
        viewModelScope.launch {
            val uuid: UUID = UUID.randomUUID()
            val id:String = uuid.toString()
            if (chatCollection.id!!.isEmpty()||chatCollection.id==null){
                chatCollection.id=id
            }
            if(url!=null && url.isNotEmpty()){
                repo.addLoweResImageFromGalleryToStorage(id, url.toUri()).collect{ response ->
                    when(response){
                        is Response.Success ->{
                            chatCollection.create_date= getTime()
                            chatCollection.imageUrl=response.data
                            repo.addChatCollection(chatCollection).collect{
                                    response->
                                _addChatCollectionState.value=response
                            }
                            onFinished(response.data)
                        }
                        is Response.Loading ->{
                            _addChatCollectionState.value=Response.Loading
                        }
                        is Response.Failure ->{}

                    }
                }
            }else{
                chatCollection.create_date= getTime()
                repo.addChatCollection(chatCollection).collect{
                        response->
                    _addChatCollectionState.value=response
                }
                onFinished("")
            }


        }
    }
    fun updateActivityImage(id: String,url:String,onFinished:(String)->Unit={}) {
        viewModelScope.launch {
                repo.updateActivityImage(id, url.toUri()).collect{ response ->
                    when(response){
                        is Response.Success ->{

                        }
                        is Response.Loading ->{
                            _addChatCollectionState.value=Response.Loading
                        }
                        is Response.Failure ->{}

                    }
                }



        }
    }
    fun addChatCollection(chatCollection: Chat,url:String?,onFinished:(String)->Unit={}) {
        viewModelScope.launch {

            if(url!=null && url.isNotEmpty()){
                repo.addImageFromGalleryToStorage(chatCollection.id!!, url.toUri()).collect{ response ->
                    when(response){
                        is Response.Success ->{
                            chatCollection.create_date= getTime()
                            chatCollection.imageUrl=response.data
                            repo.addChatCollection(chatCollection).collect{
                                    response->
                                _addChatCollectionState.value=response
                            }
                            onFinished(response.data)
                        }
                        is Response.Loading ->{
                            _addChatCollectionState.value=Response.Loading
                        }
                        is Response.Failure ->{}

                    }
                }
            }else{
                chatCollection.create_date= getTime()
                repo.addChatCollection(chatCollection).collect{
                        response->
                    _addChatCollectionState.value=response
                }
                onFinished("")
            }


        }
    }
    fun deleteChatCollection(id: String) {
        viewModelScope.launch {
            repo.deleteChatCollection(id).collect{
                    response->
                _deleteChatCollectionState.value=response
                /*todo delete the chat image from storage*/
            }
        }
    }

    fun updateChatCollectionRecentMessage(
        id: String,
        recentMessage: String
    ) {
        viewModelScope.launch {
            repo.updateChatCollectionRecentMessage(id,recentMessage).collect{
                    response->
                _updateChatCollectionRecentMessageState.value=response
            }
        }
    }

    fun updateChatCollectionMembers(
        members_list: List<String>,
        id: String
    ) {
        viewModelScope.launch {
            repo.updateChatCollectionMembers(members_list,id).collect{
                    response->
                _updateChatCollectionMembersState.value=response
            }
        }
    }

    fun updateChatCollectionName(
        chatCollectionName: String,
        id: String
    ) {
        viewModelScope.launch {
            repo.updateChatCollectionName(chatCollectionName,id).collect{
                    response->
                _updateChatCollectionNameState.value=response
            }
        }
    }

    fun getMessage(
        chat_collection_id: String,
        message_id: String
    ) {
        viewModelScope.launch {
            repo.getMessage(chat_collection_id,message_id).collect{
                    response->
                _messageState.value=response
            }
        }
    }


    fun getMessages(id: String,current_time:String) {
        viewModelScope.launch {
            repo.getMessages(id,current_time).collect{response->
            }

        }
    }
    fun getFirstMessages(id: String,current_time:String) {
        viewModelScope.launch {
            repo.getFirstMessages(id,current_time).collect{response->

                _firstMessagesState.value=response

            }

        }
    }
    fun resetLoadedMessages(){
        _loadedMessages.clear()
    }
    fun getMoreMessages(id: String) {
        viewModelScope.launch {
            repo.getMoreMessages(id).collect { response ->
                when (response) {
                    is Response.Success -> {
                        _loadedMessages.addAll(response.data)
                        _moreMessagesState.value = response
                    }
                    is Response.Failure -> {
                        _moreMessagesState.value = response
                    }
                    else->{}
                }
            }
        }
    }

    fun addMessage(
        chat_collection_id: String,
        message: ChatMessage
    ) {
        val uuid: UUID = UUID.randomUUID()
        val id:String = uuid.toString()
        message.id=id
        message.sent_time= getCurrentUTCTime()
        viewModelScope.launch {
            repo.addMessage(chat_collection_id,message).collect{
                    response->
                _addMessageState.value=response
            }

            repo.updateChatCollectionRecentMessage(chat_collection_id, recent_message =
            if (message.message_type.equals("latLng")){"shared location"}else{  message.text}
          , recent_message_time = message.sent_time).collect{
                    response->
                _addMessageState.value=response
            }
        }

    }
    fun addImageToStorage(id:String,picture_uri: Uri){
        viewModelScope.launch {
            repo.addImageFromGalleryToStorage(id, picture_uri).collect{ response ->
                _isImageAddedToStorageState.value=response
            }
        }
    }
    fun deleteMessages(
        chat_collection_id: String,
    ) {
        viewModelScope.launch {
            repo.deleteMessage(chat_collection_id).collect{
                    response->
                _deleteMessageState.value=response
            }
        }
    }

    fun addHighLight(group_id:String,highlitedMessageText: String) {
        viewModelScope.launch {
            repo.addGroupHighlight(group_id,highlitedMessageText).collect{
                    response->
                _highlightAddedState.value=response
            }
        }
    }
    fun removeHighLight(group_id:String,highlitedMessageText: String) {
        viewModelScope.launch {
            repo.removeGroupHighlight(group_id).collect{
                    response->
                _highlightRemovedState.value=response
            }
        }
    }

    fun sendImage(chat_id: String,message:ChatMessage, uri: Uri) {
        _isImageAddedToStorageAndFirebaseState.value=Response.Loading
        val uuid: UUID = UUID.randomUUID()
        val id:String = uuid.toString()
        message.id=id

        message.sent_time= getCurrentUTCTime()
        viewModelScope.launch {
            repo.addImageFromGalleryToStorage(id, uri).collect{ response ->
                _isImageAddedToStorageState.value=response
                when(response){
                    is Response.Success->{
                        val new_url:String=response.data
                        message.text=new_url
                        Log.d("ImagePicker","url add message"+message.text)
                        repo.addMessage(chat_id,message).collect{
                                response->
                            _addMessageState.value=response
                            _isImageAddedToStorageAndFirebaseState.value=Response.Success("successfully added image")
                        }
                        repo.updateChatCollectionRecentMessage(chat_id, recent_message = "image sent", recent_message_time = message.sent_time).collect{
                                response->
                            _addMessageState.value=response
                        }
                    }
                    is Response.Loading->{

                    }
                    is Response.Failure->{

                    }
                }

            }

        }
    }

}