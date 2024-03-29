package com.palkowski.friendupp.Login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.palkowski.friendupp.Components.NameEditText
import com.palkowski.friendupp.Components.PasswordEditText
import com.palkowski.friendupp.R
import com.palkowski.friendupp.di.AuthViewModel
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme


sealed class LoginEvents{
    object Register:LoginEvents()
    class Login(val email:String,val password:String):LoginEvents()
    object LoginWithGoogle:LoginEvents()
    object LoginWithFB:LoginEvents()
    object GoBack:LoginEvents()
    object GoToHome:LoginEvents()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen (modifier: Modifier,onEvent:(LoginEvents)->Unit,authViewModel:AuthViewModel){

    val focusRequester = remember { FocusRequester() }

    BackHandler(true) {
        onEvent(LoginEvents.GoBack)
    }
    Column(modifier = modifier.fillMaxSize()
        .background(SocialTheme.colors.uiBackground)
        .verticalScroll(rememberScrollState())
        .padding(top = 64.dp) , horizontalAlignment = Alignment.CenterHorizontally) {


        Text(
            text ="Welcome", style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = SocialTheme.colors.textPrimary
        ))
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text ="Please enter your details to sign in \n" +
                    "into your account", style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                color =SocialTheme.colors.textPrimary.copy(0.5f)
            ), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(18.dp))
        val passwordState by rememberSaveable(stateSaver = PasswordStateSaver) {
            mutableStateOf(PasswordState())
        }
        val emailState by rememberSaveable(stateSaver = EmailStateSaver) {
            mutableStateOf(EmailState())
        }
        NameEditText( modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
            focusRequester=focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Email", textState =emailState
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
        Spacer(modifier = Modifier.height(24.dp))
        Card(modifier = Modifier, onClick = { onEvent(LoginEvents.Login(emailState.text.trim(),passwordState.text.trim())) }, shape = RoundedCornerShape(8.dp)) {
            Box(modifier = Modifier.background(SocialTheme.colors.textInteractive), contentAlignment = Alignment.Center){
                Text(modifier=modifier.padding(vertical = 12.dp, horizontal = 120.dp),text = "Sign in", style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White
                ))
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier
                            .height(2.dp)
                            .background(Color(0xFFD9D9D9).copy(0.5f))
                            .width(48.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text ="Or sign in with", style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = SocialTheme.colors.textPrimary.copy(0.5f)
                ))
            Spacer(modifier = Modifier.width(12.dp))

            Spacer(modifier = Modifier
                .height(2.dp)
                .background(Color(0xFFD9D9D9).copy(0.5f))
                .width(48.dp))

        }
        
        Spacer(modifier = Modifier.height(48.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DoubleButton("Google",image= R.drawable.google,textColor=Color(0xFFD9503F),onClick={onEvent(LoginEvents.LoginWithGoogle)})
        /*   DoubleButton("Facebook",image= R.drawable.fb,textColor=Color(0xFF3F71B5), onClick = {})*/
        }



        Spacer(modifier = Modifier.weight(1f))
        Row(Modifier.clickable(onClick = {onEvent(LoginEvents.Register)})) {
            Text(
                text ="Don't have an account?", style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = Color.Black.copy(0.5f)
                ))
            Text(
                text ="Sign up", style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color.Black.copy(0.5f)
                ))
        }

        Spacer(modifier = Modifier.height(24.dp))

    }
}
