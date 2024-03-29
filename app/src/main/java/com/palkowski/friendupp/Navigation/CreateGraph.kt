package com.palkowski.friendupp.Navigation

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.navigation.*
import com.palkowski.friendupp.Camera.CameraEvent
import com.palkowski.friendupp.Camera.CameraView
import com.palkowski.friendupp.Components.connectTimeAndDate
import com.palkowski.friendupp.Create.*
import com.palkowski.friendupp.FriendPicker.FriendPickerEvents
import com.palkowski.friendupp.FriendPicker.FriendPickerScreen
import com.palkowski.friendupp.GroupParticipants.GroupParticipantsViewModel
import com.palkowski.friendupp.Map.MapViewModel
import com.palkowski.friendupp.bottomBar.ActivityUi.ActivityState
import com.palkowski.friendupp.di.*
import com.palkowski.friendupp.model.*
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import java.io.File
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executor


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.createGraph(
    navController: NavController,
    currentActivity: MutableState<Activity>,
    outputDirectory: File,
    executor: Executor,
    activityViewModel: ActivityViewModel,
    chatViewModel: ChatViewModel,
    userViewModel: UserViewModel,
    mapViewModel: MapViewModel,
    activityState: ActivityState,
    activeUserViewModel: ActiveUsersViewModel,
    authViewModel: AuthViewModel,
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
                    if (activityState.imageUrl.isNotEmpty()) {
                        activityState.imageUrl.toUri()
                    } else {
                        null
                    }
                )
            }

            val context = LocalContext.current
            CameraView(modifier= Modifier.fillMaxSize(),
                outputDirectory = outputDirectory,
                executor = executor,
                onImageCaptured = { uri ->
                    photoUri = uri
                },
                onError = {},
                onEvent = { event ->
                    when (event) {
                        is CameraEvent.GoBack -> {
                            if (photoUri != null) {
                                if(photoUri!!.toFile().exists()){
                                    photoUri!!.toFile().delete()

                                }
                            }
                            navController.navigate("Create")
                        }
                        is CameraEvent.AcceptPhoto -> {
                            if (photoUri != null) {
                                val photo: String = photoUri.toString()
                                activityState.imageUrl = photo
                                Log.d("CreateGraphActivity", "SETTING")
                                Log.d("CreateGraphActivity", photo)
                                navController.navigate("Create")
                                /*todo dooo sth with the final uri */
                            } else {
                                currentActivity.value = currentActivity.value.copy(image = "")
                            }
                        }
                        is CameraEvent.DeletePhoto -> {
                            if (photoUri != null) {
                                photoUri!!.toFile().delete()
                            }
                            Log.d("CreateGraphActivity", "dElete photo")
                            activityState.imageUrl = ""
                            photoUri = null
                        }
                        is CameraEvent.Download -> {
                            Toast.makeText(context, "Image saved in gallery", Toast.LENGTH_SHORT)
                                .show()
                            activityState.imageUrl = ""
                            photoUri = null
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
            if(UserData
                    .user!=null){
                LaunchedEffect(Unit) {
                    Log.d("FriendsViewModel","Get friends called")
                    userViewModel.getFriends(UserData
                        .user!!.id)
                }
            }else{
                navController.popBackStack()
            }

            var friendList= userViewModel.getFriendsList()
            FriendPickerScreen(friendListResponse=userViewModel.friendsLoading.value,
                modifier = Modifier.safeDrawingPadding(),
                userViewModel = userViewModel,
                goBack = { navController.popBackStack() },
                chatViewModel = chatViewModel,
                selectedUsers = selectedUsers,
                onUserSelected = { selectedUsers.add(it) },
                onUserDeselected = { selectedUsers.remove(it) },

                onAllFriends = {

                    if (it) {
                        UserData.user!!.friends_ids.keys.forEach { id ->
                            if (!UserData.user!!.blocked_ids.contains(id)) {
                                selectedUsers.add(id)
                            }
                        }
                    } else {
                        UserData.user!!.friends_ids.keys.forEach { id ->
                            if (!UserData.user!!.blocked_ids.contains(id)) {
                                selectedUsers.remove(id)
                            }
                        }
                    }
                },
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
                        val startTime = String.format(
                            "%04d-%02d-%02d %02d:%02d",
                            selectedStartYear,
                            selectedStartMonth,
                            selectedStartDay,
                            startHours,
                            startMinutes
                        )

                        // Formatting the end time
                        val endTime = String.format(
                            "%04d-%02d-%02d %02d:%02d",
                            selectedEndYear,
                            selectedEndMonth,
                            selectedEndDay,
                            endHours,
                            endMinutes
                        )
                        val initParticipants: kotlin.collections.ArrayList<String> =
                            arrayListOf<String>()
                        initParticipants.add(UserData.user!!.id)
                        currentActivity.value = currentActivity.value.copy(
                            start_time = convertToUTC(startTime),
                            image = activityState.imageUrl,
                            end_time = convertToUTC(endTime),
                            title = activityState.titleState.text,
                            tags = activityState.tags,
                            public = activityState.selectedOptionState.option == Option.PUBLIC,
                            description = activityState.descriptionState.text,
                            participants_ids = initParticipants

                        )


                        //Add current user to invited list
                        selectedUsers.add(user.id)
                        currentActivity.value = currentActivity.value.copy(
                            invited_users = ArrayList(selectedUsers),
                            creator_profile_picture = user.pictureUrl ?: "",
                            creator_name = user.name ?: "",
                            creator_username = user.username ?: "",
                            creator_id = user.id,
                        )
                        Log.d("CreateGraphActivity", currentActivity.toString())
                        val uuid: UUID = UUID.randomUUID()
                        val id: String = uuid.toString()
                        currentActivity.value = currentActivity.value.copy(id = id)
                        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                        val startTimeS = LocalDateTime.parse(startTime, inputFormatter)

                        val date = startTimeS.format(outputFormatter)

                        currentActivity.value = currentActivity.value.copy(
                            creation_time = getCurrentUTCTime(),
                            date = if (activityState.selectedOptionState.option == Option.PUBLIC) date else "",
                        )
                        if (activityState.location.latitude != null && activityState.location.latitude != 0.0) {

                            val lat = activityState.location.latitude
                            val lng = activityState.location.longitude
                            //Create geohash
                            val geoHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lng))


                            currentActivity.value =
                                currentActivity.value.copy(geoHash = geoHash, lat = lat, lng = lng)
                        }

                        createGroup(
                            currentActivity.value,
                            activityViewModel = activityViewModel,
                            context,
                            chatViewModel = chatViewModel,
                            group_picture = currentActivity.value.image ?: "",
                            public = activityState.selectedOptionState.option == Option.PUBLIC,
                            disableNotification=currentActivity.value.disableNotification
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to read current user, please re-login",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    navController.popBackStack("Home",true)
                },friendList=friendList,onEvent={
                    when(it){
                        is FriendPickerEvents.GetMoreFriends->{
                            userViewModel.getMoreFriends(UserData.user!!.id)

                        }
                    }
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
                            navController.navigate("Camera/Create")
                        }
                        is CreateEvents.LocationPicker -> {
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
                modifier = Modifier.safeDrawingPadding(), activity = currentActivity.value, activityState = activityState

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
                    is LocationPickerEvent.DeleteLocation -> {
                        activityState.location = LatLng(0.0, 0.0)
                    }
                    is LocationPickerEvent.ConfirmLocation -> {
                        activityState.location = event.latLng
                        navController.navigate("Create")
                    }

                }
            }, activityState = activityState, mapViewModel = mapViewModel)

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
            CreateSettings(modifier=Modifier.safeDrawingPadding(),onEvent = { event ->
                when (event) {
                    is CreateSettingsEvents.GoBack -> {
                        val location = event.location
                        if (location == null) {
                            currentActivity.value = currentActivity.value.copy(
                                custom_location = event.customLocation,
                                location = null,
                                minUserCount = event.minUserCount,
                                maxUserCount = event.maxUserCount,
                                disableChat = event.disableChat,
                                participantConfirmation = event.participantConfirmation,
                                enableActivitySharing = event.activitySharing,
                                disableNotification = event.disableNotification
                            )
                        } else {
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
            }, activity = currentActivity.value, activityState = activityState)

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
            val context = LocalContext.current

            val calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())
            val activityState = rememberLiveActivityState(
                initialStartHours = LocalTime.now().hour,
                initialStartMinutes = LocalTime.now().minute,
                initialEndHours = LocalTime.now().plusHours(2).hour,
                initialEndMinutes = LocalTime.now().minute,
                initialStartDay = calendar.get(Calendar.DAY_OF_MONTH),
                initialStartMonth = calendar.get(Calendar.MONTH) + 1,
                initialStartYear = calendar.get(Calendar.YEAR),
                initialEndDay = calendar.get(Calendar.DAY_OF_MONTH),
                initialEndMonth = calendar.get(Calendar.MONTH) + 1,
                initialEndYear = calendar.get(Calendar.YEAR),
                initialNote = "",
                initialLocation = LatLng(0.0, 0.0)
            )

            val timeStartState = activityState.timeStartState
            val timeEndState = activityState.timeEndState
            val startDateState = activityState.startDateState
            val endDateState = activityState.endDateState
            BackHandler(true) {
                navController.popBackStack()
            }
            LiveScreen(modifier=Modifier.safeDrawingPadding(),onEvent = { event ->
                when (event) {
                    is LiveScreenEvents.GoBack -> {
                        navController.navigate("Home")
                    }
                    is LiveScreenEvents.GoToFriendPicker -> {
                        navController.navigate("FriendPicker")
                    }
                    is LiveScreenEvents.CreateLive -> {

                        var startTime = connectTimeAndDate(
                            year = startDateState!!.selectedYear,
                            month = startDateState.selectedMonth,
                            day = startDateState.selectedDay,
                            hour = timeStartState!!.hours,
                            minute = timeStartState.minutes
                        )
                        var endTime = connectTimeAndDate(
                            year = endDateState!!.selectedYear,
                            month = endDateState.selectedMonth,
                            day = endDateState.selectedDay,
                            hour = timeEndState!!.hours,
                            minute = timeEndState.minutes
                        )

                        val uuid: UUID = UUID.randomUUID()
                        val id: String = uuid.toString()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val current = LocalDateTime.now().format(formatter)

                        //todo what if current user is null
                        val participants_profile_pictures: java.util.HashMap<String, String> =
                            hashMapOf()
                        val participants_usernames: java.util.HashMap<String, String> = hashMapOf()
                        participants_profile_pictures[authViewModel.currentUser!!.uid] =
                            UserData.user!!.pictureUrl!!
                        participants_usernames[authViewModel.currentUser!!.uid] =
                            UserData.user!!.username!!
                        val invited_users = ArrayList<String>(UserData.user!!.friends_ids.keys)
                        invited_users.add(authViewModel.currentUser!!.uid)

                        activeUserViewModel.addActiveUser(
                            ActiveUser(
                                id = id,
                                creator_id = if (authViewModel.currentUser == null) {
                                    ""
                                } else {
                                    authViewModel.currentUser!!.uid.toString()
                                },
                                participants_profile_pictures = participants_profile_pictures,
                                participants_usernames = participants_usernames,
                                location = GeoPoint(
                                    activityState.location.latitude,
                                    activityState.location.longitude
                                ),
                                time_end = endTime,
                                time_start = startTime,
                                create_time = current,
                                invited_users = invited_users,
                                destroy_time = endTime,
                                note = activityState.note.text.toString()
                            )
                        )

                        navController.navigate("Home")
                    }
                }
            }, liveActivityState = activityState, mapViewModel = mapViewModel)

        }

    }
}

fun createActivity(
    currentActivity: Activity,
    activityViewModel: ActivityViewModel,
    context: Context,disableNotification:Boolean
) {

    activityViewModel.addActivity(currentActivity)
    activityViewModel.isActivityAddedState.value.let {
        when (it) {
            is Response<Void?>? -> {
                Log.d("ActivityTesting", "Added")
                if(!disableNotification){
                    currentActivity.invited_users.forEach { id ->
                        if(id!=UserData.user!!.id){
                            sendNotification(
                                receiver = id,
                                username = "",
                                message = currentActivity.creator_username + "is up to something, check it out!",
                                title = "Invited to activity",
                                picture = currentActivity.image,
                                type = "createActivity",
                                id=currentActivity.id
                            )
                        }
                    }

                }
                activityViewModel.likeActivity(currentActivity.id,UserData.user!!)

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
    chatViewModel: ChatViewModel,
    group_picture: String,
    public: Boolean,
    disableNotification:Boolean
) {
    val members = arrayListOf(UserData.user!!.id)
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
        invites =  emptyList(),
        members = members,
        user_one_username = null,
        user_two_username = null,
        user_one_profile_pic = null,
        user_two_profile_pic = null,
        highlited_message = "",
        description = "",
        numberOfUsers = 1,
        numberOfActivities = 1,
        public = public,
        reports = 0,
        blocked = false,
        user_one_id = null,
        user_two_id = null,
    )
    chatViewModel.addChatCollection(chat, group_picture, onFinished = { picture ->
        if (picture.isEmpty()) {
            createActivity(currentActivity, activityViewModel, context,disableNotification=disableNotification)
        } else {
            currentActivity.image = picture
            createActivity(currentActivity, activityViewModel, context,disableNotification=disableNotification)
        }
    })


}

fun createGroupAlone(
    create_date: String,
    owner_id: String,
    public: Boolean,
    id: String,
    name: String,
    image: String,
    description: String,
    context: Context,
    chatViewModel: ChatViewModel,
    invited_users: List<String>,
    groupParticipantsViewModel:GroupParticipantsViewModel
) {
    val invites = arrayListOf<String>()
    invites.addAll(invited_users)


    val members = arrayListOf(UserData.user!!.id)


    val chat = Chat(
        create_date = create_date,
        owner_id = owner_id,
        id = id,
        name = name,
        imageUrl = image,
        recent_message = "say hi!",
        recent_message_time = create_date,
        type = "group",
        invites = invites,
        members =members,
        user_one_username = null,
        user_two_username = null,
        user_one_profile_pic = null,
        user_two_profile_pic = null,
        highlited_message = "",
        description = description,
        numberOfUsers = 1,
        numberOfActivities = 1,
        public = public,
        reports = 0,
        blocked = false,
        user_one_id = null,
        user_two_id = null

    )
    val participant=Participant(id=UserData.user!!.id, username = UserData.user!!.username!!, profile_picture = UserData.user!!.pictureUrl!!, name = UserData.user!!.name!!, timestamp = getCurrentUTCTime())
    groupParticipantsViewModel.addParticipant(id,participant)
    chatViewModel.addGroupAlone(chat, image, onFinished = { picture ->
        Toast.makeText(context, "Crated group", Toast.LENGTH_SHORT).show()
    })


}
