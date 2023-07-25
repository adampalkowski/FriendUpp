package com.example.friendupp.ActivityPreview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Home.eButtonSimple
import com.example.friendupp.R
import com.example.friendupp.bottomBar.ActivityUi.ActivityEvents
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme



@Composable
fun TextButton(
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    selected: Boolean = false,
    text:String,
    textInactive:String
) {
    val backColor = if (selected) {
        SocialTheme.colors.textInteractive
    } else {
        SocialTheme.colors.uiBorder.copy(0.1f)
    }
    val iconColor = if (selected) {
        Color.White
    } else {
        SocialTheme.colors.iconPrimary
    }
    val textColor = if (selected) {
        Color.White
    } else {
        SocialTheme.colors.iconPrimary
    }
    var textDisplay = if (selected) {
        text
    } else {
        textInactive
    }




    Box(
        modifier = Modifier.wrapContentWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(backColor)
            .clickable(onClick = onClick), contentAlignment = Alignment.Center
    ) {
        Row(Modifier.padding(horizontal = 12.dp),verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconColor
            )
            if(text!=null){
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = textDisplay,    style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = textColor
                )
                )

            }
        }


    }
}
