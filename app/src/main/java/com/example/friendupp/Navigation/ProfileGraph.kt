package com.example.friendupp.Navigation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
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
import com.example.friendupp.ActivityPreview.handleActivityEvents
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.Invites.InvitesViewModel
import com.example.friendupp.Profile.*
import com.example.friendupp.R
import com.example.friendupp.Request.RequestViewModel
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
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import java.io.File
import java.util.*
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


val modifier= Modifier
    .fillMaxSize()
    .safeDrawingPadding()
@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profileGraph(
    navController: NavController, outputDirectory: File,
    executor: Executor, userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    invitesViewModel: InvitesViewModel,
    requestViewModel:RequestViewModel
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

            CameraView(modifier=modifier,
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

                ProfileScreen(modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
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
                                navController.navigate("FriendList/"+UserData.user!!.id)
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

                            is ProfileEvents.OpenCamera -> {
                                navController.navigate("CameraProfile")
                            }
                            is ProfileEvents.GoToProfile -> {
                                navController.navigate("ProfileDisplay/" + event.id)
                            }

                            is ProfileEvents.OpenChat -> {
                                navController.navigate("ChatItem/" + event.id)

                            }

                        }

                    },
                    onClick = { navController.navigate("EditProfile") },
                    user = user,
                    activityViewModel = activityViewModel
                , activityEvents = {event->
                        handleActivityEvents(
                            event = event,
                            activityViewModel = activityViewModel,
                            userViewModel = userViewModel,
                            homeViewModel = homeViewModel,
                            navController = navController,
                            context = context,requestViewModel=requestViewModel
                        )
                    })
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
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
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
            "FriendList/{userID}",    arguments = listOf(navArgument("userID") { type = NavType.StringType }),
            enterTransition = {
                when (initialState.destination.route) {
                    "EditProfile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
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

                    else -> null
                }
            }
        ) { backStackEntry ->
            val userID = backStackEntry.arguments?.getString("userID")
            if(userID!=null){
                LaunchedEffect(Unit) {
                    Log.d("FriendsViewModel","Get friends called")
                        userViewModel.getFriends(userID)
                }
            }else{
                navController.popBackStack()
            }

            var friendList= userViewModel.getFriendsList()
            var isLoading = userViewModel.friendsLoading.value

            FriendListScreen(modifier=Modifier.safeDrawingPadding(),onEvent = { event ->
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
                    is FriendListEvents.GetMoreFriends -> {
                        userViewModel.getMoreFriends(UserData.user!!.id)
                    }
                    else -> {}
                }
            },friendList=friendList,isLoading)
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
                    userViewModel.getUserListener(userID)
                }
            }
            val localClipboardManager = LocalClipboardManager.current
            val context = LocalContext.current

            val user =userViewModel.getUserProfile()
            if (user == null) {
                CircularProgressIndicator()
            } else {
                //check if user is me then go to profiel
                if (user.id == UserData.user!!.id) {
                    navController.navigate("Profile")
                }else if(user.blocked_ids.contains(UserData.user!!.id)){
                    navController.popBackStack()
                }
                ProfileDisplayScreen(modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
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
                                userViewModel.removeFriendFromBothUsers(UserData.user!!.id, event.user_id)

                                //to update the user data ??
                                val currentUser = authViewModel.currentUser
                                if (currentUser != null) {
                                    userViewModel.validateUser(currentUser)
                                }

                                navController.popBackStack()
                                Toast.makeText(
                                    context,
                                    "User blocked ", Toast.LENGTH_LONG
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

                            is ProfileDisplayEvents.GoToFriendList -> {
                                navController.navigate("FriendList/"+event.id)
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
                                    Log.d("ProfileDisplay","DElete Chat collection")
                                    chatViewModel.deleteChatCollection(chat_id)
                                    chatViewModel.deleteMessages(chat_id)
                                    Toast.makeText(
                                            context,
                                    "Removed", Toast.LENGTH_LONG
                                    ).show()

                                }

                            }

                            is ProfileDisplayEvents.InviteUser -> {
                                /*add inivte to both users*/
                                val timestamp = getCurrentUTCTime() // Using the provided Timestamp class or any other suitable timestamp representation
                                val newInvite = Invite(
                                    id = UserData.user!!.id+event.user_id, // You need to generate a unique inviteId, it could be a random string or a combination of IDs and timestamp.
                                    senderId = UserData.user!!.id,
                                    receiverId = event.user_id,
                                    timestamp = timestamp,
                                    senderName = UserData.user?.username!!,
                                    senderProfilePictureUrl = UserData.user!!.pictureUrl!!
                                )

                                // create invite and add it to invites repo
                                invitesViewModel.addInvite(newInvite)

                                /*handle notification*/
                                sendNotification(receiver = event.user_id, message = " sent you a friend request",
                                    title = "New friend request", username = UserData.user?.username!!, picture = UserData.user!!.pictureUrl,
                                type="friendRequest",id=UserData.user!!.id)
                                Toast.makeText(
                                    context,
                                    "User " + event.user_id + " invited ", Toast.LENGTH_LONG
                                ).show()
                            }
                            is ProfileDisplayEvents.GoToChat -> {
                                navController.navigate("ChatItem/" + event.chat_id)
                            }

                            is ProfileDisplayEvents.OpenChat -> {
                                navController.navigate("ChatItem/" + event.id)

                            }


                            is ProfileDisplayEvents.GoToProfile -> {
                                navController.navigate("ProfileDisplay/" + event.id)
                            }
                        }
                    }, activityEvents = {event->
                        handleActivityEvents(
                            event = event,
                            activityViewModel = activityViewModel,
                            userViewModel = userViewModel,
                            homeViewModel = homeViewModel,
                            navController = navController,
                            context = context,
                                    requestViewModel=requestViewModel)
                    }, user = user, activityViewModel = activityViewModel)
            }




        }
    }
}



fun generateInviteId(): String {
    return UUID.randomUUID().toString()
}
