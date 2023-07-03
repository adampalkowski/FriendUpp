package com.example.friendupp.Home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.friendupp.Home.Live.CreateLive
import com.example.friendupp.Home.Live.LiveUserItem
import com.example.friendupp.R
import com.example.friendupp.model.ActiveUser
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.SocialTheme



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionPicker(onEvent: (HomeEvents) -> Unit,    openFilter: () -> Unit,  onClick: () -> Unit,
                 calendarClicked: Boolean,
                 filterClicked: Boolean,displayFilters:Boolean=true,activeUsers:MutableList<ActiveUser>,moreActiveUsers:MutableList<ActiveUser>,currentUserActiveUser:MutableList<ActiveUser>) {
    val dividerColor = SocialTheme.colors.uiBorder
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
        LazyRow(verticalAlignment = Alignment.CenterVertically){
            item{
                if (currentUserActiveUser.isNotEmpty()){
                }else{
                    CreateLive(
                        onClick = { onEvent(HomeEvents.CreateLive) },
                        imageUrl =  UserData.user!!.pictureUrl.toString()

                    )
                    Spacer(
                        modifier = Modifier
                            .width(8.dp)
                            .height(1.dp)
                            .background(dividerColor)
                    )
                }

            }
            items(currentUserActiveUser){activeUser->
                LiveUserItem(
                    text = activeUser.note,
                    imageUrl =activeUser.participants_profile_pictures.get(activeUser.creator_id).toString(),
                    onClick = {onEvent(HomeEvents.OpenLiveUser(activeUser.creator_id))}, clickable = true
                )
            }
            items(activeUsers){activeUser->
                if (currentUserActiveUser.isNotEmpty() && activeUser==currentUserActiveUser.get(0)){
                }else{
                    LiveUserItem(
                        text = activeUser.note,
                        imageUrl =activeUser.participants_profile_pictures.get(activeUser.creator_id).toString(),
                        onClick = {onEvent(HomeEvents.OpenLiveUser(activeUser.creator_id))}
                    )
                    Spacer(
                        modifier = Modifier
                            .width(8.dp)
                            .height(1.dp)
                            .background(dividerColor)
                    )
                }

            }
            items(moreActiveUsers){activeUser->
                LiveUserItem(
                    text = activeUser.note,
                    imageUrl =activeUser.participants_profile_pictures.get(activeUser.creator_id).toString(),
                    onClick = {onEvent(HomeEvents.OpenLiveUser(activeUser.creator_id))}
                )
                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                        .height(1.dp)
                        .background(dividerColor)
                )
            }
        }




    }


}
