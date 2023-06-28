package com.example.friendupp.Create

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.example.friendupp.ActivityUi.ActivityState
import com.example.friendupp.Categories.Category
import com.example.friendupp.Components.FilterList
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.model.Activity
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import org.w3c.dom.Text


sealed class CreateSettingsEvents {

    class GoBack(
        val customLocation: String?,
        val location: LatLng?,
        val minUserCount: Int,
        val maxUserCount: Int,
        val disableChat: Boolean,
        val participantConfirmation: Boolean ,
        val disableNotification: Boolean,
        val activitySharing: Boolean,

    ) : CreateSettingsEvents()

    object Create : CreateSettingsEvents()
}

@Composable
fun CreateSettings(onEvent: (CreateSettingsEvents) -> Unit, activity: Activity,activityState:ActivityState) {
    val focusRequester = remember { FocusRequester() }
    var locationState by remember {
        mutableStateOf<LatLng?>(null)
    }
    val customLocationState by rememberSaveable(stateSaver = DescriptionStateSaver) {
        mutableStateOf(DescriptionState())
    }

    var activitySharing by remember { mutableStateOf(false) }
    var disableChat by remember { mutableStateOf(false) }
    var participantConfirmation by remember { mutableStateOf(false) }
    var disableNotification by remember { mutableStateOf(false) }
    val minParticipantsState by rememberSaveable(stateSaver = NumberStateSaver) {
        mutableStateOf(NumberState())
    }
    val maxParticipantsState by rememberSaveable(stateSaver = NumberStateSaver) {
        mutableStateOf(NumberState())
    }
    var minUserCount=-1
    if (minParticipantsState.text.isNotEmpty()){
        minUserCount=minParticipantsState.text.toInt()
    }
    var maxUserCount=-1
    if (maxParticipantsState.text.isNotEmpty()){
        maxUserCount=maxParticipantsState.text.toInt()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        BackHandler(true) {
            onEvent(
                CreateSettingsEvents.GoBack(
                    customLocation = customLocationState.text,
                    location = locationState,
                    minUserCount = minUserCount,
                    maxUserCount = maxUserCount,
                    disableChat = disableChat,
                    activitySharing = activitySharing,
                    participantConfirmation = participantConfirmation,disableNotification=disableNotification
                )
            )
        }

        Column() {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
            ) {
                ScreenHeading(onBack = {
                    onEvent(
                        CreateSettingsEvents.GoBack(
                            customLocation = customLocationState.text,
                            location = locationState,
                            minUserCount = minUserCount,
                            maxUserCount = maxUserCount,
                            disableChat = disableChat,
                            activitySharing = activitySharing,
                            participantConfirmation = participantConfirmation,disableNotification=disableNotification
                        )
                    )
                }, backButton = true, title = "Additional settings") {}
                Spacer(modifier = Modifier.height(8.dp))
             /*   LocationSettings(locationPicked={
                     latLng->
                     locationState=latLng
                 })*/
                CustomLocationSettings(focusRequester, customLocationState)
                TagsSettings(
                    tags = activityState.tags,
                    onSelected = { activityState.tags.add(it) },
                    onDeSelected = { activityState.tags.remove(it) })
                ParticipantsLimitsSettings(focusRequester,minState=minParticipantsState,maxState=maxParticipantsState)
                Customize(activitySharing,
                    onActivitySharingChanged = { newValue -> activitySharing = newValue },
                    disableChat,
                    onDisableChatChanged = { newValue -> disableChat = newValue },
                    disableNotification = disableNotification,
                    onDisableNotificationChanged = { newValue -> disableNotification = newValue },
                    participantConfirmation = participantConfirmation,
                    onParticipantConfirmationChanged = { newValue ->
                        participantConfirmation = newValue
                    },
                )

            }
            BottomBarSettings(onClick = {
                onEvent(
                    CreateSettingsEvents.GoBack(
                        customLocation = customLocationState.text,
                        location = locationState,
                        minUserCount = minUserCount,
                        maxUserCount = maxUserCount,
                        disableChat = disableChat,
                        activitySharing = activitySharing,
                        participantConfirmation = participantConfirmation,disableNotification=disableNotification
                    )
                )
            })
        }

    }
}

@Composable
fun ParticipantsLimitsSettings(focusRequester: FocusRequester,minState:TextFieldState,maxState:TextFieldState) {


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading(
            text = "Participants limits",
            icon = com.example.friendupp.R.drawable.ic_checklist,
            tip = true,
            description = "Define the minimum and maximum number of users allowed to join the activity"
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            NumberEditText(
                modifier = Modifier,
                focusRequester = focusRequester,
                label = "Min",
                textState = minState
            )
            NumberEditText(
                modifier = Modifier,
                focusRequester = focusRequester,
                label = "Max",
                textState = maxState
            )
        }
        if (minState.text.toLongOrNull() ?: 0 > maxState.text.toLongOrNull() ?: Long.MAX_VALUE) {
            Text(
                text = "Maximum is less than minimum ",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold
                ),
                color = SocialTheme.colors.error
            )
        }
    }
}

@Composable
fun CustomLocationSettings(focusRequester: FocusRequester, customLocationState: TextFieldState) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading(
            text = "Custom location",
            icon = com.example.friendupp.R.drawable.ic_custom_location
        )
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Where is it?", textState = customLocationState
        )
    }
}

@Composable
fun TagsSettings(
    tags: List<String>,
    onSelected: (String) -> Unit,
    onDeSelected: (String) -> Unit,
) {
    Column() {
        CreateHeading(
            text = "Tags",
            icon = com.example.friendupp.R.drawable.ic_tag,
            tip = true,
            description = "Select tags to categorize activities for easier filtering and discovery."
        )
        FilterList(tags, onSelected, onDeSelected)
    }
}

@Composable
fun LocationSettings(locationPicked: (LatLng) -> Unit) {
    Column() {
        CreateHeading(text = "Location", icon = com.example.friendupp.R.drawable.ic_location)
        LocationPickerSettings(locationPicked = locationPicked)
    }
}

@Composable
fun LocationPickerSettings(locationPicked: (LatLng) -> Unit) {
    var extend by rememberSaveable {
        mutableStateOf(false)

    }
    BackHandler(true) {
        if (extend) {
            extend = !extend
        } else {

        }
    }
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(50.0, 20.0), 11f)
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
    val animateHeight by animateDpAsState(
        targetValue = if (extend) configuration.screenHeightDp.dp - 200.dp else 300.dp,
        animationSpec = tween(700)
    )

    val mapModifier = Modifier
        .padding(horizontal = if (extend) 6.dp else 24.dp)
        .clip(RoundedCornerShape(24.dp))
        .height(animateHeight)

    val locationPicker = rememberSaveable {
        mutableStateOf<LatLng?>(null)
    }

    Box(modifier = mapModifier) {
        GoogleMap(
            Modifier.fillMaxSize(), cameraPositionState,
            properties = properties, onMapLoaded = {
                isMapLoaded = true
            }, onMapLongClick = { latLng ->
                locationPicker.value = latLng
                locationPicked(latLng)

            }, onMapClick = {
            },
            uiSettings = uiSettings
        ) {
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


        }
        Box(modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(end = 12.dp, top = 12.dp)
            .clip(
                CircleShape
            )
            .background(Color.Black.copy(0.5f))
            .clickable(onClick = { extend = !extend })
        ) {
            Icon(
                modifier = Modifier.padding(6.dp),
                painter = painterResource(id = com.example.friendupp.R.drawable.ic_expand),
                contentDescription = null,
                tint = Color.White.copy(0.8f)
            )
        }

        locationPicker.value.let {
            if (it != null) {
                Box(modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 12.dp, start = 12.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(Color.Black.copy(0.5f))
                    .clickable(onClick = { locationPicker.value = null })
                ) {
                    Text(
                        modifier = Modifier.padding(6.dp),
                        text = "Remove marker",
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }

    }
}

@Composable
fun Customize(
    activitySharing: Boolean, onActivitySharingChanged: (Boolean) -> Unit,
    disableChat: Boolean, onDisableChatChanged: (Boolean) -> Unit,
    disableNotification: Boolean, onDisableNotificationChanged: (Boolean) -> Unit,
    participantConfirmation: Boolean, onParticipantConfirmationChanged: (Boolean) -> Unit,
) {

    Column() {
        CreateHeading(text = "Customize", icon = com.example.friendupp.R.drawable.ic_filte_300)
        CustomizeItem(
            title = "Activity sharing", info = "Enable or Disable Activity Sharing",
            switchValue = activitySharing,
            onSwitchValueChanged = onActivitySharingChanged
        )
        CustomizeItem(
            title = "Chat", info = "Enable or disable chat visibility.",
            switchValue = disableChat,
            onSwitchValueChanged = onDisableChatChanged
        )

        CustomizeItem(
            title = "Participant Confirmation",
            info = "Require confirmation from the activity creator for users who want to join the activity.",
            switchValue = participantConfirmation,
            onSwitchValueChanged = onParticipantConfirmationChanged
        )
        CustomizeItem(
            title = "Disable notification", info = "Don't notify users about your invite.",
            switchValue = disableNotification,
            onSwitchValueChanged = onDisableNotificationChanged
        )
    }
}

@Composable
fun CustomizeItem(
    title: String,
    info: String,
    switchValue: Boolean,
    onSwitchValueChanged: (Boolean) -> Unit,
) {
    var grayColor = SocialTheme.colors.uiBorder.copy(0.6f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = SocialTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = info,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp
                ),
                color = SocialTheme.colors.textPrimary.copy(0.6f)
            )
        }
        Switch(
            checked = switchValue,
            onCheckedChange = onSwitchValueChanged,
            modifier = Modifier.padding(start = 16.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = SocialTheme.colors.textInteractive,
                checkedTrackColor = grayColor, uncheckedTrackColor = grayColor,
                checkedIconColor = SocialTheme.colors.textInteractive,
                uncheckedThumbColor = Color.White,
                uncheckedIconColor = Color.White,
                uncheckedBorderColor = grayColor,
                checkedBorderColor = grayColor
            )
        ,thumbContent={
                AnimatedVisibility(visible = switchValue) {

            Icon(painter = painterResource(id = com.example.friendupp.R.drawable.ic_done),tint= Color.White, contentDescription =null )
        } }
        )
    }
}
