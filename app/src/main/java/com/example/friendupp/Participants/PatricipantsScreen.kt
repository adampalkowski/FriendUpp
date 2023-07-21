package com.example.friendupp.Participants

import androidx.activity.compose.BackHandler
import androidx.camera.core.ImageProcessor.Response
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.friendupp.ChatUi.ChatSettingsEvents
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.MapEvent
import com.example.friendupp.Profile.FriendItem
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.User

sealed class ParticipantsEvents{
    object GoBack:ParticipantsEvents()
    class GoToUserProfile(val id:String):ParticipantsEvents()
}


@Composable
fun ParticipantsScreen(modifier:Modifier,userViewModel: UserViewModel,onEvent:(ParticipantsEvents)->Unit,activityId:String){
    BackHandler(true) {
        onEvent(ParticipantsEvents.GoBack)
    }
    val users = remember { mutableStateListOf<User>() }
    var gotUsers= remember {
        mutableStateOf(false)
    }
    val moreUsers = remember { mutableStateListOf<User>() }

    loadUsers(userViewModel=userViewModel,users=users,moreUsers=moreUsers,gotUsers=gotUsers)

    Column(modifier=modifier) {
        ScreenHeading(title = "Participants", onBack = {onEvent(ParticipantsEvents.GoBack)}, backButton = true) {}
        LazyColumn{
            items(users){
                    user ->
                FriendItem(
                    username = user.username.toString(),
                    name = user.name.toString(),
                    pictureUrl = user.pictureUrl.toString(),
                    onEvent = { onEvent(ParticipantsEvents.GoToUserProfile(it)) },
                    user = user
                )
            }
            items(moreUsers){
                    user ->
                FriendItem(
                    username = user.username.toString(),
                    name = user.name.toString(),
                    pictureUrl = user.pictureUrl.toString(),
                    onEvent ={ onEvent(ParticipantsEvents.GoToUserProfile(it)) },
                    user = user
                )
            }
            item {
                Spacer(modifier = Modifier.height(58.dp))

            }
            item {
                LaunchedEffect(gotUsers){
                    if(gotUsers.value){
                        userViewModel.getMoreActivityUsers(activityId)
                    }
                }
            }
        }

    }



}

@Composable
fun loadUsers(userViewModel: UserViewModel, users:MutableList<User>, moreUsers:MutableList<User>,gotUsers:MutableState<Boolean>){

    userViewModel.activityUsersState.value.let { response->
        when(response){
            is com.example.friendupp.model.Response.Loading->{}
            is com.example.friendupp.model.Response.Success->{
                users.clear()
                users.addAll(response.data)
                userViewModel.clearUsers()
                gotUsers.value=true
            }
            is com.example.friendupp.model.Response.Failure->{}
            else->{}
        }
    }
    userViewModel.moreActivityUsersState.value.let { response->
        when(response){
            is com.example.friendupp.model.Response.Loading->{}
            is com.example.friendupp.model.Response.Success->{
                moreUsers.clear()
                moreUsers.addAll(response.data)
                userViewModel.clearMoreUsers()
            }
            is com.example.friendupp.model.Response.Failure->{}
            else->{}

        }
    }
}
