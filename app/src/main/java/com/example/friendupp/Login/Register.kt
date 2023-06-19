package com.example.friendupp.Login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.ChatUi.ChatEvents
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.PasswordEditText
import com.example.friendupp.Profile.NameState
import com.example.friendupp.Profile.NameStateSaver
import com.example.friendupp.Profile.UsernameState
import com.example.friendupp.Profile.UsernameStateSaver
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class RegisterEvents{
    object GoBack:RegisterEvents()
    class Register(val email:String,val fullname:String,val username:String,val password:String):RegisterEvents()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(modifier: Modifier,onEvent:(RegisterEvents)->Unit){
    var passwordMatch by rememberSaveable {
        mutableStateOf(false)
    }
    val focusRequester = remember { FocusRequester() }

    val emailState by rememberSaveable(stateSaver = EmailStateSaver) {
        mutableStateOf(EmailState())
    }

    val nameState by rememberSaveable(stateSaver = NameStateSaver ) {
        mutableStateOf(NameState())
    }

    val usernameState by rememberSaveable(stateSaver = UsernameStateSaver ) {
        mutableStateOf(UsernameState())
    }

    val passwordStateSaver = textFieldStateSaver(PasswordState())
    val passwordState by rememberSaveable(stateSaver = passwordStateSaver) {
        mutableStateOf(PasswordState())
    }


    val reEnterPasswordStateSaver = textFieldStateSaver(PasswordState())
    val reEnterPasswordState by rememberSaveable(stateSaver = reEnterPasswordStateSaver) {
        mutableStateOf(PasswordState())
    }
    BackHandler(true) {
        onEvent(RegisterEvents.GoBack)
    }
    Column (modifier= modifier
        .fillMaxSize()
        .padding(24.dp)
        .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally ){
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
            IconButton(onClick = { onEvent(RegisterEvents.GoBack) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconPrimary
                )
            }

        }
        Spacer(modifier = Modifier.height(4.dp))

        Text(modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
            text = "Create your account",
            style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 22.sp), color = SocialTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
            text = "Enter your details to get sign in \n" +
                    "to your account",
            style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Normal, fontSize = 18.sp), color = SocialTheme.colors.textPrimary.copy(0.5f)
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
        Spacer(modifier = Modifier.height(8.dp))
        NameEditText( modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
            focusRequester=focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Full name", textState =nameState
        )
        Spacer(modifier = Modifier.height(8.dp))
        NameEditText( modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
            focusRequester=focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Username", textState =usernameState
        )
        Spacer(modifier = Modifier.height(8.dp))


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
   /*     if(!passwordMatch){
            Text(modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                text = "Passwords don't match",
                style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Normal, fontSize = 12.sp), color = SocialTheme.colors.error.copy(1f)
            )
        }*/


        Spacer(modifier = Modifier.height(24.dp))
        Card(modifier = Modifier, onClick = {
            val password = passwordState.text
            val reEnterPassword = reEnterPasswordState.text
            if (password == reEnterPassword) {
                onEvent(RegisterEvents.Register(emailState.text, nameState.text,usernameState.text,passwordState.text))

            } else {
                passwordMatch=false
            }

                                            }, shape = RoundedCornerShape(8.dp)) {
            Box(modifier = Modifier.background(SocialTheme.colors.textInteractive), contentAlignment = Alignment.Center){
                Text(modifier=modifier.padding(vertical = 12.dp, horizontal = 120.dp),text = "Sign up", style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White
                ))
            }
        }


        Spacer(modifier = Modifier.weight(1f))
        Text(modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
            text = "By  signing in, you  agree to our Terms and Privacy Policy",
            style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Normal, fontSize = 12.sp), color = SocialTheme.colors.textPrimary.copy(0.5f)
        )
    }

}