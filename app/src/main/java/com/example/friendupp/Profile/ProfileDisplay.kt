package com.example.friendupp.Profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.R
import com.example.friendupp.bottomBar.ActivityUi.ActivityEvents
import com.example.friendupp.bottomBar.ActivityUi.activityItem
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.User
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class ProfileDisplayEvents {
    object GoToSearch : ProfileDisplayEvents()
    class InviteUser(val user_id :String): ProfileDisplayEvents()
    object MainProfile: ProfileDisplayEvents()
    class GetMoreUserActivities(val user_id :String): ProfileDisplayEvents()
    class GetMoreJoinedActivities(val user_id :String): ProfileDisplayEvents()
    class BlockUser(val user_id :String): ProfileDisplayEvents()
    class UnBlock(val user_id :String): ProfileDisplayEvents()
    class ShareProfileLink(val user_id :String): ProfileDisplayEvents()
    class GoToChat(val chat_id :String): ProfileDisplayEvents()
    class RemoveFriend(val user_id :String): ProfileDisplayEvents()
    object GoBack : ProfileDisplayEvents()
    object GoToSettings : ProfileDisplayEvents()
    object GoToEditProfile : ProfileDisplayEvents()

    /*todo*/
    class GoToFriendList (val id :String): ProfileDisplayEvents()
    object GetProfileLink : ProfileDisplayEvents()

    class GoToProfile(val id: String) : ProfileDisplayEvents()
    class OpenChat(val id: String) : ProfileDisplayEvents()
}

enum class UserOption {
    FRIEND,
    INVITED,
    BLOCKED,
    UNKNOWN,
    REQUEST
}


@Composable
fun ProfileDisplayScreen(
    modifier: Modifier,
    onEvent: (ProfileDisplayEvents) -> Unit,
    activityEvents: (ActivityEvents) -> Unit,
    userResponse: Response<User>?,
    context:Context,
    joinedActivitiesResponse: Response<List<Activity>>,
    createdActivitiesResponse: Response<List<Activity>>
) {
    // Handle null safety and loading state
    when(userResponse) {
        is Response.Success->{
            //check if user is me then go to profiel
            if (userResponse.data.id == UserData.user!!.id) {
                onEvent(ProfileDisplayEvents.MainProfile)
            } else if (userResponse.data.blocked_ids.contains(UserData.user!!.id)) {
                onEvent(ProfileDisplayEvents.GoBack)

            }
            // The data has been successfully fetched, and group is not null
            ProfileDisplayContent(modifier=modifier,
                onEvent =onEvent,activityEvents=activityEvents,
                user=userResponse.data
            ,joinedActivitiesResponse=joinedActivitiesResponse,createdActivitiesResponse=createdActivitiesResponse)
        }
        is Response.Loading->{
            // Show a loading indicator while data is being fetched
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is Response.Failure->{
            // Show an error message or navigate back on failure
            Toast.makeText(
                context,
                "Failed to load user profile. Please try again later.",
                Toast.LENGTH_LONG
            ).show()
            // You can also navigate back using the onEvent callback
            onEvent(ProfileDisplayEvents.GoBack)
        }
        else->{
            // Show a loading indicator or some placeholder content
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

    }
}
@Composable
fun ProfileDisplayContent(
    modifier: Modifier,
    onEvent: (ProfileDisplayEvents) -> Unit,
    activityEvents: (ActivityEvents) -> Unit,
    user: User,
    joinedActivitiesResponse: Response<List<Activity>>,
    createdActivitiesResponse: Response<List<Activity>>,
) {

    var displaySettings by remember {
        mutableStateOf(false)
    }

    //LOAD IN PROFILE ACTIVITIES

    var selectedItem by remember { mutableStateOf("Upcoming") }
    var ifCalendar by remember { mutableStateOf(true) }
    var ifHistory by remember { mutableStateOf(false) }



    var joinedActivitiesExist= remember { mutableStateOf(false) }
    var historyActivitiesExist= remember { mutableStateOf(false) }






    // check if is Friend
    var userOption = if(user.friends_ids.containsKey(UserData.user!!.id)){UserOption.FRIEND}
    else if(UserData.user!!.invited_ids.contains(user.id)){
        UserOption.INVITED
    } else if(user.invited_ids.contains((UserData.user!!.id))){
        UserOption.REQUEST
    }
    else{
        UserOption.UNKNOWN
    }

    var isBlocked =UserData.user!!.blocked_ids.contains(user.id)
    userOption= if(isBlocked){UserOption.BLOCKED}else{userOption}


    BackHandler(true) {
        onEvent(ProfileDisplayEvents.GoBack)
    }

    LazyColumn {
        item {
            Column(modifier) {
                ScreenHeading(
                    title = "User",
                    backButton = true,
                    onBack = { onEvent(ProfileDisplayEvents.GoBack) }) {
                        Spacer(modifier = Modifier.weight(1f))
                        ButtonAdd(icon = R.drawable.ic_more, onClick = {
                            displaySettings=true
                        })
                }
                Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoDisplay(
                        name = user.name ?:"",
                        username = user.username?:"",
                        profilePictureUrl = user.pictureUrl?:"",
                        location = user.location,
                        description = user.biography
                    )
                if(userOption!=UserOption.BLOCKED){
                    TagDivider(user.tags)
                    ProfileStats(
                        modifier = Modifier.fillMaxWidth(),
                        user.friends_ids.size,
                        user.activitiesCreated,
                        user.usersReached,
                        GoToFriends = {onEvent(ProfileDisplayEvents.GoToFriendList(user.id))}
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                    ProfileDisplayOptions(userOption,
                        onEvent=onEvent, user_id = user.id, InviteCall = {userOption=UserOption.INVITED})



            }
        }

        item{

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


            }
        }
        if(userOption==UserOption.FRIEND){

        if(ifCalendar){
            when(joinedActivitiesResponse){
                is Response.Success->{
                    items(joinedActivitiesResponse.data) { activity ->
                        activityItem(
                            activity,
                            onClick = {
                                // Handle click event
                            },
                            onEvent = activityEvents
                        )
                    }
                }
                is Response.Loading->{
                    item{
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                }
                is Response.Failure->{

                }
                else->{}
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
            item {
                LaunchedEffect(true) {
                    if (joinedActivitiesExist.value) {
                        onEvent(ProfileDisplayEvents.GetMoreJoinedActivities(UserData.user!!.id))

                    }
                }
            }
        }
        if(ifHistory){
            when(createdActivitiesResponse){
                is Response.Success->{
                    items(createdActivitiesResponse.data) { activity ->
                        activityItem(
                            activity,
                            onClick = {
                                // Handle click event
                            },
                            onEvent = activityEvents
                        )
                    }
                }
                is Response.Loading->{
                    item{
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                }
                is Response.Failure->{

                }
                else->{}
            }
            item {
                Spacer(modifier = Modifier.height(64.dp))

            }
            item {
                LaunchedEffect(true) {
                    if (historyActivitiesExist.value) {
                        onEvent(ProfileDisplayEvents.GetMoreUserActivities(UserData.user!!.id))
                    }
                }
            }
        }
        }else{
            item{
                Spacer(modifier = Modifier.height(24.dp))
                Text(modifier=Modifier.padding(horizontal = 12.dp),textAlign = TextAlign.Center,text = LocalContext.current.getString(R.string.Profile_display_not_friends_history_text), style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Normal, fontSize = 14.sp),color=SocialTheme.colors.iconPrimary)
            }
        }

    }

    AnimatedVisibility(visible = displaySettings) {
        Dialog(onDismissRequest = { displaySettings=false }) {
            ProfileDisplaySettingContent(onCancel={displaySettings=false},onRemoveFriend={
                onEvent(ProfileDisplayEvents.RemoveFriend(user.id))
                displaySettings=false
            },shareProfileLink={
                onEvent(ProfileDisplayEvents.ShareProfileLink(user.id))
                displaySettings=false
            },blockUser={
                onEvent(ProfileDisplayEvents.BlockUser(user.id))
                displaySettings=false
            },userOption)
        }
    }

}

@Composable
fun ProfileDisplayOptions(userOption: UserOption,onEvent: (ProfileDisplayEvents) -> Unit,InviteCall: () -> Unit,user_id:String) {
    when(userOption){
        UserOption.UNKNOWN->{
            ProfileOptionItem(R.drawable.ic_add,"Invite user", onClick = {
                InviteCall()
                onEvent(ProfileDisplayEvents.InviteUser(user_id=user_id))
            })

        }
        UserOption.FRIEND->{
            ProfileOptionItem(R.drawable.ic_chat_300,"Chat", onClick = {
                Log.d("profiledisplay","GO TO click")

                val chat_id=UserData.user!!.friends_ids.get(user_id)
                if(chat_id!=null){
                    Log.d("profiledisplay","GO ")

                    onEvent(ProfileDisplayEvents.GoToChat(chat_id=chat_id))
                }else{
                }

            })

        }
        UserOption.BLOCKED->{
            ProfileOptionItem(R.drawable.ic_block,"Unblock", onClick = {onEvent(ProfileDisplayEvents.UnBlock(user_id=user_id))})

        }
        UserOption.INVITED->{
            ProfileOptionItem(R.drawable.ic_add,"Pending invite...", onClick = {onEvent(ProfileDisplayEvents.InviteUser(user_id=user_id))})
        }
        UserOption.REQUEST->{
            ProfileOptionItem(R.drawable.ic_add,"Accept invite", onClick = {})
        }

    }


}

@Composable
fun ProfileInfoDisplay(name:String,username:String,profilePictureUrl:String,location:String,description:String){
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
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "stringResource(R.string.description)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )


            }

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
                        fontSize = 12.sp
                    ),
                    color = SocialTheme.colors.textPrimary
                )
                val truncatedLocation = if (location.length > 50) location.substring(0, 50)+"..." else location
                androidx.compose.material.Text(
                    text = truncatedLocation,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp
                    ),
                    color = SocialTheme.colors.textPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
            val truncatedDescription = if (description.length > 500) description.substring(0, 500)+"..." else description
            if(truncatedDescription.isNotEmpty()){
                androidx.compose.material.Text(
                    text = truncatedDescription,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SocialTheme.colors.textPrimary
                )
            }


    }
}



@Composable
fun  ProfileDisplaySettingContent(onCancel: () -> Unit={},onRemoveFriend: () -> Unit={},shareProfileLink: () -> Unit={},blockUser: () -> Unit={},userOption: UserOption) {
    Column(Modifier.clip(RoundedCornerShape(24.dp))) {
        ProfileDisplaySettingsItem(label="Share",icon=R.drawable.ic_share, textColor = SocialTheme.colors.textPrimary, onClick = shareProfileLink)

        if(userOption==UserOption.UNKNOWN){
            ProfileDisplaySettingsItem(label="Invite",icon=R.drawable.ic_add , textColor = SocialTheme.colors.error, onClick =onRemoveFriend)
            ProfileDisplaySettingsItem(label="Block",icon=R.drawable.ic_block , textColor = SocialTheme.colors.error, onClick = blockUser)


        }else if(userOption==UserOption.FRIEND){
            ProfileDisplaySettingsItem(label="Remove friend",icon=R.drawable.ic_delete , textColor = SocialTheme.colors.error, onClick =onRemoveFriend)
            ProfileDisplaySettingsItem(label="Block",icon=R.drawable.ic_block , textColor = SocialTheme.colors.error, onClick = blockUser)

        }else if(userOption==UserOption.BLOCKED){
            ProfileDisplaySettingsItem(label="Unblock",icon=R.drawable.ic_unblock , textColor = SocialTheme.colors.error, onClick =onRemoveFriend)

        }else if(userOption==UserOption.INVITED){
            ProfileDisplaySettingsItem(label="Block",icon=R.drawable.ic_block , textColor = SocialTheme.colors.error, onClick = blockUser)

        }


        ProfileDisplaySettingsItem(label="Cancel" , turnOffIcon = true, textColor = SocialTheme.colors.textPrimary.copy(0.5f), onClick = onCancel)
    }
}

@Composable
fun ProfileDisplaySettingsItem(turnOffIcon:Boolean=false,icon:Int=R.drawable.ic_x,label:String,textColor:androidx.compose.ui.graphics.Color=androidx.compose.ui.graphics.Color.Black,onClick: () -> Unit={}) {
    Column(Modifier.background(SocialTheme.colors.uiBackground)) {

        
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(vertical = 16.dp, horizontal = 12.dp), horizontalArrangement =Arrangement.Center){
        if(!turnOffIcon){
            Icon(painter = painterResource(id = icon), contentDescription =null,tint=textColor )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(text =label, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, fontFamily = Lexend), color = textColor )
        if(!turnOffIcon){
            Spacer(modifier = Modifier.width(32.dp))

        }
    }
    Box(modifier = Modifier
        .height(0.5.dp)
        .fillMaxWidth()
        .background(SocialTheme.colors.uiBorder))
    }
}

