package com.example.friendupp.Settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.PasswordEditText
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.CreateButton
import com.example.friendupp.Login.EmailState
import com.example.friendupp.Login.EmailStateSaver
import com.example.friendupp.Login.textFieldStateSaver
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

sealed class EmailEvents{
    object GoBack:EmailEvents()
}

@Composable
fun EmailScreen(onEvent:(EmailEvents)->Unit){
    val EmailStateSaverChange = textFieldStateSaver(EmailState())
    val emailState by rememberSaveable(stateSaver = EmailStateSaverChange) {
        mutableStateOf(EmailState())
    }
    val focusRequester = remember { FocusRequester() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeading(title = "Email", backButton = true, onBack = {onEvent(EmailEvents.GoBack)}) { }

        Spacer(modifier = Modifier.height(12.dp))
        androidx.compose.material.Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "Ensure that you have access to the new email address as it will be used for future communication and account verification.",
            style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Light),
            color = SocialTheme.colors.textPrimary.copy(0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))

        NameEditText( modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
            focusRequester=focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Email", textState =emailState
        )
        Spacer(modifier = Modifier.weight(1f))
        CreateButton(modifier = Modifier.fillMaxWidth().padding(48.dp),text = "Change email", disabled =!emailState.isValid, createClicked = {})

    }
}