package com.palkowski.friendupp.Components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.*

@Composable
fun DisplayLocationDialog(latLng: LatLng,onCancel:()->Unit){
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 11f)
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
    Dialog(onDismissRequest =onCancel) {
        Box(
            Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(SocialTheme.colors.uiBackground)){

                Column() {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onCancel)
                        .padding(vertical = 8.dp, horizontal = 12.dp), horizontalArrangement = Arrangement.Center){

                        Text(text ="Location", style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, fontFamily = Lexend), color = SocialTheme.colors.textPrimary )

                    }
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(SocialTheme.colors.uiBorder))

                    GoogleMap(
                        Modifier
                            .height(300.dp)

                        , cameraPositionState,
                        properties = properties, onMapLoaded = {
                            isMapLoaded = true
                        }, onMapLongClick = {
                        }, onMapClick = {
                        },
                        uiSettings = uiSettings
                    ) {
                        latLng.let {
                            MarkerInfoWindow(
                                state = MarkerState(
                                    position = it
                                )
                            ) {

                            }
                        }
                    }
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(SocialTheme.colors.uiBorder))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onCancel)
                        .padding(vertical = 16.dp, horizontal = 12.dp), horizontalArrangement = Arrangement.Center){
                        Text(text ="Dismiss", style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, fontFamily = Lexend), color = SocialTheme.colors.iconPrimary )

                    }

                }



        }

    }
}