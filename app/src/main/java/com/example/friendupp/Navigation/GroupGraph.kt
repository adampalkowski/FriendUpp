package com.example.friendupp.Navigation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.Components.FriendUppDialog
import com.example.friendupp.Create.Option
import com.example.friendupp.FriendPicker.FriendPickerEvents
import com.example.friendupp.FriendPicker.FriendPickerScreen
import com.example.friendupp.GroupParticipants.GroupParticipantsViewModel
import com.example.friendupp.Groups.*
import com.example.friendupp.Participants.ParticipantsEvents
import com.example.friendupp.Participants.ParticipantsScreen
import com.example.friendupp.ParticipantsViewModel
import com.example.friendupp.Profile.EditProfileEvents
import com.example.friendupp.R
import com.example.friendupp.bottomBar.ActivityUi.ChangeDescriptionDialog
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.*
import com.example.friendupp.ui.theme.SocialTheme
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executor

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.groupGraph(
    navController: NavController,
    chatViewModel: ChatViewModel,
    groupState: GroupState,
    outputDirectory: File,
    executor: Executor,
    userViewModel: UserViewModel, activityViewModel: ActivityViewModel,groupInvitesViewModel: GroupInvitesViewModel


    ) {
    navigation(startDestination = "Groups", route = "GroupGraph") {


        composable(
            "FriendPickerGroup",
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
            Log.d("CHATDEBUG", "GETFRIENDSCALLED")
            if (UserData
                    .user != null
            ) {
                LaunchedEffect(Unit) {
                    Log.d("FriendsViewModel", "Get friends called")
                    userViewModel.getFriends(
                        UserData
                            .user!!.id
                    )
                }
            } else {
                navController.popBackStack()
            }

            var friendList = userViewModel.getFriendsList()
            var isLoading = userViewModel.friendsLoading.value
            val groupParticipantsViewModel: GroupParticipantsViewModel = hiltViewModel()

            FriendPickerScreen(
                modifier = Modifier.safeDrawingPadding(),
                userViewModel = userViewModel,
                goBack = { navController.popBackStack() },
                chatViewModel = chatViewModel,
                selectedUsers = selectedUsers,
                onUserSelected = { selectedUsers.add(it) },
                onUserDeselected = { selectedUsers.remove(it) },
                createActivity = {
                    val user = UserData.user
                    if (user != null) {
                        //Add current user to invited list
                        selectedUsers.add(user.id)
                        val uuid: UUID = UUID.randomUUID()
                        val id: String = uuid.toString()
                        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")
                        val date = LocalDateTime.now().format(formatterDate)

                        createGroupAlone(
                            create_date = date,
                            owner_id = UserData.user!!.id,
                            id = id,
                            name = groupState.groupName.text,
                            description = groupState.descriptionState.text,
                            context = context,
                            chatViewModel = chatViewModel,
                            image = groupState.imageUrl ?: UserData.user!!.pictureUrl.toString(),
                            invited_users = selectedUsers.toList(),
                            public = groupState.selectedOptionState.option == Option.PUBLIC,
                            groupParticipantsViewModel = groupParticipantsViewModel
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to read current user, please re-login",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    navController.navigate("Home")
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
                }, friendList = friendList, isLoading = isLoading, onEvent = {
                    when (it) {
                        is FriendPickerEvents.GetMoreFriends -> {
                            userViewModel.getMoreFriends(UserData.user!!.id)

                        }
                    }
                })
        }

        composable("Groups") {
            var called by remember { mutableStateOf(true) }
            val groupsList = groupInvitesViewModel.getGroupsList()
            val groupsInvitesList = groupInvitesViewModel.getGroupInvites()
            val groupsListLoading = groupInvitesViewModel.groupListLoading.value
            val groupsListInvitesLoading = groupInvitesViewModel.groupListInvitesLoading.value

            LaunchedEffect(called) {
                if (called) {
                    groupInvitesViewModel.getGroups(UserData.user!!.id)
                    groupInvitesViewModel.getGroupInvites(UserData.user!!.id)
                    called = false
                }
            }
            GroupsScreen(modifier = Modifier.safeDrawingPadding(), onEvent = { event ->
                when (event) {
                    is GroupsEvents.CreateGroup -> {
                        navController.navigate("GroupsCreate")
                    }
                    is GroupsEvents.GoBack -> {
                        navController.popBackStack()
                    }
                    is GroupsEvents.AcceptGroupInvite -> {
                        val participant=Participant(id=UserData.user!!.id, profile_picture = UserData.user!!.pictureUrl!!,name=UserData.user!!.name!!,username=UserData.user!!.username!!, timestamp = getCurrentUTCTime())
                        groupInvitesViewModel.addParticipantToGroup(event.group,participant)
                    }
                    is GroupsEvents.RemoveGroupInvite -> {
                        groupInvitesViewModel.removeInvite(event.group,UserData.user!!.id)
                    }
                    is GroupsEvents.GoToGroupDisplay -> {
                        navController.navigate("GroupDisplay/" + event.groupId)
                    }
                    is GroupsEvents.GetMoreGroups -> {
                        groupInvitesViewModel.getMoreGroups(UserData.user!!.id)
                    }
                    is GroupsEvents.GetMoreGroupInvites -> {
                        groupInvitesViewModel.getMoreGroupsInvites(UserData.user!!.id)
                    }
                    else -> {}
                }
            }, groups = groupsList, groupsInvites = groupsInvitesList)
        }
        composable("GroupCamera",
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
                    if (groupState.imageUrl.isNotEmpty()) {
                        groupState.imageUrl.toUri()
                    } else {
                        null
                    }
                )
            }

            val context = LocalContext.current
            CameraView(
                modifier = modifier,
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
                                photoUri!!.toFile().delete()
                            }

                            navController.navigate("GroupsCreate")
                        }
                        is CameraEvent.AcceptPhoto -> {
                            if (photoUri != null) {
                                val photo: String = photoUri.toString()
                                groupState.imageUrl = photo
                                Log.d("CreateGraphActivity", "SETTING")
                                Log.d("CreateGraphActivity", photo)
                                navController.navigate("GroupsCreate")
                                /*todo dooo sth with the final uri */
                            } else {

                            }
                        }
                        is CameraEvent.DeletePhoto -> {
                            Log.d("CreateGraphActivity", "dElete photo")
                            groupState.imageUrl = ""
                            photoUri = null
                        }
                        is CameraEvent.Download -> {
                            Toast.makeText(context, "Image saved in gallery", Toast.LENGTH_SHORT)
                                .show()

                            Log.d("CreateGraphActivity", "dElete photo")
                            photoUri = null
                        }
                        else -> {}
                    }
                },
                photoUri = photoUri,
            )

        }



        composable("GroupsCreate") {

            GroupsCreateScreen(modifier = Modifier.safeDrawingPadding(), onEvent = { event ->
                when (event) {
                    is GroupCreateEvents.GoBack -> {
                        navController.popBackStack()
                    }
                    is GroupCreateEvents.GoToFriendPicker -> {
                        navController.navigate("FriendPickerGroup")
                    }
                    is GroupCreateEvents.OpenCamera -> {
                        navController.navigate("GroupCamera")

                    }

                }
            }, groupState = groupState)
        }

        composable( "GroupCamera/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
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
        ) {backStackEntry->
            val id =backStackEntry.arguments?.getString("id")
            var photoUri by remember {
                mutableStateOf<Uri?>(null)
            }


            val context = LocalContext.current
            CameraView(modifier=modifier,outputDirectory =outputDirectory , executor = executor, onImageCaptured = {uri->
                photoUri= uri
                /*todo handle the image uri*/
            }, onError = {}, onEvent = {event->
                when(event){
                    is CameraEvent.GoBack->{
                        if(photoUri!=null){
                            photoUri!!.toFile().delete()
                        }
                        navController.popBackStack()
                    }
                    is CameraEvent.AcceptPhoto->{
                        Log.d("CAMERAGRAPHACTIvity","ACASDASDASD")
                        if (photoUri!=null){
                            if(id!=null){
                                val photo:String = photoUri.toString()
                                chatViewModel.updateGroupImage(id,photo)
                                Toast.makeText(context,"Uploading image..",Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }else{
                                navController.popBackStack()
                                Toast.makeText(context,"Failed to upload image",Toast.LENGTH_SHORT).show()

                            }


                            /*todo dooo sth with the final uri */
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
                    else ->{}
                }
            },photoUri=photoUri)

        }
        composable(
            "GroupDisplay/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.StringType }),
        ) { backStackEntry ->

            val userViewModel: UserViewModel = hiltViewModel()
            val groupId = backStackEntry.arguments?.getString("groupId")
            val activityViewModel: ActivityViewModel = hiltViewModel()
            val groupInvitesViewModel: GroupInvitesViewModel = hiltViewModel()

            var openReportDialog by remember{mutableStateOf(false)}
            var openLeaveDialog by remember{mutableStateOf(false)}
            var openDeleteDialog by remember{mutableStateOf(false)}
            var openChangeNameDialog by remember{mutableStateOf(false)}

            if (groupId != null) {
                LaunchedEffect(key1 = groupId) {
                    Log.d("SEARCHSCREENDEBUG", "get user")
                    chatViewModel.getChatCollection(groupId)
                }
            }
            val localClipboardManager = LocalClipboardManager.current
            val context = LocalContext.current
            val chatFlow = chatViewModel.chatCollectionState.collectAsState()
            val chat = remember { mutableStateOf<Chat?>(null) }
            if (chat.value == null) {
                CircularProgressIndicator()
            } else {
                GroupDisplayScreen(modifier = Modifier.safeDrawingPadding(), onEvent = { event ->
                    when (event) {
                        is GroupDisplayEvents.GoBack -> {
                            navController.popBackStack()
                        }
                        is GroupDisplayEvents.GoToMembers -> {
                            navController.navigate("GroupMembers/" +  event.id)
                        }
                        is GroupDisplayEvents.ChangeImage -> {
                            navController.navigate("GroupCamera/" +  event.id)
                        }
                        is GroupDisplayEvents.AddUsers -> {
                            navController.navigate("FriendPickerAddGroupUsers/" + chat.value!!.id)
                        }
                        is GroupDisplayEvents.LeaveGroup -> {
                            openLeaveDialog=true

                        }
                        is GroupDisplayEvents.ChangeGroupName -> {
                            openChangeNameDialog=true
                        }
                        is GroupDisplayEvents.DeleteGroup -> {
                            openDeleteDialog=true

                        }
                        is GroupDisplayEvents.ReportGroup -> {
                            openReportDialog=true

                        }
                        is GroupDisplayEvents.ShareGroupLink -> {
                            //CREATE A DYNAMINC LINK TO DOMAIN
                            val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                                link =
                                    Uri.parse("https://link.friendup.app/" + "Group" + "/" + event.id)
                                domainUriPrefix = "https://link.friendup.app/"
                                // Open links with this app on Android
                                androidParameters { }
                            }
                            val dynamicLinkUri = dynamicLink.uri
                            //COPY LINK AND MAKE A TOAST
                            localClipboardManager.setText(AnnotatedString(dynamicLinkUri.toString()))
                            Toast.makeText(
                                context,
                                "Copied group link to clipboard",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                        else -> {}
                    }

                },group = chat.value!!)
            }

            if(openDeleteDialog){
                FriendUppDialog(
                    label = "Delete the group? Group chat will also be deleted. This action is irreversible and will lose all the group information. ",
                    icon = R.drawable.ic_delete,
                    onCancel = { openDeleteDialog=false },
                    onConfirm = {
                        groupInvitesViewModel.deleteGroup(chat.value!!)
                        Toast.makeText(context,"Group deleted",Toast.LENGTH_SHORT).show()
                        openDeleteDialog=false
                                navController.popBackStack()}, confirmLabel = "Delete")
            }
            if(openChangeNameDialog){
                ChangeDescriptionDialog(
                    label = "Change group name.",
                    icon = R.drawable.ic_edit,
                    onCancel = { openChangeNameDialog = false },
                    onConfirm = { name ->
                        if (!chat.value!!.id.isNullOrEmpty()) {
                            chatViewModel.updateChatCollectionName(name,chat.value!!.id!! )
                            chat.value?.let { it->
                                chat.value=it.copy(name=name)
                            }
                            openChangeNameDialog = false
                            Toast.makeText(
                                context,
                                "Group name updated.",
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
                    disableConfirmButton = false,editTextLabel="Group name"
                )
            }
            if(openReportDialog){
                FriendUppDialog(
                    label = "If the group contains any violations, inappropriate content, or any other concerns that violate community guidelines, please report it.",
                    icon = R.drawable.ic_flag,
                    onCancel = { openReportDialog=false },
                    onConfirm = {
                        chatViewModel.reportChat(chat.value!!.id.toString())
                        Toast.makeText(context,"Group reported",Toast.LENGTH_SHORT).show()
                        openReportDialog=false}, confirmLabel = "Report")
            }
            if(openLeaveDialog){
                FriendUppDialog(
                    label = "Leave group? You will be able to rejoin it if somone invites you again.",
                    icon = R.drawable.ic_logout,
                    onCancel = { openDeleteDialog=false },
                    onConfirm = {
                        groupInvitesViewModel.removeParticipantFromGroupOnlyId(chat.value!!,UserData.user!!.id)
                        Toast.makeText(
                            context,
                            "Left group "+chat.value!!.name.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        openDeleteDialog=false
                        navController.popBackStack()}, confirmLabel = "Leave group", confirmTextColor = SocialTheme.colors.error)
            }




            val LOADING_TIMEOUT_DURATION = 5000L

            LaunchedEffect(true) {
                // Wait for the loading response with a timeout
                val response = withTimeoutOrNull(LOADING_TIMEOUT_DURATION) {
                    chatFlow.value
                }

                when (response) {
                    is Response.Success -> {
                        chat.value = response.data
                        chatViewModel.resetChat()
                    }
                    is Response.Failure -> {
                        // Handle the loading failure if needed
                    }
                    is Response.Loading -> {
                        // Loading is still in progress after the timeout, navigate back
                        navController.popBackStack()
                        Toast.makeText(
                            context,
                            "Loading timeout. Please try again later.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    null -> {
                        // chatFlow.value is null (not sure if this is a possible case)
                    }
                }
            }

        }
    }
    composable(
        "GroupMembers/{groupId}",
        arguments = listOf(navArgument("groupId") { type = NavType.StringType }),
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


        val groupParticipantsViewModel: GroupParticipantsViewModel = hiltViewModel()

        val groupId = backStackEntry.arguments?.getString("groupId")
        var called by remember { mutableStateOf(true) }
        val participantsList = groupParticipantsViewModel.getParticipantsList()
        val participantsLoading = groupParticipantsViewModel.participantsLoading.value
        if (groupId.isNullOrEmpty()) {
            navController.popBackStack()
        } else {
            LaunchedEffect(called) {
                if (called) {
                    groupParticipantsViewModel.getParticipants(groupId)
                    called = false
                }
            }
            ParticipantsScreen(
                modifier = Modifier.safeDrawingPadding(),
                onEvent = { event ->
                    when (event) {
                        is ParticipantsEvents.GoBack -> {
                            navController.popBackStack()
                        }
                        is ParticipantsEvents.GoToUserProfile -> {
                            navController.navigate("ProfileDisplay/" + event.id)
                        }
                        is ParticipantsEvents.GetMoreParticipants -> {
                            groupParticipantsViewModel.getMoreParticipants(groupId)
                        }
                    }
                },
                participantsList, participantsLoading
            )

        }


    }
    composable(
        "FriendPickerAddGroupUsers/{groupId}",
        arguments = listOf(navArgument("groupId") { type = NavType.StringType }),
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
        val groupId = backStackEntry.arguments?.getString("groupId")

        val context = LocalContext.current
        if (UserData
                .user != null
        ) {
            LaunchedEffect(Unit) {
                Log.d("FriendsViewModel", "Get friends called")
                userViewModel.getFriends(
                    UserData
                        .user!!.id
                )
            }
        } else {
            navController.popBackStack()
        }

        var friendList = userViewModel.getFriendsList()
        var isLoading = userViewModel.friendsLoading.value
        val selectedUsers = remember { mutableStateListOf<String>() }
        if (groupId != null) {
            FriendPickerScreen(
                modifier = Modifier.safeDrawingPadding(),
                userViewModel = userViewModel,
                goBack = { navController.popBackStack() },
                chatViewModel = chatViewModel,
                selectedUsers = selectedUsers,
                onUserSelected = { selectedUsers.add(it) },
                onUserDeselected = { selectedUsers.remove(it) },
                createActivity = {
                    chatViewModel.updateChatCollectionInvites(
                        selectedUsers.distinct(),
                        id = groupId
                    )
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
                }, friendList = friendList, isLoading = isLoading, onEvent = {
                    when (it) {
                        is FriendPickerEvents.GetMoreFriends -> {
                            userViewModel.getMoreFriends(UserData.user!!.id)

                        }
                    }
                })
        } else {
            navController.popBackStack()
        }

    }
}

