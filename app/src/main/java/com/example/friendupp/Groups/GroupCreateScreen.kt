package com.example.friendupp.Groups

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.*
import com.example.friendupp.R

sealed class GroupCreateEvents{
    object GoBack:GroupCreateEvents()
    object OpenCamera:GroupCreateEvents()
}


@Composable
fun GroupsCreateScreen(modifier: Modifier=Modifier,onEvent:(GroupCreateEvents)->Unit){
    val focusRequester = remember { FocusRequester() }

    val titleState by rememberSaveable(stateSaver = TitleStateSaver) {
        mutableStateOf(TitleState())
    }
    var progressBlocked by rememberSaveable {
        mutableStateOf(false)
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeading(title = "Create group", backButton = true, onBack = {onEvent(GroupCreateEvents.GoBack)}) {}
        CreateHeading("Group name", icon = R.drawable.ic_edit)
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Name", textState = titleState
        )

        BottomBarCreate(photo ="",
            onClick = {

            },
            createClicked = {

            },
            openCamera = { onEvent(GroupCreateEvents.OpenCamera) },disabled=progressBlocked)
    }
}