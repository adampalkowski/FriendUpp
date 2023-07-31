package com.palkowski.friendupp.Home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkowski.friendupp.Home.Live.CreateLive
import com.palkowski.friendupp.Home.Live.LiveUserItem
import com.palkowski.friendupp.R
import com.palkowski.friendupp.model.ActiveUser
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.model.UserData
import com.palkowski.friendupp.ui.theme.SocialTheme



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionPicker(onEvent: (HomeEvents) -> Unit,    openFilter: () -> Unit,  onClick: () -> Unit,
                 calendarClicked: Boolean,
                 filterClicked: Boolean,
                 displayFilters:Boolean=true
                 ,activeUsersReponse:Response<List<ActiveUser>>
                 ,currentUserActiveUser:Response<List<ActiveUser>>) {

    val dividerColor = SocialTheme.colors.uiBorder
    Box(modifier = Modifier, contentAlignment = Alignment.Center){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(dividerColor))
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()

    ) {

        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(dividerColor)
        )


        LazyRow(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            item{
                AnimatedVisibility(visible = displayFilters, enter = slideInHorizontally(), exit = slideOutHorizontally()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SocialButtonNormal(
                            icon = R.drawable.ic_filte_300,
                            onClick = openFilter,
                            filterClicked
                        )
                        Spacer(modifier = Modifier
                            .width(12.dp)
                            .height(1.dp)
                            .background(dividerColor))
                        SocialButtonNormal(
                            icon = R.drawable.ic_calendar_300,
                            onClick = onClick,
                            calendarClicked
                        )
                        Spacer(
                            modifier = Modifier
                                .width(24.dp)
                                .height(1.dp)
                                .background(dividerColor)
                        )
                    }

                }
            }

            item{
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .background(dividerColor)
                )
            }

            when(currentUserActiveUser){
                is Response.Success->{
                    items(currentUserActiveUser.data){activeUser->
                        LiveUserItem(
                            text = activeUser.note,
                            imageUrl =activeUser.participants_profile_pictures.get(activeUser.creator_id).toString(),
                            onClick = {onEvent(HomeEvents.OpenLiveUser(activeUser.creator_id))}, clickable = true
                        )

                    }
                }
                else->{
                    item{
                        CreateLive(
                            onClick = { onEvent(HomeEvents.CreateLive) },
                            imageUrl =  UserData.user!!.pictureUrl.toString()

                        )


                    }
                }
            }

            when(activeUsersReponse){
                is Response.Success->{
                    items(activeUsersReponse.data){activeUser->
                        when(currentUserActiveUser){
                            is Response.Success->{
                                if (activeUsersReponse.data.isNotEmpty() && activeUser==currentUserActiveUser.data.get(0)){
                                }else{

                                    LiveUserItem(
                                        text = activeUser.note,
                                        imageUrl =activeUser.participants_profile_pictures.get(activeUser.creator_id).toString(),
                                        onClick = {onEvent(HomeEvents.OpenLiveUser(activeUser.creator_id))}
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(8.dp)
                                            .background(dividerColor)
                                    )
                                }
                            }
                            else->{

                                    LiveUserItem(
                                        text = activeUser.note,
                                        imageUrl =activeUser.participants_profile_pictures.get(activeUser.creator_id).toString(),
                                        onClick = {onEvent(HomeEvents.OpenLiveUser(activeUser.creator_id))}
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(8.dp)
                                            .background(dividerColor)
                                    )
                            }
                        }


                    }
                }
                is Response.Failure->{}
                is Response.Loading->{
                    item {
                        CircularProgressIndicator()

                    }
                }
            }

        }




    }
    }


}
