package com.example.friendupp.Navigation

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.Create.*
import com.example.friendupp.FriendPicker.FriendPickerScreen
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.firestore.GeoPoint
import java.io.File
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.createGraph(
    navController: NavController, currentActivity: MutableState<Activity>, outputDirectory: File,
    executor: Executor, activityViewModel: ActivityViewModel,
) {

    navigation(startDestination = "FriendPicker", route = "CreateGraph") {

        composable("Camera",
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
        ) { backStackEntry ->
            var photoUri by rememberSaveable {
                mutableStateOf<Uri?>(
                    if(currentActivity.value.image!=null){
                        currentActivity.value.image!!.toUri()
                    }else{
                        null
                    }



                )
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
                            currentActivity.value = currentActivity.value.copy(image = null)
                            navController.navigate("Create")
                        }
                        is CameraEvent.AcceptPhoto -> {
                            if (photoUri != null) {
                                val photo: String = photoUri.toString()
                                currentActivity.value = currentActivity.value.copy(image = photo)
                                Log.d("CreateGraphActivity", "SETTING")
                                Log.d("CreateGraphActivity", photo)
                                navController.navigate("Create")
                                /*todo dooo sth with the final uri */
                            }else{
                                currentActivity.value = currentActivity.value.copy(image = "")
                            }
                        }
                        is CameraEvent.DeletePhoto -> {
                            Log.d("CreateGraphActivity", "dElete photo")
                            photoUri=null
                        }
                        else -> {}
                    }
                },
                photoUri = photoUri
            )

        }



        composable(
            "FriendPicker",
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
        ) {

            val selectedUsers = remember { mutableStateListOf<String>() }
            val context = LocalContext.current

            FriendPickerScreen(
                modifier = Modifier,
                goBack = { navController.popBackStack() },
                selectedUsers,
                onUserSelected = { selectedUsers.add(it) },
                onUserDeselected = { selectedUsers.remove(it) },
                createActivity = {
                    val user = UserData.user
                    if (user != null) {
                        //Add current user to invited list
                        selectedUsers.add(user.id)


                        currentActivity.value = currentActivity.value.copy(
                            invited_users = ArrayList(selectedUsers),
                            creator_profile_picture = user.pictureUrl?:"",
                            creator_name = user.name?:"",
                            creator_username = user.username?:"",
                            creator_id = user.id,

                            )
                        Log.d("CreateGraphActivity", currentActivity.toString())
                        val uuid: UUID = UUID.randomUUID()
                        val id: String = uuid.toString()
                        currentActivity.value = currentActivity.value.copy(id = id)
                        createActivity(
                            currentActivity.value,
                            activityViewModel = activityViewModel,
                            context
                        )
                    }else{
                        Toast.makeText(context,"Failed to read current user, please re-login",Toast.LENGTH_LONG).show()
                    }


                    navController.navigate("Home")
                })
        }

        composable(
            "Create",
            enterTransition = {
                when (initialState.destination.route) {
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Up,
                            animationSpec = tween(700)
                        )
                    "FriendPicker" -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(400)
                    )
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(400)
                        )
                    "FriendPicker" -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(400)
                    )
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "FriendPicker" -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )

                    else -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(400)
                    )
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(400)
                        )
                    "FriendPicker" -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )

                    else -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(400)
                    )
                }
            }
        ) {
            CreateScreen(
                onEvent = { event ->
                    when (event) {
                        is CreateEvents.Settings -> {
                            currentActivity.value = currentActivity.value.copy(
                                title = event.title,
                                description = event.description,
                                start_time = event.startTime,
                                public = event.public,
                                end_time = event.endTime
                            )
                            Log.d("CreateGraphActivity", currentActivity.toString())
                            navController.navigate("CreateSettings")
                        }
                        is CreateEvents.OpenCamera -> {
                            navController.navigate("Camera")
                        }
                        is CreateEvents.Create -> {
                            currentActivity.value = currentActivity.value.copy(
                                title = event.title,
                                description = event.description,
                                start_time = event.startTime,
                                public = event.public,
                                end_time = event.endTime
                            )
                            navController.navigate("FriendPicker")
                        }
                        is CreateEvents.GoBack -> {
                            navController.navigate("Home")
                        }
                    }
                },
                modifier = Modifier, activity = currentActivity.value
            )
        }

        composable(
            "CreateSettings",
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
            CreateSettings(onEvent = { event ->
                when (event) {
                    is CreateSettingsEvents.GoBack -> {
                        val location = event.location
                        if(location==null){
                            currentActivity.value = currentActivity.value.copy(
                                tags = event.tags,
                                custom_location = event.customLocation,
                                location =null,
                                minUserCount = event.minUserCount,
                                maxUserCount = event.maxUserCount,
                                disableChat = event.disableChat,
                                participantConfirmation = event.participantConfirmation,
                                enableActivitySharing = event.activitySharing,
                                disableNotification = event.disableNotification
                            )
                        }else{
                            currentActivity.value = currentActivity.value.copy(
                                tags = event.tags,
                                custom_location = event.customLocation,
                                location = GeoPoint(
                                    event.location.latitude,
                                    event.location.longitude
                                ),
                                minUserCount = event.minUserCount,
                                maxUserCount = event.maxUserCount,
                                disableChat = event.disableChat,
                                participantConfirmation = event.participantConfirmation,
                                enableActivitySharing = event.activitySharing,
                                disableNotification = event.disableNotification
                            )
                        }

                        Log.d("CreateGraphActivity", currentActivity.toString())
                        navController.navigate("Create")
                    }
                    is CreateSettingsEvents.Create -> {

                    }
                }
            }, activity = currentActivity.value)

        }



        composable(
            "CreateLive",
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
            LiveScreen(onEvent = { event ->
                when (event) {
                    is LiveScreenEvents.GoBack -> {
                        navController.navigate("Home")
                    }
                    is LiveScreenEvents.GoToFriendPicker -> {
                        navController.navigate("FriendPicker")
                    }
                }
            })

        }

    }
}

fun createActivity(
    currentActivity: Activity,
    activityViewModel: ActivityViewModel,
    context: Context,
) {

    activityViewModel.addActivity(currentActivity)

    activityViewModel.isActivityAddedState.value.let {
        when (it) {
            is Response<Void?>? -> {
                Log.d("ActivityTesting", "Added")

                Toast.makeText(context, "Activity created", Toast.LENGTH_SHORT).show()
            }
            is Response.Failure -> {
                Log.d("ActivityTesting", "Failed")

            }
            is Response.Loading -> {
                Log.d("ActivityTesting", "Loading")

            }
            else -> {

                Log.d("ActivityTesting", "DIFF")


            }
        }
    }
}
