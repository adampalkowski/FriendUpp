package com.palkowski.friendupp.Components

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkowski.friendupp.Login.EmailState
import com.palkowski.friendupp.Login.PasswordState
import com.palkowski.friendupp.Login.TextFieldState
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEditText(modifier: Modifier,focusRequester: FocusRequester, focus: Boolean, onFocusChange: (Boolean) -> Unit,label:String="label",
                 textState: TextFieldState = remember { EmailState() },    imeAction: ImeAction = ImeAction.Next,
                 onImeAction: () -> Unit = {}) {


    var focusedColor =  Color(0xFF36FF56)


    var textColor =Color.Black

    var borde2rColor =
        Color(0xFFD9D9D9)
Column(horizontalAlignment = Alignment.CenterHorizontally) {

        OutlinedTextField(
            label = {
                Text(
                    text = label,
                    style = TextStyle(
                        fontFamily = Lexend, fontSize = 14.sp,
                        fontWeight = FontWeight.Light, color = Color(0xFF707070)
                    )
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor =  SocialTheme.colors.textLink,
                textColor = SocialTheme.colors.textPrimary,
                unfocusedBorderColor = SocialTheme.colors.uiBorder,
                focusedLabelColor = SocialTheme.colors.textLink,
                cursorColor = SocialTheme.colors.textPrimary,
                leadingIconColor = SocialTheme.colors.textPrimary,
                trailingIconColor = SocialTheme.colors.textPrimary,
                errorBorderColor = SocialTheme.colors.error ,
                errorCursorColor =SocialTheme.colors.error ,
                errorLabelColor = SocialTheme.colors.error ,
                errorLeadingIconColor = SocialTheme.colors.error
            ),
            modifier = Modifier
                .focusRequester(focusRequester)
                .widthIn(TextFieldDefaults.MinWidth, TextFieldDefaults.MinWidth + 50.dp)
                .onFocusChanged { focusState ->
                    textState.onFocusChange(focusState.isFocused)
                    if (!focusState.isFocused) {
                        textState.enableShowErrors()
                    }
                },
            value = textState.text, onValueChange = { textState.text = it },
            textStyle = TextStyle(
                fontFamily = Lexend, fontSize = 14.sp,
                fontWeight = FontWeight.Light
            ), isError = textState.showErrors(),
            singleLine = false,
            maxLines = Int.MAX_VALUE,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = imeAction,
                keyboardType = KeyboardType.Email
            ),
            keyboardActions = KeyboardActions(
                onDone = {     onImeAction() }
            ),
            interactionSource =  remember { MutableInteractionSource() },
            shape = RoundedCornerShape(10.dp),
        )

        textState.getError()?.let { error -> TextFieldError(textError = error) }
}

}
@Composable
fun TextFieldError(textError: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = textError,
            modifier = Modifier.align(Alignment.Center),
            style= TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Normal, fontSize = 12.sp),
            color = MaterialTheme.colorScheme.error
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordEditText(modifier: Modifier,focusRequester: FocusRequester
                     ,  onFocusChange: (Boolean) -> Unit
                     ,label:String="label",textState: TextFieldState = remember { PasswordState() },
                     imeAction: ImeAction = ImeAction.Next,
                     onImeAction: () -> Unit = {}) {

    var focusedColor =  Color(0xFF36FF56)

    var textColor =Color.Black
    var hide by remember {
        mutableStateOf(false)
    }
    val visualTransformation = if (hide) PasswordVisualTransformation() else VisualTransformation.None

    var borderColor =
        Color(0xFFD9D9D9)

    Box(
        modifier = Modifier
            .padding(horizontal = 0.dp)
    ) {
        OutlinedTextField(
            label = {
                Text(
                    text = label,
                    style = TextStyle(
                        fontFamily = Lexend, fontSize = 14.sp,
                        fontWeight = FontWeight.Light, color = Color(0xFF707070)
                    )
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor =  SocialTheme.colors.textLink,
                textColor = SocialTheme.colors.textPrimary,
                unfocusedBorderColor = SocialTheme.colors.uiBorder,
                focusedLabelColor = SocialTheme.colors.textLink,
                cursorColor = SocialTheme.colors.textPrimary,
                leadingIconColor = SocialTheme.colors.textPrimary,
                trailingIconColor = SocialTheme.colors.textPrimary,
                errorBorderColor = SocialTheme.colors.error ,
                errorCursorColor =SocialTheme.colors.error ,
                errorLabelColor = SocialTheme.colors.error ,
                errorLeadingIconColor = SocialTheme.colors.error
            ),
            trailingIcon={
                IconButton(onClick = { hide=!hide }) {
                    Icon(painter =                    if(hide){  painterResource(id = com.palkowski.friendupp.R.drawable.ic_visibility_off)}else{  painterResource(id = com.palkowski.friendupp.R.drawable.ic_visibility)}
                          , contentDescription =null
                        , tint = Color(0xFF707070))
                }
              }, isError = textState.showErrors(),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .widthIn(TextFieldDefaults.MinWidth, TextFieldDefaults.MinWidth + 50.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    textState.onFocusChange(focusState.isFocused)
                    if (!focusState.isFocused) {
                        textState.enableShowErrors()
                    }
                },
            value = textState.text, onValueChange = { textState.text = it },
            textStyle = TextStyle(
                fontFamily = Lexend, fontSize = 14.sp,
                fontWeight = FontWeight.Light
            ),

            singleLine = false,
            maxLines = Int.MAX_VALUE,
            keyboardOptions =  KeyboardOptions.Default.copy(
                imeAction = imeAction,
                keyboardType =KeyboardType.Password
            ),
            visualTransformation = visualTransformation,
            keyboardActions = KeyboardActions(
                onDone = {     onImeAction() }
            ),
            interactionSource =  remember { MutableInteractionSource() },
            shape = RoundedCornerShape(10.dp),
        )

    }
    textState.getError()?.let { error -> TextFieldError(textError = error) }
}
