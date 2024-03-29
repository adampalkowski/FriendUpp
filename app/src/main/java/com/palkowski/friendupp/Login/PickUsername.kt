package com.palkowski.friendupp.Login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkowski.friendupp.Components.NameEditText
import com.palkowski.friendupp.Components.TextFieldError
import com.palkowski.friendupp.Create.CreateButton
import com.palkowski.friendupp.Profile.UsernameState
import com.palkowski.friendupp.Profile.UsernameStateSaver
import com.palkowski.friendupp.di.AuthViewModel
import com.palkowski.friendupp.di.UserViewModel
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme


sealed class PickUserEvent {
    object GoToHome : PickUserEvent()
    object NavigateBack : PickUserEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickUsernameScreen(userViewModel: UserViewModel?, authViewModel: AuthViewModel?, onEvent: (PickUserEvent) -> Unit){
    val usernameFlow = userViewModel?.isUsernameAddedFlow?.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val usernameState by rememberSaveable(stateSaver = UsernameStateSaver) {
        mutableStateOf(UsernameState())
    }
        Column(modifier = Modifier.safeDrawingPadding()
            .fillMaxSize()
            .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text= "Enter your username to help others identify you"
                , color = SocialTheme.colors.textPrimary, style = TextStyle(fontFamily = Lexend,
                    fontSize = 24 .sp,
                    fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.height(48.dp))

            NameEditText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                focusRequester = focusRequester,
                focus = false,
                onFocusChange = { focusState ->

                }, label = "Username", textState = usernameState
            )

            Spacer(modifier = Modifier.height(24.dp))

            CreateButton(modifier=Modifier.fillMaxWidth().padding(horizontal = 48.dp),text = "Set username", disabled =usernameState.showErrors(),createClicked={
                if(usernameState.text.length>3 && usernameState.text.length<=25){
                    userViewModel?.addUsernameToUser(authViewModel!!.currentUser!!.uid,usernameState.text.trim())
                }else{
                    usernameState.enableShowErrors()

                }

                                                                                                                                                                },
  )

        }

    usernameFlow?.value?.let {
            databaseResponse ->
        when(databaseResponse){
            is Response.Success->{
                Toast.makeText(LocalContext.current,"username set", Toast.LENGTH_LONG).show()
                onEvent(PickUserEvent.GoToHome)
                userViewModel.resetIsUsernameAdded()
            }
            is Response.Failure->{
                userViewModel.resetIsUsernameAdded()
                Toast.makeText(LocalContext.current,    databaseResponse.e.message.toString(), Toast.LENGTH_LONG).show()
            }
            else->{}

        }
    }
}