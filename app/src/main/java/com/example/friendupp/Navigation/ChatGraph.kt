package com.example.friendupp.Navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.ChatUi.*
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.model.*
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.firebase.firestore.auth.User
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.chatGraph(navController: NavController,chatViewModel:ChatViewModel,currentChat: MutableState<Chat?>) {
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
                    chatViewModel.getMessages(chatID, formattedDateTime)
                    chatViewModel.getFirstMessages(chatID, formattedDateTime)
                }
            }

            ChatContent(
                modifier = Modifier,
                onEvent = { event ->
                    when (event) {
                        is ChatEvents.GoBack -> {
                            navController.popBackStack()
                        }
                        is ChatEvents.OpenChatSettings -> {
                            navController.navigate("ChatSettings")
                        }
                        is ChatEvents.GetMoreMessages -> {
                            chatViewModel.getMoreMessages(event.chat_id)
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




        }


    }
}


