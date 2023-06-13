package com.example.friendupp.Navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.friendupp.ActivityUi.ActivityPreview
import com.example.friendupp.ActivityUi.ActivityPreviewEvents
import com.example.friendupp.FriendPicker.FriendPickerScreen
import com.example.friendupp.Home.HomeEvents
import com.example.friendupp.Home.HomeScreen
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.MapScreen
import com.example.friendupp.Search.SearchEvents
import com.example.friendupp.Search.SearchScreen
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainGraph(
    navController: NavController,
    openDrawer: () -> Unit,
    activityViewModel: ActivityViewModel,
    userViewModel: UserViewModel,chatViewModel:ChatViewModel,
    homeViewModel:HomeViewModel

) {
    navigation(startDestination = "Home", route = "Main") {

        activityViewModel.getActivitiesForUser(UserData.user!!.id)

        composable(
            "Home",
            enterTransition = {
                when (initialState.destination.route) {
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
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
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
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
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
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
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
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


            HomeScreen(modifier = Modifier, onEvent = { event ->
                when (event) {
                    is HomeEvents.OpenDrawer -> {
                        openDrawer()
                    }
                    is HomeEvents.CreateLive -> {
                        navController.navigate("CreateLive")
                    }
                    is HomeEvents.JoinActivity -> {
                        activityViewModel.likeActivity(
                            event.id,
                            UserData.user!!
                        )

                    }
                    is HomeEvents.OpenChat -> {
                        navController.navigate("ChatItem/"+event.id)

                    }
                    is HomeEvents.LeaveActivity -> {
                        activityViewModel?.unlikeActivity(
                            event.id,
                            UserData.user!!.id
                        )
                    }
                    is HomeEvents.ExpandActivity -> {
                        Log.d("ACTIVITYDEBUG","LAUNCH PREIVEW")
                        homeViewModel.setExpandedActivity(event.activityData)
                        navController.navigate("ActivityPreview")
                    }
                }
            }, activityViewModel = activityViewModel)

        }
        composable(
            "ActivityPreview",
            enterTransition = {
                when (initialState.destination.route) {
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
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
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
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
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
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
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
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

            ActivityPreview(onEvent = { event ->
                when (event) {
                    is ActivityPreviewEvents.GoBack -> {
                        navController.navigate("Home")
                    }
                }
            }, homeViewModel = homeViewModel)
        }



        composable(
            "Map",
            enterTransition = {
                when (initialState.destination.route) {
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
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
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
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
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
                    "Chat" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
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
            val context = LocalContext.current

            val mapViewModel = remember { MapViewModel(context) }
            DisposableEffect(Unit) {
                mapViewModel.checkLocationPermission(
                    permissionDenied = {
                        Toast.makeText(context, "denied", Toast.LENGTH_SHORT).show()
                    },
                    permissionGranted = {
                        Toast.makeText(context, "grant", Toast.LENGTH_SHORT).show()

                        mapViewModel.startLocationUpdates()
                    }
                )

                onDispose {
                    Toast.makeText(context, "dispose", Toast.LENGTH_SHORT).show()
                    mapViewModel.stopLocationUpdates()
                }
            }


            MapScreen(mapViewModel)
        }


        composable(
            "Search",
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
            //flow for user search
            val userFlow = userViewModel.userState.collectAsState()

            //RESET USER VALUE
            userViewModel.resetUserValue()


            SearchScreen(onEvent = { event ->
                when (event) {
                    is SearchEvents.GoBack -> {
                        navController.popBackStack()
                    }
                    is SearchEvents.SearchForUser -> {
                        userViewModel.getUserByUsername(event.username)
                    }
                    is SearchEvents.OnInviteAccepted -> {
                        val uuid: UUID = UUID.randomUUID()
                        val id:String = uuid.toString()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val current = LocalDateTime.now().format(formatter)
                        userViewModel.acceptInvite(UserData.user!!,event.user , Chat(current,
                            owner_id =event.user.id,
                            id =id,
                            name =null,
                            imageUrl =null,
                            recent_message =null,
                            recent_message_time =current,
                            type ="duo",
                            members = arrayListOf(UserData.user!!.id,event.user.id),
                            user_one_username =UserData.user!!.username,
                            user_two_username =event.user.username,
                            user_one_profile_pic = UserData.user!!.pictureUrl,
                            user_two_profile_pic = event.user.pictureUrl,
                            highlited_message = "",
                            description="",
                             numberOfUsers=2,
                            numberOfActivities=0,

                        )
                        )

                    }
                }
            },userViewModel=userViewModel)

            /*
            CHECK IF USER EXISTS in search, if succes navigate to profile with user
            * */
            userFlow.value.let {
                when (it) {
                    is Response.Success -> {
                        if(it.data!=null){
                            Log.d("SEARCHSCREENDEBUG","search cseren scuesss")

                            navController.navigate("ProfileDisplay/"+it.data.id.toString())
                        }

                    }
                    is Response.Failure -> {
                        Toast.makeText(
                            LocalContext.current,
                            "Failed to find user with given username", Toast.LENGTH_LONG
                        ).show()

                    }
                    else -> {}

                }
            }
        }


    }
}

