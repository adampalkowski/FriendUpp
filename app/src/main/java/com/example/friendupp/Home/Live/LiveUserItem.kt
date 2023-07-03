package com.example.friendupp.Home.Live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveUserItem(imageUrl: String, text: String = "",onClick: () -> Unit,clickable:Boolean=false) {
    var modifier =if(clickable){
        Modifier
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick, interactionSource = remember {
                MutableInteractionSource()
            }, indication = rememberRipple(color = Color.Black))}else{

        Modifier
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))}
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_profile_300),
            contentDescription = "stringResource(R.string.description)",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(
                    BorderStroke(1.dp, Color.Green),
                    CircleShape
                )
                .border(
                    BorderStroke(3.dp, SocialTheme.colors.uiBackground),
                    CircleShape
                )
        )
        if (text.isNotEmpty()) {
            Card(
                modifier = Modifier.align(Alignment.BottomCenter),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, SocialTheme.colors.uiBorder)
            ) {
                Box(
                    modifier = Modifier.background(SocialTheme.colors.uiBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                        text = text,
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = SocialTheme.colors.textPrimary.copy(0.8f)
                        )
                    )
                }
            }
        }


    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLive(imageUrl: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick), contentAlignment = Alignment.Center

    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "stringResource(R.string.description)",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(
                    BorderStroke(1.dp, Color.Green),
                    CircleShape
                )
                .border(
                    BorderStroke(3.dp, SocialTheme.colors.uiBackground),
                    CircleShape
                )
        )
        Card(
            shape = RoundedCornerShape(100),
            colors = CardDefaults.cardColors(
                contentColor = Color.Transparent,
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(color = Color.Black.copy(0.8f)), contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    tint = Color.White,
                    contentDescription = null
                )
            }

        }

    }

}