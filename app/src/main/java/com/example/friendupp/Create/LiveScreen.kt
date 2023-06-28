package com.example.friendupp.Create

import android.content.Context
import android.provider.Settings.Global.getString
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Components.*
import com.example.friendupp.Components.TimePicker.TimeState
import com.example.friendupp.Login.EmailState
import com.example.friendupp.Login.PasswordState
import com.example.friendupp.R
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
        TimeSelection(startTimeState = TimeState(10,10), endTimeState = TimeState(12,12), modifier = Modifier)
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
        CreateHeading(LocalContext.current.getString(R.string.liveShareLocation), icon = com.example.friendupp.R.drawable.ic_location)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Switch(
                checked = shareLocation,
                onCheckedChange = { shareLocation = !shareLocation },
                modifier = Modifier.padding(start = 16.dp),
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = SocialTheme.colors.textInteractive,
                    checkedTrackColor = grayColor, uncheckedTrackColor = grayColor,
                    checkedIconColor = SocialTheme.colors.textInteractive,
                    uncheckedThumbColor = Color.White,
                    uncheckedIconColor = Color.White,
                    uncheckedBorderColor = grayColor,
                    checkedBorderColor = grayColor
                ), thumbContent = {
                    androidx.compose.animation.AnimatedVisibility(visible = shareLocation) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_done),
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
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
