package com.example.friendupp.Navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.friendupp.Login.LoginScreen
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.loginGraph(navController: NavController) {
    navigation(startDestination = "Login", route = "Welcome") {
        composable("Login") {
            LoginScreen(
                modifier = Modifier,
                goBack = { navController.popBackStack() },
                onLogin = { navController.navigate("main") })
        }
        composable("Splash_screen") { }
        composable("Registration") { }
    }
}

