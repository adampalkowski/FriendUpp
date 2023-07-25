package com.example.friendupp.Navigation

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import android.window.SplashScreen
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.friendupp.Create.CreateButton
import com.example.friendupp.Groups.GroupInvitesViewModel
import com.example.friendupp.Login.*
import com.example.friendupp.R
import com.example.friendupp.di.AuthViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.OneTapResponse
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.SocialTheme
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import kotlinx.coroutines.delay
import okhttp3.internal.wait
import java.time.Duration

val TAG= "LOGINGRAPHDEBUG"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.loginGraph(navController: NavController,userViewModel:UserViewModel
                               ,authViewModel: AuthViewModel,    groupInvitesViewModel: GroupInvitesViewModel
) {

    navigation(startDestination = "Login", route = "Welcome") {

        composable("Login") {
            /*
            check if user is already signed in
             */
            val loginFLow = authViewModel.loginFlow.collectAsState()
            val userFlow = userViewModel.userValidation.collectAsState()

            /* CHECK IF USER ALREADY SIGNED IN*/
            if(authViewModel.currentUser!=null){
                if(authViewModel.isUserAuthenticated){
                    /*AUTH IS THERE*/
                    userViewModel.validateUser(authViewModel.currentUser!!)
                    /*VALIDATE IT*/
                    userFlow.value.let {
                            validationResponse ->
                        when(validationResponse){
                            is Response.Success->{
                                if(validationResponse.data){
                                    LaunchedEffect(Unit){
                                        /*succesfully validated*/
                                        if(UserData.user!=null){
                                            groupInvitesViewModel.getGroupInvites(UserData.user!!.id)

                                        }
                                       navController.navigate("Home")
                                    }


                                }else{
                                    /*user doestn have username assigned*/
                                    LaunchedEffect(Unit){
                                        Log.d(TAG,"No username")
                                        userViewModel.resetUserValidation()
                                        navController.navigate("pickUsername")
                                    }
                                }

                            }
                            is Response.Failure->{
                            }
                            else->{}
                        }
                    }
                }
            }


            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    try {
                        val credentials = authViewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                        val googleIdToken = credentials.googleIdToken
                        val googleCredentials = getCredential(googleIdToken, null)
                        authViewModel.signInWithGoogle(googleCredentials)
                    } catch (it: ApiException) {
                        print(it)
                    }
                }
            }

            fun launch(signInResult: BeginSignInResult) {
                val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
                launcher.launch(intent)
            }

            authViewModel.oneTapSignInResponse.let {response->
                when(val oneTapSignInResponse = response) {
                    is OneTapResponse.Loading -> CircularProgressIndicator()
                    is OneTapResponse.Success -> oneTapSignInResponse.data?.let {
                        Log.d("ONETAP","RESponse")
                        Log.d("ONETAP",it.toString())
                        LaunchedEffect(it) {
                            Log.d("ONETAP","launch")
                            launch(it)
                        }
                    }
                    is OneTapResponse.Failure -> LaunchedEffect(Unit) {
                        print(oneTapSignInResponse.e)
                    }
                    else->{}
                }
            }
            SignInWithGoogle(
                navigateToHomeScreen = { signedIn ->
                    if (signedIn) {
                       navController.navigate("Home")
                    }
                }
            )
            Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()){
                LoginScreen(
                    modifier = Modifier,
                    onEvent = {event->
                        when(event) {
                            is LoginEvents.GoBack->{
                                navController.navigate("Login")
                            }
                            is LoginEvents.Login->{
                                authViewModel.signin(event.email,event.password)
                            }
                            is LoginEvents.Register->{
                                navController.navigate("Registration")
                            }
                            is LoginEvents.GoToHome->{
                                navController.navigate("Home")
                            }
                            is LoginEvents.LoginWithGoogle->{
                                authViewModel.oneTapSignIn()
                            }
                            else->{}
                        }
                    },authViewModel,
                )

                /* wait for login success then go to validate*/
                loginFLow.value?.let {
                    when(it){
                        is Response.Success->{
                            userViewModel.validateUser(it.data)
                            userFlow.value.let {
                                    validationResponse ->
                                when(validationResponse){
                                    is Response.Success->{
                                        if(validationResponse.data){
                                            LaunchedEffect(Unit){
                                                /*sucessfully validated user */
                                                Log.d(TAG,"Login success validation")

                                                navController.navigate("Home")
                                            }
                                        }else{
                                            /*user doestn have username assigned*/
                                            LaunchedEffect(Unit){
                                                Log.d(TAG,"Login no username")
                                                userViewModel.resetUserValidation()
                                                navController.navigate("pickUsername")
                                            }
                                        }
                                    }
                                    is Response.Failure->{
                                        val context = LocalContext.current
                                        userViewModel.resetUserValidation()
                                        Toast.makeText(context,"Failed to validate user code 102",
                                            Toast.LENGTH_LONG).show()
                                        /*user not inDB*/
                                    }
                                    else->{}
                                }
                            }

                        }
                        is Response.Loading ->{
                            CircularProgressIndicator(Modifier.align(Alignment.Center), color = SocialTheme.colors.textPrimary)
                        }
                        is Response.Failure ->{
                            val context = LocalContext.current
                            Toast.makeText(context,"Failed to login, account doesn't exist, check if input email and password are correct.",Toast.LENGTH_LONG).show()
                            authViewModel.resetLoginFlow()
                        }
                        else->{}
                    }
                }
            }

        }
        composable("Splash_screen", enterTransition = {
            when (initialState.destination.route) {
                "Home" ->
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                "Map" ->
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                "Profile" ->
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Right,
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
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Profile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
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
                    "Map" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    "Profile" ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Home" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Map" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    "Profile" ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    else -> null
                }
            }){
            val splash_screen_delay:Long=1000
            /*
         check if user is already signed in
          */
            val loginFLow = authViewModel.loginFlow.collectAsState()
            val userFlow = userViewModel.userValidation.collectAsState()

            /* CHECK IF USER ALREADY SIGNED IN*/
            if(authViewModel.currentUser!=null){
                if(authViewModel.isUserAuthenticated){
                    /*AUTH IS THERE*/
                    userViewModel.validateUser(authViewModel.currentUser!!)
                    /*VALIDATE IT*/
                    userFlow.value.let {
                            validationResponse ->
                        when(validationResponse){
                            is Response.Success->{
                                if(validationResponse.data){
                                    LaunchedEffect(Unit){
                                        /*succesfully validated*/
                                        delay(splash_screen_delay) // Delay for 1 second (1000 milliseconds) to display splash screen

                                        navController.navigate("Home")
                                    }


                                }else{
                                    /*user doestn have username assigned*/
                                    LaunchedEffect(Unit){
                                        Log.d(TAG,"No username")
                                        userViewModel.resetUserValidation()
                                        delay(splash_screen_delay) // Delay for 1 second (1000 milliseconds) to display splash screen

                                        navController.navigate("pickUsername")
                                    }
                                }

                            }
                            is Response.Failure->{
                            }
                            else->{}
                        }
                    }
                }
            }
            /* wait for login success then go to validate*/
            loginFLow.value?.let {
                when(it){
                    is Response.Success->{
                        userViewModel.validateUser(it.data)
                        userFlow.value.let {
                                validationResponse ->
                            when(validationResponse){
                                is Response.Success->{
                                    if(validationResponse.data){
                                        LaunchedEffect(Unit){
                                            /*sucessfully validated user */
                                            Log.d(TAG,"Login success validation")
                                                delay(splash_screen_delay) // Delay for 1 second (1000 milliseconds) to display splash screen
                                            navController.navigate("Home")
                                        }
                                    }else{
                                        /*user doestn have username assigned*/
                                        LaunchedEffect(Unit){
                                            Log.d(TAG,"Login no username")
                                            userViewModel.resetUserValidation()
                                            delay(splash_screen_delay) // Delay for 1 second (1000 milliseconds) to display splash screen

                                            navController.navigate("pickUsername")
                                        }
                                    }
                                }
                                is Response.Failure->{
                                    val context = LocalContext.current
                                    userViewModel.resetUserValidation()
                                    Toast.makeText(context,"Failed to validate user code 102",
                                        Toast.LENGTH_LONG).show()
                                    /*user not inDB*/
                                }
                                else->{}
                            }
                        }

                    }
                    is Response.Loading ->{
                        CircularProgressIndicator()
                    }
                    is Response.Failure ->{
                        val context = LocalContext.current
                        Toast.makeText(context,"Failed to login, account doesn't exist, check if input email and password are correct.",Toast.LENGTH_LONG).show()
                        authViewModel.resetLoginFlow()
                    }
                    else->{}
                }
            }

            SplashScreen(splash_screen_delay)


        }
        composable("TagPicker") {


            Log.d(TAG,"TAG PICKER")
            TagPickerScreen(modifier=Modifier, SetTags = {tags->
                 userViewModel.setUserTags(authViewModel.currentUser!!.uid,tags)

            })
            userViewModel.isUserTagsAdded.value.let {
                when(it){
                    is Response.Success->{
                        navController.navigate("Home")
                    }
                    else->{}
                }
            }


        }
        composable("pickUsername") {
            val userFlow = userViewModel.userValidation.collectAsState()

            PickUsernameScreen(userViewModel = userViewModel,authViewModel=authViewModel, onEvent = {
                event->
                when (event) {
                    is PickUserEvent.GoToHome -> {
                        userViewModel.validateUser(authViewModel.currentUser!!)
                    }
                    is PickUserEvent.NavigateBack -> {
                        navController.popBackStack()
                    }

                }
            })
            userFlow.value.let {
                    validationResponse ->
                when(validationResponse){
                    is Response.Success->{
                        if(validationResponse.data){
                            LaunchedEffect(Unit){
                                /*sucessfully validated user */
                                Log.d(TAG,"Pick usernaem success validation")


                                /* if user doesnt have tags display a tag picker screen*/
                                val user =UserData.user
                                if (user!=null){

                                    if (user.tags.isEmpty()||user.tags.size<1){
                                        navController.navigate("TagPicker")
                                    }else{
                                        navController.navigate("Home")

                                    }

                                }else{
                                    navController.navigate("Welcome")

                                }

                            }
                        }else{
                            /*user doestn have username assigned*/
                            LaunchedEffect(Unit){
                                Log.d(TAG,"Pick usernaem invalid validation")
                                userViewModel.resetUserValidation()
                            }
                        }
                    }
                    is Response.Failure->{
                        val context = LocalContext.current
                        userViewModel.resetUserValidation()
                        Toast.makeText(context,"Failed to validate user code 102",
                            Toast.LENGTH_LONG).show()
                        /*user not inDB*/
                    }
                    else->{}
                }
            }

        }
        composable("Registration") {

            val singupFlow = authViewModel.signupFlow.collectAsState()
            val userFlow = userViewModel.userValidation.collectAsState()

            RegisterScreen(
                modifier=Modifier.safeDrawingPadding(),
                onEvent={event->
                    when(event){
                        is RegisterEvents.GoBack->{
                            navController.navigate("Login")
                        }
                        is RegisterEvents.Register->{
                            authViewModel.signup(event.fullname,event.email,event.password)
                        }
                    }
                }
            )


            singupFlow.value.let {
                when(it){
                    is Response.Success->{
                        if(it.data!=null){
                            userViewModel.validateUser(it.data)
                            userFlow.value.let {
                                    validationResponse ->
                                when(validationResponse){
                                    is Response.Success->{
                                        if(validationResponse.data){
                                            LaunchedEffect(Unit){
                                                /*sucessfully validated user */
                                                Log.d(TAG,"Register success validation")
                                                navController.navigate("Home")
                                            }
                                        }else{
                                            /*user doestn have username assigned*/
                                            LaunchedEffect(Unit){
                                                Log.d(TAG,"Register invalid usernaem")
                                                userViewModel.resetUserValidation()

                                                navController.navigate("pickUsername")
                                            }
                                        }

                                    }
                                    is Response.Failure->{
                                        val context = LocalContext.current
                                        userViewModel.resetUserValidation()
                                        Toast.makeText(context,"Failed to validate user code 102",
                                            Toast.LENGTH_LONG).show()
                                        /*user not inDB*/

                                    }
                                    else->{}
                                }
                            }
                        } else{
                            val context = LocalContext.current
                            Toast.makeText(context,"Fireauth user null",
                                Toast.LENGTH_LONG).show()
                        }

                    }
                    is Response.Loading ->{
                        CircularProgressIndicator()
                    }
                    is Response.Failure ->{
                        val context = LocalContext.current
                        Toast.makeText(context,it.e.message, Toast.LENGTH_LONG).show()
                    }
                    else->{}
                }
            }
        }
    }
}


@Composable
fun SignInWithGoogle(
    viewModel: AuthViewModel = hiltViewModel(),
    navigateToHomeScreen: (signedIn: Boolean) -> Unit
) {
    when(val signInWithGoogleResponse = viewModel.signInWithGoogleResponse) {
        is Response.Loading -> CircularProgressIndicator()
        is  Response.Success -> signInWithGoogleResponse.data?.let { signedIn ->
            LaunchedEffect(signedIn) {
                navigateToHomeScreen(signedIn)
            }
        }
        is  Response.Failure -> LaunchedEffect(Unit) {
            print(signInWithGoogleResponse.e)
        }
    }
}

