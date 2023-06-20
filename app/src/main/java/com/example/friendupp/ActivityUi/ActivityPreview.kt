package com.example.friendupp.ActivityUi

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.example.friendupp.Create.CreateHeading
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.Profile.ProfileDisplaySettingsItem
import com.example.friendupp.Profile.ProfilePickerItem
import com.example.friendupp.Profile.TagDivider
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

sealed class ActivityPreviewEvents{
    object GoBack:ActivityPreviewEvents()
    class ShareActivityLink(val link:String):ActivityPreviewEvents()
}

/*
* Activity Preview screen
* Date, time, title, description, profile info, check chat bookmark buttons, activity image, activity settings, creation date, creator,participants, location
*
* */
@Composable
fun ActivityPreview(onEvent: (ActivityPreviewEvents) -> Unit, homeViewModel: HomeViewModel){
    val activityData=homeViewModel.expandedActivity.collectAsState()
    BackHandler(true) {
        onEvent(ActivityPreviewEvents.GoBack)
    }
    var displaySettings by remember {
        mutableStateOf(false)
    }
    activityData.value.let {activity->

        Box(
            Modifier
                .fillMaxSize()
                .background(color = SocialTheme.colors.uiBackground)){
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Box(modifier = Modifier.fillMaxWidth()){
                    if(activity!!.image!=null){
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(activity!!.image)
                                .crossfade(true)
                                .build(),
                            contentDescription =null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(150.dp, 300.dp)
                        )
                    }

                    TopButtons(modifier = Modifier.align(Alignment.TopCenter), onClose={        onEvent(ActivityPreviewEvents.GoBack)
                    },onSettings={displaySettings=true})
                }

                    Column(    modifier = Modifier
                        .graphicsLayer { translationY = -50f }
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(SocialTheme.colors.uiBackground)
                        .padding(vertical = 8.dp)) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ActivityInformation(description = activity!!.description
                            , title =activity!!.title, username = activity.creator_username,
                            profilePictureUrl =activity!!.creator_profile_picture, name = activity!!.creator_name)
                        Spacer(modifier = Modifier.height(12.dp))

                        TagDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        ActivityPreviewOption()

                    }

                Spacer(modifier = Modifier.height(128.dp))

            }

            ActivityPreviewButtonRow(onEvent = {},modifier=Modifier.align(Alignment.BottomCenter))
        }
    }

    AnimatedVisibility(visible = displaySettings) {
        Dialog(onDismissRequest = { displaySettings=false }) {
            ActivityPreviewSettings(onCancel={displaySettings=false},shareActivityLink= {


                if(activityData.value!=null){
                    onEvent(ActivityPreviewEvents.ShareActivityLink(activityData.value!!.id))
                    displaySettings=false
                }
                })
        }
    }
}

@Composable
fun ActivityPreviewOption() {
    var selectedItem by remember { mutableStateOf("Date") }
    var ifDate by remember { mutableStateOf(true) }
    var ifLocation by remember { mutableStateOf(false) }
    var ifParticipants by remember { mutableStateOf(false) }
    Column (Modifier.fillMaxSize()){
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.Center) {
            ProfilePickerItem(
                label = "",
                icon = R.drawable.ic_date,
                selected = selectedItem == "Date",
                onItemSelected = {
                    selectedItem = "Date"
                    ifDate = true
                    ifLocation = false
                    ifParticipants = false
                }
            )
            ProfilePickerItem(
                label = "",
                icon = R.drawable.ic_pindrop_300,
                selected = selectedItem == "Location",
                onItemSelected = {
                    selectedItem = "Location"
                    ifDate = false
                    ifLocation = true
                    ifParticipants = false
                }
            )
            ProfilePickerItem(
                label = "",
                icon = R.drawable.ic_group,
                selected = selectedItem == "Participants",
                onItemSelected = {
                    selectedItem = "Participants"
                    ifDate = false
                    ifLocation = false
                    ifParticipants = true
                }
            )
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SocialTheme.colors.uiBorder))

        AnimatedVisibility(visible = ifDate) {
            DataAndTimeComponent()
        }
        AnimatedVisibility(visible = ifLocation) {
            ActivityPreviewLocation(LatLng(50.0,20.0))
        }
        AnimatedVisibility(visible = ifParticipants) {
        }
    }
}

@Composable
fun ActivityPreviewSettings(onCancel: () -> Unit={},shareActivityLink: () -> Unit={}) {
    Column(Modifier.clip(RoundedCornerShape(24.dp))) {
        ProfileDisplaySettingsItem(label="Share",icon=R.drawable.ic_share, textColor = SocialTheme.colors.textPrimary, onClick = shareActivityLink)
        ProfileDisplaySettingsItem(label="Report",icon=R.drawable.ic_flag, textColor = SocialTheme.colors.error)
        ProfileDisplaySettingsItem(label="Leave",icon=R.drawable.ic_logout , textColor = SocialTheme.colors.error)
        ProfileDisplaySettingsItem(label="Add users",icon=R.drawable.ic_person_add , textColor = SocialTheme.colors.textPrimary)
        ProfileDisplaySettingsItem(label="Cancel" , turnOffIcon = true, textColor = SocialTheme.colors.textPrimary.copy(0.5f), onClick = onCancel)
    }
}


@Composable
fun ActivityInformation(name:String,username:String,profilePictureUrl:String,description:String,title:String){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)) {
        Row (verticalAlignment = Alignment.CenterVertically){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val truncatedName = if (name.length > 30) name.substring(0, 30)+"..." else name
                androidx.compose.material.Text(
                    text = truncatedName,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = SocialTheme.colors.textPrimary
                )
                val truncatedUsername = if (username.length > 30) username.substring(0, 30)+"..." else username
                androidx.compose.material.Text(
                    text = truncatedUsername,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp
                    ),
                    color = SocialTheme.colors.textPrimary
                )

            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material.Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold
            ),
            color = SocialTheme.colors.textPrimary
        )
        val truncatedDescription = if (description.length > 500) description.substring(0, 500)+"..." else description
        androidx.compose.material.Text(
            text = truncatedDescription,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.Normal
            ),
            color = SocialTheme.colors.textPrimary.copy(0.5f)
        )
    }
}

@Composable
fun TopButtons(modifier:Modifier=Modifier,onClose: () -> Unit, onSettings: () -> Unit) {
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 24.dp, horizontal = 24.dp)
        .background(Color.Transparent), horizontalArrangement = Arrangement.SpaceBetween ){
        TransButton(onClick=onClose,icon=R.drawable.ic_x)
        TransButton(onClick=onSettings,icon=R.drawable.ic_more)
    }
}

@Composable
fun TransButton(onClick: () -> Unit, icon: Int) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .size(48.dp)
        .background(Color.Black.copy(0.2f))
        .clickable(onClick = onClick), contentAlignment = Alignment.Center){
        Icon(painter = painterResource(id = icon), contentDescription =null, tint = Color.White )
    }
}


@Composable
fun ActivityPreviewButtonRow(modifier:Modifier,onEvent:(ActivityPreviewEvents)->Unit){

    var bookmarked by remember { mutableStateOf(false) }
    var joined by remember { mutableStateOf(false) }

    Column(modifier.background(Color.Transparent)) {
        Card(elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(100.dp), colors = CardDefaults.cardColors(contentColor = SocialTheme.colors.uiBackground, containerColor = SocialTheme.colors.uiBackground)) {
            Row( modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){

                ActivityPreviewButtonRowItem(icon=R.drawable.ic_check_300, onClick = {joined=!joined}, selected = joined )
                Spacer(modifier = Modifier.width(24.dp))
                ActivityPreviewButtonRowItem(icon=R.drawable.ic_chat_300, onClick = {})
                Spacer(modifier = Modifier.width(24.dp))

                ActivityPreviewButtonRowItem(icon=R.drawable.ic_bookmark_300, onClick = {bookmarked=!bookmarked}, selected =bookmarked)



            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

}

@Composable
fun ActivityPreviewButtonRowItem(icon: Int,onClick: () -> Unit,selected:Boolean=false){
    val backGroundColor = if(selected){SocialTheme.colors.textInteractive}else{SocialTheme.colors.uiBorder.copy(0.2f)}
    val iconColor = if(selected){
        Color.White
    }else{
        SocialTheme.colors.iconPrimary
    }


    Column (horizontalAlignment = Alignment.CenterHorizontally){
        Box(modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(backGroundColor)
            .padding(12.dp)){
            Icon(painter = painterResource(id = icon), contentDescription =null, tint = iconColor )

        }
    }

}


@Composable
fun DataAndTimeComponent(){
    Column() {
        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically) {
            DateItem(month="March",dayNumber="7",dayLabel="Sat")
            Spacer(modifier = Modifier.width(12.dp))
            Row(modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(
                    BorderStroke(1.dp, SocialTheme.colors.uiBorder.copy(0.4f)),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(SocialTheme.colors.uiBackground)
                .padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically){

                androidx.compose.material.Text(
                    text = "15:00",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Medium
                    ),
                    color = SocialTheme.colors.textPrimary.copy(0.6f)
                )
                Spacer(modifier = Modifier.width(24.dp))

                Icon(painter = painterResource(id = R.drawable.ic_long_right), contentDescription = null, tint = SocialTheme.colors.textPrimary.copy(0.8f))
                Spacer(modifier = Modifier.width(24.dp))
                androidx.compose.material.Text(
                    text = "17:00",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Medium
                    ),
                    color = SocialTheme.colors.textPrimary.copy(0.6f)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            DateItem(month="March",dayNumber="8",dayLabel="Sun")

        }

    }


}

@Composable
fun DateItem(month: String, dayNumber: String, dayLabel: String) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            androidx.compose.material.Text(
                text = month,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Light
                ),
                color = SocialTheme.colors.textPrimary.copy(0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Box(modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(48.dp)
                .background(SocialTheme.colors.textInteractive), contentAlignment = Alignment.Center){
                androidx.compose.material.Text(
                    text = dayNumber,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            androidx.compose.material.Text(
                text = dayLabel,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Light
                ),
                color = SocialTheme.colors.textPrimary.copy(0.6f)
            )

        }

}


@Composable
fun ActivityPreviewLocation(latLng: LatLng) {
        LocationPicker(latLng)
}
@Composable
fun LocationPicker(latLng:LatLng) {
    var extend by rememberSaveable {
        mutableStateOf(false)

    }
    BackHandler(true) {
        if(extend){
            extend=!extend
        }else{

        }
    }
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

    Box(modifier = mapModifier){
        GoogleMap(
            Modifier.fillMaxSize(), cameraPositionState,
            properties = properties, onMapLoaded = {
                isMapLoaded = true
            }, onMapLongClick = { latLng ->
                locationPicker.value=latLng

            }, onMapClick = {
            },
            uiSettings = uiSettings
        ) {
            locationPicker.value.let {
                if (it!=null){
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
            .clickable(onClick = { extend = !extend })){
            Icon(modifier = Modifier.padding(6.dp), painter = painterResource(id = R.drawable.ic_expand), contentDescription =null, tint = Color.White.copy(0.8f) )
        }

        locationPicker.value.let {
            if (it!=null){
                Box(modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 12.dp, start = 12.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(Color.Black.copy(0.5f))
                    .clickable(onClick = { locationPicker.value = null })){
                    Text(modifier = Modifier.padding(6.dp), text = "Remove marker", style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 16.sp), color = Color.White)
                }
            }}

    }
}


@Composable
fun ActivityPreviewCustomLocation(customLocation:String){
    Column() {
        CreateHeading(text = "Custom location", icon =R.drawable.ic_custom_location )
        Text(modifier =Modifier.padding(horizontal = 24.dp), text =customLocation ,style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 16.sp), color = SocialTheme.colors.textPrimary)
    }
}