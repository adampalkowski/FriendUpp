package com.example.friendupp.Profile

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.ActivityUi.activityItem
import com.example.friendupp.Categories.Category
import com.example.friendupp.ChatUi.ButtonAdd

import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.R
import com.example.friendupp.model.Activity

import com.example.friendupp.model.User

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
}



@Composable
fun ProfileScreen(modifier: Modifier, onEvent: (ProfileEvents) -> Unit, user: User, onClick: () -> Unit) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher


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
                description =user.biography
            )
            TagDivider(tags=user.tags)
            ProfileStats(modifier=Modifier.fillMaxWidth(), activitiesCreated = user.activitiesCreated, friendCount = user.friends_ids.size, usersReached = user.usersReached)
            ProfileOptions(onEvent=onEvent)

        } }



        item {   ProfileDisplayPicker()}
    }

}

@Composable
fun ProfileInfo(name:String,username:String,profilePictureUrl:String,location:String,description:String){
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
                    .size(90.dp)
                    .clip(CircleShape)
            )
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
        Spacer(modifier = Modifier.height(16.dp))
        val truncatedDescription = if (description.length > 500) description.substring(0, 500)+"..." else description
        Text(
            text = truncatedDescription,
            style = TextStyle(fontSize = 14.sp, fontFamily = Lexend, fontWeight = FontWeight.SemiBold),
            color = SocialTheme.colors.textPrimary
        )
    }
}

@Composable
fun TagDivider(tags:ArrayList<com.example.friendupp.Categories.Category> = arrayListOf(Category.SPORTS,Category.ComputerGames)){
    LazyRow (modifier= Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically){
        items(tags){

            Spacer(modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(SocialTheme.colors.uiBorder))
            Tag(icon=it.icon, label=it.label)
        }
        item {
            Spacer(modifier = Modifier
                .height(1.dp).fillMaxWidth()
                .background(SocialTheme.colors.uiBorder))
        }

    }
}
@Composable
fun Tag(icon:Int,label:String){
    Row() {
        Icon(painter = painterResource(id = icon), contentDescription =null,tint=SocialTheme.colors.textPrimary.copy(0.7f) )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text =label, style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),color=SocialTheme.colors.textPrimary.copy(0.7f) )
    }
}


@Composable
fun ProfileStats(modifier: Modifier,friendCount:Int,activitiesCreated:Int,usersReached:Int){
    Row(modifier = modifier,horizontalArrangement = Arrangement.SpaceEvenly) {
        Stat(value=friendCount.toString(), label = "Friends")
        Stat(value=activitiesCreated.toString(), label = "Activities")
        Stat(value=usersReached.toString(), label = "Participants")

    }
}

@Composable
fun Stat(value:String,label:String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
    Card(onClick=onClick, shape = RoundedCornerShape(10.dp), elevation = 0.dp, backgroundColor = SocialTheme.colors.uiBackground, contentColor = SocialTheme.colors.uiBackground) {
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
fun ProfileDisplayPicker() {
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
            ProfileCalendar(modifier=Modifier.weight(1f))
        }
        AnimatedVisibility(visible = ifHistory) {
            ProfileHistory(modifier=Modifier.weight(1f))
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
val activity = Activity(
    image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRdWGlV7yT3SV_JMbf1brUQdhWwOMA3Tx6lmg&usqp=CAU",
    id = "activity123",
    title = "Hiking Adventure",
    date = "2023-06-10",
    start_time = "09:00 AM",
    time_length = "3 hours",
    creator_id = "user123",
    description = "Join us for an exciting hiking adventure in the mountains!",
    creator_username = "john_doe",
    creator_name = "John Doe",
    creator_profile_picture = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRON6j1rnxsQqaSa9cbOv-v_s3tUowQWnsk6Q&usqp=CAU"
    ,    time_left = "2 days",
    end_time = "12:00 PM",
    geoHash = null,
    lat = null,
    lng = null,
    custom_location = "Mountain Peak",
    minUserCount = 2,
    maxUserCount = 10,
    disableChat = false,
    likes = 5,
    invited_users = arrayListOf("user456", "user789"),
    participants_profile_pictures = hashMapOf("user456" to "https://example.com/profile_picture2.jpg", "user789" to "https://example.com/profile_picture3.jpg"),
    participants_usernames = hashMapOf("user456" to "jane_smith", "user789" to "alice_johnson"),
    creation_time = "2023-06-08 14:00",
    location = "Mountain Range",
    pictures = hashMapOf("pic1" to "https://example.com/pic1.jpg", "pic2" to "https://example.com/pic2.jpg"),
    enableActivitySharing = true,
    disablePictures = false,
    disableNotification = false,
    privateChat = false,
    public = true,
    participants_ids = arrayListOf("user456", "user789"),
    awaitConfirmation = false,
    requests = arrayListOf(),
    reports = 0,
    tags = arrayListOf(Category.CREATIVE,Category.SOCIAL)
)
@Composable
fun ProfileCalendar(modifier:Modifier){


    Column(
        modifier
            .fillMaxWidth()
            .background(SocialTheme.colors.uiBorder.copy(0.1f))
        ){
        activityItem(
            activity=activity,
            onClick = {
            }, onExpand ={}
        )
        activityItem(
            activity=activity,
            onClick = {
            }, onExpand ={}
        )
    }
}

@Composable
fun ProfileHistory(modifier:Modifier){

    Column(
        modifier
            .fillMaxWidth()
            .background(SocialTheme.colors.uiBorder.copy(0.2f))
           ){

        activityItem(
            activity=activity,
            onClick = {
            }, onExpand ={}
        )

    }
}







