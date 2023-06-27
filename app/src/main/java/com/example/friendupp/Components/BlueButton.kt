package com.example.friendupp.Components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.friendupp.Home.Option
import com.example.friendupp.ui.theme.SocialTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueButton(onClick: () -> Unit,disabled:Boolean=false,icon:Int) {

    val frontColor =  if(disabled) SocialTheme.colors.textInteractive.copy(0.2f )else SocialTheme.colors.textInteractive
    var border = null
    var backColor = if(disabled)SocialTheme.colors.iconInteractive.copy(0.2f )else SocialTheme.colors.iconInteractive

    val iconColor =     Color.White


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
                    if(!disabled){
                        onClick()
                    }
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
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteButton(onClick: () -> Unit,disabled:Boolean=false,icon:Int) {

    val frontColor =  SocialTheme.colors.textInteractive
    var border = null
    var backColor =            SocialTheme.colors.iconInteractive

    val iconColor =     Color.White


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
                }
            }
        }
    }
}