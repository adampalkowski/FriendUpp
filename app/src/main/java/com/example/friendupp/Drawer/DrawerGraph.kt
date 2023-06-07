package com.example.friendupp.Drawer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Settings.*
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.drawerGraph(navController: NavController) {
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
                    RangeScreen(onEvent = {event->
                        when(event){
                            is RangeEvents.GoBack->{navController.navigate("Settings")}
                        }

                    })
                }
                "Joined"->{
                    PasswordScreen(onEvent = {event->
                        when(event){
                            is  PasswordEvents.GoBack->{navController.navigate("Settings")}
                        }

                    })
                }
                "Created"->{
                    EmailScreen(onEvent = {event->
                        when(event){
                            is EmailEvents.GoBack->{navController.navigate("Settings")}
                        }

                    })
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