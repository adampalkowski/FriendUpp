package com.example.friendupp.Navigation

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.friendupp.*
import com.example.friendupp.ChatUi.*
import com.example.friendupp.Drawer.drawerGraph
import com.example.friendupp.bottomBar.BottomBar
import com.example.friendupp.bottomBar.BottomBarOption
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.AuthViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.SocialTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executor

fun customShape() =  object : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(0f,0f,size.width/1.5f /* width */, size.height /* height */))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationComponent(navController: NavHostController = rememberAnimatedNavController(), outputDirectory: File,
                        executor: Executor) {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val bottomDestinations = listOf("Home", "Chat", "Map", "Profile")
    val currentBackStackEntry by navController.currentBackStackEntryAsState()


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


        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        bottomBar = {

            if (currentBackStackEntry != null && bottomDestinations.contains(navController.currentDestination?.route)) {
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

            val backHandler = BackHandler(onBack = {
                if (scaffoldState.drawerState.isOpen) {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            })

            val activityViewModel: ActivityViewModel = hiltViewModel()
            val userViewModel : UserViewModel = hiltViewModel()
            val authViewModel : AuthViewModel = hiltViewModel()

            val currentActivity = remember { mutableStateOf(Activity()) }
            //get the front page activities for user ->friends activities ?? if not exist then public
            //called on each homescreen recompose
            AnimatedNavHost(navController, startDestination = "Welcome") {
                loginGraph(navController,userViewModel, authViewModel = authViewModel)
                mainGraph(navController, openDrawer = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.open()
                    }
                },activityViewModel)
                chatGraph(navController)
                profileGraph(navController, outputDirectory =outputDirectory , executor =executor)
                createGraph(navController,currentActivity, outputDirectory =outputDirectory , executor =executor,activityViewModel=activityViewModel)
                settingsGraph(navController,authViewModel,userViewModel)
                drawerGraph(navController)
                groupGraph(navController)
                cameraGraph(navController, outputDirectory =outputDirectory , executor =executor )
            }
        }

    }


}


