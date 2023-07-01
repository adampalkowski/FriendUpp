package com.example.friendupp.Navigation

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.ChatUi.*
import com.example.friendupp.Components.DisplayLocationDialog
import com.example.friendupp.Components.FriendUppDialog
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.model.*
import com.example.friendupp.ui.theme.SocialTheme
import com.example.friendupp.util.getTime
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.auth.User
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor


@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
fun NavGraphBuilder.chatGraph(navController: NavController, chatViewModel:ChatViewModel, currentChat: MutableState<Chat?>, outputDirectory: File,
                              executor: Executor,mapViewModel: MapViewModel
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
            val user= UserData.user
            if(user!=null){
                chatViewModel.getChatCollections(user.id)
            }
            ChatCollection(modifier = Modifier.fillMaxSize(),
                chatEvent = {event->
                    when(event){
                        is ChatCollectionEvents.GoToChat->{
                            currentChat.value=event.chat
                            navController.navigate("ChatItem/"+event.chat.id)}
                        is ChatCollectionEvents.GoBack->{
                            navController.navigate("Home")
                        }
                        is ChatCollectionEvents.GoToGroups->{
                            navController.navigate("Groups")
                        }
                        is ChatCollectionEvents.GoToSearch->{navController.navigate("Search")}
                    }

                }
                    ,chatViewModel=chatViewModel)
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
            ChatSettings(
                TYPE.GROUP
                , chatSettingsEvents = {event->
                    when(event){
                        is ChatSettingsEvents.GoBack->{navController.popBackStack() }
                        is ChatSettingsEvents.GoToUserProfile->{navController.navigate("Profile")}
                        else ->{}
                    }
                })
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
                            navController.popBackStack()
                        }
                        is CameraEvent.AcceptPhoto -> {
                            if (photoUri != null) {
                                chatViewModel.onUriReceived(photoUri!!)
                                navController.popBackStack()
                            }else{
                                navController.popBackStack()

                            }
                        }
                        is CameraEvent.DeletePhoto -> {
                            Log.d("CreateGraphActivity", "dElete photo")
                            photoUri=null
                        }
                        else -> {}
                    }
                },
                photoUri = photoUri
            )

        }


        composable(
            "ChatItem/{chatID}",   arguments = listOf(navArgument("chatID") { type = NavType.StringType }),
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
            LaunchedEffect(chatID) {
                if (chatID != null) {
                    Log.d("CHATDEBUG","GET CHAT CALLED "+chatID)
                    chatViewModel.getChatCollection(chatID)
                }
            }

            val locationPermissionState = rememberPermissionState(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            var displayLocationDialog by remember {
                mutableStateOf<LatLng?>(null)
            }
            var sendLocationDialog by remember{ mutableStateOf(false) }
            var highlightDialog by remember { mutableStateOf<String?>(null) }
            //keep current location for send location
            var currentLocation by remember { mutableStateOf<LatLng?>(null) }

            val flow = mapViewModel.currentLocation.collectAsState()
            flow.value.let { latLng ->
                if (latLng != null) {
                    currentLocation = latLng
                }
            }
            ChatContent(
                modifier = Modifier,
                onEvent = { event ->
                    when (event) {
                        is ChatEvents.GoBack -> {
                            navController.popBackStack()
                        }
                        is ChatEvents.SendImage -> {

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
                        is ChatEvents.ShareLocation ->{
                            /*share your location to chat
                        1.check permission
                        2.display confirmation dialog
                        3.send message
                        */
                            if(locationPermissionState.status.isGranted){
                                sendLocationDialog=true
                            }else{
                                sendLocationDialog=false
                                locationPermissionState.launchPermissionRequest()
                            }


                        }
                        is ChatEvents.CreateNonExistingChatCollection->{
                            Log.d("CHATREPOSITYIMPLCHATCOLLECT","Creatae colleciton")
                            when(chatViewModel.chat_type.value) {
                                "duo" -> {
                                    Log.d("CHATREPOSITYIMPLCHATCOLLECT","duo")

                                }
                                "activity" -> {

                                }
                                "group" -> {

                                }
                                else -> {

                                }
                           }
                        }
                        is ChatEvents.SendMessage->{
                            chatViewModel.addMessage(
                               event.chat_id,
                                ChatMessage(
                                    text = event.message,
                                    sender_picture_url = UserData.user?.pictureUrl!!,
                                    sent_time =   getCurrentUTCTime()
                                ,
                                    sender_id = UserData.user!!.id,
                                    message_type = "text",
                                    id = "",
                                    collectionId = event.chat_id,
                                    replyTo = null
                                )
                            )
                        }
                        is ChatEvents.SendReply->{
                            chatViewModel.addMessage(
                                event.chat_id,
                                ChatMessage(
                                    text = event.message,
                                    sender_picture_url = UserData.user?.pictureUrl!!,
                                    sent_time =   getCurrentUTCTime()
                                    ,
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

                },chatViewModel=chatViewModel, displayLocation = {displayLocationDialog=it}, higlightDialog = {messageToHighlight->
                    highlightDialog=messageToHighlight

                })

            if(highlightDialog!=null){
                val trunctuatedMessage =highlightDialog
                if(trunctuatedMessage!!.length>30){
                    trunctuatedMessage.take(30)+"..."
                }
                FriendUppDialog(
                    label="Make sure everybody sees the message("+trunctuatedMessage+") by highlighting it.",
                    confirmLabel = "Highlight",
                    icon = com.example.friendupp.R.drawable.ic_highlight_300,
                    onCancel = {     highlightDialog=null},
                    onConfirm = {
                        chatViewModel.addHighLight(chatID!!, highlightDialog.toString())
                        highlightDialog=null

                    })
            }
            if(displayLocationDialog!=null){
                DisplayLocationDialog(latLng = displayLocationDialog!!, onCancel = {
                    displayLocationDialog=null
                })
            }
            if(sendLocationDialog){

                FriendUppDialog(
                    label="Your current location will be shared to chat",
                    confirmLabel = "Share current location",
                    icon = com.example.friendupp.R.drawable.ic_pindrop_300,
                    onCancel = {sendLocationDialog=false},
                    onConfirm = {
                        if(currentLocation!=null){
                            chatViewModel.addMessage(
                                chatID.toString(),
                                ChatMessage(
                                    text = currentLocation?.latitude.toString()+","+ currentLocation?.longitude.toString(),
                                    sender_picture_url = UserData.user?.pictureUrl!!,
                                    sent_time ="",
                                    sender_id = UserData.user!!.id,
                                    message_type = "latLng",
                                    id = "",
                                    collectionId = chatID.toString(),
                                    replyTo = null
                                ))
                        }
                        sendLocationDialog=false

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



fun getCurrentUTCTime(): String  {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val currentDateTime = Calendar.getInstance().time
    return dateFormat.format(currentDateTime)
}