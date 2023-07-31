package com.palkowski.friendupp.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.palkowski.friendupp.ui.theme.SocialTheme

@Composable
fun WhiteButton(){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 48.dp)){
        Box(modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .padding(vertical = 8.dp)
            .border(
                BorderStroke(0.5.dp, SocialTheme.colors.textInteractive),
                shape = RoundedCornerShape(24.dp)
            ), contentAlignment = Alignment.Center){
            Text(text = "Go Live")
        }
    }
}