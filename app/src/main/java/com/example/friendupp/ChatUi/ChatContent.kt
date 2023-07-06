package com.example.friendupp.ChatUi

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Home.eButtonSimple
import com.example.friendupp.Home.eButtonSimpleBlue
import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.Navigation.getCurrentUTCTime
import com.example.friendupp.Profile.ProfileDisplayEvents
import com.example.friendupp.R
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.ChatMessage
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.LatLng
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


sealed class ChatEvents {
    object GoBack : ChatEvents()
    object GoToProfile : ChatEvents()
    class SendImage(message: Uri) : ChatEvents() {
        val message = message
    }

    object CloseDialog : ChatEvents()
    object ShareLocation : ChatEvents()
    object OpenGallery : ChatEvents()
    class GetMoreMessages(val chat_id: String) : ChatEvents()
    class SendMessage(val chat_id: String, val message: String) : ChatEvents()
    class SendReply(val chat_id: String, val message: String, var replyTo: String) : ChatEvents()
    object OpenChatSettings : ChatEvents()
    class Reply(val message: ChatMessage) : ChatEvents()
    object Report : ChatEvents()
    class Delete(val id: String) : ChatEvents()
    class Copy(val text: String) : ChatEvents()
    object CreateNonExistingChatCollection : ChatEvents()

    object Share : ChatEvents()
}

fun createLatLngFromString(coordinates: String): LatLng? {
    val latLngPattern = Regex("(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
    val matchResult = latLngPattern.find(coordinates)

    return if (matchResult != null && matchResult.groupValues.size == 3) {
        val lat = matchResult.groupValues[1].toDouble()
        val lng = matchResult.groupValues[2].toDouble()
        LatLng(lat, lng)
    } else {
        null
    }
}
fun getChatNameAndImage(chat: Chat): Pair<String, String> {
    var chatName = chat.name.toString()
    var chatImage = chat.imageUrl.toString()

    if (chat.type.equals("duo")) {
        if (chat.user_one_username == UserData.user!!.username) {
            chatName = chat.user_two_username.toString()
            chatImage =chat.user_two_profile_pic.toString()

        } else {
            chatName = chat.user_one_username.toString()
            chatImage = chat.user_one_profile_pic.toString()

        }
    }


    return Pair(chatName, chatImage)
}
@Composable
fun ChatContent(
    modifier: Modifier,
    onEvent: (ChatEvents) -> Unit,
    chatViewModel: ChatViewModel,
    displayLocation: (LatLng) -> Unit,
    higlightDialog: (String) -> Unit,
    chat:Chat,
    data: MutableList<ChatMessage>,
    new_data: MutableList<ChatMessage>,
    first_data: MutableList<ChatMessage>,
    valueExist: Boolean,
    displayImage:(String)->Unit

    ) {
    DisposableEffect(Unit) {
        onDispose {
            chatViewModel.resetChat()
            chatViewModel.resetLoadedMessages()
        }
    }
    var highlight_dialog by remember { mutableStateOf(false) }

    //HIGHLIGHT
    var highlite_message by remember { mutableStateOf(false) }
    var highlited_message_text by remember { mutableStateOf("") }
    BackHandler(true) {
        onEvent(ChatEvents.GoBack)
    }
    //handle image loading animatipn
    var showLoading by remember { mutableStateOf(false) }
    val flowimageaddition = chatViewModel?.isImageAddedToStorageAndFirebaseState?.collectAsState()

    val replyMessage = remember { mutableStateOf<ChatMessage?>(null) }
    val permission_flow = chatViewModel.granted_permission.collectAsState()
    val location_flow = chatViewModel.location.collectAsState()
    val isImageAddedToStorage by chatViewModel.isImageAddedToStorageFlow.collectAsState()


        val (chat_name, chat_image) =  getChatNameAndImage(chat)

        Box(Modifier.fillMaxSize()) {

            if (highlight_dialog) {
                HighLightDialog(modifier = Modifier.align(Alignment.TopCenter), onEvent = { it ->
                    when (it) {
                        is ChatEvents.CloseDialog -> {
                            chat.highlited_message = null
                            highlight_dialog = false
                        }
                        else -> {}
                    }
                }, highlitedMessage = chat.highlited_message!!)
            }
            Column(Modifier.background(SocialTheme.colors.uiBackground)) {
                TopChatBar(
                    title = chat_name,
                    image = chat_image,
                    chatEvents = onEvent
                )
                ChatMessages(
                    modifier.weight(1f),
                    onEvent = { event ->
                        Log.d("CHATDEBUG", "EVENT")

                        when (event) {
                            is ChatEvents.Reply -> {
                                Log.d("CHATDEBUG", "REPLY")
                                replyMessage.value = event.message
                            }
                            else -> {
                                onEvent(event)
                            }
                        }
                    },
                    data,
                    new_data,
                    first_data,
                    valueExist = valueExist,
                    chat_id = chat.id.toString(),
                    highlightMessage = { highlited_message_text = it },
                    highlight_message = highlite_message, displayLocation = displayLocation,
                    higlightDialog = {
                        higlightDialog(it)
                        highlite_message = false
                    },
                    displayImage = displayImage
                )
                LoadingImage(showLoading = showLoading)
                BottomChatBar(modifier = Modifier,
                    onEvent = onEvent,
                    replyMessage = replyMessage,
                    chat.id.toString(),
                    shareLocation = {
                        onEvent(ChatEvents.ShareLocation)
                    },
                    highlightMessage = {
                        highlite_message = !highlite_message
                    },
                    addImage = { onEvent(ChatEvents.OpenGallery) },
                    liveActivity = {}, highlite_message = highlite_message
                )
            }

        }


    DisposableEffect(Unit) {
        onDispose { showLoading = false }
    }

    flowimageaddition?.value.let {
        when (it) {
            is Response.Success -> {
                if (it.data.equals("successfully added image"))
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

@Composable
fun LoadingImage(showLoading: Boolean) {
    val iconTint = SocialTheme.colors.iconPrimary
    val textColor = SocialTheme.colors.textPrimary
    val backColor = SocialTheme.colors.uiBorder
    if (showLoading) {
        Box(
            modifier = Modifier
                .background(backColor.copy(0.4f))
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_loading),
                    contentDescription = null,
                    tint = iconTint
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Hol up...",
                    color = textColor.copy(0.8f),
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Lexend,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@Composable
fun loadMessages(
    fristData: MutableList<ChatMessage>,
    data: MutableList<ChatMessage>,
    dataNew: MutableList<ChatMessage>,
    chatViewModel: ChatViewModel,
    valueExist: MutableState<Boolean>,
    chatID: String,
) {
    val activitiesFetched = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = activitiesFetched.value) {
        if (!activitiesFetched.value) {

            Log.d("CHATDEBUG", "BIGGGGG CALLLL")
            chatViewModel.getFirstMessages(chatID, getCurrentUTCTime())
            chatViewModel.getMessages(chatID, getCurrentUTCTime())
            activitiesFetched.value = true
        }
    }


    /*Here clean up the loaded messages , otherwise when loading chat again loaded messages will double*/
    DisposableEffect(Unit) {
        onDispose {
            Log.d("CHATDEBUGed", "DISPOSE")
            chatViewModel.resetLoadedMessages()
        }
    }

    chatViewModel.firstMessagesState.value.let {
        when (it) {
            is Response.Success -> {
                fristData.clear()
                fristData.addAll(it.data)
                Log.d("CHATMESSAGEDDEUG", "First")
                Log.d("CHATMESSAGEDDEUG", it.data.toString())
                valueExist.value = true
                chatViewModel.resetFirstMessages()
            }
            is Response.Loading -> {

                CircularProgressIndicator(color = SocialTheme.colors.textPrimary)
                fristData.clear()
            }
            is Response.Failure -> {
                fristData.clear()
            }
            else -> {}
        }
    }
    chatViewModel.messagesState.value.let {
        when (it) {
            is Response.Success -> {
                dataNew.clear()
                it.data.forEach {
                    if (it.collectionId == chatID) {
                        dataNew.add(it)
                    }
                }
                Log.d("CHATMESSAGEDDEUG", "new")
                Log.d("CHATMESSAGEDDEUG", it.data.toString())
                chatViewModel.resetNewMessages()

            }
            is Response.Loading -> {
                data.clear()
            }
            is Response.Failure -> {
                data.clear()
            }
            else -> {}
        }
    }
    chatViewModel.moreMessagesState.value.let {
        when (it) {
            is Response.Success -> {

                Log.d("CHATMESSAGEDDEUG", "more")
                Log.d("CHATMESSAGEDDEUG", it.data.toString())
                data.clear()
                data.addAll(chatViewModel.loadedMessages)
                chatViewModel.resetMoreMessages()

            }
            is Response.Loading -> {
            }
            is Response.Failure -> {
            }
            else -> {}
        }
    }
}

@Composable
fun loadChat(
    modifier: Modifier,
    chatViewModel: ChatViewModel,
    chat: MutableState<Chat?>,
    chatNonExistent: () -> Unit,
) {

    val chatState = chatViewModel.chatCollectionState.collectAsState()

    when (val result = chatState.value) {
        is Response.Loading -> {
            // Display a circular loading indicator
            androidx.compose.material.CircularProgressIndicator(
                modifier = modifier,
                color = SocialTheme.colors.textPrimary
            )
        }
        is Response.Success -> {
            chat.value = result.data

        }
        is Response.Failure -> {
            Log.d("CHATREPOSITYIMPLCHATCOLLECT", "FAILURE")
            Toast.makeText(
                LocalContext.current,
                "Can't load in chat. Please try again later",
                Toast.LENGTH_SHORT
            ).show()
            if (result.e.message.equals("document_null")) {
                chatNonExistent()
            }

        }
        else -> {}
    }
}

@Composable
fun ChatMessages(
    modifier: Modifier,
    onEvent: (ChatEvents) -> Unit,
    data: MutableList<ChatMessage>,
    new_data: MutableList<ChatMessage>,
    first_data: MutableList<ChatMessage>,
    valueExist: Boolean,
    chat_id: String,
    highlightMessage: (String) -> Unit,
    highlight_message: Boolean,
    displayLocation: (LatLng) -> Unit,
    higlightDialog: (String) -> Unit,
    displayImage:(String)->Unit

    ) {

    val lazyListState = rememberLazyListState()
    var messagesLoaded by remember {
        mutableStateOf(false)
    }


    var lastMessageSenderID: String? = null
    LazyColumn(
        modifier,
        reverseLayout = true,
        state = lazyListState
    ) {
        items(new_data) { message ->
            val shouldGroup =
                lastMessageSenderID == message.sender_id || lastMessageSenderID == null
            ChatBox(
                message,
                onLongPress = {
                },
                highlite_message = highlight_message,
                displayPicture = {},
                highlightMessage = { },
                openDialog = higlightDialog,
                onEvent = onEvent,
                shouldGroup = shouldGroup,
                displayLocation = displayLocation,
                displayImage=displayImage
            )

            lastMessageSenderID = message.sender_id
        }

        items(first_data) { message ->
            val shouldGroup =
                lastMessageSenderID == message.sender_id || lastMessageSenderID == null
            ChatBox(
                message,
                onLongPress = {
                },
                highlite_message = highlight_message,
                displayPicture = {},
                highlightMessage = highlightMessage,
                openDialog = higlightDialog,
                onEvent = onEvent,
                shouldGroup = shouldGroup,
                displayLocation = displayLocation,
                displayImage = displayImage
            )

            lastMessageSenderID = message.sender_id
        }
        items(data) { message ->
            val shouldGroup =
                lastMessageSenderID == message.sender_id || lastMessageSenderID == null
            ChatBox(
                message,
                onLongPress = {
                },
                highlite_message = highlight_message,
                displayPicture = {},
                highlightMessage = highlightMessage,
                openDialog = higlightDialog,
                onEvent = onEvent,
                shouldGroup = shouldGroup, displayLocation = displayLocation,
                displayImage=displayImage

            )

            lastMessageSenderID = message.sender_id
        }
        item {
            LaunchedEffect(true) {
                if (valueExist) {
                    Log.d("CHATDEBUG", "CAALED FOR OLDER MESSAGES")
                    Log.d("CHATDEBUG", chat_id)
                    onEvent(ChatEvents.GetMoreMessages(chat_id = chat_id))
                }
            }
        }
    }


}

fun convertUTCtoLocal(utcDate: String, outputFormat: String): String {
    val utcDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val utcDateWithoutSecondsFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    utcDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    utcDateWithoutSecondsFormat.timeZone = TimeZone.getTimeZone("UTC")

    val localDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
    localDateFormat.timeZone = TimeZone.getDefault()

    var utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    try {
        utcCalendar.time = utcDateFormat.parse(utcDate)!!
    } catch (e: ParseException) {
        try {
            utcCalendar.time = utcDateWithoutSecondsFormat.parse(utcDate)!!
        } catch (e: ParseException) {
            return "Invalid date format"
        }
    }

    return localDateFormat.format(utcCalendar.time)
}

@Composable
fun ChatBox(
    chat: ChatMessage,
    highlite_message: Boolean,
    onLongPress: () -> Unit,
    onEvent: (ChatEvents) -> Unit,
    openDialog: (String) -> Unit,
    displayPicture: (String) -> Unit,
    highlightMessage: (String) -> Unit,
    shouldGroup: Boolean = false,
    displayLocation: (LatLng) -> Unit,
    displayImage:(String)->Unit

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
            timeSent = convertUTCtoLocal(chat.sent_time, outputFormat = "yyyy-MM-dd HH:mm:ss"),
            onEvent = onEvent,
            chat = chat,
            onClick = {
                if (highlite_message) {
                    if (chat.message_type.equals("live") || chat.message_type.equals("latLng")) {

                    } else {
                        openDialog(chat.text)
                        highlightMessage(chat.text)
                    }
                } else {
                    if (chat.message_type.equals("uri")) {
                        displayPicture(chat.text)

                    }
                }
            },
            displayLocation = displayLocation,
            highlite_message = highlite_message,
            replyTo = chat.replyTo,
            displayImage=displayImage
        )
    } else {
        Spacer(modifier = Modifier.height(padding))

        ChatItemLeft(
            text_type = chat.message_type,
            text = chat.text,
            timeSent = convertUTCtoLocal(chat.sent_time, outputFormat = "yyyy-MM-dd HH:mm:ss"),
            onEvent = onEvent,
            chat = chat,
            onClick = {
                if (highlite_message) {
                    if (chat.message_type.equals("live") || chat.message_type.equals("latLng")) {
                    } else {
                        openDialog(chat.text)
                        highlightMessage(chat.text)
                    }
                } else {
                    if (chat.message_type.equals("uri")) {
                        displayPicture(chat.text)
                    }
                }
            },
            displayLocation = displayLocation,
            highlite_message = highlite_message,
            replyTo = chat.replyTo,
            displayImage=displayImage

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


@Composable
fun ChatSettingItem(label: String, icon: Int, onClick: () -> Unit) {
    val color = SocialTheme.colors.uiBorder.copy(0.2f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
    ) {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialEditText(
    modifier: Modifier, focus: Boolean, onFocusChange: (Boolean) -> Unit, textState: TextFieldState,
    reply: Boolean,
) {

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
                value = textState.text, onValueChange = { textState.text = it },
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
                    if (reply) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_reply_arrow),
                            contentDescription = null,
                            tint = SocialTheme.colors.uiBorder
                        )
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
                    } else {
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
fun chatButtonsRow(
    modifier: Modifier,
    shareLocation: () -> Unit,
    highlightMessage: () -> Unit,
    addImage: () -> Unit,
    liveActivity: () -> Unit,
    highlite_message: Boolean = false,
) {


    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .width(32.dp)
                .height(1.dp)
                .background(color = Color.Transparent)
        )
        eButtonSimple(icon = R.drawable.ic_pindrop_300, onClick = shareLocation)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
        eButtonSimple(icon = R.drawable.ic_highlight_300, onClick = {
            highlightMessage()
        }, selected = highlite_message)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
        eButtonSimple(icon = R.drawable.ic_image_300, onClick = addImage)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
        eButtonSimple(icon = R.drawable.ic_wave_300, onClick = liveActivity)

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
fun BottomChatBar(
    modifier: Modifier = Modifier,
    onEvent: (ChatEvents) -> Unit,
    replyMessage: MutableState<ChatMessage?>,
    chat_id: String,
    shareLocation: () -> Unit,
    highlightMessage: () -> Unit,
    addImage: () -> Unit,
    liveActivity: () -> Unit,
    highlite_message: Boolean,
) {
    var focused by remember { mutableStateOf(false) }
    var text by rememberSaveable(stateSaver = MessageStateSaver) {
        mutableStateOf(MessageState())
    }
    Column(modifier.background(color = SocialTheme.colors.uiBackground.copy(0.7f))) {
        chatButtonsRow(modifier = Modifier,
            shareLocation = {
                shareLocation()
            },
            addImage = {
                addImage()
            },
            liveActivity = {
                liveActivity()
            },
            highlightMessage = {
                highlightMessage()

            }, highlite_message = highlite_message
        )
        if (replyMessage.value != null) {
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
                reply = replyMessage.value != null
            )
            Spacer(
                modifier = Modifier
                    .width(12.dp)
            )
            eButtonSimpleBlue(icon = R.drawable.ic_send, onClick = {
                if (text.text.isNotEmpty()) {
                    if (replyMessage.value != null) {
                        onEvent(
                            ChatEvents.SendReply(
                                chat_id = chat_id,
                                text.text,
                                replyTo = replyMessage.value!!.text.toString()
                            )
                        )
                        replyMessage.value = null
                        text.text = ""
                    } else {
                        onEvent(ChatEvents.SendMessage(chat_id = chat_id, text.text))
                        text.text = ""
                    }

                } else {

                }
            }
            )
        }

    }
}

@Composable
fun ReplyMessage(chat: ChatMessage) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(color = SocialTheme.colors.uiBorder.copy(0.3f))
            .padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        if (chat.sender_id == UserData.user!!.id) {
            ChatItemRight(
                text_type = chat.message_type,
                text = chat.text,
                onEvent = {},
                chat = chat,
                onClick = {},
                displayLocation = {},
                highlite_message = false,
                isReply = true,
                replyTo = null
            , displayImage = {})

        } else {

            ChatItemLeft(
                text_type = chat.message_type,
                text = chat.text,
                onEvent = {},
                chat = chat,
                onClick = {}, displayLocation = {}, highlite_message = false, isReply = true,
                replyTo = null
                , displayImage = {})

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
            Text(modifier=Modifier.clickable(onClick = {chatEvents(ChatEvents.GoToProfile)}),
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
