package com.example.friendupp.ChatUi

import android.net.Uri
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Home.eButtonSimple
import com.example.friendupp.Home.eButtonSimpleBlue
import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.Profile.NameState
import com.example.friendupp.Profile.NameStateSaver
import com.example.friendupp.R
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.ChatMessage
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.LatLng


sealed class ChatEvents {
    object GoBack : ChatEvents()
    class GetMoreMessages(val chat_id:String) : ChatEvents()
    class SendMessage (val chat_id:String,val message:String): ChatEvents()
    object OpenChatSettings : ChatEvents()
    class Reply(val message:ChatMessage) : ChatEvents()
    object Report : ChatEvents()
    class Delete (val id :String): ChatEvents()
    class Copy (val text:String): ChatEvents()
    object Share : ChatEvents()
}


@Composable
fun ChatContent(
    modifier: Modifier,
    onEvent: (ChatEvents) -> Unit,
    chatViewModel: ChatViewModel,
) {
    var chat = remember{ mutableStateOf<Chat?>(null) }
    loadChat(chatViewModel,chat)
    var data = remember{ mutableStateListOf<ChatMessage>()}
    var data_new = remember{ mutableStateListOf<ChatMessage>()}
    var frist_data = remember{ mutableStateListOf<ChatMessage>()}
    val valueExist = remember { mutableStateOf(false) }
    loadMessages(frist_data,data,data_new,chatViewModel, valueExist = valueExist)

    BackHandler(true) {
        onEvent(ChatEvents.GoBack)
    }
    val replyMessage = remember { mutableStateOf<ChatMessage?>(null) }
    val permission_flow = chatViewModel.granted_permission.collectAsState()
    val location_flow = chatViewModel.location.collectAsState()
    val isImageAddedToStorage by chatViewModel.isImageAddedToStorageFlow.collectAsState()

    if (chat.value!=null){
        var chat_name = chat.value!!.name.toString()
        if (chat.value!!.type.equals("duo")){
            if(chat.value!!.user_one_username==UserData.user!!.username){
                chat_name=chat.value!!.user_two_username.toString()
            }else{
                chat_name=chat.value!!.user_one_username.toString()

            }
        }
        var chat_image = chat.value!!.imageUrl.toString()
        if (chat.value!!.type.equals("duo")){
            if(chat.value!!.user_one_profile_pic==UserData.user!!.pictureUrl){
                chat_image=chat.value!!.user_two_profile_pic.toString()
            }else{
                chat_image=chat.value!!.user_one_profile_pic.toString()

            }
        }

        Column(Modifier.background(SocialTheme.colors.uiBackground)) {
            TopChatBar(
                title = chat_name,
                image = chat_image,
                chatEvents = onEvent
            )
            ChatMessages(
                modifier.weight(1f),
                onEvent = { event->
                    Log.d("CHATDEBUG","EVENT")

                    when(event){
                        is ChatEvents.Reply->{
                            Log.d("CHATDEBUG","REPLY")
                            replyMessage.value=event.message
                        }
                        else->{ onEvent(event) }
                    }
                },
                data,
                data_new,
                frist_data,
                valueExist = valueExist.value,
                chat_id = chat.value!!.id.toString()
            )
            BottomChatBar(modifier = Modifier,onEvent=onEvent,replyMessage=replyMessage,chat.value!!.id.toString())
        }
    }


    var uri by remember { mutableStateOf<Uri?>(null) }
    val uriReceived by chatViewModel.uriReceived
    chatViewModel.uri.observe(LocalLifecycleOwner.current) { newUri ->
        Log.d("ImageFromGallery", "image passed" + uri.toString())
        uri = newUri
    }
    //handle image loading animatipn
    var showLoading by remember { mutableStateOf(false) }
    val flowimageaddition = chatViewModel?.isImageAddedToStorageAndFirebaseState?.collectAsState()
    flowimageaddition?.value.let {
        when (it) {
            is Response.Success -> {
                showLoading = false

            }
            is Response.Failure -> {}
            is Response.Loading -> {
                showLoading = true
            }
            else -> {}
        }
    }


    isImageAddedToStorage.let { response ->
        Log.d("ImagePicker", response.toString())
        when (response) {
            is Response.Success -> {}
            is Response.Loading -> {}
            is Response.Failure -> {
                Log.d("ImagePicker", "failure")
                Toast.makeText(LocalContext.current, "Failed to send the image", Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {}
        }
    }




}

fun loadMessages(fristData: MutableList<ChatMessage>, data:  MutableList<ChatMessage>, dataNew: MutableList<ChatMessage>,chatViewModel:ChatViewModel,valueExist: MutableState<Boolean>) {
    chatViewModel.firstMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                fristData.clear()
                fristData.addAll(it.data)
                valueExist.value = true
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
            else -> {}
        }
    }
    chatViewModel.messagesState.value.let {
        when (it) {
            is Response.Success -> {
                data.clear()
                data.addAll(it.data)
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
            else -> {}
        }
    }
    chatViewModel.moreMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                dataNew.clear()
                dataNew.addAll(it.data)
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
            else -> {}
        }
    }
}

@Composable
fun loadChat(chatViewModel: ChatViewModel, chat: MutableState<Chat?>) {
    val chatState = chatViewModel.chatCollectionState.collectAsState()
    when (val result = chatState.value) {
        is Response.Loading -> {
            // Display a circular loading indicator
            androidx.compose.material.CircularProgressIndicator()
        }
        is Response.Success -> {
            chat.value=result.data

        }
        is Response.Failure -> {


        }
    }
}

@Composable
fun ChatMessages(
    modifier:Modifier,
    onEvent: (ChatEvents) -> Unit,
    data: MutableList<ChatMessage>,
    new_data: MutableList<ChatMessage>,
    first_data: MutableList<ChatMessage>,
    valueExist: Boolean,
    chat_id: String
) {

    val lazyListState = rememberLazyListState()

    // Trigger the "Get more messages" event when scrolled to the bottom
    LaunchedEffect(lazyListState) {
        val totalItems = data.size + new_data.size + first_data.size
        val visibleItems = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        if (visibleItems >= totalItems - 1 && valueExist) {
            Log.d("CHATDEBUG", "GET MORE MESSAGES CALLED")
            onEvent(ChatEvents.GetMoreMessages(chat_id = chat_id))
        }
    }
        var lastMessageSenderID: String? = null
        LazyColumn(modifier,
            reverseLayout = true,
            state = lazyListState
        ) {
            items(data) { message ->
                val shouldGroup = lastMessageSenderID == message.sender_id || lastMessageSenderID==null
                ChatBox(
                    message,
                    onLongPress = {
                    },
                    highlite_message = false,
                    displayPicture = {},
                    highlightMessage = {  },
                    openDialog = {
                    },
                    onEvent = onEvent,
                    shouldGroup = shouldGroup,
                )

                lastMessageSenderID = message.sender_id
            }
            items(first_data) { message ->
                val shouldGroup =  lastMessageSenderID == message.sender_id || lastMessageSenderID==null
                ChatBox(
                    message,
                    onLongPress = {
                    },
                    highlite_message = false,
                    displayPicture = {},
                    highlightMessage = {  },
                    openDialog = {
                    },
                    onEvent =onEvent,
                    shouldGroup = shouldGroup,
                )

                lastMessageSenderID = message.sender_id
            }
            items(new_data) { message ->
                val shouldGroup = lastMessageSenderID == message.sender_id || lastMessageSenderID==null
                ChatBox(
                    message,
                    onLongPress = {
                    },
                    highlite_message = false,
                    displayPicture = {},
                    highlightMessage = {  },
                    openDialog = {
                    },
                    onEvent = onEvent,
                    shouldGroup = shouldGroup,
                )

                lastMessageSenderID = message.sender_id
            }



        }

}


@Composable
fun ChatBox(
    chat: ChatMessage,
    highlite_message: Boolean,
    onLongPress: () -> Unit,
    onEvent: (ChatEvents) -> Unit,
    openDialog: () -> Unit,
    displayPicture: (String) -> Unit,
    highlightMessage: (String) -> Unit,
    shouldGroup: Boolean = false,
) {
    var padding = if (shouldGroup) {
        0.dp
    } else {
        12.dp
    }

    if (chat.sender_id == UserData.user!!.id) {
        Spacer(modifier = Modifier.height(padding))

        ChatItemRight(
            text_type = chat.message_type,
            text = chat.text,
            timeSent = chat.sent_time, highlite_message = highlite_message, onEvent = onEvent,
            chat=chat
        )
    } else {
        Spacer(modifier = Modifier.height(padding))

        ChatItemLeft(
            text_type = chat.message_type,
            text = chat.text,
            timeSent = chat.sent_time, highlite_message = highlite_message, onEvent = onEvent,
            chat=chat
        )

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatButtonItemLeft(icon: Int, label: String, onClick: () -> Unit) {
    val bgColor = SocialTheme.colors.uiBorder.copy(0.2f)
    val color = SocialTheme.colors.textPrimary.copy(0.8f)



    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(
                shape = RoundedCornerShape(
                    topEnd = 8.dp,
                    topStart = 8.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 8.dp
                )
            )
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        Icon(painter = painterResource(id = icon), contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label, style = TextStyle(
                fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 14.sp
            ), color = color
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatItemLeft(
    text_type: String,
    text: String,
    timeSent: String = "12:12",
    highlite_message: Boolean,
    onEvent: (ChatEvents) -> Unit,chat:ChatMessage
) {
    var clicked by remember {
        mutableStateOf(false)
    }
    var selected by remember {
        mutableStateOf(false)
    }

    var elevation = if (selected) {
        4.dp
    } else {
        0.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .padding(horizontal = 12.dp)
    ) {
        AnimatedVisibility(visible = clicked) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                text = timeSent,
                color = SocialTheme.colors.iconPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        /*todo add emoji reaction to text message
        AnimatedVisibility(visible = selected) {
            Row(
                Modifier

                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            }
        }*/


        Box(
            modifier = Modifier
                .clip(
                    shape = RoundedCornerShape(
                        topEnd = 8.dp,
                        topStart = 8.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 8.dp
                    )
                )
                .combinedClickable(
                    onClick = {
                        clicked = !clicked
                    },
                    onLongClick = {
                        selected = !selected
                    },
                )
                .background(color = SocialTheme.colors.uiBackground)

                .border(
                    border = BorderStroke(1.dp, SocialTheme.colors.uiBorder),
                    shape = RoundedCornerShape(
                        topEnd = 8.dp,
                        topStart = 8.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 8.dp
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = SocialTheme.colors.textPrimary
                )
            )
        }
        AnimatedVisibility(visible = selected) {
            Row(
                Modifier

                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChatSettingItem(
                    label = "Reply",
                    icon = R.drawable.ic_reply,
                    onClick = { onEvent(ChatEvents.Reply(chat)) })
                ChatSettingItem(
                    label = "Copy",
                    icon = R.drawable.ic_copy,
                    onClick = { onEvent(ChatEvents.Copy(chat.text)) })
                ChatSettingItem(
                    label = "Share",
                    icon = R.drawable.ic_share,
                    onClick = { onEvent(ChatEvents.Share) })
                ChatSettingItem(
                    label = "Delete",
                    icon = R.drawable.ic_delete,
                    onClick = { onEvent(ChatEvents.Delete(chat.id)) })
                ChatSettingItem(
                    label = "Report",
                    icon = R.drawable.ic_flag,
                    onClick = { onEvent(ChatEvents.Report) })
            }
        }

    }
}

@Composable
fun ChatSettingItem(label: String, icon: Int, onClick: () -> Unit) {
    val color = SocialTheme.colors.uiBorder.copy(0.2f)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
        .clip(RoundedCornerShape(9.dp))
        .clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color)
                .padding(12.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = SocialTheme.colors.textPrimary.copy(0.8f)
            )

        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label,
            style = TextStyle(
                fontFamily = Lexend,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            ),
            color = SocialTheme.colors.textPrimary.copy(0.8f)
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatItemRight(
    text_type: String,
    text: String,
    timeSent: String = "12:12",
    highlite_message: Boolean,
    onEvent: (ChatEvents) -> Unit,chat:ChatMessage
) {
    var clicked by remember {
        mutableStateOf(false)
    }

    var selected by remember {
        mutableStateOf(false)
    }

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .padding(horizontal = 12.dp)
    ) {
        AnimatedVisibility(visible = clicked) {
            Text(
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ), text = timeSent, color = SocialTheme.colors.iconPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Box(
            modifier = Modifier
                .clip(
                    shape = RoundedCornerShape(
                        topEnd = 8.dp,
                        topStart = 8.dp,
                        bottomStart = 8.dp,
                        bottomEnd = 0.dp
                    )
                )
                .combinedClickable(
                    onClick = {
                        if (highlite_message) {
                            if (text_type.equals("live") || text_type.equals("latLng")) {

                            } else {
                                /*
                                *      openDialog()
                                highlightMessage(chat.text)
                                * */

                            }
                        } else {
                            if (text_type.equals("uri")) {
                                /*
                                    displayPicture(chat.text)
                                * */

                            }
                        }


                        clicked = !clicked
                    },
                    onLongClick = {
                        selected = !selected
                    },
                )

                .background(color = SocialTheme.colors.textPrimary.copy(alpha = 0.8f))

                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = SocialTheme.colors.uiBackground.copy(alpha = 0.9f)
                )
            )
        }
        AnimatedVisibility(visible = selected) {
            Row(
                Modifier

                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChatSettingItem(
                    label = "Reply",
                    icon = R.drawable.ic_reply,
                    onClick = { onEvent(ChatEvents.Reply(chat)) })
                ChatSettingItem(
                    label = "Copy",
                    icon = R.drawable.ic_copy,
                    onClick = { onEvent(ChatEvents.Copy(chat.text)) })
                ChatSettingItem(
                    label = "Share",
                    icon = R.drawable.ic_share,
                    onClick = { onEvent(ChatEvents.Share) })
                ChatSettingItem(
                    label = "Delete",
                    icon = R.drawable.ic_delete,
                    onClick = { onEvent(ChatEvents.Delete(chat.id)) })

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialEditText(modifier: Modifier, focus: Boolean, onFocusChange: (Boolean) -> Unit,textState:TextFieldState
,reply:Boolean) {

    val focusRequester = remember { FocusRequester() }


    var focus by remember {
        mutableStateOf(false)
    }
    var borderColor = if (focus) {
        Color(0xFFF3DB86)
    } else {
        SocialTheme.colors.uiBorder
    }
    Card(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .wrapContentHeight(align = Alignment.Top),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SocialTheme.colors.uiBackground)
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        onFocusChange(it.isFocused)
                    }
                    .wrapContentHeight(align = Alignment.Top)
                    .fillMaxWidth(),
                value = textState.text, onValueChange = { textState.text = it},
                textStyle = TextStyle(
                    fontFamily = Lexend, fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                ),
                singleLine = false,
                maxLines = Int.MAX_VALUE,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { /* Perform action on Done button press */ }
                ),
            )
            if (textState.text.isEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (reply){
                        Icon(painter = painterResource(id = R.drawable.ic_reply_arrow), contentDescription =null, tint = SocialTheme.colors.uiBorder )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            modifier = Modifier
                                .padding(start = 0.dp),
                            text = "Reply",
                            style = TextStyle(
                                fontFamily = Lexend,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Light
                            )
                        )
                    }else{
                        Text(
                            modifier = Modifier
                                .padding(start = 0.dp),
                            text = "Send a message",
                            style = TextStyle(
                                fontFamily = Lexend,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Light
                            )
                        )
                    }

                }

            }
        }
    }

}

@Composable
fun chatButtonsRow(modifier: Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .width(32.dp)
                .height(1.dp)
                .background(color = Color.Transparent)
        )
        eButtonSimple(icon = R.drawable.ic_pindrop_300)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
        eButtonSimple(icon = R.drawable.ic_highlight_300)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
        eButtonSimple(icon = R.drawable.ic_image_300)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
        eButtonSimple(icon = R.drawable.ic_wave_300)

        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )

        Spacer(
            modifier = Modifier
                .width(56.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
    }

}

@Composable
fun BottomChatBar(modifier: Modifier = Modifier,onEvent: (ChatEvents) -> Unit,replyMessage:MutableState<ChatMessage?>,chat_id:String) {
    var focused by remember { mutableStateOf(false) }
    var text by rememberSaveable(stateSaver = MessageStateSaver) {
        mutableStateOf(MessageState())
    }
    Column(modifier.background(color = SocialTheme.colors.uiBackground.copy(0.7f))) {
        chatButtonsRow(modifier = Modifier)
        if (replyMessage.value!=null){
            ReplyMessage(replyMessage.value!!)
        }
        //todo set appropirate color to theme
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp, top = 6.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            SocialEditText(
                modifier = Modifier.weight(1f),
                focus = false,
                onFocusChange = { focusState ->
                    focused = focusState
                }, textState = text,
            reply=replyMessage.value!=null
                )
            Spacer(
                modifier = Modifier
                    .width(12.dp)
            )
            eButtonSimpleBlue(icon = R.drawable.ic_send, onClick = {
                if(text.text.isNotEmpty()){
                    onEvent(ChatEvents.SendMessage(chat_id = chat_id,text.text))
                    text.text=""
                }else{

                }
                }
            )
        }

    }
}

@Composable
fun ReplyMessage(chat:ChatMessage) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(color = SocialTheme.colors.uiBorder.copy(0.3f))
            .padding(vertical = 8.dp, horizontal = 24.dp)) {
        if (chat.sender_id == UserData.user!!.id) {

            ChatItemRight(text_type = chat.message_type, text =chat.text , highlite_message = false, onEvent ={}, chat = chat)

        } else {

            ChatItemLeft(text_type = chat.message_type, text =chat.text , highlite_message = false, onEvent ={}, chat = chat)

        }
    }
}

@Composable
fun TopChatBar(title: String, image: String, chatEvents: (ChatEvents) -> Unit) {
    Column {

        //todo set appropirate color to theme
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { chatEvents(ChatEvents.GoBack) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SocialTheme.colors.textPrimary
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { chatEvents(ChatEvents.OpenChatSettings) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconPrimary
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )

    }
}
