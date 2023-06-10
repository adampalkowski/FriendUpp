package com.example.friendupp.Navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Settings.*
import com.example.friendupp.di.AuthViewModel
import com.example.friendupp.di.UserViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsGraph(navController: NavController,authViewModel:AuthViewModel,userViewModel: UserViewModel) {
    navigation(startDestination = "Settings", route = "SettingsGraph") {
        composable(
            "Settings",
            enterTransition = {
                when (initialState.destination.route) {
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Language" ->
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
                    "Language" ->
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
                    "Language" ->
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
                    "Language" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) {

            SettingsScreen(modifier = Modifier, settingsEvents = {
                    event->
                when(event){
                    is SettingsEvents.GoBack->{navController.navigate("Home")}
                    is SettingsEvents.ChangeLanguage->{navController.navigate("Settings/Language")}
                    is SettingsEvents.ChangeSearchRange->{navController.navigate("Settings/Range")}

                    is SettingsEvents.TermsAndPrivacy->{navController.navigate("Settings/Terms")}
                    is SettingsEvents.Notifications->{navController.navigate("Settings/Notification")}
                    is SettingsEvents.Support->{navController.navigate("Settings/Support")}
                    is SettingsEvents.FAQ->{navController.navigate("Settings/FAQ")}
                    is SettingsEvents.ChangePassword->{navController.navigate("Settings/Password")}
                    is SettingsEvents.UpdateEmail->{navController.navigate("Settings/Email")}
                    is SettingsEvents.LogOut->{
                    /*log user out*/
                        authViewModel.logout()
                        userViewModel.resetUserValidation()
                        navController.navigate("Login")
                    }
                    else ->{}
                }

            })
         }
        }


    composable(
        "Settings/{type}",    arguments = listOf(navArgument("type") { type = NavType.StringType }),
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
            navController.navigate("Settings")
        }
        when(backStackEntry.arguments?.getString("type")){
            "Language"->{

                LanguageScreen(onEvent = {event->
                    when(event){
                        is LanguageEvents.GoBack->{navController.navigate("Settings")}
                    }

                })

            }
            "Range"->{
                RangeScreen(onEvent = {event->
                    when(event){
                        is RangeEvents.GoBack->{navController.navigate("Settings")}
                    }

                })
            }
            "Password"->{
                PasswordScreen(onEvent = {event->
                    when(event){
                        is  PasswordEvents.GoBack->{navController.navigate("Settings")}
                    }

                })
            }
            "Email"->{
                EmailScreen(onEvent = {event->
                    when(event){
                        is EmailEvents.GoBack->{navController.navigate("Settings")}
                    }

                })
            }
            "Notification"->{
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
            "Support"->{
                SupportScreen(onEvent = {event->
                    when(event){
                        is SupportEvents.GoBack->{navController.navigate("Settings")}
                    }

                })
            }
            "Terms"->{
                TermsScreen(onEvent = {event->
                    when(event){
                        is TermsEvents.GoBack->{navController.navigate("Settings")}
                    }

                })
            }
        }


    }


}
