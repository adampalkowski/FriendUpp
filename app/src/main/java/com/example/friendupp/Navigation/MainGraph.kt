package com.example.friendupp.Navigation

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.ActivityPreview.CreatorSettingsEvent
import com.example.friendupp.ActivityPreview.CreatorSettingsScreen
import com.example.friendupp.bottomBar.ActivityUi.ActivityPreview
import com.example.friendupp.bottomBar.ActivityUi.ActivityPreviewEvents
import com.example.friendupp.Camera.getActivity
import com.example.friendupp.Components.FriendUppDialog
import com.example.friendupp.FriendPicker.FriendPickerScreen
import com.example.friendupp.Home.HomeEvents
import com.example.friendupp.Home.HomeScreen
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.Home.LiveUserSettingsDialog
import com.example.friendupp.Invites.InvitesViewModel
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.MapEvent
import com.example.friendupp.MapScreen
import com.example.friendupp.Participants.ParticipantsEvents
import com.example.friendupp.Participants.ParticipantsScreen
import com.example.friendupp.Profile.ProfileDisplayEvents
import com.example.friendupp.R
import com.example.friendupp.Search.SearchEvents
import com.example.friendupp.Search.SearchScreen
import com.example.friendupp.Settings.ChangeEmailDialog
import com.example.friendupp.bottomBar.ActivityUi.ChangeDescriptionDialog
import com.example.friendupp.bottomBar.ActivityUi.RemoveUsersDialog
import com.example.friendupp.di.ActiveUsersViewModel
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.SocialTheme
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
fun NavGraphBuilder.mainGraph(
    navController: NavController,
    openDrawer: () -> Unit,
    activityViewModel: ActivityViewModel,
    userViewModel: UserViewModel, chatViewModel: ChatViewModel,
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    activeUserViewModel: ActiveUsersViewModel,
    invitesViewModel: InvitesViewModel
) {
    navigation(startDestination = "Home", route = "Main") {

        composable(
            "Participants/{activityId}",
            arguments = listOf(navArgument("activityId") { type = NavType.StringType }),
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
        ) { backStackEntry ->


            val activityId = backStackEntry.arguments?.getString("activityId")
            var called by remember { mutableStateOf(true) }


            if (activityId.isNullOrEmpty()) {
                navController.popBackStack()
            } else {
                LaunchedEffect(called) {
                    if (called) {
                        userViewModel.getActivityUsers(activityId)
                        called = false
                    }
                }
                ParticipantsScreen(
                    modifier = Modifier.safeDrawingPadding(),
                    userViewModel = userViewModel,
                    onEvent = { event ->
                        when (event) {
                            is ParticipantsEvents.GoBack -> {
                                navController.popBackStack()
                            }
                            is ParticipantsEvents.GoToUserProfile -> {
                                navController.navigate("ProfileDisplay/" + event.id)
                            }
                        }
                    },
                    activityId = activityId
                )

            }


        }
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
            DisposableEffect(Unit) {
                onDispose {
                    activeUserViewModel.cancelCurrentUserActiveListener()
                }
            }
            homeViewModel.deep_link.value.let { deep_link ->
                when (deep_link?.pathSegments?.get(0)) {
                    "Activity" -> {
                        val activity_id = deep_link.pathSegments?.get(1).toString()
                        activityViewModel.getActivity(activity_id)
                        Log.d("MAINGRAPHACTIVITY", "Activity")


                        homeViewModel.resetDeepLink()
                    }
                    "User" -> {
                        homeViewModel.resetDeepLink()
                        navController.navigate(
                            "ProfileDisplay/" + deep_link.pathSegments?.get(1).toString()
                        )

                    }
                    "Group" -> {
                        homeViewModel.resetDeepLink()
                        navController.navigate(
                            "GroupDisplay/" + deep_link.pathSegments?.get(1).toString()
                        )

                    }
                }
            }
            homeViewModel.notificationType.value.let { type ->

                when (type) {
                    "message" -> {
                        homeViewModel.notificationLink.value.let { link ->
                            homeViewModel.resetNotificationLink()

                            navController.navigate(
                                "ChatItem/" + link
                            )
                        }

                    }
                    "joinActivity" -> {
                        homeViewModel.notificationLink.value.let { link ->
                            activityViewModel.getActivity(link.toString())
                            activityViewModel.activityState.value.let { response ->
                                when (response) {
                                    is Response.Success -> {
                                        homeViewModel.setExpandedActivity(response.data)
                                        homeViewModel.resetNotificationLink()
                                        navController.navigate("ActivityPreview")


                                    }
                                    else -> {}
                                }

                            }

                        }
                        homeViewModel.resetNotificationLink()

                    }
                    "createActivity" -> {
                        homeViewModel.notificationLink.value.let { link ->
                            activityViewModel.getActivity(link.toString())
                            activityViewModel.activityState.value.let { response ->
                                when (response) {
                                    is Response.Success -> {
                                        homeViewModel.setExpandedActivity(response.data)
                                        homeViewModel.resetNotificationLink()

                                        navController.navigate("ActivityPreview")

                                    }
                                    else -> {}
                                }

                            }

                        }
                        homeViewModel.resetNotificationLink()
                    }
                    "friendRequest" -> {
                        homeViewModel.notificationLink.value.let { link ->
                            homeViewModel.resetNotificationLink()
                            navController.navigate(
                                "ProfileDisplay/" + link
                            )


                        }

                    }
                }
            }
            activityViewModel.activityState.value.let {
                when (it) {
                    is Response.Success -> {
                        Log.d("MAINGRAPHACTIVITY", "ActivityPreviewD")
                        homeViewModel.setExpandedActivity(it.data)
                        activityViewModel.resetActivityState()
                        navController.navigate("ActivityPreview")
                    }
                    is Response.Failure -> {
                        Toast.makeText(
                            LocalContext.current,
                            "Couldn't display activity",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    is Response.Loading -> {
                    }
                }
            }
            var liveUserDialogSettings by remember {
                mutableStateOf<String?>(null)

            }
            val context = LocalContext.current
            HomeScreen(
                modifier = Modifier.safeDrawingPadding(),
                onEvent = { event ->
                    when (event) {
                        is HomeEvents.OpenDrawer -> {
                            openDrawer()
                        }
                        is HomeEvents.CreateLive -> {
                            navController.navigate("CreateLive")
                        }

                        is HomeEvents.OpenChat -> {
                            navController.navigate("ChatItem/" + event.id)

                        }
                        is HomeEvents.JoinActivity -> {
                            if (event.activity.participants_ids.size < 6) {
                                userViewModel.addActivityToUser(event.activity.id, UserData.user!!)
                                activityViewModel.likeActivity(
                                    event.activity.id,
                                    UserData.user!!
                                )
                                if (event.activity.creator_id != UserData.user!!.id) {
                                    sendNotification(
                                        receiver = event.activity.creator_id,
                                        picture = UserData.user!!.pictureUrl,
                                        message = UserData.user?.username + " joined your activity",
                                        title = context.getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE),
                                        username = "",
                                        id = event.activity.id
                                    )
                                }

                            } else {
                                userViewModel.addActivityToUser(event.activity.id, UserData.user!!)
                                activityViewModel.likeActivityOnlyId(
                                    event.activity.id,
                                    UserData.user!!
                                )
                                if (event.activity.creator_id != UserData.user!!.id) {
                                    sendNotification(
                                        receiver = event.activity.creator_id,
                                        picture = UserData.user!!.pictureUrl,
                                        message = UserData.user?.username + " joined your activity",
                                        title = context.getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE),
                                        username = "",
                                        id = event.activity.id
                                    )
                                }

                            }
                        }
                        is HomeEvents.LeaveActivity -> {
                            if (event.activity.participants_usernames.containsKey(UserData.user!!.id)) {
                                userViewModel.removeActivityFromUser(
                                    id = event.activity.id,
                                    user_id = UserData.user!!.id
                                )

                                activityViewModel?.unlikeActivity(
                                    event.activity.id,
                                    UserData.user!!.id
                                )
                            } else {
                                userViewModel.removeActivityFromUser(
                                    id = event.activity.id,
                                    user_id = UserData.user!!.id
                                )

                                activityViewModel?.unlikeActivityOnlyId(
                                    event.activity.id,
                                    UserData.user!!.id
                                )
                            }

                        }
                        is HomeEvents.UnBookmark -> {
                            activityViewModel.unBookMarkActivity(
                                event.id,
                                UserData.user!!.id
                            )
                        }
                        is HomeEvents.Bookmark -> {
                            activityViewModel?.bookMarkActivity(
                                event.id,
                                UserData.user!!.id
                            )
                        }
                        is HomeEvents.ExpandActivity -> {
                            Log.d("ACTIVITYDEBUG", "LAUNCH PREIVEW")
                            homeViewModel.setExpandedActivity(event.activityData)
                            navController.navigate("ActivityPreview")
                        }
                        is HomeEvents.GoToProfile -> {
                            navController.navigate("ProfileDisplay/" + event.id)
                        }
                        is HomeEvents.OpenLiveUser -> {
                            liveUserDialogSettings = event.id
                        }
                    }
                },
                activityViewModel = activityViewModel,
                mapViewModel = mapViewModel,
                activeUserViewModel = activeUserViewModel
            )
            if (liveUserDialogSettings != null) {
                val context = LocalContext.current
                LiveUserSettingsDialog(onDismissRequest = { liveUserDialogSettings = null },
                    deleteActiveUser = {
                        activeUserViewModel.deleteActiveUser(liveUserDialogSettings!!)
                        liveUserDialogSettings = null
                        Toast.makeText(context, "Live user deleted", Toast.LENGTH_SHORT).show()


                    })
            }


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
            val localClipboardManager = LocalClipboardManager.current
            val context = LocalContext.current
            var openReportDialog by remember { mutableStateOf<String?>(null) }

            ActivityPreview(modifier = modifier, onEvent = { event ->
                when (event) {
                    is ActivityPreviewEvents.GoBack -> {
                        navController.popBackStack()
                    }
                    is ActivityPreviewEvents.GoToActivityParticipants -> {
                        navController.navigate("Participants/" + event.id)
                    }
                    is ActivityPreviewEvents.ShareActivityLink -> {
                        //CREATE A DYNAMINC LINK TO DOMAIN
                        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                            link =
                                Uri.parse("https://link.friendup.app/" + "Activity" + "/" + event.link)
                            domainUriPrefix = "https://link.friendup.app/"
                            // Open links with this app on Android
                            androidParameters { }
                        }
                        val dynamicLinkUri = dynamicLink.uri
                        //COPY LINK AND MAKE A TOAST
                        localClipboardManager.setText(AnnotatedString(dynamicLinkUri.toString()))
                        Toast.makeText(
                            context,
                            "Copied activity link to clipboard",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is ActivityPreviewEvents.Join -> {
                        homeViewModel.expandedActivity.value.let { it ->
                            if (it != null) {
                                if (it.participants_ids.size < 6) {
                                    userViewModel.addActivityToUser(it.id, UserData.user!!)
                                    activityViewModel.likeActivity(
                                        it.id,
                                        UserData.user!!
                                    )
                                    if (it.creator_id != UserData.user!!.id) {
                                        sendNotification(
                                            receiver = it.creator_id,
                                            picture = UserData.user!!.pictureUrl,
                                            message = UserData.user?.username + " joined your activity",
                                            title = context.getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE),
                                            username = "",
                                            id = it.id
                                        )
                                    }

                                } else {
                                    userViewModel.addActivityToUser(it.id, UserData.user!!)
                                    activityViewModel.likeActivityOnlyId(
                                        it.id,
                                        UserData.user!!
                                    )

                                    if (it.creator_id != UserData.user!!.id) {
                                        sendNotification(
                                            receiver = it.creator_id,
                                            picture = UserData.user!!.pictureUrl,
                                            message = UserData.user?.username + " joined your activity",
                                            title = context.getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE),
                                            username = "",
                                            id = it.id
                                        )
                                    }
                                }
                            }

                        }


                    }
                    is ActivityPreviewEvents.AddUsers -> {
                        navController.navigate("FriendPickerAddActivityUsers/" + event.id)
                    }
                    is ActivityPreviewEvents.Bookmark -> {
                        activityViewModel.bookMarkActivity(
                            event.id,
                            UserData.user!!.id
                        )

                    }
                    is ActivityPreviewEvents.CreatorSettings -> {
                        navController.navigate("CreatorSettings/" + event.id)

                    }
                    is ActivityPreviewEvents.UnBookmark -> {
                        activityViewModel.unBookMarkActivity(
                            event.id,
                            UserData.user!!.id
                        )

                    }
                    is ActivityPreviewEvents.OpenChat -> {
                        navController.navigate("ChatItem/" + event.id)

                    }
                    is ActivityPreviewEvents.ReportActivity -> {

                        openReportDialog = event.id

                    }
                    is ActivityPreviewEvents.Leave -> {
                        activityViewModel?.unlikeActivity(
                            event.id,
                            UserData.user!!.id
                        )
                    }
                }
            }, homeViewModel = homeViewModel)

            if (openReportDialog != null) {
                FriendUppDialog(
                    label = "If the activity contains any violations, inappropriate content, or any other concerns that violate community guidelines, please report it.",
                    icon = R.drawable.ic_flag,
                    onCancel = { openReportDialog = null },
                    onConfirm = {
                        chatViewModel.reportChat(openReportDialog.toString())
                        Toast.makeText(context, "Activity reported", Toast.LENGTH_SHORT).show()
                        openReportDialog = null
                    }, confirmLabel = "Report"
                )
            }
        }
        composable(
            "CreatorSettings/{activityId}",
            arguments = listOf(navArgument("activityId") { type = NavType.StringType }),
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
        ) { backStackEntry ->
            val localClipboardManager = LocalClipboardManager.current
            var openDeleteActivityDialog by remember {
                mutableStateOf<com.example.friendupp.model.Activity?>(null)
            }
            var openEditDescription by rememberSaveable {
                mutableStateOf(false)
            }
            var openRemoveUsers by rememberSaveable {
                mutableStateOf(false)
            }
            var openDeleteImageDialog by remember {
                mutableStateOf<com.example.friendupp.model.Activity?>(null)
            }
            val context = LocalContext.current
            val activityId = backStackEntry.arguments?.getString("activityId")
            val activityData = homeViewModel.expandedActivity.collectAsState()
            activityData.value.let { activity ->
                CreatorSettingsScreen(modifier = Modifier.safeDrawingPadding(), onEvent = { event ->
                    when (event) {
                        is CreatorSettingsEvent.GoBack -> {
                            navController.popBackStack()
                        }
                        is CreatorSettingsEvent.Share -> {
                            //CREATE A DYNAMINC LINK TO DOMAIN
                            val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                                link =
                                    Uri.parse("https://link.friendup.app/" + "Activity" + "/" + activityId)
                                domainUriPrefix = "https://link.friendup.app/"
                                // Open links with this app on Android
                                androidParameters { }
                            }
                            val dynamicLinkUri = dynamicLink.uri
                            //COPY LINK AND MAKE A TOAST
                            localClipboardManager.setText(AnnotatedString(dynamicLinkUri.toString()))
                            Toast.makeText(
                                context,
                                "Copied activity link to clipboard",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is CreatorSettingsEvent.DeleteActivity -> {
                            openDeleteActivityDialog = activity

                        }

                        is CreatorSettingsEvent.AddUsers -> {
                            navController.navigate("FriendPickerAddActivityUsers/" + activity!!.id)
                        }
                        is CreatorSettingsEvent.RemoveParticipant -> {
                            openRemoveUsers = true


                        }
                        is CreatorSettingsEvent.EditDescription -> {
                            openEditDescription = true
                        }
                        else -> {}
                    }
                }, activity = activity!!,
                    updateCutomization = { activitySharing, disableChat, participantConifrmation, disableNotification ->
                        Log.d("CreateorSettingsCreen", "updated customization")
                        Log.d("CreateorSettingsCreen", activity.id)
                        activityViewModel.updateActivityCustomization(
                            activityId = activity.id,
                            activitySharing = activitySharing,
                            disableChat = disableChat,
                            participantConfirmation = participantConifrmation
                        )
                        homeViewModel.expandedActivity.value?.let { expandedActivity ->
                            // Update the properties of the expandedActivity object with new values
                            homeViewModel.expandedActivity.value = expandedActivity.copy(
                                enableActivitySharing = activitySharing,
                                disableChat = disableChat,
                                participantConfirmation = participantConifrmation
                            )
                        }
                    })
            }
            if (openDeleteActivityDialog != null) {
                FriendUppDialog(
                    label = "Confirm deletion of activity, this action is not reversible.",
                    icon = R.drawable.ic_delete,
                    onCancel = { openDeleteActivityDialog = null },
                    onConfirm = {

                        activityViewModel.deleteActivity(
                            openDeleteActivityDialog!!,
                            manualyDeleted = true
                        )
                        Toast.makeText(
                            context,
                            "Activity deleted",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigate("Home")
                    })
            }
            if (openEditDescription) {
                ChangeDescriptionDialog(
                    label = "Update activity description.",
                    icon = R.drawable.ic_edit,
                    onCancel = { openEditDescription = false },
                    onConfirm = { description ->
                        if (!activityId.isNullOrEmpty()) {
                            activityViewModel.updateDescription(activityId, description)
                            homeViewModel.expandedActivity.value?.let { expandedActivity ->
                                // Update the properties of the expandedActivity object with new values
                                homeViewModel.expandedActivity.value = expandedActivity.copy(
                                    description = description
                                )
                            }

                            openEditDescription = false
                            Toast.makeText(
                                context,
                                "Activity description updated.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Failure.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    },
                    confirmTextColor = SocialTheme.colors.textInteractive,
                    disableConfirmButton = false
                )
            }
            if (openRemoveUsers) {
                activityData.value?.let {
                    RemoveUsersDialog(
                        label = "Select users to be removed.",
                        icon = R.drawable.ic_person_remove,
                        onCancel = { openRemoveUsers = false },
                        onConfirm = { ids ->
                            if (!activityId.isNullOrEmpty()) {
                                openRemoveUsers = false
                                ids.forEach { id ->
                                    activityViewModel.unlikeActivity(activityId, id)
                                }
                                homeViewModel.expandedActivity.value?.let { expandedActivity ->
                                    // Update the properties of the expandedActivity object with new values
                                    val participants_ids = expandedActivity.participants_ids
                                    val participants_usernames =
                                        expandedActivity.participants_usernames
                                    val participants_profile_pictures =
                                        expandedActivity.participants_profile_pictures
                                    ids.forEach { id ->
                                        participants_ids.remove(id)
                                        participants_usernames.remove(id)
                                        participants_profile_pictures.remove(id)
                                    }
                                    homeViewModel.expandedActivity.value = expandedActivity.copy(
                                        participants_ids = participants_ids,
                                        participants_usernames=participants_usernames,
                                        participants_profile_pictures=participants_profile_pictures

                                    )
                                }
                                Toast.makeText(
                                    context,
                                    "Removed users from activity.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failure.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        confirmTextColor = SocialTheme.colors.textInteractive,
                        disableConfirmButton = false, activity = it
                    )
                }

            }

        }
        composable(
            "FriendPickerAddActivityUsers/{activityId}",
            arguments = listOf(navArgument("activityId") { type = NavType.StringType }),
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
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId")

            val selectedUsers = remember { mutableStateListOf<String>() }
            val context = LocalContext.current
            if (activityId != null) {

                FriendPickerScreen(
                    modifier = Modifier.safeDrawingPadding(),
                    userViewModel = userViewModel,
                    goBack = { navController.popBackStack() },
                    chatViewModel = chatViewModel,
                    selectedUsers = selectedUsers,
                    onUserSelected = { selectedUsers.add(it) },
                    onUserDeselected = { selectedUsers.remove(it) },
                    createActivity = {
                        val invites = arrayListOf<String>()
                        invites.addAll(selectedUsers)
                        activityViewModel.updateActivityInvites(
                            activity_id = activityId,
                            invites = invites
                        )
                        Toast.makeText(context, "Invited users", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()

                    },

                    onAllFriends = {
                        if (it) {
                            UserData.user!!.friends_ids_list.forEach { id ->
                                if (!UserData.user!!.blocked_ids.contains(id)) {
                                    selectedUsers.add(id)
                                }
                            }
                        } else {
                            UserData.user!!.friends_ids_list.forEach { id ->
                                if (!UserData.user!!.blocked_ids.contains(id)) {
                                    selectedUsers.remove(id)
                                }
                            }
                        }
                    })
            } else {
                navController.popBackStack()
            }

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
            // Location permission state
            val locationPermissionState = rememberPermissionState(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )


            if (locationPermissionState.status.isGranted) {
                mapViewModel.startLocationUpdates()

                MapScreen(
                    mapViewModel,
                    activityViewModel = activityViewModel,
                    onEvent = { event ->
                        when (event) {
                            is MapEvent.PreviewActivity -> {
                                homeViewModel.setExpandedActivity(event.activity)
                                navController.navigate("ActivityPreview")
                            }
                            else -> {}
                        }
                    })
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Share your current location to search for nearby activities"
                    )
                    Button(onClick = {
                        locationPermissionState.launchPermissionRequest()


                    }) {

                    }
                }
            }


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


            SearchScreen(modifier = Modifier.safeDrawingPadding(), onEvent = { event ->
                when (event) {
                    is SearchEvents.GoBack -> {
                        navController.popBackStack()
                    }
                    is SearchEvents.DisplayUser -> {
                        navController.navigate("ProfileDisplay/" + event.id)

                    }
                    is SearchEvents.SearchForUser -> {
                        userViewModel.getUserByUsername(event.username)
                    }
                    is SearchEvents.OnInviteAccepted -> {
                        invitesViewModel.removeInvite(event.invite)
                        val uuid: UUID = UUID.randomUUID()
                        val id: String = uuid.toString()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val current = LocalDateTime.now().format(formatter)
                        userViewModel.acceptInvite(
                            UserData.user!!, event.invite.senderId, Chat(
                                current,
                                owner_id = event.invite.senderId,
                                id = id,
                                name = null,
                                imageUrl = null,
                                recent_message = "",
                                recent_message_time = current,
                                type = "duo",
                                members = arrayListOf(UserData.user!!.id, event.invite.senderId),
                                user_one_username = UserData.user!!.username,
                                user_two_username = event.invite.senderName,
                                user_one_profile_pic = UserData.user!!.pictureUrl,
                                user_two_profile_pic = event.invite.senderProfilePictureUrl,
                                highlited_message = "",
                                description = "",
                                numberOfUsers = 2,
                                numberOfActivities = 0,
                                public = false,
                                reports = 0,
                                blocked = false,
                                user_one_id = UserData.user!!.id.toString(),
                                user_two_id =event.invite.senderId.toString(),
                            )
                        )

                    }
                }
            }, userViewModel = userViewModel, invitesViewModel = invitesViewModel)

            /*
            CHECK IF USER EXISTS in search, if succes navigate to profile with user
            * */
            userFlow.value.let {
                when (it) {
                    is Response.Success -> {
                        if (it.data != null) {
                            Log.d("SEARCHSCREENDEBUG", "search cseren scuesss")
                            //check if user is me then go to profiel
                            if (it.data.id == UserData.user!!.id) {
                                navController.navigate("Profile")
                            }else if(it.data.blocked_ids.contains(UserData.user!!.id)){
                                Toast.makeText(
                                    LocalContext.current,
                                    "Failed to find user with given username", Toast.LENGTH_LONG
                                ).show()
                            }else{
                                navController.navigate("ProfileDisplay/" + it.data.id.toString())

                            }
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

