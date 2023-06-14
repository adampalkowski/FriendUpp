package com.example.friendupp

import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.friendupp.Categories.Category
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.ui.theme.FriendUppTheme
import com.example.friendupp.Navigation.NavigationComponent
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.AuthViewModel
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Response
import com.example.friendupp.model.User
import com.example.friendupp.model.UserData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private val userViewModel by viewModels<UserViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val homeViewModel by viewModels<HomeViewModel>()
    private val activityViewModel by viewModels<ActivityViewModel>()
    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val _granted_permission = MutableStateFlow<Boolean>(false)

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                Log.d("Mapfragment","location callback")
                val location = locationList.last()
                activityViewModel.setLocation(LatLng(location.latitude, location.longitude))
            }
        }
    }
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }
    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            shouldShowCamera.value = true
        } else {
        }
    }

    override fun onRestart() {
        super.onRestart()
    }
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserData.user= User()


        if(authViewModel.isUserAuthenticated)
        {
            Log.d("MAINACTIVItyDebug","auth")
            userViewModel.currentUserState.value.let {response->
            when(response){
                is Response.Success->{
                    UserData.user=response.data
                    Log.d("MAINACTIVItyDebug",response.data.toString())

                }
                else->{}
            }
            }
        }
        requestCameraPermission()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this.applicationContext!!)
        if (ActivityCompat.checkSelfPermission(
                this.applicationContext!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.applicationContext!!,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient!!.requestLocationUpdates(
                LocationRequest(),
                locationCallback,
                Looper.getMainLooper()
            )

        }

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.\

                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }

            }
        when {
            ContextCompat.checkSelfPermission(
                this.applicationContext!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                _granted_permission.value=true
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.

            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        var deepLinkHasBeenSet = false  // Flag variable to track if deep link has been set

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                val deepLink: Uri? = pendingDynamicLinkData?.link

                if (deepLink != null && !deepLinkHasBeenSet) {
                    homeViewModel.setDeepLink(deepLink)
                    deepLinkHasBeenSet = true  // Set the flag to indicate that deep link has been set
                }
            }
            .addOnFailureListener(this) { e ->
                Log.w(ContentValues.TAG, "getDynamicLink:onFailure", e)
            }

        setContent {
            FriendUppTheme {
                NavigationComponent(outputDirectory =outputDirectory, executor = cameraExecutor,
                    authViewModel=authViewModel, chatViewModel = chatViewModel,
                userViewModel = userViewModel,homeViewModel=homeViewModel, activityViewModel = activityViewModel)
            }

        }
    }


    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA

            ) -> {}

            else -> requestPermissionLauncher.launch(                android.Manifest.permission.CAMERA
            )
        }
    }

    private fun handleImageCapture(uri: Uri) {
        shouldShowCamera.value = false

        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

