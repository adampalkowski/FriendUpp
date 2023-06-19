package com.example.friendupp.Navigation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.friendupp.ActivityUi.ActivityPreview
import com.example.friendupp.ActivityUi.ActivityPreviewEvents
import com.example.friendupp.FriendPicker.FriendPickerScreen
import com.example.friendupp.Home.HomeEvents
import com.example.friendupp.Home.HomeScreen
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.MapEvent
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
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainGraph(
    navController: NavController,
    openDrawer: () -> Unit,
    activityViewModel: ActivityViewModel,
    userViewModel: UserViewModel,chatViewModel:ChatViewModel,
    homeViewModel:HomeViewModel,
    mapViewModel:MapViewModel

) {
    navigation(startDestination = "Home", route = "Main") {



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
            homeViewModel.deep_link.value.let {deep_link->
                when(deep_link?.pathSegments?.get(0)){
                    "Activity"->{
                        val activity_id = deep_link.pathSegments?.get(1).toString()
                        activityViewModel.getActivity(activity_id)
                        Log.d("MAINGRAPHACTIVITY","Activity")


                        homeViewModel.resetDeepLink()
                    }
                    "User"->{
                        homeViewModel.resetDeepLink()
                        navController.navigate("ProfileDisplay/"+ deep_link.pathSegments?.get(1).toString())

                    }
                }
            }

            activityViewModel.activityState.value.let {
                when(it){
                    is Response.Success->{
                        Log.d("MAINGRAPHACTIVITY","ActivityPreviewD")
                        homeViewModel.setExpandedActivity(it.data)
                        activityViewModel.resetActivityState()
                        navController.navigate("ActivityPreview")
                    }
                    is Response.Failure->{
                        Toast.makeText(LocalContext.current,"Couldn't display activity",Toast.LENGTH_SHORT).show()

                    }
                    is Response.Loading->{
                    }
                }
            }

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
            }, activityViewModel = activityViewModel,mapViewModel=mapViewModel)


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
            val localClipboardManager= LocalClipboardManager.current
            val context= LocalContext.current



            ActivityPreview(onEvent = { event ->
                when (event) {
                    is ActivityPreviewEvents.GoBack -> {
                        navController.popBackStack()
                    }
                    is ActivityPreviewEvents.ShareActivityLink->{
                        //CREATE A DYNAMINC LINK TO DOMAIN
                        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                            link = Uri.parse("https://link.friendup.app/" + "Activity" + "/" + event.link)
                            domainUriPrefix = "https://link.friendup.app/"
                            // Open links with this app on Android
                            androidParameters { }
                        }
                        val dynamicLinkUri = dynamicLink.uri
                        //COPY LINK AND MAKE A TOAST
                        localClipboardManager.setText(AnnotatedString(dynamicLinkUri.toString()))
                        Toast.makeText(context, "Copied activity link to clipboard", Toast.LENGTH_LONG).show()
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

            MapScreen(mapViewModel,activityViewModel=activityViewModel, onEvent = {
                event->
                when(event){
                    is MapEvent.PreviewActivity->{
                        Log.d("ACTIVITYDEBUG","LAUNCH PREIVEW")
                        homeViewModel.setExpandedActivity(event.activity)
                        navController.navigate("ActivityPreview")
                    }
                    else->{}
                }
            })
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

