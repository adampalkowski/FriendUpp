package com.palkowski.friendupp.Navigation

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
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
import com.palkowski.friendupp.Groups.GroupInvitesViewModel
import com.palkowski.friendupp.Invites.InvitesViewModel
import com.palkowski.friendupp.Login.*
import com.palkowski.friendupp.di.AuthViewModel
import com.palkowski.friendupp.di.UserViewModel
import com.palkowski.friendupp.model.OneTapResponse
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.model.UserData
import com.palkowski.friendupp.ui.theme.SocialTheme
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import kotlinx.coroutines.delay

val TAG= "LOGINGRAPHDEBUG"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.loginGraph(navController: NavController,userViewModel:UserViewModel
                               ,authViewModel: AuthViewModel,    groupInvitesViewModel: GroupInvitesViewModel,invitesViewModel: InvitesViewModel
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
                                            invitesViewModel.getInvites(UserData.user!!.id)
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
                val context= LocalContext.current

            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                Log.d("SIGNINWITHGOOGLE", "Result code: ${result.resultCode}")
                Log.d("SIGNINWITHGOOGLE", "Data: ${result.data?.extras.toString()}")

                if (result.resultCode == RESULT_OK) {
                    // The user successfully signed in
                    Log.d("SIGNINWITHGOOGLE","result ok")

                    try {
                        val credentials = authViewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                        if (credentials.googleIdToken != null) {
                            // This is a Google credential, proceed with sign-in
                            val googleIdToken = credentials.googleIdToken
                            val googleCredentials = getCredential(googleIdToken, null)
                            authViewModel.signInWithGoogle(googleCredentials)
                        } else {
                            // This is not a Google credential, handle the error gracefully
                            Toast.makeText(context, "Please select a Google account.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (it: ApiException) {
                        print(it)
                    }
                } else if (result.resultCode == RESULT_CANCELED) {
                    // The user canceled the sign-in operation
                    Toast.makeText(context, "Sign-in canceled.", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle other result codes if needed
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
            TagPickerScreen(modifier=Modifier.safeDrawingPadding(), SetTags = {tags->
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

    var signInWithGoogleResponse = viewModel.signInWithGoogleResponse
    Log.d("GOOGLESIGNIN",signInWithGoogleResponse.toString())
    when(signInWithGoogleResponse) {
        is Response.Loading -> CircularProgressIndicator()
        is  Response.Success -> signInWithGoogleResponse.data.let { signedIn ->
                navigateToHomeScreen(true)
        }
        is  Response.Failure -> LaunchedEffect(Unit) {
            print(signInWithGoogleResponse.e)
        }
        else->{}
    }
}

