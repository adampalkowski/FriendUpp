package com.example.friendupp.Camera


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.getActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }



sealed class CameraEvent {
    object BackPressed : CameraEvent()
    object Download : CameraEvent()
    object Delete : CameraEvent()
    object AcceptPhoto : CameraEvent()
    object TakePhoto : CameraEvent()
    class SetPicture(val image_url: Uri) : CameraEvent()
    object RemovePhoto : CameraEvent()
    object DeletePhoto : CameraEvent()
    object GoBack : CameraEvent()
    object ImageSent : CameraEvent()
    object Flash : CameraEvent()
    object OpenGallery : CameraEvent()
    object Flip : CameraEvent()
}
private fun requestGalleryPermission(permissionsLauncher: ActivityResultLauncher<String>, context: Context) {
    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
    val permissionGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    if (!permissionGranted) {
        permissionsLauncher.launch(permission)
    } else {
        // Permission already granted
    }
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CameraView(
    onEvent: (CameraEvent) -> Unit,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri?) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    photoUri:Uri?
) {


    BackHandler(true) {
        onEvent(CameraEvent.GoBack)
    }


    //set status bar TRANSPARENT
    val flash_on = remember { mutableStateOf(false) }
    val systemUiController = rememberSystemUiController()
    DisposableEffect(Unit) {
        // Set the system bar colors to black when the composable is first displayed
        systemUiController.setStatusBarColor(color = Color.Black)
        systemUiController.setNavigationBarColor(color = Color.Black)

        onDispose {
            // Set the system bar colors back to default when the composable is removed
            systemUiController.setStatusBarColor(color = Color.White)
            systemUiController.setNavigationBarColor(color = Color.White)
        }
    }
    //lock screen orientation
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    // 1

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var cameraInfo by remember { mutableStateOf<CameraInfo?>(null) }


    // 2
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        cameraControl = camera.cameraControl
        cameraInfo = camera.cameraInfo
        preview.setSurfaceProvider(previewView.surfaceProvider)

    }
    flash_on.value.let {
        if (it) {
            cameraControl?.enableTorch(true)
        } else {
            cameraControl?.enableTorch(false)
        }
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                onImageCaptured(uri)
            }
        }
    }
    val permissionsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launcher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        } else {
            // Handle permission denied
        }
    }
    // create a mutable state to keep track of whether the icon should be shown or hidden
    val isIconVisible = remember { mutableStateOf(false) }
    var iconPosition by remember { mutableStateOf(Offset.Zero) }
    val zoomSensitivity = 0.6f
    var currentZoom by remember { mutableStateOf(1f) }
    val scaleGestureDetector = remember {
        ScaleGestureDetector(
            previewView.context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scale = cameraInfo?.zoomState?.value?.zoomRatio!! * detector.scaleFactor

                    // Update camera zoom level
                    cameraControl?.setZoomRatio(scale)

                    return true
                }
            }
        )
    }
    // 3
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.Black)) {
            if(photoUri==null){
                CameraTopBar(onEvent={topbarevent->
                    when(topbarevent){
                        is CameraEvent.OpenGallery->{
                            launcher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
                        }
                        is CameraEvent.GoBack->{onEvent(CameraEvent.GoBack)}
                        else->{}
                    }
                })
            }else{
                CameraTopBarDisplay(onEvent={topbarevent->
                    when(topbarevent){
                        is CameraEvent.Delete->{onImageCaptured(null)}
                        else->{}
                    }
                })
            }

            if(photoUri!=null){
                ImageDisplay(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .fillMaxHeight(fraction = 0.8f)
                    .clip(
                        RoundedCornerShape(24.dp)
                    ), photoUri =photoUri , onEvent ={} )

            }else{
                AndroidView(factory = { previewView },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .fillMaxHeight(fraction = 0.8f)
                        .clip(
                            RoundedCornerShape(24.dp)
                        ),
                    update = {
                        it.setOnTouchListener { _, event ->
                            scaleGestureDetector.onTouchEvent(event)
                            if (event.action == MotionEvent.ACTION_DOWN) {
                                isIconVisible.value = true
                                val factory = previewView.meteringPointFactory
                                val point = factory.createPoint(event.x, event.y)
                                val action = FocusMeteringAction.Builder(point!!).build()

                                cameraControl?.startFocusAndMetering(action)
                            }
                            true
                        }
                    }
                )

            }



            AnimatedVisibility(visible = photoUri!=null) {
                CameraBottomBarDisplay(onEvent={bottombarevent->
                    when(bottombarevent){
                        is CameraEvent.Delete->{
                            onImageCaptured(null)
                        }
                        is CameraEvent.Flip->{
                            if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                lensFacing = CameraSelector.LENS_FACING_FRONT
                            } else {
                                lensFacing = CameraSelector.LENS_FACING_BACK
                            }
                        }
                        is CameraEvent.TakePhoto->{
                            takePhoto(  filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                                imageCapture = imageCapture,
                                outputDirectory = outputDirectory,
                                executor = executor,
                                onImageCaptured = onImageCaptured,
                                onError = onError,frontCamera= lensFacing == CameraSelector.LENS_FACING_FRONT)
                        }
                        is CameraEvent.AcceptPhoto->{
                            onEvent(CameraEvent.AcceptPhoto)
                        }
                        else ->{}
                    }
                })
            }

            AnimatedVisibility(visible = photoUri==null) {
                CameraBottomBar(onEvent={bottombarevent->
                    when(bottombarevent){
                        is CameraEvent.Flash->{
                            flash_on.value=!flash_on.value
                        }
                        is CameraEvent.Flip->{
                            if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                lensFacing = CameraSelector.LENS_FACING_FRONT
                            } else {
                                lensFacing = CameraSelector.LENS_FACING_BACK
                            }
                        }
                        is CameraEvent.TakePhoto->{
                            takePhoto(  filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                                imageCapture = imageCapture,
                                outputDirectory = outputDirectory,
                                executor = executor,
                                onImageCaptured = onImageCaptured,
                                onError = onError,frontCamera=lensFacing == CameraSelector.LENS_FACING_FRONT)
                        }
                        else ->{}
                    }
                })
            }


        }


}

@Composable
fun CameraTopBar(onEvent: (CameraEvent) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 24.dp, vertical = 24.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onEvent(CameraEvent.GoBack) }) {
            Icon(modifier=Modifier.size(24.dp),painter = painterResource(id = R.drawable.ic_back), contentDescription =null,tint=Color.White)
        }
        val color = SocialTheme.colors.uiBorder.copy(0.2f)
        Spacer(modifier = Modifier.weight(1f))
        Row (modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = { onEvent(CameraEvent.OpenGallery) })
            .background(color)
            .padding(12.dp),verticalAlignment =Alignment.CenterVertically){
                Icon(painter = painterResource(id = R.drawable.ic_gallery), contentDescription = null, tint = Color.White)

            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Gallery", style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Normal), color = Color.White)
        }

    }
}

@Composable
fun CameraTopBarDisplay(onEvent: (CameraEvent) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 24.dp, vertical = 24.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onEvent(CameraEvent.Delete)}) {
            Icon(modifier=Modifier.size(24.dp),painter = painterResource(id = R.drawable.ic_x), contentDescription =null,tint=Color.White)

        }


    }
}
@Composable
fun CameraBottomBar(onEvent: (CameraEvent) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

        CameraButton(label = "Flash", icon = R.drawable.ic_flash, onClick = {
            onEvent(CameraEvent.Flash)

        })
        Spacer(modifier = Modifier.width(48.dp))
        CircleButton(){onEvent(CameraEvent.TakePhoto)}
        Spacer(modifier = Modifier.width(48.dp))
        CameraButton(label = "Flip", icon = R.drawable.ic_camera_flip, onClick = {
            onEvent(CameraEvent.Flip)
        })

    }
}

@Composable
fun CameraBottomBarDisplay(onEvent: (CameraEvent) -> Unit) {

    val context= LocalContext.current
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

        CameraButton(label = "Delete  ", icon = R.drawable.ic_delete, onClick = {
            onEvent(CameraEvent.Delete)

        })
        Spacer(modifier = Modifier.width(48.dp))
        AcceptButton(){

            onEvent(CameraEvent.AcceptPhoto)

        }
        Spacer(modifier = Modifier.width(48.dp))
        CameraButton(label = "Download", icon = R.drawable.ic_download, onClick = {
            onEvent(CameraEvent.Download)
        })

    }
}
@Composable
fun CameraButton(label:String,icon:Int,onClick: () -> Unit){

    val interactionSource = remember { MutableInteractionSource() }
    val rippleColor = SocialTheme.colors.iconPrimary.copy(0.2f)
        val color = SocialTheme.colors.uiBorder.copy(0.2f)
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Box(modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onClick)
                .background(color)
                .padding(12.dp)){
                Icon(painter = painterResource(id = icon), contentDescription =null, tint =Color.White )

            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = label, style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Normal), color = Color.White)
        }

}
@Composable
fun AcceptButton(onClick: () -> Unit) {
    val color = SocialTheme.colors.textInteractive

        Box(modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(color), contentAlignment = Alignment.Center
        ){
            Icon(modifier = Modifier.size(30.dp), painter = painterResource(id = R.drawable.ic_done), contentDescription =null, tint = Color.White )
        }

}
@Composable
fun CircleButton(onClick: () -> Unit) {
    val color = Color.White

    Box(modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .clickable(onClick = onClick)
        .border(BorderStroke(2.dp, color), shape = CircleShape), contentAlignment = Alignment.Center ){
        Box(modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(color),
         ){

        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImageDisplay(
    modifier: Modifier, photoUri: Uri, onEvent: (CameraEvent) -> Unit,
   displayPhoto: Boolean=false
) {

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = androidx.compose.ui.graphics.Color.Black)
        ) {
            Image(
                painter = rememberImagePainter(photoUri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CameraButton(
    onClick: () -> Unit,
    icon: Int,
    iconTint:androidx.compose.ui.graphics.Color,
    textColor:androidx.compose.ui.graphics.Color=androidx.compose.ui.graphics.Color.White,
    backgroundColor:androidx.compose.ui.graphics.Color=androidx.compose.ui.graphics.Color.Transparent,
    color: androidx.compose.ui.graphics.Color,
    text: String
) {
    Card(
        modifier = Modifier,
        onClick = onClick,
        border = BorderStroke(
            1.dp,
            color
        ),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = backgroundColor,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                tint = iconTint,
                painter = painterResource(id = icon),
                contentDescription = "send picture"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = textColor,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )

        }
    }
}

private fun takePhoto(
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    frontCamera:Boolean
) {
    val metadata = ImageCapture.Metadata()
    metadata.isReversedHorizontal =frontCamera

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(
            filenameFormat,
            Locale.getDefault()
        ).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile) .setMetadata(metadata).build()

    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            Log.e("kilo", "Take photo error:", exception)
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = Uri.fromFile(photoFile)
            onImageCaptured(savedUri)
        }
    })
}

