package com.example.friendupp.bottomBar.ActivityUi

import android.util.Log
import android.widget.Toast
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
import com.example.friendupp.Components.ActionButtonDefault
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.example.friendupp.Create.CreateHeading
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.Profile.ProfileDisplaySettingsItem
import com.example.friendupp.Profile.ProfilePickerItem
import com.example.friendupp.Profile.TagDivider
import com.example.friendupp.TimeFormat.getFormattedDateNoSeconds
import com.example.friendupp.model.UserData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

sealed class ActivityPreviewEvents{
    object GoBack: ActivityPreviewEvents()
    class ShareActivityLink(val link:String): ActivityPreviewEvents()
    class OpenChat(val id:String): ActivityPreviewEvents()
    class ReportActivity(val id:String): ActivityPreviewEvents()
    class Leave(val id:String): ActivityPreviewEvents()
    class Join(val id:String,val creator_id:String): ActivityPreviewEvents()
    class UnBookmark(val id:String): ActivityPreviewEvents()
    class GoToActivityParticipants(val id:String): ActivityPreviewEvents()
    class GoToActivityRequests(val id:String): ActivityPreviewEvents()
    class Bookmark(val id:String): ActivityPreviewEvents()
    class AddUsers(val id:String): ActivityPreviewEvents()
    class CreatorSettings(val id:String): ActivityPreviewEvents()
}
val TAG="ActivityPrewviewDebug"
/*
* Activity Preview screen
* Date, time, title, description, profile info, check chat bookmark buttons, activity image, activity settings, creation date, creator,participants, location
*
* */
@Composable
fun ActivityPreview(modifier:Modifier,onEvent: (ActivityPreviewEvents) -> Unit, homeViewModel: HomeViewModel){
    val activityData=homeViewModel.expandedActivity.collectAsState()
    BackHandler(true) {
        onEvent(ActivityPreviewEvents.GoBack)
    }
    var displaySettings by remember {
        mutableStateOf(false)
    }
    var displayMap by remember {
        mutableStateOf(false)
    }

    activityData.value.let {activity->
        Box(
            modifier=modifier
                .background(color = SocialTheme.colors.uiBackground)){
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, 300.dp)){
                    if(!activity!!.image.isNullOrEmpty()){
                        Log.d(TAG,"image ")
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(activity!!.image)
                                .crossfade(true)
                                .build(),
                            contentDescription =null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxSize()

                        )
                        TopButtons(modifier = Modifier.align(Alignment.TopCenter), onClose={        onEvent(
                            ActivityPreviewEvents.GoBack
                        )
                        },onSettings={displaySettings=true},mapAvailable= activity.lat!=null, openMap =
                        {
                            displayMap=true
                        }, closeMap = {
                            displayMap=false

                        },mapIsDisplay=displayMap,imageAvaiable=activity.image!=null)
                    }else if (activity.lat!=null){
                        Log.d(TAG,"map")
                        TopButtons(modifier = Modifier.align(Alignment.TopCenter), onClose={        onEvent(
                            ActivityPreviewEvents.GoBack
                        )
                        },onSettings={displaySettings=true},mapAvailable= activity.lat!=null, openMap =
                        {
                            displayMap=true
                        }, closeMap = {
                            displayMap=false

                        },mapIsDisplay=displayMap,imageAvaiable=!activity.image.isNullOrEmpty())
                        displayMap=true
                    }else{
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    if(displayMap){
                        ActivityPreviewLocation(Modifier.fillMaxSize(),LatLng(activity.lat!!,activity.lng!!))
                    }

                }

                    Column( modifier = Modifier
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
                        Spacer(modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(SocialTheme.colors.uiBorder.copy(0.5f)))
                        DatePreview(activity.start_time,activity.end_time)
                        Spacer(modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(SocialTheme.colors.uiBorder.copy(0.5f)))
                        Spacer(modifier = Modifier.height(12.dp))
                        ParticipantsPreview(activity.participants_ids,activity.participants_usernames,activity.participants_profile_pictures,GoToActivityParticipants={onEvent(
                            ActivityPreviewEvents.GoToActivityParticipants(activity.id)
                        )})
                        if(activity.creator_id==UserData.user!!.id) {
                            RequestsPreview(activity.requests_ids.size,GoToRequestsPreview={onEvent(
                                ActivityPreviewEvents.GoToActivityRequests(activity.id)
                            )})
                        }


                       /* ActivityPreviewOption()*/

                    }

                Spacer(modifier = Modifier.height(128.dp))

            }
            var joined=activity?.participants_ids!!.contains(UserData.user!!.id)
            var bookmarked=activity.bookmarked!!.contains(UserData.user!!.id)

            var switch by remember { mutableStateOf(joined) }
            var bookmark by remember { mutableStateOf(bookmarked) }

            ActivityPreviewButtonRow(onEvent = onEvent,modifier=Modifier.align(Alignment.BottomCenter),id=activity.id,
                joined=switch,joinChanged={it->
                    switch=it
                }, bookmarkChanged = {bookmark=it}, bookmarked = bookmark,openSettings={displaySettings=true},creator=activity.creator_id==UserData.user!!.id,CreatorSettings={onEvent(
                    ActivityPreviewEvents.CreatorSettings(activity.id)
                )},creator_id=activity.creator_id,chatDisabled=activity.disableChat)
        }
    }
    val context = LocalContext.current
    AnimatedVisibility(visible = displaySettings) {
        Dialog(onDismissRequest = { displaySettings=false }) {
            ActivityPreviewSettings(onCancel={displaySettings=false},shareActivityLink= {
                if(activityData.value!=null){
                    onEvent(ActivityPreviewEvents.ShareActivityLink(activityData.value!!.id))
                    displaySettings=false
                }
                },enableActivitySharing=activityData.value!!.enableActivitySharing, addUsers = {
                displaySettings=false
                onEvent(ActivityPreviewEvents.AddUsers(activityData.value!!.id))},joined=activityData.value!!.participants_ids.contains(UserData.user!!.id), leaveActivity = {
                onEvent(ActivityPreviewEvents.Leave(activityData.value!!.id))
                Toast.makeText(context,"Activity left",Toast.LENGTH_SHORT).show()

                displaySettings=false
            }, reportActivity = {
                onEvent(ActivityPreviewEvents.ReportActivity(activityData.value!!.id))
                displaySettings=false
            })
        }
    }
}

@Composable
fun ParticipantsPreview(participantsIds: ArrayList<String>, participantsUsernames: HashMap<String, String>, participantsProfilePictures: HashMap<String, String>,GoToActivityParticipants:()->Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = GoToActivityParticipants)
            .padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = R.drawable.ic_group), contentDescription = null,tint=SocialTheme.colors.iconPrimary)
        Spacer(modifier = Modifier.width(24.dp))
        Column() {
            Text(text ="Participants", style = TextStyle(fontFamily = Lexend, fontSize = 16.sp, fontWeight = FontWeight.Light),color=SocialTheme.colors.textPrimary)

        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(painter = painterResource(id = R.drawable.arrow_right), contentDescription =null , tint = SocialTheme.colors.iconPrimary)

    }

}
@Composable
fun RequestsPreview(particirpants:Int, GoToRequestsPreview:()->Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = GoToRequestsPreview)
            .padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = R.drawable.ic_hand), contentDescription = null,tint=SocialTheme.colors.iconPrimary)
        Spacer(modifier = Modifier.width(24.dp))
        Column() {
            Text(text ="Requests", style = TextStyle(fontFamily = Lexend, fontSize = 16.sp, fontWeight = FontWeight.Light),color=SocialTheme.colors.textPrimary)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(text =particirpants.toString(), style = TextStyle(fontFamily = Lexend, fontSize = 16.sp, fontWeight = FontWeight.Light),color=SocialTheme.colors.textPrimary)

        Spacer(modifier = Modifier.width(24.dp))
        Icon(painter = painterResource(id = R.drawable.arrow_right), contentDescription =null , tint = SocialTheme.colors.iconPrimary)

    }

}

@Composable
fun DatePreview(startTime: String, endTime: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = R.drawable.ic_date), contentDescription = null,tint=SocialTheme.colors.iconPrimary)
        Spacer(modifier = Modifier.width(24.dp))
        Column() {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text ="From:", style = TextStyle(fontFamily = Lexend, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),color=SocialTheme.colors.textPrimary)
                Text(text = getFormattedDateNoSeconds(startTime) , style = TextStyle(fontFamily = Lexend, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),color=SocialTheme.colors.textPrimary)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text ="To: ", style = TextStyle(fontFamily = Lexend, fontSize = 16.sp, fontWeight = FontWeight.Light),color=SocialTheme.colors.textPrimary)

                Text(text = getFormattedDateNoSeconds(endTime) , style = TextStyle(fontFamily = Lexend, fontSize = 16.sp, fontWeight = FontWeight.Light),color=SocialTheme.colors.textPrimary)

            }

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
        }
        AnimatedVisibility(visible = ifParticipants) {
        }
    }
}

@Composable
fun ActivityPreviewSettings(onCancel: () -> Unit={},shareActivityLink: () -> Unit={},leaveActivity: () -> Unit={},reportActivity: () -> Unit={},addUsers: () -> Unit={},enableActivitySharing:Boolean,joined:Boolean) {
    Column(Modifier.clip(RoundedCornerShape(24.dp))) {
        ProfileDisplaySettingsItem(label="Share",icon=R.drawable.ic_share, textColor = SocialTheme.colors.textPrimary, onClick = shareActivityLink)
        ProfileDisplaySettingsItem(label="Report",icon=R.drawable.ic_flag, textColor = SocialTheme.colors.error, onClick = reportActivity)
        if(joined){
            ProfileDisplaySettingsItem(label="Leave",icon=R.drawable.ic_logout , textColor = SocialTheme.colors.error, onClick = leaveActivity)

        }
        if(enableActivitySharing ){
                ProfileDisplaySettingsItem(label="Add users",icon=R.drawable.ic_person_add , textColor = SocialTheme.colors.textPrimary,onClick=addUsers)
        }
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
fun TopButtons(modifier:Modifier=Modifier,onClose: () -> Unit, onSettings: () -> Unit
               ,mapAvailable:Boolean,closeMap:()->Unit,openMap:()->Unit,mapIsDisplay:Boolean,imageAvaiable:Boolean) {
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 24.dp, horizontal = 24.dp)
        .background(Color.Transparent) ){
        Spacer(modifier = Modifier.weight(1f))
        if(mapAvailable){
            if(mapIsDisplay){
                if(imageAvaiable){
                    TransButton(onClick=closeMap,icon=R.drawable.ic_image_300)

                }
            }else{
                TransButton(onClick=openMap,icon=R.drawable.ic_location)

            }

        }
        Spacer(modifier = Modifier.width(16.dp))
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
fun ActivityPreviewButtonRow(modifier:Modifier, onEvent:(ActivityPreviewEvents)->Unit, id:String,
                             joined: Boolean = false,
                             joinChanged: (Boolean) -> Unit,
                             bookmarked: Boolean = false,
                             bookmarkChanged: (Boolean) -> Unit,
                             openSettings: () -> Unit,
                             creator: Boolean = false,
                             CreatorSettings: () -> Unit,
                             creator_id:String,
                             chatDisabled:Boolean
){



    Column(modifier.background(Color.Transparent)) {
            Row( modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                Spacer(modifier = Modifier
                    .height(1.dp)
                    .width(24.dp)
                    .background(SocialTheme.colors.uiBorder))
                ActionButtonDefault(
                    icon = R.drawable.ic_back,
                    isSelected = false,
                    onClick =  {
                        onEvent(ActivityPreviewEvents.GoBack)
                    }
                )
                Spacer(modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(SocialTheme.colors.uiBorder))
                ActionButtonDefault(
                    icon = R.drawable.ic_check_300,
                    isSelected = joined,
                    onClick =  {
                        if (joined) {
                            onEvent(ActivityPreviewEvents.Leave(id))
                            joinChanged(false)
                        } else {
                            onEvent(ActivityPreviewEvents.Join(id,creator_id))
                            joinChanged(true)

                        }
                    }
                )
               /* ActivityPreviewButtonRowItem(icon=R.drawable.ic_check_300, onClick = {joined=!joined}, selected = joined )*/
                Spacer(modifier = Modifier
                    .height(1.dp)
                    .width(12.dp)
                    .background(SocialTheme.colors.uiBorder))
                if(!chatDisabled){
                    ActionButtonDefault(
                        icon = R.drawable.ic_chat_300,
                        isSelected = false,
                        onClick =  {onEvent(ActivityPreviewEvents.OpenChat(id)) }
                    )
                    /*  ActivityPreviewButtonRowItem(icon=R.drawable.ic_chat_300, onClick = {})*/
                    Spacer(modifier = Modifier
                        .height(1.dp)
                        .width(12.dp)
                        .background(SocialTheme.colors.uiBorder))

                }

                ActionButtonDefault(
                    icon = R.drawable.ic_bookmark_300,
                    isSelected = bookmarked,
                    onClick =  {
                        if (bookmarked) {
                            onEvent(ActivityPreviewEvents.UnBookmark(id))
                            bookmarkChanged(false)
                        } else {
                            onEvent(ActivityPreviewEvents.Bookmark(id))
                            bookmarkChanged(true)

                        }
                    }
                )
                Spacer(modifier = Modifier
                    .height(1.dp)
                    .width(12.dp)
                    .background(SocialTheme.colors.uiBorder))
                if(creator){
                    ActionButtonDefault(
                        icon = R.drawable.ic_settings,
                        isSelected = false,
                        onClick =  {
                            CreatorSettings()
                        }
                    )
                }else{
                    ActionButtonDefault(
                        icon = R.drawable.ic_more,
                        isSelected = false,
                        onClick =  {
                            openSettings()
                        }
                    )
                }

          /*      ActivityPreviewButtonRowItem(icon=R.drawable.ic_bookmark_300, onClick = {bookmarked=!bookmarked}, selected =bookmarked)*/
                Spacer(modifier = Modifier
                    .height(1.dp)
                    .width(16.dp)
                    .background(SocialTheme.colors.uiBorder))

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
fun ActivityPreviewLocation(modifier:Modifier,latLng: LatLng) {
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


    val locationPicker = rememberSaveable {
        mutableStateOf<LatLng?>(null)
    }

        GoogleMap(
            modifier.fillMaxSize(), cameraPositionState,
            properties = properties, onMapLoaded = {
                isMapLoaded = true
            }, onMapLongClick = { latLng ->

            }, onMapClick = {
            },
            uiSettings = uiSettings
        ) {
                    MarkerInfoWindow(
                        zIndex = 0.5f,
                        state = MarkerState(
                            position = latLng
                        )

                    ) {

                    }


        }


}

@Composable
fun ActivityPreviewCustomLocation(customLocation:String){
    Column() {
        CreateHeading(text = "Custom location", icon =R.drawable.ic_custom_location )
        Text(modifier =Modifier.padding(horizontal = 24.dp), text =customLocation ,style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 16.sp), color = SocialTheme.colors.textPrimary)
    }
}