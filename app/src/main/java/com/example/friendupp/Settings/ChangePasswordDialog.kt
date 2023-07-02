package com.example.friendupp.Settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.PasswordEditText
import com.example.friendupp.Login.EmailState
import com.example.friendupp.Login.PasswordState
import com.example.friendupp.Login.textFieldStateSaver
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


@Composable
fun ChangePasswordDialog(
    label: String,
    icon: Int,
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit,
    confirmLabel: String = "Confirm",
    cancelLabel: String = "Cancel",
    iconTint: Color = SocialTheme.colors.iconPrimary,
    textColor: Color = SocialTheme.colors.textPrimary,
    backgroundColor: Color = SocialTheme.colors.uiBackground,
    confirmTextColor: Color = SocialTheme.colors.textInteractive,
    cancelTextColor: Color = SocialTheme.colors.iconPrimary,
    disableConfirmButton: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }

    val passwordStateSaver = textFieldStateSaver(PasswordState())
    val passwordState by rememberSaveable(stateSaver = passwordStateSaver) {
        mutableStateOf(PasswordState())
    }


    val reEnterPasswordStateSaver = textFieldStateSaver(PasswordState())
    val reEnterPasswordState by rememberSaveable(stateSaver = reEnterPasswordStateSaver) {
        mutableStateOf(PasswordState())
    }
    var disableConfirm by remember {
        mutableStateOf(false)
    }
    disableConfirm=!reEnterPasswordState.isValid||!passwordState.isValid||passwordState.text!=reEnterPasswordState.text
    Dialog(onDismissRequest = onCancel) {
        Box(
            Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(backgroundColor)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                )
            ) {
                Column(
                    Modifier.padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = cancelTextColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = label,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            fontFamily = Lexend
                        ),
                        color = cancelTextColor
                    )
                }
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
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Transparent
                    ), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column() {


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    if (disableConfirm) {

                                    } else {
                                        if (passwordState.text.length>0){
                                            onConfirm(passwordState.text)

                                        }
                                    }
                                })
                                .border(BorderStroke(0.5.dp, SocialTheme.colors.uiBorder))
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = confirmLabel,
                                style = TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    fontFamily = Lexend
                                ),
                                color =     if (disableConfirm)  confirmTextColor.copy(0.2f) else confirmTextColor
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onCancel)
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = cancelLabel,
                                style = TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    fontFamily = Lexend
                                ),
                                color = cancelTextColor
                            )

                        }
                    }
                }
            }


        }


    }

}