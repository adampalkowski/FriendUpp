package com.example.friendupp.Create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

@Composable
fun CreateHeading(text: String, icon: Int, tip: Boolean = false, description: String = "") {
    var displayDesription by remember {
        mutableStateOf(false)
    }

    var color = SocialTheme.colors
        .textPrimary.copy(0.8f)
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            Modifier
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = color
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = color,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            if (tip) {
                IconButton(onClick = { displayDesription = !displayDesription }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_ligh),
                        contentDescription = null,
                        tint = SocialTheme.colors.iconPrimary.copy(0.5f)
                    )

                }
            }
        }
        AnimatedVisibility(
            visible = displayDesription,
            enter = slideInVertically(),
            exit = slideOutVertically()
        ) {
            Text(
                text = description, color = SocialTheme.colors.textPrimary.copy(0.5f),
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                )
            )
        }

    }
}
