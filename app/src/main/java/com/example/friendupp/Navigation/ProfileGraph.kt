package com.example.friendupp.Navigation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.Categories.Category
import com.example.friendupp.ChatUi.ChatEvents
import com.example.friendupp.Groups.GroupItemEvent

import com.example.friendupp.Profile.*
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Response
import com.example.friendupp.model.User
import com.example.friendupp.model.UserData
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profileGraph(
    navController: NavController, outputDirectory: File,
    executor: Executor,userViewModel:UserViewModel,
    chatViewModel: ChatViewModel
) {
    navigation(startDestination = "FriendList", route = "ProfileGraph") {

        composable("CameraProfile",
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
        ) {
            var photoUri by remember {
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
                            navController.navigate("Home")
                        }
                        is CameraEvent.AcceptPhoto -> {
                            Log.d("CAMERAGRAPHACTIvity", "ACASDASDASD")
                            if (photoUri != null) {
                                val photo: String = photoUri.toString()


                                /*todo dooo sth with the final uri */
                            }
                        }
                        else -> {}
                    }
                },
                photoUri = photoUri
            )

        }
        composable(
            "Profile",
            enterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(350)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) {
            val user = UserData.user
            if (user == null) {

                navController.navigate("Welcome")
            } else {
                ProfileScreen(modifier = Modifier.fillMaxSize(),
                    onEvent = { event ->
                        when (event) {
                            is ProfileEvents.GoBack -> {
                                navController.popBackStack()
                            }
                            is ProfileEvents.GoToEditProfile -> {
                                navController.navigate("EditProfile")
                            }
                            is ProfileEvents.GoToSearch -> {
                                navController.navigate("Search")
                            }
                            is ProfileEvents.GoToFriendList -> {
                                navController.navigate("FriendList")
                            }
                            is ProfileEvents.GoToSettings -> {
                                navController.navigate("Settings")
                            }
                            is ProfileEvents.GetProfileLink -> {}
                            is ProfileEvents.OpenCamera -> {
                                navController.navigate("CameraProfile")
                            }

                        }

                    },
                    onClick = { navController.navigate("EditProfile") }, user = user
                )
            }


        }
        composable(
            "EditProfile",
            enterTransition = {
                when (initialState.destination.route) {
                    "Profile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Profile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "Profile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Profile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) {
            val user = UserData.user
            if(user!=null){
                EditProfile(
                    modifier = Modifier,
                    goBack = { navController.navigate("Profile") },user=user, onEvent = {
                            event->
                        when(event){
                            is EditProfileEvents.GoBack->{navController.popBackStack()}
                            is EditProfileEvents.ConfirmChanges->{}
                        }


                    })
            }else{
                navController.popBackStack()
            }

        }
        composable(
            "FriendList",
            enterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(350)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) {
            userViewModel.getFriends(UserData.user!!.id)


            FriendListScreen(onEvent = { event ->
                when (event) {
                    is FriendListEvents.GoBack -> {
                        navController.navigate("Profile")
                    }
                    is FriendListEvents.GoToAddFriends -> {
                        navController.navigate("Search")
                    }
                    is FriendListEvents.ProfileDisplay -> {
                        navController.navigate("ProfileDisplay/"+event.userId)
                    }
                    else -> {}
                }
            },userViewModel=userViewModel)
        }
        composable(
            "ProfileDisplay/{userID}",
            arguments = listOf(navArgument("userID") { type = NavType.StringType }),
            enterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(350)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            val userViewModel :UserViewModel= hiltViewModel()
            val userID = backStackEntry.arguments?.getString("userID")

            Log.d("SEARCHSCREENDEBUG","USER ID")
            if (userID != null) {
                LaunchedEffect(key1 = userID) {
                    Log.d("SEARCHSCREENDEBUG","get user")
                    userViewModel.getUser(userID)
                }
            }
            val context = LocalContext.current
            val userFlow = userViewModel.userState?.collectAsState()
            val user = remember{ mutableStateOf<User?>(null) }
            if(user.value==null){
                CircularProgressIndicator()
            }else{
                //check if user is me then go to profiel
                if (user.value!!.id==UserData.user!!.id){navController.navigate("Profile")}
                ProfileDisplayScreen(modifier = Modifier.fillMaxSize(),
                    onEvent = { event ->
                        when (event) {
                            is ProfileDisplayEvents.GoBack -> {
                                navController.popBackStack()
                                userViewModel.resetUserValue()
                            }
                            is ProfileDisplayEvents.GoToEditProfile -> {
                                navController.navigate("EditProfile")
                            }
                            is ProfileDisplayEvents.GoToSearch -> {
                                navController.navigate("Search")
                            }
                            is ProfileDisplayEvents.GoToFriendList -> {
                                navController.navigate("FriendList")
                            }
                            is ProfileDisplayEvents.GoToSettings -> {
                                navController.navigate("Settings")
                            }
                            is ProfileDisplayEvents.GetProfileLink -> {}
                            is ProfileDisplayEvents.RemoveFriend -> {

                                userViewModel.removeInvitedIdFromUser(UserData.user!!.id,event.user_id)
                                userViewModel.removeFriendFromBothUsers(UserData.user!!.id,event.user_id)


                                val chat_id =UserData.user!!.friends_ids.get(event.user_id)
                                // REMOVE CHAT BETWEEN USERS ????/
                                if(chat_id!=null){
                                    chatViewModel.deleteChatCollection(chat_id)

                                }

                                Toast.makeText(context,
                                    "Invite to " + event.user_id+ " removed ",Toast.LENGTH_LONG).show()

                            }

                            is ProfileDisplayEvents.InviteUser -> {
                                userViewModel.addInvitedIdToUser(UserData.user!!.id,event.user_id)
                                Toast.makeText(
                                  context,
                                    "User " + event.user_id+ " invited ", Toast.LENGTH_LONG).show()
                            }
                            is ProfileDisplayEvents.GoToChat->{
                                navController.navigate("ChatItem/"+event.chat_id)
                            }

                        }
                    }
                    , user = user.value!!
                )
            }



            userFlow?.value.let { response ->
                when (response) {
                    is Response.Success -> {
                        Log.d("SEARCHSCREENDEBUG","saearch sucess")
                        user.value=response.data
                        userViewModel.resetUserValue()
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
}

