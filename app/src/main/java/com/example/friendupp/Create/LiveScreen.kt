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
import com.example.friendupp.Components.*
import com.example.friendupp.Login.EmailState
import com.example.friendupp.Login.PasswordState
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import java.util.*


sealed class LiveScreenEvents{


    object GoBack:LiveScreenEvents()
    object GoToFriendPicker:LiveScreenEvents()

}



@Composable
fun LiveScreen(onEvent:(LiveScreenEvents)->Unit){

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    calendar.add(Calendar.HOUR_OF_DAY, 2) // Add one hour
    val endhour = calendar.get(Calendar.HOUR_OF_DAY)
    val endminute = calendar.get(Calendar.MINUTE)



    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeading(title = "Go live", backButton = true, onBack = {
            onEvent(LiveScreenEvents.GoBack)
        }){}

        Spacer(modifier = Modifier.height(24.dp))
        CreateHeading("Time", icon = com.example.friendupp.R.drawable.ic_time)

        NoteComponent()
        LocationComponent()

        Spacer(modifier = Modifier.weight(1f))
        CreateButton(modifier = Modifier.padding(horizontal = 24.dp), text = "Go live", disabled = false)
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
