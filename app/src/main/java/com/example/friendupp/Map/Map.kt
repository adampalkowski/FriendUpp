package com.example.friendupp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.friendupp.Home.SocialButtonNormal
import com.example.friendupp.Home.loadPublicActivities
import com.example.friendupp.Map.MapActivityItem
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*


sealed class MapEvent {
    class PreviewActivity(val activity: Activity) : MapEvent()
    class GoToProfile(val id: String) : MapEvent()
    object GetMorePublicActivities : MapEvent()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreen(

    onEvent: (MapEvent) -> Unit,
    currentLocation:LatLng,
    publicActivitiesResponse:Response<List<Activity>>
) {
    val publicActivities = remember { mutableStateListOf<Activity>() }
    val morePublicActivities = remember { mutableStateListOf<Activity>() }
    var publicActivitiesExist = remember { mutableStateOf(false) }

    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }


    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 11f)
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
    var markerOptions by remember { mutableStateOf<MarkerOptions?>(null) }

    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val activityToScroll = remember { mutableStateOf<Activity?>(null) }

    Box(modifier = Modifier.consumeWindowInsets(WindowInsets.ime)) {




        var hideActivities by rememberSaveable {
            mutableStateOf(true)
        }

        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val icon= if(hideActivities){
            painterResource(id = R.drawable.ic_down)}else{
            painterResource(id = R.drawable.ic_up)}
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 40.dp,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetBackgroundColor = SocialTheme.colors.uiBackground,
            sheetContentColor = SocialTheme.colors.uiBackground,
            backgroundColor = SocialTheme.colors.uiBackground,
            sheetContent = {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(SocialTheme.colors.uiBorder)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when (publicActivitiesResponse) {
                        is Response.Success -> {
                            MapActivitiesDisplay(
                                modifier = Modifier,
                                publicActivities = publicActivitiesResponse.data,
                                CenterOnPoint = { latLng ->
                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(latLng, 13f)

                                },
                                activityToScroll = activityToScroll,
                                onEvent = onEvent,
                                hideActivities = hideActivities,
                                GetMoreActivities = { onEvent(MapEvent.GetMorePublicActivities) })
                        }
                        is Response.Loading -> {
                            androidx.compose.material3.CircularProgressIndicator(color = SocialTheme.colors.textPrimary)
                        }
                        else -> {}
                    }

                }

            }) { innerPadding ->
            GoogleMap(
                Modifier.fillMaxSize(), cameraPositionState,
                properties = properties, onMapLoaded = {
                    isMapLoaded = true
                }, onMapLongClick = { latlng ->
                    selectedLocation = latlng
                }, onMapClick = {
                },
                uiSettings = uiSettings
            ) {

                val bitmap = getBitmapDescriptor(context, R.drawable.ic_puck)
                selectedLocation.let { selectedLatLng ->
                    if (selectedLatLng != null) {

                        MarkerInfoWindow(
                            state = MarkerState(
                                position = selectedLatLng
                            )
                        ) {

                        }
                    }
                }
                when(publicActivitiesResponse){
                    is Response.Success->{
                        publicActivitiesResponse.data.forEach { activity ->
                            if (activity.lat != null) {
                                val latLng = LatLng(activity.lat!!, activity.lng!!)
                                MarkerInfoWindow(
                                    alpha = 0.8f,
                                    state = MarkerState(
                                        position = latLng,
                                    )
                                ) {
                                    activityToScroll.value = activity
                                }
                            }

                        }
                    }
                    else->{}
                }


                currentLocation.let { latLng ->
                    MarkerInfoWindow(
                        alpha = 0.8f,
                        state = MarkerState(
                            position = latLng
                        ), icon = bitmap
                    ) {

                    }
                }

            }

        }



        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(24.dp)
                .padding(top = 24.dp)
        ) {

            Card(elevation = 5.dp) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SocialTheme.colors.uiBackground)
                        .clickable(onClick = {
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(currentLocation, 11f)
                        }), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_my_location),
                        contentDescription = null,
                        tint = SocialTheme.colors.textInteractive
                    )
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            Card(elevation = 5.dp) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SocialTheme.colors.uiBackground)
                        .clickable(onClick = {
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(currentLocation, 11f)
                        }), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_refresh_map),
                        contentDescription = null,
                        tint =  SocialTheme.colors.textInteractive

                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(elevation = 5.dp, shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SocialTheme.colors.textInteractive)
                        .clickable(onClick = {
                            hideActivities = !hideActivities
                        }), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter =icon,
                        contentDescription = null,
                        tint =  Color.White
                    )
                }
            }
/*
            Spacer(modifier = Modifier.weight(1f))
            Card(elevation = 5.dp) {
                SocialButtonNormal(
                    icon = R.drawable.ic_filte_300,
                    onClick = { },
                    false
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Card(elevation = 5.dp) {
                SocialButtonNormal(
                    icon = R.drawable.ic_calendar_300,
                    onClick = { },
                    false
                )


            }
*/
        }


    }


}

@Composable
fun MapActivitiesDisplay(
    activityToScroll: MutableState<Activity?>,
    modifier: Modifier, publicActivities: List<Activity>,
    CenterOnPoint: (LatLng) -> Unit, onEvent: (MapEvent) -> Unit, GetMoreActivities: () -> Unit,hideActivities:Boolean
) {

    val lazyListState = rememberLazyListState()
    Column(horizontalAlignment = Alignment.Start,                modifier = modifier.fillMaxWidth(),
    ) {
        if(!hideActivities){
            Spacer(modifier = Modifier.height(24.dp))
        }
        AnimatedVisibility(visible = hideActivities) {
            LazyRow(
                state = lazyListState
            ) {
                items(publicActivities) { activity ->
                        MapActivityItem(onClick = {
                            val latLng = LatLng(activity.lat!!, activity.lng!!)

                            CenterOnPoint(latLng)
                        }, activity = activity, onEvent = onEvent)
                        Spacer(modifier = Modifier.width(16.dp))

                }
                item{
                    Spacer(modifier = Modifier.width(80.dp))
                }
                item{
                    GetMoreActivities()
                }
            }
        }
    Spacer(modifier = Modifier.height(12.dp))
    }
    // Trigger scrolling when the activityToScroll value changes
    LaunchedEffect(activityToScroll.value) {
        val index = publicActivities.indexOf(activityToScroll.value)
        if (index != -1) {
            lazyListState.animateScrollToItem(index)
        }
    }
}


fun getBitmapDescriptor(context: Context, id: Int): BitmapDescriptor? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val vectorDrawable = getDrawable(context, id) as VectorDrawable
        val h = vectorDrawable.intrinsicHeight
        val w = vectorDrawable.intrinsicWidth
        vectorDrawable.setBounds(0, 0, w, h)
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        vectorDrawable.draw(canvas)
        BitmapDescriptorFactory.fromBitmap(bm)
    } else {
        BitmapDescriptorFactory.fromResource(id)
    }
}

fun loadIcon(
    context: Context,
    url: String?,
    placeHolder: Int,
): BitmapDescriptor? {
    try {
        var bitmap: Bitmap? = null
        Glide.with(context)
            .asBitmap()
            .load(url)
            .error(placeHolder)
            // to show a default icon in case of any errors
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?,
                ) {

                    bitmap = resource

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
        return BitmapDescriptorFactory.fromBitmap(bitmap!!)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreenTablet(
    currentLocation:LatLng,
    onEvent: (MapEvent) -> Unit,    publicActivitiesResponse:Response<List<Activity>>
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 11f)
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
    var markerOptions by remember { mutableStateOf<MarkerOptions?>(null) }

    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val activityToScroll = remember { mutableStateOf<Activity?>(null) }

    Box(modifier = Modifier.consumeWindowInsets(WindowInsets.ime)) {


        var hideActivities by rememberSaveable {
            mutableStateOf(true)
        }

        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val icon = if (hideActivities) {
            painterResource(id = R.drawable.ic_down)
        } else {
            painterResource(id = R.drawable.ic_up)
        }
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 40.dp,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetBackgroundColor = SocialTheme.colors.uiBackground,
            sheetContentColor = SocialTheme.colors.uiBackground,
            backgroundColor = SocialTheme.colors.uiBackground,
            sheetContent = {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(SocialTheme.colors.uiBorder)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when (publicActivitiesResponse) {
                        is Response.Success -> {
                            MapActivitiesDisplay(
                                modifier = Modifier,
                                publicActivities = publicActivitiesResponse.data,
                                CenterOnPoint = { latLng ->
                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(latLng, 13f)

                                },
                                activityToScroll = activityToScroll,
                                onEvent = onEvent,
                                hideActivities = hideActivities,
                                GetMoreActivities = { onEvent(MapEvent.GetMorePublicActivities) })
                        }
                        is Response.Loading -> {
                            androidx.compose.material3.CircularProgressIndicator(color = SocialTheme.colors.textPrimary)
                        }
                        else -> {}
                    }

                }


            }) { innerPadding ->

            Column() {


                GoogleMap(
                    Modifier.fillMaxWidth().weight(1f), cameraPositionState,
                    properties = properties, onMapLoaded = {
                        isMapLoaded = true
                    }, onMapLongClick = { latlng ->
                        selectedLocation = latlng
                    }, onMapClick = {
                    },
                    uiSettings = uiSettings
                ) {

                    val bitmap = getBitmapDescriptor(context, R.drawable.ic_puck)
                    selectedLocation.let { selectedLatLng ->
                        if (selectedLatLng != null) {

                            MarkerInfoWindow(
                                state = MarkerState(
                                    position = selectedLatLng
                                )
                            ) {

                            }
                        }
                    }
                    when (publicActivitiesResponse) {
                        is Response.Success -> {
                            publicActivitiesResponse.data.forEach { activity ->
                                if (activity.lat != null) {
                                    val latLng = LatLng(activity.lat!!, activity.lng!!)
                                    MarkerInfoWindow(
                                        alpha = 0.8f,
                                        state = MarkerState(
                                            position = latLng,
                                        )
                                    ) {
                                        activityToScroll.value = activity
                                    }
                                }

                            }
                        }
                        else -> {}
                    }


                    currentLocation.let { latLng ->
                        MarkerInfoWindow(
                            alpha = 0.8f,
                            state = MarkerState(
                                position = latLng
                            ), icon = bitmap
                        ) {

                        }
                    }

                }


            Spacer(modifier = Modifier.height(200.dp))
            }
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(24.dp)
                    .padding(top = 24.dp)
            ) {

                Card(elevation = 5.dp) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SocialTheme.colors.uiBackground)
                            .clickable(onClick = {
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(currentLocation, 11f)
                            }), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_my_location),
                            contentDescription = null,
                            tint = SocialTheme.colors.textInteractive
                        )
                    }
                }


                Spacer(modifier = Modifier.height(12.dp))

                Card(elevation = 5.dp) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SocialTheme.colors.uiBackground)
                            .clickable(onClick = {
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(currentLocation, 11f)
                            }), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_refresh_map),
                            contentDescription = null,
                            tint = SocialTheme.colors.textInteractive

                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                ) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SocialTheme.colors.textInteractive)
                            .clickable(onClick = {
                                hideActivities = !hideActivities
                            }), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
        }


/*
            Spacer(modifier = Modifier.weight(1f))
            Card(elevation = 5.dp) {
                SocialButtonNormal(
                    icon = R.drawable.ic_filte_300,
                    onClick = { },
                    false
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Card(elevation = 5.dp) {
                SocialButtonNormal(
                    icon = R.drawable.ic_calendar_300,
                    onClick = { },
                    false
                )


            }
*/
        }


    }

}
