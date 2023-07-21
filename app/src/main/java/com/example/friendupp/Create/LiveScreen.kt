package com.example.friendupp.Create

import android.Manifest
import android.content.Context
import android.provider.Settings.Global.getString
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Components.*
import com.example.friendupp.Components.TimePicker.TimeState
import com.example.friendupp.Login.EmailState
import com.example.friendupp.Login.PasswordState
import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import java.time.LocalTime
import java.util.*


sealed class LiveScreenEvents{


    object GoBack:LiveScreenEvents()
    object CreateLive:LiveScreenEvents()
    object GoToFriendPicker:LiveScreenEvents()

}



@Composable
fun LiveScreen(modifier:Modifier,onEvent:(LiveScreenEvents)->Unit,liveActivityState: LiveActivityState,mapViewModel:MapViewModel){
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    val flow = mapViewModel.currentLocation.collectAsState()
    flow.value.let { latLng ->
        if (latLng != null) {
            currentLocation = latLng
        }
    }
    val context = LocalContext.current
    val noteState = liveActivityState.note
    Column() {
        Column(modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            ScreenHeading(title = "Go live", backButton = true, onBack = {
                onEvent(LiveScreenEvents.GoBack)
            }){}

            Spacer(modifier = Modifier.height(24.dp))
            CreateHeading("Time", icon = com.example.friendupp.R.drawable.ic_time)
            TimeSelection(startTimeState = liveActivityState.timeStartState, endTimeState =liveActivityState.timeEndState, modifier = Modifier)
            NoteComponent(noteState= noteState)
            LocationComponent(shareLocation = {
                if(currentLocation!=null){
                    liveActivityState.location=currentLocation!!
                }else{
                    Toast.makeText(context,"Couldn't access curent location",Toast.LENGTH_SHORT).show()
                }
            })
            Spacer(modifier = Modifier.weight(1f))
            CreateButton(modifier = Modifier.padding(horizontal = 48.dp), text = "Go live", disabled = false, createClicked = {
                onEvent(LiveScreenEvents.CreateLive)
            })
            Spacer(modifier = Modifier.height(64.dp))
        }

    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationComponent(shareLocation:()->Unit) {
    var shareLocation by remember { mutableStateOf(false) }

    var grayColor = SocialTheme.colors.uiBorder.copy(0.6f)
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    Column() {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                Modifier
                    .padding(top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = null,
                    tint = SocialTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                androidx.compose.material3.Text(
                    text = "Share current location",
                    color = SocialTheme.colors.textPrimary,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                androidx.compose.material3.Switch(
                    checked = shareLocation,
                    onCheckedChange = {value->
                        if(value){
                            if (locationPermissionState.status.isGranted) {
                                shareLocation()
                                shareLocation = true
                            } else {
                                locationPermissionState.launchPermissionRequest()
                            }

                        }else{
                            shareLocation = false
                        }

                                      },
                    modifier = Modifier.padding(start = 16.dp),
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = SocialTheme.colors.textInteractive,
                        checkedTrackColor = grayColor, uncheckedTrackColor = grayColor,
                        checkedIconColor = SocialTheme.colors.textInteractive,
                        uncheckedThumbColor = Color.White,
                        uncheckedIconColor = Color.White,
                        uncheckedBorderColor = grayColor,
                        checkedBorderColor = grayColor
                    ), thumbContent = {
                        androidx.compose.animation.AnimatedVisibility(visible = shareLocation) {

                            Icon(
                                painter = painterResource(id = R.drawable.ic_done),
                                tint = Color.White,
                                contentDescription = null
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
           
        }

    }
}


@Composable
fun NoteComponent(noteState: TextFieldState){
    val focusRequester = remember { FocusRequester() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading("Add text note", icon = com.example.friendupp.R.drawable.ic_edit)
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->
            }, label = "Note", textState = noteState
        )

    }


}
