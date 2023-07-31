package com.palkowski.friendupp.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkowski.friendupp.ChatUi.ButtonAdd
import com.palkowski.friendupp.R
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme


@Composable
fun ScreenHeading(modifier: Modifier=Modifier,title:String,backButton:Boolean=false,
                  onBack:()->Unit={},backIcon:Int=R.drawable.ic_back,content:@Composable ()->Unit,){

    var padding =if(backButton){100.dp}else{64.dp}
    Box(modifier = modifier.fillMaxWidth()){
        Row(Modifier.align(Alignment.Center).padding(top = 12.dp),verticalAlignment = Alignment.CenterVertically){
            Spacer(modifier = Modifier
                .background(SocialTheme.colors.uiBorder).height(1.dp)
                .width(padding))
            Text(modifier= Modifier.padding(bottom = 4.dp),text = title, style = TextStyle(fontFamily = Lexend, fontSize = 20.sp, fontWeight = FontWeight.SemiBold), color = SocialTheme.colors.textPrimary.copy(0.8f))
            Spacer(modifier = Modifier
                .background(SocialTheme.colors.uiBorder).height(1.dp)
                .weight(1f))
            Spacer(modifier = Modifier
                .background(SocialTheme.colors.uiBorder).height(1.dp)
                .weight(1f))

        }
        Box(modifier = Modifier
            .padding(top=12.dp,end=24.dp)
            .align(Alignment.TopEnd)){
            content()
        }
        if (backButton){
            Box(modifier = Modifier
                .padding(top=12.dp, start = 24.dp)
                .align(Alignment.TopStart)){
                ButtonAdd(onClick = onBack, icon = backIcon)
            }
        }
    }

}