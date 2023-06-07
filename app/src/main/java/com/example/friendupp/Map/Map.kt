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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
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
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*


@Composable
fun MapScreen(mapViewModel:MapViewModel) {
    val flow = mapViewModel.currentLocation.collectAsState()
    val context= LocalContext.current
    var currentLocation by remember { mutableStateOf(LatLng(50.0, 20.0)) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    flow.value.let { latLng ->
        if (latLng != null) {
            currentLocation = latLng
        }
    }
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

            val bitmap = getBitmapDescriptor(context,R.drawable.ic_pin)

            selectedLocation.let { selectedLatLng ->
                if (selectedLatLng != null) {

                    MarkerInfoWindow(
                        state = MarkerState(
                            position = selectedLatLng
                        ),
                        icon =bitmap ,
                    ) {

                    }
                }
            }


            currentLocation.let { latLng ->
                MarkerInfoWindow(alpha=0.8f,
                    state = MarkerState(
                        position = latLng
                    )
                ) {

                }
            }

        }

        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
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

    }


}

private fun getBitmapDescriptor(context:Context,id: Int): BitmapDescriptor? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val vectorDrawable = getDrawable(context,id) as VectorDrawable
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