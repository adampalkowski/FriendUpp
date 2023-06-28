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
import com.example.friendupp.ActivityUi.ActivityState
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.Components.Calendar.rememberHorizontalDatePickerState2
import com.example.friendupp.Components.TimePicker.rememberTimeState
import com.example.friendupp.Create.*
import com.example.friendupp.FriendPicker.FriendPickerScreen
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.AuthViewModel
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import java.io.File
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.createGraph(
    navController: NavController, currentActivity: MutableState<Activity>, outputDirectory: File,
    executor: Executor, activityViewModel: ActivityViewModel,chatViewModel:ChatViewModel,userViewModel:UserViewModel,mapViewModel:MapViewModel,
activityState:ActivityState

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
                    if(activityState.imageUrl.isNotEmpty()){
                        activityState.imageUrl.toUri()
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
                },
                onError = {},
                onEvent = { event ->
                    when (event) {
                        is CameraEvent.GoBack -> {
                            navController.navigate("Create")
                        }
                        is CameraEvent.AcceptPhoto -> {
                            if (photoUri != null) {
                                val photo: String = photoUri.toString()
                                activityState.imageUrl=photo
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
                            activityState.imageUrl=""
                            photoUri=null
                        }
                        else -> {}
                    }
                },
                photoUri = photoUri,
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
            Log.d("CHATDEBUG","GETFRIENDSCALLED")
            userViewModel.getFriends(UserData.user!!.id)
            chatViewModel.getGroups(UserData.user!!.id)
            FriendPickerScreen(
                modifier = Modifier,
                userViewModel = userViewModel,
                goBack = { navController.popBackStack() },
                selectedUsers,
                onUserSelected = { selectedUsers.add(it) },
                onUserDeselected = { selectedUsers.remove(it) },
                createActivity = {
                    val user = UserData.user
                    if (user != null) {
                        // Assuming the provided values are stored in variables as follows:
                        val selectedStartDay = activityState.startDateState.selectedDay
                        val selectedStartMonth = activityState.startDateState.selectedMonth
                        val selectedStartYear = activityState.startDateState.selectedYear
                        val startHours = activityState.timeStartState.hours
                        val startMinutes = activityState.timeStartState.minutes

                        val selectedEndDay = activityState.endDateState.selectedDay
                        val selectedEndMonth = activityState.endDateState.selectedMonth
                        val selectedEndYear = activityState.endDateState.selectedYear
                        val endHours = activityState.timeEndState.hours
                        val endMinutes = activityState.timeEndState.minutes

                    // Formatting the start time
                        val startTime = String.format("%04d-%02d-%02d %02d:%02d", selectedStartYear, selectedStartMonth, selectedStartDay, startHours, startMinutes)

                    // Formatting the end time
                        val endTime = String.format("%04d-%02d-%02d %02d:%02d", selectedEndYear, selectedEndMonth, selectedEndDay, endHours, endMinutes)

                        currentActivity.value = currentActivity.value.copy(start_time = startTime,
                        image=activityState.imageUrl, end_time = endTime
                        )




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
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val current = LocalDateTime.now().format(formatter)
                        val date = LocalDateTime.now().format(formatterDate)
                        currentActivity.value = currentActivity.value.copy(creation_time = current,date=date)
                       if(currentActivity.value.location!=null){

                           val lat=activityState.location.latitude
                           val lng=  activityState.location.longitude
                           //Create geohash
                           val geoHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lng))


                           currentActivity.value = currentActivity.value.copy(geoHash = geoHash,lat=lat,lng=lng)
                       }

                        createGroup(
                            currentActivity.value,
                            activityViewModel = activityViewModel,
                            context,
                            chatViewModel = chatViewModel,
                            group_picture = currentActivity.value.image?:""
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
                            Log.d("CreateGraphActivity", currentActivity.toString())
                            navController.navigate("CreateSettings")
                        }
                        is CreateEvents.OpenCamera -> {
                            navController.navigate("Camera")
                        }
                        is CreateEvents.LocationPicker->{
                            navController.navigate("LocationPicker")
                        }
                        is CreateEvents.Create -> {

                            navController.navigate("FriendPicker")
                        }
                        is CreateEvents.GoBack -> {
                            navController.navigate("Home")
                        }
                    }
                },
                modifier = Modifier, activity = currentActivity.value, activityState = activityState

            )
        }

        composable(
            "LocationPicker",
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
            LocationPickerScreen(onEvent = { event ->
                when (event) {
                    is LocationPickerEvent.GoBack -> {

                        navController.navigate("Create")
                    }
                    is LocationPickerEvent.DeleteLocation->{
                        activityState.location= LatLng(0.0,0.0)
                    }
                    is LocationPickerEvent.ConfirmLocation->{
                        activityState.location=event.latLng
                        navController.navigate("Create")
                    }

                }
            },activityState=activityState, mapViewModel = mapViewModel)

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
            }, activity = currentActivity.value,activityState=activityState)

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
fun createGroup(
    currentActivity: Activity,
    activityViewModel: ActivityViewModel,
    context: Context,
    chatViewModel:ChatViewModel,
    group_picture:String
) {
    val members= arrayListOf(UserData.user!!.id)
    members.addAll(currentActivity.invited_users)

    val chat = Chat(
        create_date = currentActivity.creation_time,
        owner_id = currentActivity.id,
        id = currentActivity.id,
        name = currentActivity.title,
        imageUrl = UserData.user!!.pictureUrl!!,
        recent_message = "say hi!",
        recent_message_time = currentActivity.creation_time,
        type = "activity",
        members =members ,
        user_one_username = null,
        user_two_username = null,
        user_one_profile_pic = null,
        user_two_profile_pic = null,
        highlited_message = "",
        description="",
        numberOfUsers=1,
        numberOfActivities=1
    )
    chatViewModel.addChatCollection(chat,group_picture, onFinished = {picture->
        if(picture.isEmpty()){
            createActivity(currentActivity,activityViewModel,context)
        }else{

            currentActivity.image = picture
            createActivity(currentActivity,activityViewModel,context)

        }


    })



}
