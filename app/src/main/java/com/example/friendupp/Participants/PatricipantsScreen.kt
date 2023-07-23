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
import com.example.friendupp.model.Participant
import com.example.friendupp.model.User

sealed class ParticipantsEvents{
    object GoBack:ParticipantsEvents()
    class GoToUserProfile(val id:String):ParticipantsEvents()
    object GetMoreParticipants:ParticipantsEvents()
}


@Composable
fun ParticipantsScreen(modifier:Modifier,onEvent:(ParticipantsEvents)->Unit ,participantsList:List<Participant>,isLoading:Boolean){
    BackHandler(true) {
        onEvent(ParticipantsEvents.GoBack)
    }


    Column(modifier=modifier) {
        ScreenHeading(title = "Participants", onBack = {onEvent(ParticipantsEvents.GoBack)}, backButton = true) {}
        LazyColumn{
            items(participantsList){
                    participant ->
                FriendItem(
                    username = participant.username.toString(),
                    pictureUrl = participant.profile_picture.toString(),
                    onEvent = { onEvent(ParticipantsEvents.GoToUserProfile(participant.id)) },
                    name=participant.name
                )
            }
            item {
                Spacer(modifier = Modifier.height(58.dp))

            }
            item {
                LaunchedEffect(Unit){
                       onEvent(ParticipantsEvents.GetMoreParticipants)
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
