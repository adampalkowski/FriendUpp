package com.example.friendupp.Drawer

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.Profile.ProfileEvents
import com.example.friendupp.Settings.*
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.UserData
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.drawerGraph(navController: NavController,activityViewModel: ActivityViewModel,homeViewModel:HomeViewModel) {
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
                            is JoinedActivitiesEvents.GoBack->{navController.popBackStack()}
                            is JoinedActivitiesEvents.GoToProfile->{
                                navController.navigate("ProfileDisplay/"+event.id)
                            }
                            is JoinedActivitiesEvents.JoinActivity -> {
                                activityViewModel.likeActivity(
                                    event.id,
                                    UserData.user!!
                                )

                            }
                            is JoinedActivitiesEvents.OpenChat -> {
                                navController.navigate("ChatItem/" + event.id)

                            }
                            is JoinedActivitiesEvents.LeaveActivity -> {
                                activityViewModel?.unlikeActivity(
                                    event.id,
                                    UserData.user!!.id
                                )
                            }
                            is JoinedActivitiesEvents.ExpandActivity -> {
                                Log.d("ACTIVITYDEBUG", "LAUNCH PREIVEW")
                                homeViewModel.setExpandedActivity(event.activityData)
                                navController.navigate("ActivityPreview")
                            }
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
                            is CreatedActivitiesEvents.JoinActivity -> {
                                activityViewModel.likeActivity(
                                    event.id,
                                    UserData.user!!
                                )

                            }
                            is CreatedActivitiesEvents.OpenChat -> {
                                navController.navigate("ChatItem/" + event.id)

                            }
                            is CreatedActivitiesEvents.LeaveActivity -> {
                                activityViewModel?.unlikeActivity(
                                    event.id,
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