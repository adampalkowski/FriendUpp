package com.example.friendupp.Profile

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.bottomBar.ActivityUi.ActivityEvents
import com.example.friendupp.bottomBar.ActivityUi.activityItem
import com.example.friendupp.Categories.Category
import com.example.friendupp.ChatUi.ButtonAdd

import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Components.getExpandedTags
import com.example.friendupp.Home.HomeEvents
import com.example.friendupp.R
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.DEFAULT_PROFILE_PICTURE_URL
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response

import com.example.friendupp.model.User
import com.example.friendupp.model.UserData

import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme




sealed class ProfileEvents {
    object GoToSearch : ProfileEvents()
    object GoBack : ProfileEvents()
    object GoToSettings : ProfileEvents()
    object GoToEditProfile : ProfileEvents()
    /*todo*/
    object GoToFriendList : ProfileEvents()
    object GetProfileLink : ProfileEvents()
    object OpenCamera : ProfileEvents()
    class ExpandActivity(val activityData: Activity) : ProfileEvents()
    class JoinActivity(val activity: Activity) : ProfileEvents()
    class UnBookmark(val id: String) : ProfileEvents()
    class Bookmark(val id: String) : ProfileEvents()
    class GoToProfile(val id: String) : ProfileEvents()
    class LeaveActivity(val activity: Activity) : ProfileEvents()
    class OpenChat(val id: String) : ProfileEvents()
}



@Composable
fun ProfileScreen(modifier: Modifier, onEvent: (ProfileEvents) -> Unit, user: User, onClick: () -> Unit,activityViewModel:ActivityViewModel) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    //FOR ACTIVITIES DISPLLAY
    var selectedItem by remember { mutableStateOf("Upcoming") }
    var ifCalendar by remember { mutableStateOf(true) }
    var ifHistory by remember { mutableStateOf(false) }
    var joinedActivitiesExist= remember { mutableStateOf(false) }
    var historyActivitiesExist= remember { mutableStateOf(false) }

    //LOAD IN PROFILE ACTIVITIES

    val activitiesHistory = remember { mutableStateListOf<Activity>() }
    val moreHistoryActivities = remember { mutableStateListOf<Activity>() }


    val joinedActivities = remember { mutableStateListOf<Activity>() }
    val moreJoinedActivities = remember { mutableStateListOf<Activity>() }

    if(selectedItem=="Upcoming"){

        loadJoinedActivities(activityViewModel,joinedActivities,user.id)
        loadMoreJoinedActivities(activityViewModel,moreJoinedActivities)


    }else{
        loadActivitiesHistory(activityViewModel,activitiesHistory,user.id)
        loadMoreActivitiesHistory(activityViewModel,moreHistoryActivities)
    }







    BackHandler(true) {
        onEvent(ProfileEvents.GoBack)
    }
    LazyColumn{
        item {     Column(modifier) {
            ScreenHeading(title="Profile") {
                Row(Modifier,verticalAlignment = Alignment.CenterVertically){
                    ButtonAdd(icon = R.drawable.ic_group_add, onClick = {        onEvent(ProfileEvents.GoToSearch)         })
                    Spacer(modifier = Modifier
                        .background(SocialTheme.colors.uiBorder)
                        .width(16.dp))
                    ButtonAdd(icon = R.drawable.ic_settings, onClick = {

                        onEvent(ProfileEvents.GoToSettings)})
                }
            }
            ProfileInfo(
                name =user.name?:"",
                username = user.username?:"",
                profilePictureUrl = user.pictureUrl?:"",
                location =user.location ,
                description =user.biography,
                onEvent=onEvent
            )
            TagDivider(tags=user.tags)
            ProfileStats(modifier=Modifier.fillMaxWidth(), activitiesCreated = user.activitiesCreated, friendCount = user.friends_ids.size, usersReached = user.usersReached,GoToFriends={onEvent(ProfileEvents.GoToFriendList)})
            ProfileOptions(onEvent=onEvent)

        } }



        item {      Column (Modifier.fillMaxSize()){
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ProfilePickerItem(
                    label = "Upcoming",
                    icon = R.drawable.ic_calendar_upcoming,
                    selected = selectedItem == "Upcoming",
                    onItemSelected = {
                        selectedItem = "Upcoming"
                        ifCalendar = true
                        ifHistory = false
                    }
                )
                ProfilePickerItem(
                    label = "History",
                    icon = R.drawable.ic_history,
                    selected = selectedItem == "History",
                    onItemSelected = {
                        selectedItem = "History"
                        ifCalendar = false
                        ifHistory = true
                    }
                )
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SocialTheme.colors.uiBorder))

        }
        }
        if(ifCalendar){
            items(joinedActivities) { activity ->
                activityItem(
                    activity,
                    onClick = {
                        // Handle click event
                    },
                    onEvent = { event->
                        handleActivityEvent(event, onEvent = onEvent)
                    }
                )
            }
            items(moreJoinedActivities) { activity ->
                activityItem(
                    activity,
                    onClick = {
                        // Handle click event
                    },
                    onEvent = { event->
                        handleActivityEvent(event, onEvent = onEvent)
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(64.dp))

            }
            item {
                LaunchedEffect(true) {
                    if (joinedActivitiesExist.value) {
                        activityViewModel.getMoreJoinedActivities(UserData.user!!.id)
                    }
                }
            }
        }
        if(ifHistory){
            items(activitiesHistory) { activity ->
                activityItem(
                    activity,
                    onClick = {
                        // Handle click event
                    },
                    onEvent = { event->
                        handleActivityEvent(event, onEvent = onEvent)
                    }
                )
            }

            items(moreHistoryActivities) { activity ->
                activityItem(
                    activity,
                    onClick = {
                        // Handle click event
                    },
                    onEvent = { event->
                        handleActivityEvent(event, onEvent = onEvent)
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(64.dp))

            }
            item {
                LaunchedEffect(true) {
                    if (historyActivitiesExist.value) {
                        activityViewModel.getMoreUserActivities(UserData.user!!.id)
                    }
                }
            }
        }


    }
}


@Composable
fun loadActivitiesHistory(activityViewModel: ActivityViewModel, activitiesHistory: MutableList<Activity>,user_id:String) {

    //call get activities only once
    val activitiesFetched = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = activitiesFetched.value) {
        if (!activitiesFetched.value) {
            activityViewModel.getUserActivities(user_id)
            activitiesFetched.value = true
        }
    }
    val historyFlow =activityViewModel.userActivitiesState.collectAsState()
    historyFlow.value.let {
            response -> when(response){
        is com.example.friendupp.model.Response.Success->{
            activitiesHistory.clear()
            activitiesHistory.addAll(response.data)
        }
        is com.example.friendupp.model.Response.Loading->{
            CircularProgressIndicator()
        }
        is com.example.friendupp.model.Response.Failure->{
        }
        else->{}
    }
    }
}

@Composable
fun loadMoreActivitiesHistory(activityViewModel: ActivityViewModel, activities: MutableList<Activity>) {
    activityViewModel.userMoreActivitiesState.value.let {
        when (it) {
            is Response.Success -> {
                activities.clear()
                activities.addAll(it.data)
            }
            else -> {}
        }
    }

}

@Composable
fun loadMoreJoinedActivities(activityViewModel: ActivityViewModel, activities: MutableList<Activity>) {
    activityViewModel.moreJoinedActivitiesState.value.let {
        when (it) {
            is Response.Success -> {
                activities.clear()
                activities.addAll(it.data)
            }
            else -> {}
        }
    }

}
@Composable
fun loadJoinedActivities(activityViewModel: ActivityViewModel, joinedActivities: MutableList<Activity>,user_id: String) {

    val activitiesFetched = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = activitiesFetched.value) {
        if (!activitiesFetched.value) {
            activityViewModel.getJoinedActivities(user_id)
            activitiesFetched.value = true
        }
    }
    val joinedListFlow =activityViewModel.joinedActivitiesState.collectAsState()
    joinedListFlow.value.let {
            response -> when(response){
        is com.example.friendupp.model.Response.Success->{
            joinedActivities.clear()
            joinedActivities.addAll(response.data)
        }
        is com.example.friendupp.model.Response.Loading->{
            CircularProgressIndicator()
        }
        is com.example.friendupp.model.Response.Failure->{
        }
        else->{}
    }
    }
}

@Composable
fun ProfileInfo(name:String,username:String,profilePictureUrl:String,location:String,description:String,onEvent: (ProfileEvents) -> Unit){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)) {
        Row (verticalAlignment = Alignment.CenterVertically){
            Box(modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profilePictureUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_profile_300),
                    contentDescription = "stringResource(R.string.description)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
                if(profilePictureUrl.equals(DEFAULT_PROFILE_PICTURE_URL)){
                    Box(   modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .clickable(onClick = { onEvent(ProfileEvents.OpenCamera) })
                        .background(Color.Black.copy(0.5f)), contentAlignment = Alignment.Center){

                        Icon(painter = painterResource(id = R.drawable.ic_add_image), contentDescription =null ,tint=Color.White)
                    }
                }

            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val truncatedName = if (name.length > 30) name.substring(0, 30)+"..." else name
                Text(
                    text = truncatedName,
                    style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Medium, fontSize = 16.sp),
                    color = SocialTheme.colors.textPrimary
                )
                val truncatedUsername = if (username.length > 30) username.substring(0, 30)+"..." else username
                Text(
                    text = truncatedUsername,
                    style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Light, fontSize = 12.sp),
                    color = SocialTheme.colors.textPrimary
                )
                val truncatedLocation = if (location.length > 50) location.substring(0, 50)+"..." else location
                Text(
                    text = truncatedLocation,
                    style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Light, fontSize = 12.sp),
                    color = SocialTheme.colors.textPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (description.isEmpty()){

            Text(modifier = Modifier.clickable(onClick ={onEvent(ProfileEvents.GoToEditProfile)}),
                text = "Add bio",
                style = TextStyle(fontSize = 14.sp, fontFamily = Lexend, fontWeight = FontWeight.Light),
                color = SocialTheme.colors.textInteractive
            )
        }else{
            val truncatedDescription = if (description.length > 500) description.substring(0, 500)+"..." else description
            Text(
                text = truncatedDescription,
                style = TextStyle(fontSize = 14.sp, fontFamily = Lexend, fontWeight = FontWeight.SemiBold),
                color = SocialTheme.colors.textPrimary
            )
        }

    }
}

@Composable
fun TagDivider(tags:ArrayList<String> = arrayListOf(Category.SPORTS.label,Category.ComputerGames.label)){
    val expandedTags = remember { mutableStateListOf<Category>( ) }
    // Whenever the selectedTags list changes, update the expandedTags list accordingly
    DisposableEffect(tags) {
        expandedTags.clear()
        expandedTags.addAll(getExpandedTags(tags))
        onDispose { }
    }



    LazyRow (modifier= Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically){
        items(expandedTags){

            Spacer(modifier = Modifier
                .width(24.dp)
                .height(0.5.dp)
                .background(SocialTheme.colors.uiBorder))
            Tag(icon=it.icon, label=it.label)
        }
        item {
            Spacer(modifier = Modifier
                .height(0.5.dp)
                .fillMaxWidth()
                .background(SocialTheme.colors.uiBorder))
        }

    }
}
@Composable
fun Tag(icon:Int,label:String){
    Row() {
        Icon(painter = painterResource(id = icon), contentDescription =null,tint=SocialTheme.colors.textPrimary.copy(0.5f) )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text =label, style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Normal)
            ,color=SocialTheme.colors.textPrimary.copy(0.5f) )
    }
}


@Composable
fun ProfileStats(modifier: Modifier,friendCount:Int,activitiesCreated:Int,usersReached:Int,GoToFriends:()->Unit){
    Row(modifier = modifier,horizontalArrangement = Arrangement.SpaceEvenly) {
        Stat(value=friendCount.toString(), label = "Friends",onClick=GoToFriends)
        Stat(value=activitiesCreated.toString(), label = "Activities",onClick={})
        Stat(value=usersReached.toString(), label = "Participants", onClick = {})

    }
}

@Composable
fun Stat(value:String,label:String,onClick:()->Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Text(text =value, style = TextStyle(fontFamily = Lexend, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),color=SocialTheme.colors.textPrimary )
        Text(text =label, style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Light),color=SocialTheme.colors.textPrimary.copy(0.6f) )
    }
}


@Composable
fun ProfileOptions(onEvent: (ProfileEvents) -> Unit){
    Row(
        Modifier
            .padding(vertical = 16.dp)
            .horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(24.dp))
        ProfileOptionItem(R.drawable.ic_edit,"Edit profile", onClick = {onEvent(ProfileEvents.GoToEditProfile)})
        Spacer(modifier = Modifier.width(12.dp))
        ProfileOptionItem(R.drawable.ic_list,"Friend list", onClick = {onEvent(ProfileEvents.GoToFriendList)})
        Spacer(modifier = Modifier.width(12.dp))
        ProfileOptionItem(R.drawable.ic_link,"Profile link", onClick = {onEvent(ProfileEvents.GetProfileLink)})
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileOptionItem(icon: Int,label: String,onClick:()->Unit){
    val color =SocialTheme.colors.textPrimary.copy(0.8f)
    Card(modifier=Modifier.fillMaxWidth().padding(horizontal = 24.dp),onClick=onClick, shape = RoundedCornerShape(10.dp), elevation = 0.dp, backgroundColor = SocialTheme.colors.uiBackground, contentColor = SocialTheme.colors.uiBackground) {
            Row(
                Modifier
                    .background(SocialTheme.colors.uiBorder.copy(0.2f))
                    .padding(vertical = 12.dp, horizontal = 12.dp)) {
                Icon(painter = painterResource(id = icon), contentDescription =null,tint=color )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text =label, style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    ,color=color)
            }

    }
}

@Composable
fun ProfileDisplayPicker(joinedActivities: MutableList<Activity>,activitiesHistory: MutableList<Activity>) {
    var selectedItem by remember { mutableStateOf("Upcoming") }
    var ifCalendar by remember { mutableStateOf(true) }
    var ifHistory by remember { mutableStateOf(false) }

    Column (Modifier.fillMaxSize()){
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ProfilePickerItem(
                label = "Upcoming",
                icon = R.drawable.ic_calendar_upcoming,
                selected = selectedItem == "Upcoming",
                onItemSelected = {
                    selectedItem = "Upcoming"
                    ifCalendar = true
                    ifHistory = false
                }
            )
            ProfilePickerItem(
                label = "History",
                icon = R.drawable.ic_history,
                selected = selectedItem == "History",
                onItemSelected = {
                    selectedItem = "History"
                    ifCalendar = false
                    ifHistory = true
                }
            )
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SocialTheme.colors.uiBorder))

        AnimatedVisibility(visible = ifCalendar) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    ProfileCalendar(modifier=Modifier.weight(1f),joinedActivities)
                }
            }

        }
        AnimatedVisibility(visible = ifHistory) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    ProfileHistory(modifier=Modifier.weight(1f),activitiesHistory)

                }
            }
        }
    }
}
@Composable
fun ProfilePickerItem(label: String, icon: Int, selected: Boolean, onItemSelected: () -> Unit) {
    var color = if (selected)SocialTheme.colors.textLink else SocialTheme.colors.uiBorder
    Column(
        Modifier
            .clickable(onClick = onItemSelected)
            .padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(id = icon), tint = color, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Normal),
                color = color
            )
        }
        if(selected){
            Spacer(modifier = Modifier
                .width(120.dp)
                .height(2.dp)
                .background(color))
        }

    }
}

@Composable
fun ProfileCalendar(modifier:Modifier,joinedActivities:MutableList<Activity>){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(SocialTheme.colors.uiBorder.copy(0.1f))
    ) {
        items(joinedActivities) { activity ->
            activityItem(
                activity,
                onClick = {
                    // Handle click event
                },
                onEvent = { event->
                    handleActivityEvent(event, onEvent = {  })
                }
            )
        }
    }
}

@Composable
fun ProfileHistory(modifier:Modifier,activitiesHistory: MutableList<Activity>){

    Column(
        modifier
            .fillMaxWidth()
            .background(SocialTheme.colors.uiBorder.copy(0.2f))
           ){



    }
}




private fun handleActivityEvent(event: ActivityEvents, onEvent: (ProfileEvents) -> Unit) {
    when (event) {
        is ActivityEvents.Expand -> {
            onEvent(ProfileEvents.ExpandActivity(event.activity))
        }
        is ActivityEvents.Join -> {
            onEvent(ProfileEvents.JoinActivity(event.activity))
        }
        is ActivityEvents.Leave -> {
            onEvent(ProfileEvents.LeaveActivity(event.activity))
        }
        is ActivityEvents.OpenChat -> {
            onEvent(ProfileEvents.OpenChat(event.id))
        }
        is ActivityEvents.GoToProfile->{
            onEvent(ProfileEvents.GoToProfile(event.id))
        }
        is ActivityEvents.Bookmark->{
            onEvent(ProfileEvents.Bookmark(event.id))
        }
        is ActivityEvents.UnBookmark->{
            onEvent(ProfileEvents.UnBookmark(event.id))
        }
    }
}