package com.example.friendupp.Navigation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toFile
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Camera.CameraEvent
import com.example.friendupp.Camera.CameraView
import com.example.friendupp.Create.CreateEvents
import com.example.friendupp.di.ChatViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.cameraGraph(navController: NavController, outputDirectory: File,
                                executor: Executor,chatViewModel: ChatViewModel
) {
    navigation(startDestination = "Camera", route = "CameraGraph") {

        composable( "Camera/{id}",
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
                                chatViewModel.updateActivityImage(id,photo)
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
    }
}
