package com.palkowski.friendupp

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.view.WindowCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.palkowski.friendupp.Groups.GroupInvitesViewModel
import com.palkowski.friendupp.Home.HomeViewModel
import com.palkowski.friendupp.Invites.InvitesViewModel
import com.palkowski.friendupp.ui.theme.FriendUppTheme
import com.palkowski.friendupp.Navigation.NavigationComponent
import com.palkowski.friendupp.Request.RequestViewModel
import com.palkowski.friendupp.di.*
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.model.UserData
import com.google.android.gms.location.*
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private val userViewModel by viewModels<UserViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val homeViewModel by viewModels<HomeViewModel>()
    private val activeUserViewModel by viewModels<ActiveUsersViewModel>()
    private val groupInvitesViewModel by viewModels<GroupInvitesViewModel>()
    private val invitesViewModel by viewModels<InvitesViewModel>()
    private val activityViewModel by viewModels<ActivityViewModel>()
    private val requestViewModel by viewModels<RequestViewModel>()
    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    private var notifactionLiskSet : MutableState<Boolean> = mutableStateOf(false)

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType=AppUpdateType.IMMEDIATE
    private val installStateUpdatedListener= InstallStateUpdatedListener { state->

        if (state.installStatus()==InstallStatus.DOWNLOADED) {
            Toast.makeText(applicationContext,"Download succesful. Restarting app",Toast.LENGTH_LONG).show()
            lifecycleScope.launch {
                delay(5.seconds)
                appUpdateManager.completeUpdate()
            }
        }

    }
    private fun checkForUpdates(){
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            info->
            val isUpdateAvailable = info.updateAvailability()==UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed= when (updateType){
                AppUpdateType.FLEXIBLE->info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                AppUpdateType.IMMEDIATE->info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                else->false
            }
            if (isUpdateAvailable && isUpdateAllowed){
                appUpdateManager.startUpdateFlowForResult(
                    info,updateType,this,123
                )
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123){
            if(resultCode!= RESULT_OK){
                Log.d("UpdateManager","update went wrong"+resultCode.toString())
            }
        }
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
        if(updateType==AppUpdateType.IMMEDIATE){
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info->
                if (info.updateAvailability()==UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    Toast.makeText(this,"trigered in progres",Toast.LENGTH_SHORT).show()
                    appUpdateManager.startUpdateFlowForResult(
                        info,updateType,this,123
                    )
                }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("notificationLinkSet", true)
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager=AppUpdateManagerFactory.create(applicationContext)
         appUpdateManager.registerListener(installStateUpdatedListener)
        checkForUpdates()
        val extras = intent.extras
        if (extras != null) {
            // Extract the values from the bundle
            val type = extras.getString("type")
            val id = extras.getString("id")

            val notificationLinkSet = savedInstanceState?.getBoolean("notificationLinkSet") ?: false

            if (!notificationLinkSet && !type.isNullOrEmpty() && !id.isNullOrEmpty()) {
                Log.d("Notificationdebug", "SETTINGSDATA")
                homeViewModel.setNotificationLink(type, id)
            }
        }

        if (authViewModel.isUserAuthenticated) {
            Log.d("MAINACTIVItyDebug", "auth")
            userViewModel.currentUserState.value.let { response ->
                when (response) {
                    is Response.Success -> {
                        UserData.user = response.data
                        Log.d("MAINACTIVItyDebug", response.data.toString())

                    }
                    else -> {}
                }
            }
        }
        /*try{
            //.......................................................................
            appUpdateManager = AppUpdateManagerFactory.create(this)
            appUpdateManager.registerListener(updateListener)
            checkForUpdate()
        }catch (e:Exception){
            commonLog("update01:Update e1 ${e.message}")
        }*/

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
                    deepLinkHasBeenSet =
                        true  // Set the flag to indicate that deep link has been set
                }
            }
            .addOnFailureListener(this) { e ->
                Log.w(ContentValues.TAG, "getDynamicLink:onF2ailure", e)
            }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            FriendUppTheme {
                NavigationComponent(
                    outputDirectory = outputDirectory,
                    executor = cameraExecutor,
                    authViewModel = authViewModel,
                    chatViewModel = chatViewModel,
                    userViewModel = userViewModel,
                    homeViewModel = homeViewModel,
                    activityViewModel = activityViewModel,
                    activeUserViewModel=activeUserViewModel,
                    invitesViewModel=invitesViewModel,
                    requestViewModel=requestViewModel,
                    groupInvitesViewModel=groupInvitesViewModel
                )
            }

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
            appUpdateManager.unregisterListener(installStateUpdatedListener)

    }

    fun checkLocationPermission(
        permissionGranted: () -> Unit,
        permissionDenied: () -> Unit,
    ) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted()
        } else {
            permissionDenied()

        }
    }
}

