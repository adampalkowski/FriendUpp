package com.example.friendupp.ActivityPreview

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.navigation.NavController
import com.example.friendupp.Home.HomeViewModel
import com.example.friendupp.Navigation.sendNotification
import com.example.friendupp.R
import com.example.friendupp.bottomBar.ActivityUi.ActivityEvents
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.di.UserViewModel
import com.example.friendupp.model.UserData


 fun handleActivityEvents(event: ActivityEvents,navController:NavController,activityViewModel:ActivityViewModel
                        ,userViewModel:UserViewModel,homeViewModel:HomeViewModel,context: Context
 ) {
    when (event) {

        is ActivityEvents.GoToProfile -> {
            navController.navigate("ProfileDisplay/${event.id}")
        }
        is ActivityEvents.Bookmark -> {
            activityViewModel.bookMarkActivity(event.id, UserData.user!!.id)
        }
        is ActivityEvents.UnBookmark -> {
            activityViewModel.unBookMarkActivity(event.id, UserData.user!!.id)
        }
        is ActivityEvents.Join -> {
            if (event.activity.participants_ids.size < 6) {
                userViewModel.addActivityToUser(event.activity.id, UserData.user!!)
                activityViewModel.likeActivity(event.activity.id, UserData.user!!)
                if (event.activity.creator_id != UserData.user!!.id) {
                    sendNotification(
                        receiver = event.activity.creator_id,
                        picture = UserData.user!!.pictureUrl,
                        message = "${UserData.user?.username} joined your activity",
                        title =context.getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE),
                        username = "",
                        id = event.activity.id
                    )
                }
            } else {
                userViewModel.addActivityToUser(event.activity.id, UserData.user!!)
                activityViewModel.likeActivityOnlyId(event.activity.id, UserData.user!!)
                if (event.activity.creator_id != UserData.user!!.id) {
                    sendNotification(
                        receiver = event.activity.creator_id,
                        picture = UserData.user!!.pictureUrl,
                        message = "${UserData.user?.username} joined your activity",
                        title = context.getString(R.string.NOTIFICATION_JOINED_ACTIVITY_TITLE),
                        username = "",
                        id = event.activity.id
                    )
                }
            }
        }
        is ActivityEvents.OpenChat -> {
            navController.navigate("ChatItem/${event.id}")
        }
        is ActivityEvents.Leave -> {
            if (event.activity.participants_usernames.containsKey(UserData.user!!.id)) {
                userViewModel.removeActivityFromUser(
                    id = event.activity.id,
                    user_id = UserData.user!!.id
                )

                activityViewModel?.unlikeActivity(
                    event.activity.id,
                    UserData.user!!.id
                )
            } else {
                userViewModel.removeActivityFromUser(
                    id = event.activity.id,
                    user_id = UserData.user!!.id
                )

                activityViewModel?.unlikeActivityOnlyId(
                    event.activity.id,
                    UserData.user!!.id
                )
            }
        }
        is ActivityEvents.Expand -> {
            homeViewModel.setExpandedActivity(event.activity)
            navController.navigate("ActivityPreview")
        }
        is ActivityEvents.GoBack->{
            navController.popBackStack()
        }
        else->{}
    }
}


