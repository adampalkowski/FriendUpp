package com.example.friendupp.Groups

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.ChatUi.ChatSettingItem
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.CreateButton
import com.example.friendupp.Create.CreateEvents
import com.example.friendupp.Create.CreateHeading
import com.example.friendupp.Home.eButtonSimpleBlue
import com.example.friendupp.Profile.*
import com.example.friendupp.R
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.model.*
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class GroupsEvents{
    object CreateGroup:GroupsEvents()
    object GoBack:GroupsEvents()
    object GoToFriendPicker:GroupsEvents()
    class GoToGroupDisplay(val groupId:String):GroupsEvents()
}


@Composable
fun GroupsScreen(modifier: Modifier = Modifier,onEvent:(GroupsEvents)->Unit,chatViewModel: ChatViewModel){
    val groupFlow =  chatViewModel.groupsState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val usernameState by rememberSaveable(stateSaver = UsernameStateSaver) {
        mutableStateOf(UsernameState())
    }

    val groups = remember { mutableStateListOf<Chat>() }
    val moreGroups = remember { mutableStateListOf<Chat>() }
    groupsLoading(chatViewModel = chatViewModel, groupList =groups , moreGroupList =moreGroups , id =UserData.user!!.id )
    BackHandler(true) {
        onEvent(GroupsEvents.GoBack)
    }

    Column(modifier=modifier) {
        ScreenHeading(title = "Groups", backButton = true, onBack = {onEvent(GroupsEvents.GoBack)}) {
            Row(Modifier.weight(1f)) {
                ButtonAdd(onClick = {onEvent(GroupsEvents.CreateGroup)}, icon = R.drawable.ic_add)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NameEditText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                focusRequester = focusRequester,
                focus = false,
                onFocusChange = { focusState ->

                }, label = "Search by group name", textState = usernameState
            )
            Spacer(modifier = Modifier.width(12.dp))
            eButtonSimpleBlue(icon = R.drawable.ic_search, onClick = {}, modifier = Modifier.padding(top=8.dp))
        }
        CreateHeading(text = "Your groups", icon = R.drawable.ic_group, tip = false)
        val context =LocalContext.current
        LazyColumn {
            items(groups){group->
                GroupItem(groupname = group.name.toString(),
                    description = group.description.toString(),
                    groupPicture =group.imageUrl.toString(),
                    numberOfUsers =group.members.size.toString(),
                    isCreator = group.owner_id==UserData.user!!.id,
                    onEvent = {
                        Toast.makeText(context,group.id.toString(),Toast.LENGTH_SHORT).show()
                        onEvent(GroupsEvents.GoToGroupDisplay(group.id.toString()))

                    })
            }
            items(moreGroups){group->
                GroupItem(groupname = group.name.toString(),
                    description = group.description.toString(),
                    groupPicture =group.imageUrl.toString(),
                    numberOfUsers =group.members.size.toString(),
                    isCreator = group.owner_id==UserData.user!!.id,
                    onEvent = {
                        Toast.makeText(context,group.id.toString(),Toast.LENGTH_SHORT).show()
                        onEvent(GroupsEvents.GoToGroupDisplay(group.id.toString()))

                    })
            }
            }
        }

}


sealed class GroupItemEvent{
    object GoBack:GroupItemEvent()
    object GoToAddFriends:GroupItemEvent()
    object GoToChat:GroupItemEvent()
    object Share:GroupItemEvent()
    object RemoveFriend:GroupItemEvent()
    object BlockFriend:GroupItemEvent()
}

@Composable
fun GroupItem(groupname:String,description:String,numberOfUsers:String,groupPicture:String,isCreator:Boolean,onEvent: () -> Unit){
    var expand by remember { mutableStateOf(false) }

    Column() {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .clickable(onClick = { onEvent() })
            .padding(horizontal = 24.dp, vertical = 8.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(groupPicture)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape))
            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = groupname, style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 16.sp), color = SocialTheme.colors.textPrimary)
                Text(text = description, style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Normal, fontSize = 14.sp), color = SocialTheme.colors.textPrimary.copy(0.6f))
            }
            Box(modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(36.dp)
                .background(SocialTheme.colors.uiBorder.copy(0.2f)), contentAlignment = Alignment.Center){
                Icon(painter = painterResource(id =R.drawable.arrow_right), contentDescription =null, tint = SocialTheme.colors.textPrimary.copy(0.8f))
            }

        }

        AnimatedVisibility(visible = expand) {
            Column() {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    if(isCreator){
                        ChatSettingItem(label ="Chat" , icon =R.drawable.ic_chat_300 ,onClick={
                        })
                        ChatSettingItem(label ="Share" , icon =R.drawable.ic_share ,onClick={
                        })

                        ChatSettingItem(label ="Leave" , icon =R.drawable.ic_logout,onClick={
                        } )
                        ChatSettingItem(label ="Delete" , icon =R.drawable.ic_delete ,onClick={
                        })
                        ChatSettingItem(label ="Add users" , icon =R.drawable.ic_group_add ,onClick={
                        })

                    }else{
                        ChatSettingItem(label ="Chat" , icon =R.drawable.ic_chat_300 ,onClick={
                        })
                        ChatSettingItem(label ="Share" , icon =R.drawable.ic_share ,onClick={
                        })

                        ChatSettingItem(label ="Leave" , icon =R.drawable.ic_logout,onClick={
                        } )

                    }

                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(SocialTheme.colors.uiBorder))
            }

        }
    }

}
