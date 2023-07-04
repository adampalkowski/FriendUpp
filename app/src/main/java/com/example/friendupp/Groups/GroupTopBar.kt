package com.example.friendupp.Groups

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Components.ActionButton
import com.example.friendupp.Create.Option
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


@Composable
fun GroupTopBar(
    onClick: () -> Unit,
    selectedOption: Option,
    onFriends: () -> Unit,
    onPublic: () -> Unit,
) {
    val context = LocalContext.current

    val dividerColor = SocialTheme.colors.uiBorder
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .horizontalScroll(
                rememberScrollState()
            )
    ) {
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        ButtonAdd(onClick = onClick, icon = R.drawable.ic_x)
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        Text(
            modifier = Modifier.padding(bottom = 6.dp),
            text = "Group",
            style = TextStyle(
                fontFamily = Lexend,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = SocialTheme.colors.textPrimary.copy(0.8f)
            )
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(
            option = Option.FRIENDS,
            isSelected = selectedOption == Option.FRIENDS,
            onClick = onFriends
        )
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(
            option = Option.PUBLIC,
            isSelected = selectedOption == Option.PUBLIC,
            onClick = onPublic
        )
        Spacer(
            modifier = Modifier
                .width(48.dp)
                .height(1.dp)
                .background(dividerColor)
        )

    }

}

