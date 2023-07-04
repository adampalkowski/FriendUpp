package com.example.friendupp.Create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

@Composable
fun FriendPickerItem(id:String,username:String,imageUrl:String,onClick:()->Unit, onUserSelected: (String) -> Unit,
                     onUserDeselected: (String) -> Unit,addUserName:(String)->Unit,removeUsername:(String)->Unit){
    var selected by rememberSaveable{
        mutableStateOf(false)
    }
    var textColor = if (selected){
        SocialTheme.colors.textLink
    }else{
        SocialTheme.colors.textPrimary
    }
    val truncatedUsername =if(username.length>30){username.take(30)+"..."}else{username}  // Limit the username to 30 letters

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier

            .clickable(onClick = {
                if(selected){
                    selected = !selected
                    onUserDeselected(id)
                    removeUsername(username)
                }else{
                    selected = !selected

                    onUserSelected(id)
                    addUserName(username)

                }

            }  ,interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.Black))
            .padding(horizontal = 24.dp, vertical = 12.dp)
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
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(modifier = Modifier.weight(1f),text = truncatedUsername,
            style= TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Medium, fontSize = 16.sp),color=textColor)
        RoundedCheckView(selected = selected)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedCheckView(selected:Boolean) {
    var backgroundColor = if (selected){
        SocialTheme.colors.textInteractive
    }else{
        SocialTheme.colors.uiBackground
    }
    var border = if(selected){
        null
    }else{
        BorderStroke(1.dp, SocialTheme.colors.uiBorder)
    }
    var iconColor = if(selected){
        Color.White
    }else{
        Color.Transparent
    }
    Card(
        shape = RoundedCornerShape(6.dp),
        border = border, colors = CardDefaults.cardColors(contentColor = backgroundColor, containerColor = backgroundColor)
    ) {
            Icon(painter = painterResource(id = R.drawable.ic_check), contentDescription =null,tint=iconColor )
    }
}



