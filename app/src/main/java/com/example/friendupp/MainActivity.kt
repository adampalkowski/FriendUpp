package com.example.friendupp

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import com.example.friendupp.Categories.Category
import com.example.friendupp.ui.theme.FriendUppTheme
import com.example.friendupp.Navigation.NavigationComponent
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.User
import com.example.friendupp.model.UserData
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            shouldShowCamera.value = true
        } else {
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserData.user=User(
            name = "John Doe",
            username = "johndoe",
            email = "johndoe@example.com",
            id = "123456789",
            pictureUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBOn9shF9kc5vPqgA30DXU0prx5-aYyh28Rw&usqp=CAU",
            biography = "Hello, I'm John Doe!",
            location = "New York",
            blocked_ids = ArrayList(),
            friends_ids = HashMap(),
            invited_ids = ArrayList(),
            user_requests = ArrayList(),
            activities = ArrayList(),
            activitiesCreated = 10,
            usersReached = 100,
            tags = arrayListOf(Category.SOCIAL.label, Category.CREATIVE.label)
            , accountCreateTime = ""
        )


        requestCameraPermission()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        setContent {
            FriendUppTheme {
                NavigationComponent(outputDirectory =outputDirectory, executor = cameraExecutor )
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


/* // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color =                                 SocialTheme.colors.uiBackground

                ) {
                    val configuration = LocalConfiguration.current
                    var expandActivity by remember { mutableStateOf(false) }
                    var expandedActivity by remember { mutableStateOf<String?>(null) }
                    var expandedUrl by remember { mutableStateOf<String?>(null) }
                    var expandedDescription by remember { mutableStateOf<String?>(null) }
                    val screenHeight = configuration.screenHeightDp.dp
                    val screenWidth = configuration.screenWidthDp.dp
                    var screen by rememberSaveable { mutableStateOf(BottomBarOption.Home) }
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                SocialTheme.colors.uiBackground
                            )
                    ) {
                        Column {
                            when (screen) {
                                BottomBarOption.Profile -> {
                                    LoginScreen(
                                        modifier = Modifier.weight(1f),
                                        goBack = { screen = BottomBarOption.Home })
                                }
                                BottomBarOption.Map -> {

                                }
                                BottomBarOption.Create -> {
                                    CreateScreen(    modifier = Modifier.weight(1f),
                                        goBack = { screen = BottomBarOption.Home})
                                }
                                BottomBarOption.Chat -> {
                                    var chatVis by remember {
                                        mutableStateOf(false)
                                    }
                                    if(!chatVis){
                                        ChatCollection(   modifier = Modifier.weight(1f),
                                            goBack = { screen = BottomBarOption.Home }, onClick = {chatVis=!chatVis})
                                    }else{

                                        ChatContent(
                                            modifier = Modifier.weight(1f),
                                            goBack = {chatVis=!chatVis})
                                    }

                                }
                                BottomBarOption.Home -> {
                                    HomeContent(
                                        modifier = Modifier.weight(1f),
                                        onExpand = { expandActivity = !expandActivity },

                                    )
                                }
                            }


                        }
                        if (screen != BottomBarOption.Chat && screen != BottomBarOption.Profile && screen != BottomBarOption.Create) {
                            BottomBar(
                                modifier = Modifier.align(Alignment.BottomCenter),
                                onClick = { option ->
                                    when (option) {
                                        BottomBarOption.Profile -> {
                                            screen = BottomBarOption.Profile
                                        }
                                        BottomBarOption.Map -> {
                                            screen = BottomBarOption.Map
                                        }
                                        BottomBarOption.Create -> {
                                            screen = BottomBarOption.Create
                                        }
                                        BottomBarOption.Chat -> {
                                            screen = BottomBarOption.Chat
                                        }
                                        BottomBarOption.Home -> {
                                            screen = BottomBarOption.Home
                                        }
                                    }

                                },
                                selectedOption = screen
                            )
                        }

                    }
                    Column() {
                        AnimatedVisibility(
                            visible = expandActivity,
                            enter = scaleIn(spring(), 0.0f),
                            exit = scaleOut(tween(200), 0.5f)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {

                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data("https://developer.android.com/static/images/jetpack/compose/graphics-sourceimagesmall.jpg")
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = screenHeight / 2)
                                        .aspectRatio(1f)
                                )
                                Box(modifier = Modifier.padding(start = 24.dp, top = 24.dp)) {
                                    TransparentButton(onClick = {expandActivity=!expandActivity})
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = expandActivity,
                            enter = slideInVertically(tween(300), initialOffsetY = { it }),
                            exit = slideOutVertically(tween(200), targetOffsetY = { 2 * it })
                        ) {
                            ActivityPreview(onExpand = { expandActivity = !expandActivity })
                        }
                    }

                }
            }*/
