package com.example.friendupp.Drawer

import android.content.res.Resources
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.Navigation.sendNotification
import com.example.friendupp.Profile.ProfileEvents
import com.example.friendupp.R
import com.example.friendupp.Settings.*
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.UserData
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.drawerGraph(navController: NavController,activityViewModel: ActivityViewModel,homeViewModel:HomeViewModel,userViewModel:UserViewModel) {
    navigation(startDestination = "Inbox", route = "DrawerGraph") {

        composable(
            "Drawer/{type}",    arguments = listOf(navArgument("type") { type = NavType.StringType }),
            enterTransition = {
                when (initialState.destination.route) {
                    "Settings" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Settings" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "Settings" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Settings" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) {backStackEntry ->
            BackHandler(true) {
                navController.navigate("Home")
            }


            when(backStackEntry.arguments?.getString("type")){
                "Inbox"->{
                    LanguageScreen(onEvent = {event->
                        when(event){
                            is LanguageEvents.GoBack->{navController.navigate("Settings")}
                        }
                    })

                }
                "Trending"->{
                    TrendingActivitiesScreen(onEvent={event->
                        when(event){
                            is TrendingActivitiesEvents.GoBack->{navController.popBackStack()}
                        }

                    })
                }
                "Joined"->{
                    JoinedActivitiesScreen(onEvent={event->
                        when(event){
                            is CreatedActivitiesEvents.GoBack->{navController.popBackStack()}
                            is CreatedActivitiesEvents.GoToProfile->{
                                navController.navigate("ProfileDisplay/"+event.id)
                            }
                            is CreatedActivitiesEvents.JoinActivity -> {

                                if(event.activity.participants_ids.size<6){
                                    userViewModel.addActivityToUser(event.activity.id,UserData.user!!)
                                    activityViewModel.likeActivity(
                                        event.activity.id,
                                        UserData.user!!
                                    )
                                    if(event.activity.creator_id!=UserData.user!!.id){
                                        sendNotification(receiver = event.activity.creator_id,
                                            picture = UserData.user!!.pictureUrl, message = UserData.user?.username+" joined your activity",title = Resources.getSystem().getString(
                                                R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE) ,username = "")
                                    }

                                }else{
                                    userViewModel.addActivityToUser(event.activity.id,UserData.user!!)
                                    activityViewModel.likeActivityOnlyId(
                                        event.activity.id,
                                        UserData.user!!
                                    )

                                    if(event.activity.creator_id!=UserData.user!!.id){
                                        sendNotification(receiver = event.activity.creator_id,
                                            picture = UserData.user!!.pictureUrl, message = UserData.user?.username+" joined your activity",  title = Resources.getSystem().getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE), username = "")
                                    }

                                }
                            }
                            is CreatedActivitiesEvents.LeaveActivity -> {
                                if(event.activity.participants_usernames.containsKey(UserData.user!!.id)){
                                    userViewModel.removeActivityFromUser(id=event.activity.id, user_id = UserData.user!!.id)

                                    activityViewModel?.unlikeActivity(
                                        event.activity.id,
                                        UserData.user!!.id
                                    )
                                }else{
                                    userViewModel.removeActivityFromUser(id=event.activity.id, user_id = UserData.user!!.id)

                                    activityViewModel?.unlikeActivityOnlyId(
                                        event.activity.id,
                                        UserData.user!!.id
                                    )
                                }

                            }
                            is CreatedActivitiesEvents.Bookmark -> {
                                activityViewModel.bookMarkActivity(
                                    event.id,
                                    UserData.user!!.id
                                )
                            }
                            is CreatedActivitiesEvents.UnBookmark -> {
                                activityViewModel.unBookMarkActivity(
                                    event.id,
                                    UserData.user!!.id
                                )
                            }
                            is CreatedActivitiesEvents.OpenChat -> {
                                navController.navigate("ChatItem/" + event.id)

                            }

                            is CreatedActivitiesEvents.ExpandActivity -> {
                                Log.d("ACTIVITYDEBUG", "LAUNCH PREIVEW")
                                homeViewModel.setExpandedActivity(event.activityData)
                                navController.navigate("ActivityPreview")
                            }
                            else->{}

                        }

                    },activityViewModel)
                }
                "Created"->{
                    CreatedActivitiesScreen(onEvent={event->
                        when(event){
                            is CreatedActivitiesEvents.GoBack->{navController.popBackStack()}
                            is CreatedActivitiesEvents.GoToProfile->{
                                navController.navigate("ProfileDisplay/"+event.id)
                            }
                            is CreatedActivitiesEvents.Bookmark -> {
                                activityViewModel.bookMarkActivity(
                                    event.id,
                                    UserData.user!!.id
                                )

                            }
                            is CreatedActivitiesEvents.UnBookmark -> {
                                activityViewModel.unBookMarkActivity(
                                    event.id,
                                    UserData.user!!.id
                                )

                            }
                            is CreatedActivitiesEvents.JoinActivity -> {
                                if(event.activity.participants_ids.size<6){
                                    userViewModel.addActivityToUser(event.activity.id,UserData.user!!)
                                    activityViewModel.likeActivity(
                                        event.activity.id,
                                        UserData.user!!
                                    )
                                    if(event.activity.creator_id!=UserData.user!!.id){
                                        sendNotification(receiver = event.activity.creator_id,
                                            picture = UserData.user!!.pictureUrl, message = UserData.user?.username+" joined your activity", title = Resources.getSystem().getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE), username = "")
                                    }

                                }else{
                                    userViewModel.addActivityToUser(event.activity.id,UserData.user!!)
                                    activityViewModel.likeActivityOnlyId(
                                        event.activity.id,
                                        UserData.user!!
                                    )

                                    if(event.activity.creator_id!=UserData.user!!.id){
                                        sendNotification(receiver = event.activity.creator_id,
                                            picture = UserData.user!!.pictureUrl, message = UserData.user?.username+" joined your activity", title = Resources.getSystem().getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE), username = "")
                                    }

                                }

                            }
                            is CreatedActivitiesEvents.OpenChat -> {
                                navController.navigate("ChatItem/" + event.id)

                            }
                            is CreatedActivitiesEvents.LeaveActivity -> {
                                activityViewModel?.unlikeActivity(
                                    event.activity.id,
                                    UserData.user!!.id
                                )
                            }

                            is CreatedActivitiesEvents.ExpandActivity -> {
                                Log.d("ACTIVITYDEBUG", "LAUNCH PREIVEW")
                                homeViewModel.setExpandedActivity(event.activityData)
                                navController.navigate("ActivityPreview")
                            }
                        }

                    },activityViewModel)

                }
                "Bookmarked"->{
                    val call = rememberSaveable{
                        mutableStateOf(true)
                    }
                    LaunchedEffect(call){
                        if(call.value){
                            activityViewModel.getBookmarkedActivities(UserData.user!!.id)
                            call.value=false
                        }else{}
                    }
                    BookmarkedScreen(onEvent={event->
                        when(event){
                            is CreatedActivitiesEvents.GoBack->{navController.popBackStack()}
                            is CreatedActivitiesEvents.GoToProfile->{
                                navController.navigate("ProfileDisplay/"+event.id)
                            }
                            is CreatedActivitiesEvents.Bookmark -> {
                                activityViewModel.bookMarkActivity(
                                    event.id,
                                    UserData.user!!.id
                                )

                            }
                            is CreatedActivitiesEvents.UnBookmark -> {
                                activityViewModel.unBookMarkActivity(
                                    event.id,
                                    UserData.user!!.id
                                )

                            }
                            is CreatedActivitiesEvents.JoinActivity -> {
                                if(event.activity.participants_ids.size<6){
                                    userViewModel.addActivityToUser(event.activity.id,UserData.user!!)
                                    activityViewModel.likeActivity(
                                        event.activity.id,
                                        UserData.user!!
                                    )
                                    if(event.activity.creator_id!=UserData.user!!.id){
                                        sendNotification(receiver = event.activity.creator_id,
                                            picture = UserData.user!!.pictureUrl, message = UserData.user?.username+" joined your activity", title = Resources.getSystem().getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE), username = "")
                                    }

                                }else{
                                    userViewModel.addActivityToUser(event.activity.id,UserData.user!!)
                                    activityViewModel.likeActivityOnlyId(
                                        event.activity.id,
                                        UserData.user!!
                                    )

                                    if(event.activity.creator_id!=UserData.user!!.id){
                                        sendNotification(receiver = event.activity.creator_id,
                                            picture = UserData.user!!.pictureUrl, message = UserData.user?.username+" joined your activity", title = Resources.getSystem().getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE), username = "")
                                    }

                                }
                            }
                            is CreatedActivitiesEvents.OpenChat -> {
                                navController.navigate("ChatItem/" + event.id)

                            }
                            is CreatedActivitiesEvents.LeaveActivity -> {
                                activityViewModel?.unlikeActivity(
                                    event.activity.id,
                                    UserData.user!!.id
                                )
                            }

                            is CreatedActivitiesEvents.ExpandActivity -> {
                                Log.d("ACTIVITYDEBUG", "LAUNCH PREIVEW")
                                homeViewModel.setExpandedActivity(event.activityData)
                                navController.navigate("ActivityPreview")
                            }
                        }

                    },activityViewModel)
                }
                "ForYou"->{
                    NotificationScreen(onEvent = {event->
                        when(event){
                            is NotificationEvents.GoBack->{navController.navigate("Settings")}
                        }

                    })
                }
                "FAQ"->{
                    FAQScreen(onEvent = {event->
                        when(event){
                            is FAQEvents.GoBack->{navController.navigate("Settings")}
                        }

                    })
                }
                "Groups"->{
                    SupportScreen(onEvent = {event->
                        when(event){
                            is SupportEvents.GoBack->{navController.navigate("Settings")}
                        }

                    })
                }
                "Rate"->{
                    TermsScreen(onEvent = {event->
                        when(event){
                            is TermsEvents.GoBack->{navController.navigate("Settings")}
                        }

                    })
                }
            }


        }
}}