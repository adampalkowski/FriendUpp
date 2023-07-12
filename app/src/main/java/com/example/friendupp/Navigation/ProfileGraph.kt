package com.example.friendupp.Navigation

import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.Categories.Category
import com.example.friendupp.ChatUi.ChatEvents
import com.example.friendupp.Groups.GroupItemEvent
import com.example.friendupp.Home.HomeEvents
import com.example.friendupp.Home.HomeViewModel

import com.example.friendupp.Profile.*
import com.example.friendupp.R
import com.example.friendupp.Settings.ChangeEmailDialog
import com.example.friendupp.Settings.ChangePasswordDialog
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.AuthViewModel
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.*
import com.example.friendupp.ui.theme.SocialTheme
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import java.io.File
import java.util.concurrent.Executor

fun loadUser(userViewModel: UserViewModel, currentUser: MutableState<User?>) {
    userViewModel.userState.value.let { response ->
        when (response) {
            is Response.Success -> {
                Log.d("EDITPROFILEDEBUG", "GOT DATA ")
                currentUser.value = response.data!!
                userViewModel.resetUserValue()
            }
            is Response.Failure -> {
                currentUser.value = null
            }
            is Response.Loading -> {
                currentUser.value = null
            }
            null -> {

            }
        }

    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profileGraph(
    navController: NavController, outputDirectory: File,
    executor: Executor, userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
) {
    navigation(startDestination = "FriendList", route = "ProfileGraph") {

        composable("CameraProfile",
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
            var photoUri by remember {
                mutableStateOf<Uri?>(null)
            }
            val context= LocalContext.current

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
                            if(photoUri!=null){
                                photoUri!!.toFile().delete()
                            }

                            navController.popBackStack()
                        }
                        is CameraEvent.AcceptPhoto -> {
                            Log.d("CAMERAGRAPHACTIvity", "ACASDASDASD")
                            if (photoUri != null) {
                                if (photoUri != null) {
                                    userViewModel.onUriReceived(photoUri!!)
                                    Toast.makeText(context,"Uploading image ,hol up..",Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context,"Failed to upload image",Toast.LENGTH_SHORT).show()

                                }
                                navController.popBackStack()


                            }
                        }
                        is CameraEvent.DeletePhoto -> {
                            if(photoUri!=null){
                                photoUri!!.toFile().delete()
                            }
                            Log.d("CreateGraphActivity", "dElete photo")

                            photoUri = null
                        }
                        is CameraEvent.Download -> {
                            Toast.makeText(context,"Image saved in gallery",Toast.LENGTH_SHORT).show()
                            photoUri = null
                        }
                        else -> {}
                    }
                },
                photoUri = photoUri
            )

        }
        composable(
            "Profile",
            enterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
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
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
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
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(350)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
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
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) {
            val localClipboardManager = LocalClipboardManager.current
            val context = LocalContext.current
            val user = UserData.user
            val activityViewModel: ActivityViewModel = hiltViewModel()
            if (user == null) {

                navController.navigate("Welcome")
            } else {
                activityViewModel.getJoinedActivities(user.id)
                activityViewModel.getUserActivities(user.id)

                ProfileScreen(modifier = Modifier.fillMaxSize(),
                    onEvent = { event ->
                        when (event) {
                            is ProfileEvents.GoBack -> {
                                navController.popBackStack()
                            }
                            is ProfileEvents.GoToEditProfile -> {
                                navController.navigate("EditProfile")
                            }
                            is ProfileEvents.GoToSearch -> {
                                navController.navigate("Search")
                            }
                            is ProfileEvents.GoToFriendList -> {
                                navController.navigate("FriendList")
                            }
                            is ProfileEvents.GoToSettings -> {
                                navController.navigate("Settings")
                            }
                            is ProfileEvents.GetProfileLink -> {
                                val user = UserData.user
                                if (user != null) {
                                    val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                                        link =
                                            Uri.parse("https://link.friendup.app/" + "User" + "/" + user.id)
                                        domainUriPrefix = "https://link.friendup.app/"
                                        // Open links with this app on Android
                                        androidParameters { }
                                    }
                                    val dynamicLinkUri = dynamicLink.uri
                                    //COPY LINK AND MAKE A TOAST
                                    localClipboardManager.setText(AnnotatedString(dynamicLinkUri.toString()))
                                    Toast.makeText(
                                        context,
                                        "Copied user link to clipboard",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }


                            }
                            is ProfileEvents.Bookmark -> {
                                activityViewModel.bookMarkActivity(
                                    event.id,
                                    UserData.user!!.id
                                )
                            }
                            is ProfileEvents.UnBookmark -> {
                                activityViewModel.unBookMarkActivity(
                                    event.id,
                                    UserData.user!!.id
                                )
                            }
                            is ProfileEvents.OpenCamera -> {
                                navController.navigate("CameraProfile")
                            }
                            is ProfileEvents.GoToProfile -> {
                                navController.navigate("ProfileDisplay/" + event.id)
                            }

                            is ProfileEvents.OpenChat -> {
                                navController.navigate("ChatItem/" + event.id)

                            }
                            is ProfileEvents.JoinActivity -> {

                                if(event.activity.participants_ids.size<6){
                                    userViewModel.addActivityToUser(event.activity.id,UserData.user!!)
                                    activityViewModel.likeActivity(
                                        event.activity.id,
                                        UserData.user!!
                                    )
                                }else{
                                    userViewModel.addActivityToUser(event.activity.id,UserData.user!!)
                                    activityViewModel.likeActivityOnlyId(
                                        event.activity.id,
                                        UserData.user!!
                                    )

                                }
                            }
                            is ProfileEvents.LeaveActivity -> {
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
                            is ProfileEvents.ExpandActivity -> {
                                Log.d("ACTIVITYDEBUG", "LAUNCH PREIVEW")
                                homeViewModel.setExpandedActivity(event.activityData)
                                navController.navigate("ActivityPreview")
                            }
                        }

                    },
                    onClick = { navController.navigate("EditProfile") },
                    user = user,
                    activityViewModel = activityViewModel
                )
                var uri by remember { mutableStateOf<Uri?>(null) }
                val uriFlow = userViewModel.uri.collectAsState()

                LaunchedEffect(uriFlow.value) {
                    val newUri = uriFlow.value
                    if (newUri != null) {
                        uri = newUri
                        userViewModel.changeUserProfilePicture(
                            UserData.user!!.id,
                            uri!!
                        )
                        userViewModel.onUriProcessed()
                        userViewModel.onUriReceived(null)
                    }
                }
            }


        }
        composable(
            "EditProfile",
            enterTransition = {
                when (initialState.destination.route) {
                    "Profile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Profile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "Profile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Profile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) {
            val context = LocalContext.current

            var openEditEmailDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var openChangePasswordDialog by rememberSaveable {
                mutableStateOf(false)
            }


            val user = UserData.user
            var currentUser = remember { mutableStateOf<User?>(user) }
            val callMade = rememberSaveable {
                mutableStateOf(true)
            }
            LaunchedEffect(callMade) {
                userViewModel.getUser(user!!.id)
                callMade.value = false
            }
            if (user != null) {
                loadUser(userViewModel, currentUser)
            } else {
                /*USER DATA NULL*/
            }
            Log.d("EDITPROFILEDEBUG", currentUser.value.toString())
            if (currentUser.value != null) {
                val userState =   rememberUserState(initialName =currentUser.value?.name!!, initialUsername =currentUser.value?.username!!, initialBio =currentUser.value?.biography!!, initialTags =currentUser.value?.tags!!, initialImageUrl = currentUser.value?.pictureUrl!!)
                val usernameFlow = userViewModel.isUsernameAddedFlow?.collectAsState()

                EditProfile(
                    modifier = Modifier,
                    goBack = { navController.navigate("Profile") },
                    userVa = currentUser,
                    onEvent = { event ->
                        when (event) {
                            is EditProfileEvents.GoBack -> {
                                navController.popBackStack()
                            }
                            is EditProfileEvents.ConfirmChanges -> {

                                currentUser.value.let {
                                    if(it!=null){
                                        Log.d("EDITPROFILEDEBUG",currentUser.value!!.username!!)
                                        Log.d("EDITPROFILEDEBUG",userState.usernameState.text)

                                        if(currentUser.value!!.username!!.trim()!=userState.usernameState.text.trim()){

                                            userViewModel.checkIfUsernameExists(userState.usernameState.text.trim())
                                            usernameFlow?.value.let {
                                                response->
                                                when(response){
                                                    is Response.Success->{
                                                        currentUser.value=it.copy(tags = userState.tags, biography = userState.bioState
                                                            .text.trim(), username = userState.usernameState.text.trim(), name = userState.nameState
                                                            .text.trim())
                                                        userViewModel.addUser(currentUser.value!!)
                                                        Toast.makeText(context,"Profile edited",Toast.LENGTH_SHORT).show()
                                                        userViewModel.setCurrentUser(currentUser.value!!)
                                                        userViewModel.setUserData(currentUser.value!!)
                                                        navController.navigate("Profile")
                                                    }
                                                    is Response.Failure->{
                                                        Toast.makeText(context,"Username already taken",Toast.LENGTH_SHORT).show()

                                                    }
                                                    else->{}
                                                }
                                            }

                                        }else{
                                            currentUser.value=it.copy(tags = userState.tags, biography = userState.bioState
                                                .text, username = userState.usernameState.text, name = userState.nameState
                                                .text)
                                            userViewModel.addUser(currentUser.value!!)
                                            Toast.makeText(context,"Profile edited",Toast.LENGTH_SHORT).show()
                                            userViewModel.setCurrentUser(currentUser.value!!)
                                            userViewModel.setUserData(currentUser.value!!)
                                            navController.navigate("Profile")
                                        }

                                    }
                                }




                            }
                            is EditProfileEvents.OpenCamera -> {
                                navController.navigate("CameraProfile")
                            }
                            is EditProfileEvents.openEditEmailDialog -> {
                                openEditEmailDialog = true
                            }
                            is EditProfileEvents.openChangePasswordDialog -> {
                                openChangePasswordDialog = true
                            }
                        }


                    },
                    userState = userState
                )
                var uri by remember { mutableStateOf<Uri?>(null) }
                val uriFlow = userViewModel.uri.collectAsState()

                LaunchedEffect(uriFlow.value) {
                    val newUri = uriFlow.value
                    if (newUri != null) {
                        userState.imageUrl=newUri.toString()
                        uri = newUri
                        userViewModel.changeUserProfilePicture(
                            UserData.user!!.id,
                            uri!!
                        )
                        userViewModel.onUriProcessed()
                        userViewModel.onUriReceived(null)
                    }
                }
            } else {
                navController.popBackStack()
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
                    icon = R.drawable.ic_email,
                    onCancel = { openEditEmailDialog = false },
                    onConfirm = { new_email ->

                        authViewModel.updateEmail(new_email, id = UserData.user!!.id)
                        openEditEmailDialog = false
                        Toast.makeText(
                            context,
                            "Email updated, changes may take a few minutes.",
                            Toast.LENGTH_SHORT
                        ).show()

                    },
                    confirmTextColor = SocialTheme.colors.textInteractive,
                    disableConfirmButton = false
                )
            }


        }
        composable(
            "FriendList",
            enterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
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
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
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
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(350)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
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
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) {


            FriendListScreen(onEvent = { event ->
                when (event) {
                    is FriendListEvents.GoBack -> {
                        navController.navigate("Profile")
                    }
                    is FriendListEvents.GoToAddFriends -> {
                        navController.navigate("Search")
                    }
                    is FriendListEvents.ProfileDisplay -> {
                        navController.navigate("ProfileDisplay/" + event.userId)
                    }
                    else -> {}
                }
            }, userViewModel = userViewModel)
        }
        composable(
            "ProfileDisplay/{userID}",
            arguments = listOf(navArgument("userID") { type = NavType.StringType }),
            enterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    "Home" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    "Chat" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
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
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
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
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(350)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "EditProfile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
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
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            val userViewModel: UserViewModel = hiltViewModel()
            val userID = backStackEntry.arguments?.getString("userID")
            val activityViewModel: ActivityViewModel = hiltViewModel()


            if (userID != null) {
                LaunchedEffect(key1 = userID) {
                    Log.d("SEARCHSCREENDEBUG", "get user")
                    userViewModel.getUser(userID)
                }
            }
            val localClipboardManager = LocalClipboardManager.current
            val context = LocalContext.current
            val userFlow = userViewModel.userState?.collectAsState()
            val user = remember { mutableStateOf<User?>(null) }
            if (user.value == null) {
                CircularProgressIndicator()
            } else {
                //check if user is me then go to profiel
                if (user.value!!.id == UserData.user!!.id) {
                    navController.navigate("Profile")
                }
                ProfileDisplayScreen(modifier = Modifier.fillMaxSize(),
                    onEvent = { event ->
                        when (event) {
                            is ProfileDisplayEvents.GoBack -> {
                                navController.popBackStack()
                                userViewModel.resetUserValue()
                            }
                            is ProfileDisplayEvents.GoToEditProfile -> {
                                navController.navigate("EditProfile")
                            }
                            is ProfileDisplayEvents.BlockUser -> {

                                //UPDATE DATA
                                userViewModel.addBlockedIdToUser(UserData.user!!.id, event.user_id)

                                //to update the user data ??
                                val currentUser = authViewModel.currentUser
                                if (currentUser != null) {
                                    userViewModel.validateUser(currentUser)
                                }

                                navController.popBackStack()
                                Toast.makeText(
                                    context,
                                    "User " + event.user_id + " invited ", Toast.LENGTH_LONG
                                ).show()
                            }

                            is ProfileDisplayEvents.UnBlock -> {
                                userViewModel.removeBlockedIdFromUser(
                                    UserData.user!!.id,
                                    event.user_id
                                )

                                //to update the user data ??
                                val currentUser = authViewModel.currentUser
                                if (currentUser != null) {
                                    userViewModel.validateUser(currentUser)
                                }
                                navController.popBackStack()


                                Toast.makeText(
                                    context,
                                    "User " + event.user_id + " invited ", Toast.LENGTH_LONG
                                ).show()
                            }
                            is ProfileDisplayEvents.GoToSearch -> {
                                navController.navigate("Search")
                            }
                            is ProfileDisplayEvents.Bookmark -> {
                                activityViewModel.bookMarkActivity(event.id,UserData.user!!.id)
                            }
                            is ProfileDisplayEvents.UnBookmark -> {
                              activityViewModel.unBookMarkActivity(event.id,UserData.user!!.id)

                            }
                            is ProfileDisplayEvents.GoToFriendList -> {
                                navController.navigate("FriendList")
                            }
                            is ProfileDisplayEvents.GoToSettings -> {
                                navController.navigate("Settings")
                            }
                            is ProfileDisplayEvents.ShareProfileLink -> {

                                //CREATE A DYNAMINC LINK TO DOMAIN
                                val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                                    link =
                                        Uri.parse("https://link.friendup.app/" + "User" + "/" + event.user_id)
                                    domainUriPrefix = "https://link.friendup.app/"
                                    // Open links with this app on Android
                                    androidParameters { }
                                }
                                val dynamicLinkUri = dynamicLink.uri
                                //COPY LINK AND MAKE A TOAST
                                localClipboardManager.setText(AnnotatedString(dynamicLinkUri.toString()))
                                Toast.makeText(
                                    context,
                                    "Copied user link to clipboard",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                            is ProfileDisplayEvents.GetProfileLink -> {}
                            is ProfileDisplayEvents.RemoveFriend -> {

                                userViewModel.removeInvitedIdFromUser(
                                    UserData.user!!.id,
                                    event.user_id
                                )
                                userViewModel.removeFriendFromBothUsers(
                                    UserData.user!!.id,
                                    event.user_id
                                )


                                val chat_id = UserData.user!!.friends_ids.get(event.user_id)
                                // REMOVE CHAT BETWEEN USERS ????/
                                if (chat_id != null) {
                                    chatViewModel.deleteChatCollection(chat_id)

                                }
                                Toast.makeText(
                                    context,
                                    "Removed", Toast.LENGTH_LONG
                                ).show()

                            }

                            is ProfileDisplayEvents.InviteUser -> {
                                /*add inivte to both users*/
                                userViewModel.addInvitedIdToUser(UserData.user!!.id, event.user_id)
                                /*handle notification*/
                                sendNotification(receiver = event.user_id, message = " sent you a friend request", title = "New friend request", username = UserData.user?.username!!, picture = UserData.user!!.pictureUrl)
                                Toast.makeText(
                                    context,
                                    "User " + event.user_id + " invited ", Toast.LENGTH_LONG
                                ).show()
                            }
                            is ProfileDisplayEvents.GoToChat -> {
                                navController.navigate("ChatItem/" + event.chat_id)
                            }
                            is ProfileDisplayEvents.JoinActivity -> {

                                if(event.activity.participants_ids.size<6){
                                    userViewModel.addActivityToUser(event.activity.id,UserData.user!!)
                                    activityViewModel.likeActivity(
                                        event.activity.id,
                                        UserData.user!!
                                    )
                                    if(event.activity.creator_id!=UserData.user!!.id){
                                        sendNotification(receiver = event.activity.creator_id,
                                            picture = UserData.user!!.pictureUrl, message = UserData.user?.username+" joined your activity", title = Resources.getSystem().getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE) ,username = "")
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
                            is ProfileDisplayEvents.LeaveActivity -> {
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
                            is ProfileDisplayEvents.OpenChat -> {
                                navController.navigate("ChatItem/" + event.id)

                            }

                            is ProfileDisplayEvents.ExpandActivity -> {
                                Log.d("ACTIVITYDEBUG", "LAUNCH PREIVEW")
                                homeViewModel.setExpandedActivity(event.activityData)
                                navController.navigate("ActivityPreview")
                            }
                            is ProfileDisplayEvents.GoToProfile -> {
                                navController.navigate("ProfileDisplay/" + event.id)
                            }
                        }
                    }, user = user.value!!, activityViewModel = activityViewModel)
            }



            userFlow?.value.let { response ->
                when (response) {
                    is Response.Success -> {
                        Log.d("SEARCHSCREENDEBUG", "saearch sucess")
                        user.value = response.data
                        userViewModel.resetUserValue()
                    }
                    is Response.Failure -> {

                    }
                    is Response.Loading -> {
                        CircularProgressIndicator()
                    }
                    null -> {
                    }
                }

            }


        }
    }
}



