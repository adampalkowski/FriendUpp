package com.example.friendupp.Login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoubleButton(text:String,image:Int,textColor:Color){
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier
                ,
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .height(48.dp)
                    .wrapContentWidth()
                    .zIndex(1f),
                colors = CardDefaults.cardColors(
                    contentColor = Color.Transparent,
                    containerColor = Color(0xffB8B8B8)
                ),
                shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFD1D1D1))
            ) {
                // Content of the bottom Card
                Card(
                    modifier = Modifier
                        .height(48.dp)
                        .wrapContentWidth()
                        .zIndex(2f)
                        .graphicsLayer {
                            translationY = -5f
                        },
                    colors = CardDefaults.cardColors(
                        contentColor = Color.Transparent,
                        containerColor =SocialTheme.colors.uiBackground
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp,SocialTheme.colors.uiBorder)
                ) {
                    Box(
                        modifier = Modifier
                    ) {
                        Row(Modifier.padding(vertical = 8.dp, horizontal = 64.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Image(modifier =Modifier.size(32.dp), painter = painterResource(id = image), contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = text,style= TextStyle(fontFamily = Lexend, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                , color =textColor)
                        }
                    }
                }
            }

        }
    }

}


