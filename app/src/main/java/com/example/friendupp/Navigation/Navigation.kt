package com.example.friendupp.Navigation

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.friendupp.*
import com.example.friendupp.ActivityUi.rememberActivityState
import com.example.friendupp.ChatUi.*
import com.example.friendupp.Components.Calendar.rememberHorizontalDatePickerState2
import com.example.friendupp.Components.TimePicker.rememberTimeState
import com.example.friendupp.Create.*
import com.example.friendupp.Drawer.drawerGraph
import com.example.friendupp.Groups.rememberGroupState
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.Login.SplashScreen
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.bottomBar.BottomBar
import com.example.friendupp.bottomBar.BottomBarOption
import com.example.friendupp.di.*
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Chat
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.SocialTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalTime
import java.util.*
import java.util.concurrent.Executor

fun customShape() = object : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density,
    ): Outline {
        return Outline.Rectangle(
            Rect(
                0f,
                0f,
                size.width / 1.7f /* width */,
                size.height /* height */
            )
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationComponent(
    navController: NavHostController = rememberAnimatedNavController(),
    outputDirectory: File,
    executor: Executor,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    activityViewModel: ActivityViewModel,
    activeUserViewModel: ActiveUsersViewModel,
) {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val bottomDestinations = listOf("Home", "Chat", "Map", "Profile")
    val currentBackStackEntry by navController.currentBackStackEntryAsState()


    val currentActivity = remember { mutableStateOf(Activity()) }
    val currentChat = remember { mutableStateOf<Chat?>(null) }
    var displaySplashScreen = rememberSaveable { mutableStateOf(true) }


    Scaffold(
        scaffoldState = scaffoldState,
        drawerScrimColor = Color.Black.copy(alpha = 0.3f),
        drawerShape = customShape(),
        drawerBackgroundColor = Color.Transparent,
        drawerContentColor = Color.Transparent,
        drawerContent = {
            DrawerContent(onEvent = { event ->
                when (event) {
                    is DrawerEvents.GoToSettings -> {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                            navController.navigate("Settings")
                        }
                    }
                    is DrawerEvents.GoToSearch -> {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                            navController.navigate("Search")
                        }
                    }
                    is DrawerEvents.GoToInbox -> {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                            navController.navigate("Drawer/Inbox")
                        }
                    }
                    is DrawerEvents.GoToTrending -> {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                            navController.navigate("Drawer/Trending")
                        }
                    }
                    is DrawerEvents.GoToJoined -> {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                            navController.navigate("Drawer/Joined")
                        }
                    }
                    is DrawerEvents.GoToCreated -> {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                            navController.navigate("Drawer/Created")
                        }
                    }
                    is DrawerEvents.GoToForYou -> {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                            navController.navigate("Drawer/ForYou")
                        }
                    }
                    is DrawerEvents.GoToGroups -> {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                            navController.navigate("Groups")
                        }
                    }
                    is DrawerEvents.GoToRate -> {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                            navController.navigate("Drawer/Rate")
                        }
                    }
                }
            })
        }, floatingActionButton = {
            /*
            if (currentBackStackEntry != null && bottomDestinations.contains(navController.currentDestination?.route)) {
                FloatingActionButton(elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    onClick = { navController.navigate("Create") },
                    backgroundColor = SocialTheme.colors.textInteractive.copy(0.8f),
                    content = {
                        Icon(
                            painterResource(id = com.example.friendupp.R.drawable.ic_add),
                            contentDescription = "Favorite", tint = Color.White
                        )
                    }
                )
            }
*/

        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        bottomBar = {

            if (currentBackStackEntry != null && bottomDestinations.contains(navController.currentDestination?.route) && displaySplashScreen.value == false) {
                BottomBar(
                    modifier = Modifier,
                    onClick = { option ->
                        when (option) {
                            BottomBarOption.Profile -> {
                                navController.navigate("Profile")
                            }
                            BottomBarOption.Map -> {
                                navController.navigate("Map")
                            }
                            BottomBarOption.Chat -> {
                                navController.navigate("Chat")
                            }
                            BottomBarOption.Home -> {
                                navController.navigate("Home")
                            }
                            BottomBarOption.Create -> {
                                navController.navigate("Create")

                            }
                        }

                    },
                    selectedOption = navController.currentDestination?.route
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(SocialTheme.colors.uiBackground)
                .fillMaxSize()
        ) {
            BackHandler(onBack = {
                if (scaffoldState.drawerState.isOpen) {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            })
            //GET USER LOCATION CALL
            val context = LocalContext.current

            val calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())
            val activityState = rememberActivityState(
                initialTitle = "Init",
                initialStartHours = LocalTime.now().plusHours(1).hour,
                initialStartMinutes = LocalTime.now().minute,
                initialEndHours = LocalTime.now().plusHours(2).hour,
                initialEndMinutes = LocalTime.now().minute,
                initialStartDay = calendar.get(Calendar.DAY_OF_MONTH),
                initialStartMonth = calendar.get(Calendar.MONTH) + 1,
                initialStartYear = calendar.get(Calendar.YEAR),
                initialEndDay = calendar.get(Calendar.DAY_OF_MONTH),
                initialEndMonth = calendar.get(Calendar.MONTH) + 1,
                initialEndYear = calendar.get(Calendar.YEAR),
                initialOption = Option.PUBLIC,
                initialDescription = "Init desc",
                initialTags = arrayListOf(),
                initialImageUrl = "", initialLocation = LatLng(0.0, 0.0)
            )


            val mapViewModel = remember { MapViewModel(context) }
            DisposableEffect(Unit) {
                mapViewModel.checkLocationPermission(
                    permissionDenied = {

                    },
                    permissionGranted = {
                        mapViewModel.startLocationUpdates()
                    },
                )

                onDispose {
                    mapViewModel.stopLocationUpdates()
                }
            }
            val currentChatViewModel: ChatViewModel = hiltViewModel()
            val groupState = rememberGroupState(
                initialName = "Init",
                initialOption = Option.PUBLIC,
                initialDescription = "Init desc",
                initialTags = arrayListOf(),
                initialImageUrl = ""
            )
            //get the front page activities for user ->friends activities ?? if not exist then public
            //called on each homescreen recompose
            AnimatedNavHost(navController, startDestination = "Welcome") {
                loginGraph(navController, userViewModel, authViewModel = authViewModel)
                mainGraph(
                    navController,
                    openDrawer = {
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
                    },
                    activityViewModel,
                    userViewModel,
                    chatViewModel,
                    homeViewModel = homeViewModel,
                    mapViewModel = mapViewModel,
                    activeUserViewModel = activeUserViewModel
                )
                chatGraph(
                    navController, chatViewModel, currentChat, outputDirectory = outputDirectory,
                    executor = executor, mapViewModel = mapViewModel
                )
                profileGraph(
                    navController,
                    outputDirectory = outputDirectory,
                    executor = executor,
                    userViewModel = userViewModel,
                    chatViewModel = chatViewModel,
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel
                )
                createGraph(
                    navController,
                    currentActivity,
                    outputDirectory = outputDirectory,
                    executor = executor,
                    activityViewModel = activityViewModel,
                    chatViewModel = chatViewModel,
                    userViewModel = userViewModel,
                    activityState = activityState,
                    mapViewModel = mapViewModel,
                    activeUserViewModel = activeUserViewModel,
                    authViewModel = authViewModel
                )
                settingsGraph(navController, authViewModel, userViewModel)
                drawerGraph(navController, activityViewModel, homeViewModel = homeViewModel)
                groupGraph(
                    navController, chatViewModel, groupState, outputDirectory = outputDirectory,
                    executor = executor, userViewModel = userViewModel
                ,activityViewModel=activityViewModel)
                cameraGraph(navController, outputDirectory = outputDirectory, executor = executor)
            }


            /* SPLASH SCREEN*/
            AnimatedVisibility(
                visible = displaySplashScreen.value,
                enter = fadeIn(animationSpec = tween(800)),
                exit = fadeOut(animationSpec = tween(800))
            ) {
                SplashScreen(2000)

            }
            LaunchedEffect(Unit) {
                delay(2000) // Delay for 1 second (1000 milliseconds)
                displaySplashScreen.value =
                    false // Change the value of displaySplashScreen after the delay
            }
        }
    }


}


