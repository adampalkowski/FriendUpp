package com.example.friendupp.Navigation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.ChatUi.*
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.model.*
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.firebase.firestore.auth.User
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.chatGraph(navController: NavController, chatViewModel:ChatViewModel, currentChat: MutableState<Chat?>, outputDirectory: File,
                              executor: Executor
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
            val currentDateTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDateTime = dateFormat.format(currentDateTime)


            val chatID = backStackEntry.arguments?.getString("chatID")
            LaunchedEffect(chatID) {
                if (chatID != null) {
                    Log.d("CHATDEBUG","GET MESSAGES CALLED ")
                    chatViewModel.getChatCollection(chatID)
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
                            //id and sent_time are set in view model
                            //we have URI
                            //add uri to storage and resize it
                            //get the url and add it to the message
                            chatViewModel.sendImage(chatID!!, ChatMessage(
                                text = event.message.toString(),
                                sender_picture_url = UserData.user?.pictureUrl!!,
                                sent_time = "",
                                sender_id = UserData.user!!.id,
                                message_type = "uri",
                                id = ""
                            ),event.message)

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
                        is ChatEvents.SendMessage->{
                            Log.d("CHATDEBUG","SENDING MESSAGE"+formattedDateTime.toString()+event.message.toString())
                            chatViewModel.addMessage(
                               event.chat_id,
                                ChatMessage(
                                    text = event.message,
                                    sender_picture_url = UserData.user?.pictureUrl!!,
                                    sent_time =formattedDateTime,
                                    sender_id = UserData.user!!.id,
                                    message_type = "text",
                                    id = ""
                                )
                            )
                        }
                        else -> {}
                    }

                },chatViewModel=chatViewModel)

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
                            id = ""
                        ),
                        uri!!
                    )
                    chatViewModel.onUriProcessed()
                }
            }

        }


    }
}


