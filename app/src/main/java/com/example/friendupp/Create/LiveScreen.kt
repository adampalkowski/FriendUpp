package com.example.friendupp.Create

import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class LiveScreenEvents{


    object GoBack:LiveScreenEvents()
    object GoToFriendPicker:LiveScreenEvents()

}



@Composable
fun LiveScreen(onEvent:(LiveScreenEvents)->Unit){
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeading(title = "Go live", backButton = true, onBack = {
            onEvent(LiveScreenEvents.GoBack)
        }){}

        Spacer(modifier = Modifier.height(24.dp))
        CreateHeading("Time", icon = com.example.friendupp.R.drawable.ic_time)
        TimePicker(startTime = "12:39", endTime ="17:30", lockStartTime = true)
        NoteComponent()
        LocationComponent()

        Spacer(modifier = Modifier.weight(1f))
        CreateButton(modifier = Modifier.padding(horizontal = 24.dp), text = "Go live")
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun LocationComponent() {
    var shareLocation by remember { mutableStateOf(false) }
    var grayColor = SocialTheme.colors.uiBorder.copy(0.6f)
    Column() {
        CreateHeading("Share current location", icon = com.example.friendupp.R.drawable.ic_location)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Your current location will be displayed on map", style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Normal, fontSize = 14.sp), color = SocialTheme.colors.textPrimary)
            Switch(
                checked = shareLocation,
                onCheckedChange = {shareLocation=!shareLocation},
                modifier = Modifier.padding(start = 16.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SocialTheme.colors.textInteractive,
                    checkedTrackColor =grayColor
                    , uncheckedTrackColor =grayColor,

                    )
            )
        }

    }
}


@Composable
fun NoteComponent(){
    val focusRequester = remember { FocusRequester() }
    val noteState by rememberSaveable(stateSaver =NoteStateSaver) {
        mutableStateOf(NoteState())
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading("Add text note", icon = com.example.friendupp.R.drawable.ic_edit)
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Note", textState = noteState
        )

    }


}
