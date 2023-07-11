package com.example.friendupp.Groups

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import com.example.friendupp.bottomBar.ActivityUi.ActivityState
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.*
import com.example.friendupp.R

sealed class GroupCreateEvents {
    object GoBack : GroupCreateEvents()
    object GoToFriendPicker : GroupCreateEvents()
    object OpenCamera : GroupCreateEvents()
}


@Composable
fun GroupsCreateScreen(
    modifier: Modifier = Modifier,
    onEvent: (GroupCreateEvents) -> Unit,
    groupState: GroupState,
) {
    val focusRequester = remember { FocusRequester() }
    val titleState = groupState.groupName
    val descriptionState = groupState.descriptionState
    val selectedOption = groupState.selectedOptionState

    var progressBlocked by rememberSaveable {
        mutableStateOf(false)
    }
    progressBlocked = !titleState!!.isValid
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        GroupTopBar(onClick = {
            onEvent(GroupCreateEvents.GoBack)
        },
            selectedOption = selectedOption!!.option,
            onPublic = {
                selectedOption.option = Option.PUBLIC
            },
            onFriends = {
                selectedOption!!.option = Option.FRIENDS
            })
        CreateHeading("Group name & description", icon = R.drawable.ic_edit)
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Name", textState = titleState
        )
        Spacer(modifier = Modifier.height(12.dp))
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Description", textState = descriptionState
        )
        TagsSettings(
            tags = groupState.tags,
            onSelected = { groupState.tags.add(it) },
            onDeSelected = { groupState.tags.remove(it) })
        Spacer(modifier = Modifier.weight(1f))
        GroupBottomBar(
            photo = groupState.imageUrl,
            onClick = {
            },
            createClicked = {
                onEvent(GroupCreateEvents.GoToFriendPicker)
            },
            openCamera = { onEvent(GroupCreateEvents.OpenCamera) }, disabled = progressBlocked
        )
    }
}