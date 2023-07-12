package com.example.friendupp.Navigation

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.ChatUi.*
import com.example.friendupp.Components.DisplayLocationDialog
import com.example.friendupp.Components.FriendUppDialog
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.Notification.*
import com.example.friendupp.R
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.model.*
import com.example.friendupp.ui.theme.SocialTheme
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor


@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
fun NavGraphBuilder.chatGraph(
    navController: NavController,
    chatViewModel: ChatViewModel,
    currentChat: MutableState<Chat?>,
    outputDirectory: File,
    executor: Executor,
    mapViewModel: MapViewModel,
) {
    navigation(startDestination = "Chat", route = "Chats") {
        composable(
            "Chat",
            enterTransition = {
                when (initialState.destination.route) {
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Profile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {

                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Profile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {

                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Profile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {

                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Profile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) {
            val user = UserData.user
            if (user != null) {
                chatViewModel.getChatCollections(user.id)
            }
            ChatCollection(modifier = Modifier.fillMaxSize(),
                chatEvent = { event ->
                    when (event) {
                        is ChatCollectionEvents.GoToChat -> {
                            currentChat.value = event.chat
                            navController.navigate("ChatItem/" + event.chat.id)
                        }
                        is ChatCollectionEvents.GoBack -> {
                            navController.navigate("Home")
                        }
                        is ChatCollectionEvents.GoToGroups -> {
                            navController.navigate("Groups")
                        }
                        is ChatCollectionEvents.GoToSearch -> {
                            navController.navigate("Search")
                        }
                    }

                }, chatViewModel = chatViewModel)
        }

        composable(
            "ChatSettings",
            enterTransition = {
                when (initialState.destination.route) {
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) {

            var chat = remember { mutableStateOf<Chat?>(null) }
            loadChat(modifier = Modifier, chatViewModel = chatViewModel, chat = chat) { }

            var openReportDialog by remember{mutableStateOf(false)}
            var openBlockDialog by remember{mutableStateOf(false)}
            val context = LocalContext.current

            if(chat.value!=null){
                var type by remember {
                    mutableStateOf<TYPE?>(null)
                }
                when(chat.value!!.type){
                    "duo"->{type=TYPE.DUO}
                    "activity"->{type=TYPE.ACTIVITY}
                    "group"->{type=TYPE.GROUP}
                }

                if(type==null){
                    navController.popBackStack()
                }else{
                    ChatSettings(
                        type!!, chatSettingsEvents = { event ->
                            when (event) {
                                is ChatSettingsEvents.GoBack -> {
                                    navController.popBackStack()
                                }
                                is ChatSettingsEvents.GoToUserProfile -> {


                                    when(chat.value!!.type){

                                        "duo"->{
                                            var otherUserID=""
                                            chat.value!!.members.forEach {
                                                if(it!=UserData.user?.id){
                                                    otherUserID=it
                                                }
                                            }
                                            navController.navigate("ProfileDisplay/"+otherUserID)

                                        }
                                        "activity"->{type=TYPE.ACTIVITY}
                                        "group"->{type=TYPE.GROUP}
                                    }

                                }
                                is ChatSettingsEvents.Report -> {
                                    openReportDialog=true

                                }
                                is ChatSettingsEvents.Block -> {
                                    openBlockDialog=true

                                }
                                else -> {}
                            }
                        }, chatViewModel,chat.value!!
                    )
                }

                if(openReportDialog){
                    FriendUppDialog(
                        label = "If the chat contains any violations, inappropriate content, or any other concerns that violate community guidelines, please report it.",
                        icon = R.drawable.ic_flag,
                        onCancel = { openReportDialog=false },
                        onConfirm = {       chatViewModel.reportChat(chat.value!!.id.toString())
                            Toast.makeText(context,"Chat reported",Toast.LENGTH_SHORT).show()
                            openReportDialog=false}, confirmLabel = "Report")
                }
                if(openBlockDialog){
                    FriendUppDialog(
                        label = "Enabling the chat block feature restricts the ability of users to send messages. This action is reversible, allowing for the restoration of message-sending capabilities.",
                        icon = R.drawable.ic_block,
                        onCancel = { openBlockDialog=false },
                        onConfirm = {
                            chatViewModel.blockChat(chat.value!!.id.toString())
                            Toast.makeText(context,"Chat blocked",Toast.LENGTH_SHORT).show()
                            openBlockDialog=false}, confirmLabel = "Block", confirmTextColor = SocialTheme.colors.error)
                }
            }else{
                //Chat shouldn't be null
                navController.popBackStack()
            }

        }

        composable("ChatCamera",
            enterTransition = {
                when (initialState.destination.route) {
                    "Create" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Create" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    else -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(400)
                    )
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "Create" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Create" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    else -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(400)
                    )
                }
            }
        ) { backStackEntry ->
            var photoUri by rememberSaveable {
                mutableStateOf<Uri?>(null)
            }
            val context = LocalContext.current
            CameraView(
                outputDirectory = outputDirectory,
                executor = executor,
                onImageCaptured = { uri ->
                    photoUri = uri
                    /*todo handle the image uri*/
                },
                onError = {},
                onEvent = { event ->
                    when (event) {
                        is CameraEvent.GoBack -> {
                            if(photoUri!=null){
                                photoUri!!.toFile().delete()
                            }

                            navController.popBackStack()
                        }
                        is CameraEvent.AcceptPhoto -> {
                            if (photoUri != null) {
                                chatViewModel.onUriReceived(photoUri!!)
                                navController.popBackStack()
                            } else {
                                navController.popBackStack()

                            }
                        }
                        is CameraEvent.DeletePhoto -> {
                            if(photoUri!=null){
                                photoUri!!.toFile().delete()
                            }

                            Log.d("CreateGraphActivity", "dElete photo")
                            photoUri = null
                        }
                        is CameraEvent.Download -> {
                            Toast.makeText(context,"Image saved in gallery",Toast.LENGTH_SHORT).show()
                            Log.d("CreateGraphActivity", "dElete photo")
                            photoUri = null
                        }

                        else -> {}
                    }
                },
                photoUri = photoUri
            )

        }


        composable(
            "ChatItem/{chatID}",
            arguments = listOf(navArgument("chatID") { type = NavType.StringType }),
            enterTransition = {
                when (initialState.destination.route) {
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->


            val chatID = backStackEntry.arguments?.getString("chatID")

            /*
            1.get chat id
            2.call for chat
            3.store chat in view model
            */

            LaunchedEffect(chatID) {
                if (chatID != null) {
                    Log.d("CHATDEBUG", "GET CHAT CALLED " + chatID)
                    chatViewModel.getChatCollection(chatID)

                }
            }

            val locationPermissionState = rememberPermissionState(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            var displayLocationDialog by remember {
                mutableStateOf<LatLng?>(null)
            }
            var sendLocationDialog by remember { mutableStateOf(false) }
            var highlightDialog by remember { mutableStateOf<String?>(null) }
            //keep current location for send location
            var currentLocation by remember { mutableStateOf<LatLng?>(null) }
            var imageDisplay by remember {
                mutableStateOf<String?>(null)
            }
            val flow = mapViewModel.currentLocation.collectAsState()
            flow.value.let { latLng ->
                if (latLng != null) {
                    currentLocation = latLng
                }
            }
            LaunchedEffect(Unit) {
                Log.d("RESETCHAT", "RESET")

            }

            var chat = rememberSaveable { mutableStateOf<Chat?>(null) }
            loadChat(
                modifier = Modifier,
                chatViewModel,
                chat,
                chatNonExistent = {
                    Log.d("CHATREPOSITYIMPLCHATCOLLECT", "Creatae colleciton")
                    when (chatViewModel.chat_type.value) {
                        "duo" -> {
                            Log.d("CHATREPOSITYIMPLCHATCOLLECT", "duo")

                        }
                        "activity" -> {

                        }
                        "group" -> {

                        }
                        else -> {

                        }
                    }
                })

            val dataSaver = ChatMessageListSaver()
            var data = rememberSaveable(
                saver = dataSaver,
                init = { mutableStateListOf<ChatMessage>() }
            )
            val dataNewSaver = ChatMessageListSaver()
            var data_new = rememberSaveable(
                saver = dataNewSaver,
                init = { mutableStateListOf<ChatMessage>() }
            )
            val dataFirstSaver = ChatMessageListSaver()
            var frist_data = rememberSaveable(
                saver = dataFirstSaver,
                init = { mutableStateListOf<ChatMessage>() }
            )
            val valueExist = rememberSaveable { mutableStateOf(false) }
            var chatFinal = chat.value
            if (chatFinal != null) {
                loadMessages(
                    frist_data,
                    data,
                    data_new,
                    chatViewModel,
                    valueExist = valueExist,
                    chatFinal.id!!
                )

                ChatContent(
                    modifier = Modifier,
                    onEvent = { event ->
                        when (event) {
                            is ChatEvents.GoBack -> {
                                navController.popBackStack()
                            }
                            is ChatEvents.SendImage -> {

                            }
                            is ChatEvents.GoToProfile -> {
                                when (chatViewModel.chat_type.value) {
                                    "duo" -> {
                                        navController.navigate("ProfileDisplay/"+chat.value!!.id)

                                    }
                                    "activity" -> {

                                    }
                                    "group" -> {

                                    }
                                    else -> {

                                    }
                                }
                            }
                            is ChatEvents.OpenChatSettings -> {
                                navController.navigate("ChatSettings")
                            }
                            is ChatEvents.GetMoreMessages -> {
                                chatViewModel.getMoreMessages(event.chat_id)
                            }
                            is ChatEvents.OpenGallery -> {
                                navController.navigate("ChatCamera")
                            }
                            is ChatEvents.ShareLocation -> {
                                /*share your location to chat
                            1.check permission
                            2.display confirmation dialog
                            3.send message
                            */
                                if (locationPermissionState.status.isGranted) {
                                    sendLocationDialog = true
                                } else {
                                    sendLocationDialog = false
                                    locationPermissionState.launchPermissionRequest()
                                }


                            }
                            is ChatEvents.CreateNonExistingChatCollection -> {

                            }
                            is ChatEvents.SendMessage -> {
                                chatViewModel.addMessage(
                                    event.chat_id,
                                    ChatMessage(
                                        text = event.message,
                                        sender_picture_url = UserData.user?.pictureUrl!!,
                                        sent_time = getCurrentUTCTime(),
                                        sender_id = UserData.user!!.id,
                                        message_type = "text",
                                        id = "",
                                        collectionId = event.chat_id,
                                        replyTo = null
                                    )
                                )
                            }
                            is ChatEvents.SendReply -> {
                                chatViewModel.addMessage(
                                    event.chat_id,
                                    ChatMessage(
                                        text = event.message,
                                        sender_picture_url = UserData.user?.pictureUrl!!,
                                        sent_time = getCurrentUTCTime(),
                                        sender_id = UserData.user!!.id,
                                        message_type = "reply",
                                        id = "",
                                        collectionId = event.chat_id,
                                        replyTo = event.replyTo
                                    )
                                )
                            }
                            else -> {}
                        }

                    },
                    chatViewModel = chatViewModel,
                    displayLocation = { displayLocationDialog = it },
                    higlightDialog = { messageToHighlight ->
                        highlightDialog = messageToHighlight

                    },
                    chatFinal!!,
                    data = data,
                    first_data = frist_data,
                    new_data = data_new,
                    valueExist = valueExist.value,
                    displayImage = {image->
                        imageDisplay=image
                    }
                )
            }
            AnimatedVisibility(visible =imageDisplay!=null, enter = scaleIn(), exit = scaleOut() ) {
                Box(modifier = Modifier.fillMaxSize()){
                    BackHandler() {
                        imageDisplay=null
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageDisplay)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_image_300),
                        contentDescription = "image sent",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                shape = RoundedCornerShape(
                                    topEnd = 8.dp,
                                    topStart = 8.dp,
                                    bottomStart = 8.dp,
                                    bottomEnd = 0.dp
                                )
                            )
                    )
                    Box(modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)){
                        Box(modifier = Modifier
                            .clickable(onClick = { imageDisplay = null })
                            .padding(12.dp)){
                            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription =null, tint = Color.White )

                        }
                    }
                }
            }

            if (highlightDialog != null) {
                val trunctuatedMessage = highlightDialog
                if (trunctuatedMessage!!.length > 30) {
                    trunctuatedMessage.take(30) + "..."
                }
                FriendUppDialog(
                    label = "Make sure everybody sees the message(" + trunctuatedMessage + ") by highlighting it.",
                    confirmLabel = "Highlight",
                    icon = com.example.friendupp.R.drawable.ic_highlight_300,
                    onCancel = { highlightDialog = null },
                    onConfirm = {
                        chatViewModel.addHighLight(chatID!!, highlightDialog.toString())
                        highlightDialog = null

                    })
            }
            if (displayLocationDialog != null) {
                DisplayLocationDialog(latLng = displayLocationDialog!!, onCancel = {
                    displayLocationDialog = null
                })
            }
            if (sendLocationDialog) {

                FriendUppDialog(
                    label = "Your current location will be shared to chat",
                    confirmLabel = "Share current location",
                    icon = com.example.friendupp.R.drawable.ic_pindrop_300,
                    onCancel = { sendLocationDialog = false },
                    onConfirm = {
                        if (currentLocation != null) {
                            chatViewModel.addMessage(
                                chatID.toString(),
                                ChatMessage(
                                    text = currentLocation?.latitude.toString() + "," + currentLocation?.longitude.toString(),
                                    sender_picture_url = UserData.user?.pictureUrl!!,
                                    sent_time = "",
                                    sender_id = UserData.user!!.id,
                                    message_type = "latLng",
                                    id = "",
                                    collectionId = chatID.toString(),
                                    replyTo = null
                                )
                            )
                        }
                        sendLocationDialog = false

                    })
            }


            var uri by remember { mutableStateOf<Uri?>(null) }
            val uriFlow = chatViewModel.uri.collectAsState()

            LaunchedEffect(uriFlow.value) {
                val newUri = uriFlow.value
                if (newUri != null) {
                    uri = newUri
                    chatViewModel.sendImage(
                        chatID!!,
                        ChatMessage(
                            text = uri.toString(),
                            sender_picture_url = UserData.user?.pictureUrl!!,
                            sent_time = "",
                            sender_id = UserData.user!!.id,
                            message_type = "uri",
                            id = "",
                            chatID,
                            replyTo = null

                        ),
                        uri!!
                    )
                    chatViewModel.onUriProcessed()

                }
            }

        }


    }
}


fun getCurrentUTCTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val currentDateTime = Calendar.getInstance().time
    return dateFormat.format(currentDateTime)
}
fun convertToUTC(startTime: String): String {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    inputDateFormat.timeZone = TimeZone.getDefault() // Assuming the provided start time is in the local time zone
    val startDate = inputDateFormat.parse(startTime)

    val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    outputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return outputDateFormat.format(startDate)
}
fun sendNotification(receiver: String, username: String, message: String) {
    Log.d(TAG, "notificaiton sented")
    val tokens = FirebaseDatabase.getInstance("https://friendupp-3ecc2-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tokens")
    val query = tokens.orderByKey().equalTo(receiver)
    query.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (dataSnapshot in snapshot.children) {
                val token: Token? = dataSnapshot.getValue(Token::class.java)
                val data = Data(
                    UserData.user!!.id, R.drawable.ic_profile,
                    "$username:$message", "New Message",
                    receiver
                )
                val sender = Sender(data, token?.token.toString())
                Log.d(TAG, sender.toString() + "X")
                Log.d(TAG, data.toString() + "xx")
                val apiService = Client.getClient("https://fcm.googleapis.com/")!!.create(
                    APIService::class.java
                )
                apiService.sendNotification(sender)?.enqueue(object : Callback<MyResponse?> {
                    override fun onResponse(call: Call<MyResponse?>, response: Response<MyResponse?>) {
                        Log.d(TAG, response.toString())
                        if (response.code() == 200) {
                            if (response.body()?.success != 1) {
                                Log.d(TAG, "notification failed1")
                                // FAILED
                            }
                            Log.d(TAG, "GOOD")
                        }
                    }

                    override fun onFailure(call: Call<MyResponse?>, t: Throwable) {
                        Log.d(TAG, "failure notification")
                    }
                }
                )

            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, "notificaiton failed4")
        }
    })
}
