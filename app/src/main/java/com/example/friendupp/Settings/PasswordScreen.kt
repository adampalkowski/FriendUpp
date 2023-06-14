package com.example.friendupp.Settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Components.PasswordEditText
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.CreateButton
import com.example.friendupp.Login.PasswordState
import com.example.friendupp.Login.RegisterEvents
import com.example.friendupp.Login.textFieldStateSaver
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class PasswordEvents{
    object GoBack:PasswordEvents()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(onEvent:(PasswordEvents)->Unit){
    val focusRequester = remember { FocusRequester() }

    val passwordStateSaver = textFieldStateSaver(PasswordState())
    val passwordState by rememberSaveable(stateSaver = passwordStateSaver) {
        mutableStateOf(PasswordState())
    }


    val reEnterPasswordStateSaver = textFieldStateSaver(PasswordState())
    val reEnterPasswordState by rememberSaveable(stateSaver = reEnterPasswordStateSaver) {
        mutableStateOf(PasswordState())
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeading(title = "Password", backButton = true, onBack = {onEvent(PasswordEvents.GoBack)}) { }

        Spacer(modifier = Modifier.height(12.dp))
        androidx.compose.material.Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "Keep your account safe and protected with a strong and updated password.",
            style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Light),
            color = SocialTheme.colors.textPrimary.copy(0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))

        PasswordEditText( modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
            focusRequester=focusRequester,
            onFocusChange = { focusState ->
            }, label = "Password", textState =passwordState, onImeAction = {

            }
        )
        PasswordEditText( modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
            focusRequester=focusRequester,
            onFocusChange = { focusState ->
            }, label = "Re-enter password", textState =reEnterPasswordState, onImeAction = {

            }
        )
        Spacer(modifier = Modifier.weight(1f))

        CreateButton(modifier = Modifier.fillMaxWidth().padding(48.dp),text = "Change password"
            , disabled =!reEnterPasswordState.isValid||!passwordState.isValid||passwordState.text!=reEnterPasswordState.text
            , createClicked = {})

    }
}