package com.example.friendupp.ActivityPreview
import com.example.friendupp.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentButton(onClick:()->Unit){
    Card(modifier=Modifier.size(48.dp),onClick = onClick, shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent, contentColor = Color.Transparent)) {
        Box(modifier = Modifier.fillMaxSize().background(color=Color.Black.copy(0.6f)), contentAlignment = Alignment.Center){
            Icon(painter = painterResource(id = R.drawable.ic_x), contentDescription =null, tint = Color.White.copy(0.6f) )
        }
    }
}