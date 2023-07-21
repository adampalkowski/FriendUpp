package com.example.friendupp.Login

import android.view.animation.OvershootInterpolator
import android.window.SplashScreen
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.Pacifico
import com.example.friendupp.ui.theme.SocialTheme
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(splash_screen_delay:Long){
    Box(
        Modifier
            .fillMaxSize()
            .background(SocialTheme.colors.uiBackground).safeDrawingPadding()) {
        val scale = remember {
            androidx.compose.animation.core.Animatable(0.0f)
        }

        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(800, easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
            )
            delay(splash_screen_delay)

        }
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "FriendUpp",
                textAlign = TextAlign.Center,
                style = TextStyle(fontFamily = Pacifico, fontWeight = FontWeight.Normal, fontSize = 50.sp),color=SocialTheme.colors.textPrimary,
                modifier = Modifier
                    .padding(16.dp)
                    .scale(scale.value)
            )
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Go out with friends and have fun!",
                textAlign = TextAlign.Center,
                style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Light, fontSize = 30.sp),color=SocialTheme.colors.textPrimary,
                modifier = Modifier
                    .padding(16.dp)
                    .scale(scale.value)
            )
        }


    }
}