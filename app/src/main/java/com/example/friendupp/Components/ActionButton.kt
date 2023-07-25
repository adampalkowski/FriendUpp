package com.example.friendupp.Components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.friendupp.Create.Option
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionButton(option: Option, isSelected: Boolean, onClick: () -> Unit) {
    val backColor by animateColorAsState(
        targetValue = if (isSelected) {
            SocialTheme.colors.iconInteractive
        } else {
            SocialTheme.colors.uiBorder
        }, tween(300)
    )
    val frontColor by animateColorAsState(
        if (isSelected) {
            SocialTheme.colors.textInteractive
        } else {
            SocialTheme.colors.uiBackground
        }, tween(300)
    )
    var border = if (isSelected) {
        null

    } else {
        BorderStroke(1.dp, SocialTheme.colors.uiBorder)

    }

    val iconColor by animateColorAsState(
        if (isSelected) {
            Color.White
        } else {
            SocialTheme.colors.iconPrimary
        }, tween(300)
    )


    val interactionSource = MutableInteractionSource()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scale = remember {
        Animatable(1f)
    }


    Box(
        modifier = Modifier
            .clickable(interactionSource = interactionSource, indication = null) {
                coroutineScope.launch {
                    scale.animateTo(
                        0.8f,
                        animationSpec = tween(300),
                    )
                    scale.animateTo(
                        1f,
                        animationSpec = tween(100),
                    )
                    onClick()
                }

            }
            .scale(scale = scale.value),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .height(52.dp)
                .width(52.dp)
                .zIndex(1f),
            colors = CardDefaults.cardColors(
                contentColor = Color.Transparent,
                containerColor = backColor
            ),
            border = border,
            shape = RoundedCornerShape(12.dp)
        ) {
            // Content of the bottom Card
            Card(
                modifier = Modifier
                    .height(52.dp)
                    .width(52.dp)
                    .zIndex(2f)
                    .graphicsLayer {
                        translationY = -5f
                    },
                colors = CardDefaults.cardColors(
                    contentColor = Color.Transparent,
                    containerColor = frontColor
                ),
                shape = RoundedCornerShape(12.dp),
                border = border
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = option.icon),
                        contentDescription = null,
                        tint = iconColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionButtonDefault(icon: Int, isSelected: Boolean, onClick: () -> Unit,number:String?=null) {
    val backColor by animateColorAsState(
        targetValue = if (isSelected) {
            SocialTheme.colors.iconInteractive
        } else {
            SocialTheme.colors.uiBorder
        }, tween(300)
    )
    val frontColor by animateColorAsState(
        if (isSelected) {
            SocialTheme.colors.textInteractive
        } else {
            SocialTheme.colors.uiBackground
        }, tween(300)
    )
    var border = if (isSelected) {
        null

    } else {
        BorderStroke(1.dp, SocialTheme.colors.uiBorder)

    }

    val iconColor by animateColorAsState(
        if (isSelected) {
            Color.White
        } else {
            SocialTheme.colors.iconPrimary
        }, tween(300)
    )


    val interactionSource = MutableInteractionSource()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scale = remember {
        Animatable(1f)
    }


    Box(
        modifier = Modifier
            .clickable(interactionSource = interactionSource, indication = null) {
                coroutineScope.launch {
                    scale.animateTo(
                        0.8f,
                        animationSpec = tween(300),
                    )
                    scale.animateTo(
                        1f,
                        animationSpec = tween(100),
                    )
                    onClick()
                }

            }
            .scale(scale = scale.value),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .height(52.dp)
                .width(52.dp)
                .zIndex(1f),
            colors = CardDefaults.cardColors(
                contentColor = Color.Transparent,
                containerColor = backColor
            ),
            border = border,
            shape = RoundedCornerShape(12.dp)
        ) {
            // Content of the bottom Card
            Card(
                modifier = Modifier
                    .height(52.dp)
                    .width(52.dp)
                    .zIndex(2f)
                    .graphicsLayer {
                        translationY = -5f
                    },
                colors = CardDefaults.cardColors(
                    contentColor = Color.Transparent,
                    containerColor = frontColor
                ),
                shape = RoundedCornerShape(12.dp),
                border = border
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = iconColor
                    )
                    if(number!=null){
                        Box(modifier = Modifier.align(Alignment.TopEnd).padding(end =6.dp , top = 6.dp)
                            .clip(CircleShape).background(SocialTheme.colors.textInteractive)
                            .padding(4.dp)){
                            Text(text = number, style = TextStyle(fontFamily = Lexend, fontSize = 10.sp), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
