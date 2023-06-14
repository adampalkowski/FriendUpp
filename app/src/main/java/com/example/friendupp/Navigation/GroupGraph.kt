package com.example.friendupp.Navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.friendupp.Groups.*
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.UserData
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.groupGraph(navController: NavController,chatViewModel:ChatViewModel) {
    navigation(startDestination = "Groups", route = "GroupGraph") {
        composable("Groups") {
            chatViewModel.getGroups(UserData.user!!.id)

            GroupsScreen(onEvent = {event->
                when(event){
                    is GroupsEvents.CreateGroup->{navController.navigate("GroupsCreate")}
                    is GroupsEvents.GoBack->{navController.navigate("Home")}
                    is GroupsEvents.GoToGroupDisplay->{navController.navigate("GroupDisplay/"+event.groupId)}
                    else ->{}
                }
            }, chatViewModel = chatViewModel)
        }
        composable("GroupsCreate") {
            GroupsCreateScreen(onEvent = {event->
                when(event){
                    is GroupCreateEvents.GoBack->{navController.navigate("Groups")}
                    is GroupCreateEvents.OpenCamera->{}
                }
            })
        }

        composable("GroupDisplay/{groupId}"   ,arguments = listOf(navArgument("groupId") { type = NavType.StringType }),) {
                backStackEntry ->
            val group = Chat(
                create_date = "2023-06-07",
                owner_id = "user123",
                id = "chat123",
                members = listOf("user123", "user456"),
                name = "Example Chat",
                imageUrl = "https://example.com/chat_image.jpg",
                recent_message_time = "2023-06-07T10:30:00",
                recent_message = "Hello, how are you?",
                type = "duo",
                user_one_username = "User1",
                user_one_profile_pic = "https://example.com/user1_profile_pic.jpg",
                user_two_username = "User2",
                user_two_profile_pic = "https://example.com/user2_profile_pic.jpg",
                highlited_message = "Important message",
                description = "This is an example chat",
                numberOfUsers = 2,
                numberOfActivities = 5
            )
            val groupId=   backStackEntry.arguments?.getString("groupId")
            GroupDisplayScreen(modifier = Modifier,group=group, onEvent = {
                event->
                when(event){
                    is GroupDisplayEvents.GoBack->{navController.navigate("Groups")}
                    else->{}
                }

            })
        }
    }
}

