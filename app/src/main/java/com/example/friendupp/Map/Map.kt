package com.example.friendupp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
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
import com.example.friendupp.Home.loadMorePublicActivities
import com.example.friendupp.Home.loadPublicActivities
import com.example.friendupp.Map.MapActivityItem
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*


sealed class MapEvent {
    class PreviewActivity(val activity: Activity) : MapEvent()
}

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    activityViewModel: ActivityViewModel,
    onEvent: (MapEvent) -> Unit,
) {
    val publicActivities = remember { mutableStateListOf<Activity>() }
    val morePublicActivities = remember { mutableStateListOf<Activity>() }
    var publicActivitiesExist = remember { mutableStateOf(false) }

    val flow = mapViewModel.currentLocation.collectAsState()
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf(LatLng(50.0, 20.0)) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var datePicked = remember {
        mutableStateOf<String?>(null)
    }
    flow.value.let { latLng ->
        if (latLng != null) {
            currentLocation = latLng

        }
    }
    loadPublicActivities(
        activityViewModel,
        publicActivities,
        activitiesExist = publicActivitiesExist,
        currentLocation,
        selectedTags = SnapshotStateList(),
        date = datePicked.value
    )
    loadMorePublicActivities(activityViewModel, morePublicActivities)

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

    Box(modifier = Modifier) {

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

            publicActivities.forEach { activity ->
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

        MapActivitiesDisplay(
            modifier = Modifier.align(Alignment.BottomCenter),
            publicActivities = publicActivities,
            morePublicActivities = morePublicActivities,
            CenterOnPoint = { latLng ->
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(latLng, 13f)

            },
            activityToScroll = activityToScroll,
            onEvent = onEvent
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
        ) {

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
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
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
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            SocialButtonNormal(
                icon = R.drawable.ic_filte_300,
                onClick = { },
                false
            )
            Spacer(modifier = Modifier.width(12.dp))
            SocialButtonNormal(
                icon = R.drawable.ic_calendar_300,
                onClick = { },
                false
            )


        }


    }


}

@Composable
fun MapActivitiesDisplay(
    activityToScroll: MutableState<Activity?>,
    modifier: Modifier, publicActivities: MutableList<Activity>,
    morePublicActivities: MutableList<Activity>,
    CenterOnPoint: (LatLng) -> Unit, onEvent: (MapEvent) -> Unit,
) {
    val lazyListState = rememberLazyListState()


    LazyRow(
        modifier = modifier,
        state = lazyListState
    ) {
        items(publicActivities) { activity ->
            MapActivityItem(onClick = {
                val latLng = LatLng(activity.lat!!, activity.lng!!)

                CenterOnPoint(latLng)
            }, activity = activity, onEvent = onEvent)
            Spacer(modifier = Modifier.width(16.dp))
        }
    }

    // Trigger scrolling when the activityToScroll value changes
    LaunchedEffect(activityToScroll.value) {
        val index = publicActivities.indexOf(activityToScroll.value)
        if (index != -1) {
            lazyListState.animateScrollToItem(index)
        }
    }
}


private fun getBitmapDescriptor(context: Context, id: Int): BitmapDescriptor? {
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