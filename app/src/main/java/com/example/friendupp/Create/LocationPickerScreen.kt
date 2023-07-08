package com.example.friendupp.Create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.ActivityUi.ActivityState
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Components.BlueButton
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.RedButton
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Home.loadPublicActivities
import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.R
import com.example.friendupp.getBitmapDescriptor
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay

sealed class LocationPickerEvent {
    object GoBack : LocationPickerEvent()
    object DeleteLocation : LocationPickerEvent()
    class ConfirmLocation (val latLng: LatLng): LocationPickerEvent()
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LocationPickerScreen(onEvent: (LocationPickerEvent) -> Unit, activityState: ActivityState,mapViewModel: MapViewModel) {
    val flow = mapViewModel.currentLocation.collectAsState()
    var currentLocation by remember { mutableStateOf(LatLng(50.0, 20.0)) }

    flow.value.let { latLng ->
        if (latLng != null) {
            currentLocation = latLng
        }
    }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 11f)
    }
    BackHandler(true) {
        onEvent(LocationPickerEvent.GoBack)
    }

    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true,
                indoorLevelPickerEnabled = true
            )
        )
    }
    var isMapLoaded by remember { mutableStateOf(false) }

    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val configuration = LocalConfiguration.current

    val locationPicker = rememberSaveable {
        mutableStateOf<LatLng?>(null)
    }
    val DefaultLatLng= LatLng(0.0,0.0)
    if(activityState.location!=DefaultLatLng){
        locationPicker.value=activityState.location
    }


    val context= LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            Modifier.fillMaxSize(), cameraPositionState,
            properties = properties, onMapLoaded = {
                isMapLoaded = true
            }, onMapLongClick = { latLng ->
                locationPicker.value = latLng

            }, onMapClick = {
            },
            uiSettings = uiSettings
        ) {
            val bitmap = getBitmapDescriptor(context, R.drawable.ic_puck)
            locationPicker.value.let {
                if (it != null) {
                    MarkerInfoWindow(
                        zIndex = 0.5f,
                        state = MarkerState(
                            position = locationPicker.value!!
                        )

                    ) {


                    }
                }

            }
            currentLocation.let { latLng ->
                MarkerInfoWindow(
                    alpha = 0.8f,
                    state = MarkerState(
                        position = latLng
                    ),icon=bitmap
                ) {

                }
            }

        }

        var displayTextInfo by rememberSaveable {
            mutableStateOf(true)
        }
        LaunchedEffect(Unit) {
            delay(2000) // Wait for 2 seconds
            displayTextInfo = false // Set locationPicker value to null after 2 seconds
        }
        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .padding(horizontal = 24.dp)){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ButtonAdd(onClick = {
                    if(locationPicker.value!=null){
                        onEvent(LocationPickerEvent.DeleteLocation)
                        locationPicker.value = null
                    }
                                    }, icon = R.drawable.ic_x, disabled = locationPicker.value == null)
                Spacer(modifier = Modifier.width(24.dp))
                BlueButton(onClick = {
                    if(locationPicker.value!=null){
                        onEvent(LocationPickerEvent.ConfirmLocation(locationPicker.value!!))
                    }
              }, icon = R.drawable.ic_checkl, disabled = locationPicker.value == null)
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(top = 24.dp)
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ButtonAdd(
                    onClick = { onEvent(LocationPickerEvent.GoBack) },
                    icon = R.drawable.ic_back
                )

                AnimatedVisibility(
                    visible = displayTextInfo,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(SocialTheme.colors.uiBackground)
                            .padding(12.dp)
                    ){
                        Text(text = "Long click on map to place a marker", style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Light))
                    }
                }

            }

        }

    }
}