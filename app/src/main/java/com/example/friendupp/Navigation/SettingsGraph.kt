package com.example.friendupp.Navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Components.FriendUppDialog
import com.example.friendupp.R
import com.example.friendupp.Settings.*
import com.example.friendupp.di.AuthViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.SocialTheme
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
) {
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
            var openLogOutDialog by rememberSaveable {
                mutableStateOf(false)
            }

            var openDeleteAccountDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var openDarkModeDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var openEditEmailDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var openChangePasswordDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var openChangeSearchRange by rememberSaveable {
                mutableStateOf(false)
            }
            val context = LocalContext.current

            SettingsScreen(modifier = Modifier.safeDrawingPadding(), settingsEvents = { event ->
                when (event) {
                    is SettingsEvents.GoBack -> {
                        navController.navigate("Home")
                    }
                    is SettingsEvents.ChangeLanguage -> {
                        navController.navigate("Settings/Language")
                    }
                    is SettingsEvents.ChangeSearchRange -> {
                        openChangeSearchRange= true
                    }
                    is SettingsEvents.OpenLogOutDialog -> {
                        openLogOutDialog = true
                    }
                    is SettingsEvents.OpenDeleteAccountDialog -> {
                        openDeleteAccountDialog = true
                    }
                    is SettingsEvents.TermsAndPrivacy -> {
                        navController.navigate("Settings/Terms")
                    }
                    is SettingsEvents.Notifications -> {
                        navController.navigate("Settings/Notification")
                    }
                    is SettingsEvents.Support -> {
                        navController.navigate("Settings/Support")
                    }
                    is SettingsEvents.FAQ -> {
                        navController.navigate("Settings/FAQ")
                    }
                    is SettingsEvents.ChangePassword -> {
                        openChangePasswordDialog = true
                    }
                    is SettingsEvents.UpdateEmail -> {
                        openEditEmailDialog = true
                    }
                    is SettingsEvents.DarkMode -> {
                        openDarkModeDialog = true
                    }
                    else -> {}
                }

            })
            if (openChangeSearchRange) {
                ChangeSearchRangeDialog(
                    label = "Select search for nearby activities range.",
                    icon = R.drawable.ic_ruler,
                    onCancel = { openChangeSearchRange = false },
                    onConfirm = { new_range ->
                        saveRangeValue(new_range,context)
                        openChangeSearchRange=false
                    },
                    confirmTextColor = SocialTheme.colors.textInteractive,
                    disableConfirmButton = false
                )
            }
            if (openChangePasswordDialog) {
                ChangePasswordDialog(
                    label = "Update password to keep your account safe.",
                    icon = R.drawable.ic_password,
                    onCancel = { openChangePasswordDialog = false },
                    onConfirm = { new_password ->
                        authViewModel.resetPassword(new_password)
                        Toast.makeText(
                            context,
                            "Password updated, changes may take up to few minutes.",
                            Toast.LENGTH_SHORT
                        ).show()
                        openChangePasswordDialog = false
                    },
                    confirmTextColor = SocialTheme.colors.textInteractive,
                    disableConfirmButton = false
                )
            }

            if (openEditEmailDialog) {
                ChangeEmailDialog(
                    label = "Update email address to keep your account safe.",
                    icon = com.example.friendupp.R.drawable.ic_email,
                    onCancel = { openEditEmailDialog = false },
                    onConfirm = { new_email ->

                        authViewModel.updateEmail(new_email,id=UserData.user!!.id)
                        openEditEmailDialog = false
                        Toast.makeText(context, "Email updated, changes may take a few minutes.", Toast.LENGTH_SHORT).show()

                    },
                    confirmTextColor = SocialTheme.colors.textInteractive,
                    disableConfirmButton = false
                )
            }
            if (openDarkModeDialog) {
                FriendUppDialog(
                    label = "To enable the dark mode, kindly navigate to the settings on your phone and adjust the theme settings to the 'dark' option. This will activate the dark mode feature for a more visually comfortable and immersive experience.",
                    icon = com.example.friendupp.R.drawable.ic_darkmode,
                    onCancel = { openDarkModeDialog = false },
                    onConfirm = { },
                    confirmTextColor = SocialTheme.colors.textInteractive,
                    disableConfirmButton = true
                )
            }
            if (openLogOutDialog) {
                FriendUppDialog(
                    label = "Are you sure you want to log out?",
                    icon = com.example.friendupp.R.drawable.ic_logout,
                    onCancel = { openLogOutDialog = false },
                    onConfirm = {
                        openLogOutDialog = false
                        authViewModel.logout()
                        userViewModel.resetUserValidation()
                        navController.navigate("Login")
                    }, confirmTextColor = SocialTheme.colors.error
                )
            }
            if (openDeleteAccountDialog) {

                FriendUppDialog(
                    label = "Are you sure you want to delete your account?",
                    icon = com.example.friendupp.R.drawable.ic_delete,
                    onCancel = { openDeleteAccountDialog = false },
                    onConfirm = {
                        if (UserData.user != null) {
                            authViewModel.deleteAccount(UserData.user!!)
                             authViewModel.deleteAuth()
                            userViewModel.resetUserValidation()
                            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(
                                context,
                                "Failed to delete account please try again later.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        openDeleteAccountDialog = false
                        navController.navigate("Login")

                    }, confirmTextColor = SocialTheme.colors.error
                )
            }

        }
    }


    composable(
        "Settings/{type}", arguments = listOf(navArgument("type") { type = NavType.StringType }),
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
            navController.navigate("Settings")
        }
        when (backStackEntry.arguments?.getString("type")) {
            "Language" -> {

                LanguageScreen(modifier=modifier,onEvent = { event ->
                    when (event) {
                        is LanguageEvents.GoBack -> {
                            navController.navigate("Settings")
                        }
                    }

                })

            }
            "Range" -> {
                RangeScreen(onEvent = { event ->
                    when (event) {
                        is RangeEvents.GoBack -> {
                            navController.navigate("Settings")
                        }
                    }

                })
            }
            "Password" -> {
                PasswordScreen(onEvent = { event ->
                    when (event) {
                        is PasswordEvents.GoBack -> {
                            navController.navigate("Settings")
                        }
                    }

                })
            }
            "Email" -> {
                EmailScreen(onEvent = { event ->
                    when (event) {
                        is EmailEvents.GoBack -> {
                            navController.navigate("Settings")
                        }
                    }

                })
            }
            "Notification" -> {
                NotificationScreen(modifier=modifier,onEvent = { event ->
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
            "Support" -> {
                SupportScreen(onEvent = { event ->
                    when (event) {
                        is SupportEvents.GoBack -> {
                            navController.navigate("Settings")
                        }
                    }

                })
            }
            "Terms" -> {
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
