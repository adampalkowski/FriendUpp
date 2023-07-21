package com.example.friendupp.Navigation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.Create.Option
import com.example.friendupp.FriendPicker.FriendPickerScreen
import com.example.friendupp.Groups.*
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Response
import com.example.friendupp.model.User
import com.example.friendupp.model.UserData
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executor

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.groupGraph(
    navController: NavController,
    chatViewModel: ChatViewModel,
    groupState: GroupState,
    outputDirectory: File,
    executor: Executor,
    userViewModel:UserViewModel,activityViewModel:ActivityViewModel


) {
    navigation(startDestination = "Groups", route = "GroupGraph") {


        composable(
            "FriendPickerGroup",
            enterTransition = {
                when (initialState.destination.route) {
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Create" -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Create" -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Create" -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
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
                    "Create" -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )

                    else -> null
                }
            }
        ) {

            val selectedUsers = remember { mutableStateListOf<String>() }
            val context = LocalContext.current
            Log.d("CHATDEBUG", "GETFRIENDSCALLED")

            FriendPickerScreen(
                modifier = Modifier.safeDrawingPadding(),
                userViewModel = userViewModel,
                goBack = { navController.popBackStack() },
                chatViewModel=chatViewModel,
                selectedUsers = selectedUsers,
                onUserSelected = { selectedUsers.add(it) },
                onUserDeselected = { selectedUsers.remove(it) },
                createActivity = {
                    val user = UserData.user
                    if (user != null) {
                        //Add current user to invited list
                        selectedUsers.add(user.id)
                        val uuid: UUID = UUID.randomUUID()
                        val id: String = uuid.toString()
                        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")
                        val date = LocalDateTime.now().format(formatterDate)

                        createGroupAlone(
                            create_date=date,
                            owner_id=UserData.user!!.id,
                            id=id,
                            name=groupState.groupName.text,
                            description=groupState.descriptionState.text,
                            context=context,
                            chatViewModel = chatViewModel,
                            image = groupState.imageUrl ?: UserData.user!!.pictureUrl.toString(),
                            invited_users = selectedUsers.toList(),
                            public = groupState.selectedOptionState.option==Option.PUBLIC
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to read current user, please re-login",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    navController.navigate("Home")
                },

                onAllFriends = {
                    if(it){
                        UserData.user!!.friends_ids_list.forEach{id->
                            if(!UserData.user!!.blocked_ids.contains(id)){
                                selectedUsers.add(id)
                            }
                        }
                    } else{
                        UserData.user!!.friends_ids_list.forEach{id->
                            if(!UserData.user!!.blocked_ids.contains(id)){
                                selectedUsers.remove(id)
                            }
                        }
                    }
                })
        }

        composable("Groups") {
            GroupsScreen(modifier=Modifier.safeDrawingPadding(),onEvent = { event ->
                when (event) {
                    is GroupsEvents.CreateGroup -> {
                        navController.navigate("GroupsCreate")
                    }
                    is GroupsEvents.GoBack -> {
                        navController.popBackStack()
                    }
                    is GroupsEvents.GoToGroupDisplay -> {
                        navController.navigate("GroupDisplay/" + event.groupId)
                    }
                    else -> {}
                }
            }, chatViewModel = chatViewModel)
        }
        composable("GroupCamera",
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
                mutableStateOf<Uri?>(
                    if (groupState.imageUrl.isNotEmpty()) {
                        groupState.imageUrl.toUri()
                    } else {
                        null
                    }
                )
            }

            val context = LocalContext.current
            CameraView(modifier=modifier,
                outputDirectory = outputDirectory,
                executor = executor,
                onImageCaptured = { uri ->
                    photoUri = uri
                },
                onError = {},
                onEvent = { event ->
                    when (event) {
                        is CameraEvent.GoBack -> {
                            if(photoUri!=null){
                                photoUri!!.toFile().delete()
                            }

                            navController.navigate("GroupsCreate")
                        }
                        is CameraEvent.AcceptPhoto -> {
                            if (photoUri != null) {
                                val photo: String = photoUri.toString()
                                groupState.imageUrl = photo
                                Log.d("CreateGraphActivity", "SETTING")
                                Log.d("CreateGraphActivity", photo)
                                navController.navigate("GroupsCreate")
                                /*todo dooo sth with the final uri */
                            } else {

                            }
                        }
                        is CameraEvent.DeletePhoto -> {
                            Log.d("CreateGraphActivity", "dElete photo")
                            groupState.imageUrl = ""
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
                photoUri = photoUri,
            )

        }



        composable("GroupsCreate") {

            GroupsCreateScreen(modifier=Modifier.safeDrawingPadding(),onEvent = { event ->
                when (event) {
                    is GroupCreateEvents.GoBack -> {
                        navController.popBackStack()
                    }
                    is GroupCreateEvents.GoToFriendPicker -> {
                        navController.navigate("FriendPickerGroup")
                    }
                    is GroupCreateEvents.OpenCamera -> {
                        navController.navigate("GroupCamera")

                    }

                }
            }, groupState = groupState)
        }

        composable(
            "GroupDisplay/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.StringType }),
        ) { backStackEntry ->

            val userViewModel :UserViewModel= hiltViewModel()
            val groupId = backStackEntry.arguments?.getString("groupId")
            val activityViewModel:ActivityViewModel= hiltViewModel()


            if (groupId != null) {
                LaunchedEffect(key1 = groupId) {
                    Log.d("SEARCHSCREENDEBUG","get user")
                    chatViewModel.getChatCollection(groupId)
                }
            }
            val localClipboardManager =  LocalClipboardManager.current
            val context = LocalContext.current
            val chatFlow = chatViewModel.chatCollectionState.collectAsState()
            val chat = remember{ mutableStateOf<Chat?>(null) }
            if(chat.value==null){
                CircularProgressIndicator()
            }else {
                GroupDisplayScreen(modifier = Modifier.safeDrawingPadding(), onEvent = { event ->
                    when (event) {
                        is GroupDisplayEvents.GoBack -> {
                            navController.popBackStack()
                        }
                        is GroupDisplayEvents.AddUsers -> {
                            navController.navigate("FriendPickerAddGroupUsers/"+chat.value!!.id)
                        }
                        is GroupDisplayEvents.ShareGroupLink -> {
                            //CREATE A DYNAMINC LINK TO DOMAIN
                            val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                                link = Uri.parse("https://link.friendup.app/" + "Group" + "/" + event.id)
                                domainUriPrefix = "https://link.friendup.app/"
                                // Open links with this app on Android
                                androidParameters { }
                            }
                            val dynamicLinkUri = dynamicLink.uri
                            //COPY LINK AND MAKE A TOAST
                            localClipboardManager.setText(AnnotatedString(dynamicLinkUri.toString()))
                            Toast.makeText(context, "Copied group link to clipboard", Toast.LENGTH_LONG).show()

                        }
                        else -> {}
                    }

                }, activityViewModel = activityViewModel, group = chat.value!!)
            }



            chatFlow.value.let { response ->
                when (response) {
                    is Response.Success -> {
                        Log.d("SEARCHSCREENDEBUG","saearch sucess")
                        chat.value=response.data
                        chatViewModel.resetChat()
                    }
                    is Response.Failure -> {

                    }
                    is Response.Loading -> {
                        CircularProgressIndicator()
                    }
                    null->{
                    }
                }

            }
        }
    }

    composable(
        "FriendPickerAddGroupUsers/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType }),
        enterTransition = {
            when (initialState.destination.route) {
                "Home" ->
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                "Create" -> slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                "Home" ->
                    slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                "Create" -> slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                "Home" ->
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                "Create" -> slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left,
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
                "Create" -> slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )

                else -> null
            }
        }
    ) {backStackEntry->
        val groupId = backStackEntry.arguments?.getString("groupId")

        val context =LocalContext.current
        val selectedUsers = remember { mutableStateListOf<String>() }
        if(groupId!=null){
            FriendPickerScreen(
                modifier = Modifier.safeDrawingPadding(),
                userViewModel = userViewModel,
                goBack = { navController.popBackStack() },
                chatViewModel=chatViewModel,
                selectedUsers = selectedUsers,
                onUserSelected = { selectedUsers.add(it) },
                onUserDeselected = { selectedUsers.remove(it) },
                createActivity = {
                    chatViewModel.updateChatCollectionMembers(selectedUsers.distinct(),id=groupId)
                    navController.popBackStack()

                },

                onAllFriends = {
                    if(it){
                        UserData.user!!.friends_ids_list.forEach{id->
                            if(!UserData.user!!.blocked_ids.contains(id)){
                                selectedUsers.add(id)
                            }
                        }
                    } else{
                        UserData.user!!.friends_ids_list.forEach{id->
                            if(!UserData.user!!.blocked_ids.contains(id)){
                                selectedUsers.remove(id)
                            }
                        }
                    }
                })
        }else{
            navController.popBackStack()
        }

    }
}

