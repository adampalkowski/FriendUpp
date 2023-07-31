package com.palkowski.friendupp.Drawer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.palkowski.friendupp.Activities.CreatedActivitiesViewModel
import com.palkowski.friendupp.Activities.JoinedActivitiesViewModel
import com.palkowski.friendupp.ActivityPreview.handleActivityEvents
import com.palkowski.friendupp.Home.HomeViewModel
import com.palkowski.friendupp.Navigation.modifier
import com.palkowski.friendupp.Request.RequestViewModel
import com.palkowski.friendupp.Settings.*
import com.palkowski.friendupp.di.ActivityViewModel
import com.palkowski.friendupp.di.UserViewModel
import com.palkowski.friendupp.model.UserData
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.drawerGraph(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel,
    requestViewModel :RequestViewModel,
) {
    navigation(startDestination = "Inbox", route = "DrawerGraph") {

        composable(
            "Drawer/{type}", arguments = listOf(navArgument("type") { type = NavType.StringType }),
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
        ) { backStackEntry ->
            BackHandler(true) {
                navController.navigate("Home")
            }


            when (backStackEntry.arguments?.getString("type")) {
                "Inbox" -> {
                    LanguageScreen(modifier = modifier, onEvent = { event ->
                        when (event) {
                            is LanguageEvents.GoBack -> {
                                navController.navigate("Settings")
                            }
                        }
                    })

                }
                "Trending" -> {
                    TrendingActivitiesScreen(onEvent = { event ->
                        when (event) {
                            is TrendingActivitiesEvents.GoBack -> {
                                navController.popBackStack()
                            }
                        }

                    })
                }
                "Joined" -> {
                    val context = LocalContext.current
                    val joinedActivitiesViewModel:JoinedActivitiesViewModel= hiltViewModel()
                    LaunchedEffect(true ){
                        joinedActivitiesViewModel.getJoinedActivities(UserData.user!!.id)
                    }
                    JoinedActivitiesScreen(
                        modifier = Modifier.safeDrawingPadding(),
                        activitiesEvents = {event->
                            handleActivityEvents(
                                event = event,
                                activityViewModel = activityViewModel,
                                userViewModel = userViewModel,
                                homeViewModel = homeViewModel,
                                navController = navController,
                                context = context,
                                requestViewModel = requestViewModel
                            )
                        },
                        onEvent = { event ->
                            when(event){
                                is JoinedActivitiesScreenEvents.GetMoreJoinedActivities->{
                                    joinedActivitiesViewModel.getMoreJoinedActivities(event.id)
                                }
                            }


                        },
                        joinedActivitiesResponse = joinedActivitiesViewModel.joinedActivitiesResponse.value
                    )
                }
                "Created" -> {
                    val context = LocalContext.current
                    val createdActivitiesViewModel: CreatedActivitiesViewModel = hiltViewModel()
                    LaunchedEffect(true ){
                        createdActivitiesViewModel.fetchJoinedActivities(UserData.user!!.id)
                    }
                    CreatedActivitiesScreen(
                        modifier = Modifier.safeDrawingPadding(),
                        activitiesEvents = { event ->
                            handleActivityEvents(
                                event = event,
                                activityViewModel = activityViewModel,
                                userViewModel = userViewModel,
                                homeViewModel = homeViewModel,
                                navController = navController,
                                context = context,
                                requestViewModel = requestViewModel

                            )

                        },onEvent={event->
                            when(event){
                                is CreatedActivitiesScreenEvents.GoBack->{
                                    navController.popBackStack()
                                }
                                is CreatedActivitiesScreenEvents.GetMoreCreatedActivities->{
                                    createdActivitiesViewModel.fetchMoreJoinedActivities(event.id)
                                }
                            }
                        },
                        createdActivitiesResponse = createdActivitiesViewModel.joinedActivitiesResponse.value
                    )

                }
                "Bookmarked" -> {
                    val call = rememberSaveable {
                        mutableStateOf(true)
                    }
                    val context= LocalContext.current

                    LaunchedEffect(call) {
                        if (call.value) {
                            activityViewModel.getBookmarkedActivities(UserData.user!!.id)
                            call.value = false
                        } else {
                        }
                    }
                    BookmarkedScreen(modifier = Modifier.safeDrawingPadding(), onEvent = { event ->
                        handleActivityEvents(
                            event = event,
                            activityViewModel = activityViewModel,
                            userViewModel = userViewModel,
                            homeViewModel = homeViewModel,
                            navController = navController,
                            context = context,
                                    requestViewModel=requestViewModel)

                    }, activityViewModel)
                }
                "ForYou" -> {
                    NotificationScreen(modifier = modifier, onEvent = { event ->
                        when (event) {
                            is NotificationEvents.GoBack -> {
                                navController.navigate("Settings")
                            }
                        }

                    })
                }
                "FAQ" -> {
                    FAQScreen(onEvent = { event ->
                        when (event) {
                            is FAQEvents.GoBack -> {
                                navController.navigate("Settings")
                            }
                        }

                    })
                }
                "Groups" -> {
                    SupportScreen(onEvent = { event ->
                        when (event) {
                            is SupportEvents.GoBack -> {
                                navController.navigate("Settings")
                            }
                        }

                    })
                }
                "Rate" -> {
                    TermsScreen(onEvent = { event ->
                        when (event) {
                            is TermsEvents.GoBack -> {
                                navController.navigate("Settings")
                            }
                        }

                    })
                }
            }


        }
    }
}


