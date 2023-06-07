package com.example.friendupp.Create

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Components.TextFieldError
import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.Login.textFieldStateSaver
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import java.lang.Long.MAX_VALUE

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun NumberEditText(
    modifier: Modifier,
    focusRequester: FocusRequester,
    focus: Boolean=false,
    onFocusChange: (Boolean) -> Unit={},
    label: String = "label",
    textState: TextFieldState = remember { NumberState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
) {
    var focusedColor = Color(0xFF36FF56)
    var textColor = Color.Black
    var borderColor = Color(0xFFD9D9D9)

    OutlinedTextField(
        label = {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFF707070)
                )
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = SocialTheme.colors.textLink,
            textColor = SocialTheme.colors.textPrimary,
            unfocusedBorderColor = SocialTheme.colors.uiBorder,
            focusedLabelColor = SocialTheme.colors.textLink,
            cursorColor = SocialTheme.colors.textPrimary,
            errorBorderColor = SocialTheme.colors.error,
            errorCursorColor = SocialTheme.colors.error,
            errorLabelColor = SocialTheme.colors.error,
            errorLeadingIconColor = SocialTheme.colors.error
        ),
        modifier = Modifier
            .focusRequester(focusRequester)
            .widthIn(64.dp,max=120.dp)
            .onFocusChanged { focusState ->
                textState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    textState.enableShowErrors()
                }
            },
        value = textState.text,
        onValueChange = { text ->
            val newValue = text.filter { it.isDigit() }
            if (newValue.length <= 10) {
                val numericValue = newValue.toLongOrNull()
                if (numericValue != null && numericValue < MAX_VALUE) {
                    textState.text = newValue
                }
            }
        },
        textStyle = TextStyle(
            fontFamily = Lexend,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light
        ),
        isError = textState.showErrors(),
        singleLine = false,
        maxLines = Int.MAX_VALUE,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() }
        ),
        interactionSource = remember { MutableInteractionSource() },
        shape = RoundedCornerShape(10.dp),
    )

    textState.getError()?.let { error -> TextFieldError(textError = error) }
}

class NumberState : TextFieldState() {
    override fun getError(): String? {
        val baseError = super.getError()
        if (text.isNotEmpty() && text.toLongOrNull() ?: 0 > MAX_VALUE) {
            return "Not enough people on earth"
        }
        return baseError
    }

    override fun showErrors(): Boolean {
        return super.showErrors() || (text.isNotEmpty() && text.toLongOrNull() ?: 0 > MAX_VALUE)
    }


    private companion object {
        private val NUMBER_REGEX = Regex("^[0-9]*")
        private const val MAX_VALUE = 8_000_000_000L
    }

}
val NumberStateSaver = textFieldStateSaver(NumberState())
