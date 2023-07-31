package com.palkowski.friendupp.bottomBar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkowski.friendupp.R
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme
import kotlinx.coroutines.launch

enum class BottomBarOption(val label: String, val icon: Int) {
    Home("Home", R.drawable.ic_home_300),
    Chat("Chat", R.drawable.ic_chat_300),
    Create("Create", R.drawable.ic_add),
    Map("Map", R.drawable.ic_map_300),
    Profile("Profile", R.drawable.ic_profile_300),
}
@Composable
fun BottomBar(modifier: Modifier,onClick: (BottomBarOption) -> Unit,selectedOption:String?) {

    Column(modifier .fillMaxWidth()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(color = SocialTheme.colors.uiBorder))
        Box(
            modifier
                .fillMaxWidth()
                .background(color = SocialTheme.colors.uiBackground)
                .padding(horizontal = 24.dp, vertical = 8.dp).padding(bottom =WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                BottomBarButton(modifier.semantics { contentDescription = "Home" }
               ,
                    option = BottomBarOption.Home,
                    isSelected = selectedOption == BottomBarOption.Home.label,
                    onClick = {
                        onClick(BottomBarOption.Home)
                    })
                Spacer(modifier = Modifier.width(48.dp))
                BottomBarButton(modifier.semantics { contentDescription = "Chat" }
                    ,option = BottomBarOption.Chat,
                    isSelected = selectedOption ==BottomBarOption.Chat.label,
                    onClick = {
                        onClick(BottomBarOption.Chat)
                    })
                Spacer(modifier = Modifier.width(48.dp))
                BottomBarButton(modifier.semantics { contentDescription = "Create" }
                    ,option = BottomBarOption.Create,
                    isSelected = selectedOption ==BottomBarOption.Create.label,
                    onClick = {
                        onClick(BottomBarOption.Create)
                    })
                Spacer(modifier = Modifier.width(48.dp))
                BottomBarButton(modifier.semantics { contentDescription = "Map" }
                    ,option = BottomBarOption.Map,
                    isSelected = selectedOption == BottomBarOption.Map.label,
                    onClick = {
                        onClick(BottomBarOption.Map)
                    })
                Spacer(modifier = Modifier.width(48.dp))

                BottomBarButton(modifier.semantics { contentDescription = "Profile" }
                    ,option = BottomBarOption.Profile,
                    isSelected = selectedOption == BottomBarOption.Profile.label,
                    onClick = {
                        onClick(BottomBarOption.Profile)
                    })
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarButton(modifier:Modifier=Modifier,option: BottomBarOption, isSelected: Boolean, onClick: () -> Unit) {
    val BackColor by animateColorAsState(
        targetValue = if (isSelected) {
            SocialTheme.colors.textInteractive.copy(0.8f)
        } else {
            Color(0xFFCACACA)
        },
        tween(300)
    )
    val FrontColor by animateColorAsState(
        if (isSelected) {
            Color.White
        } else {
            Color.Transparent
        }, tween(300)
    )
    var border = if (isSelected) {
        null
    } else {
       null
    }
    var contentColor = if (isSelected) {
        Color(0xff4870FD)
    } else {
        Color.White
    }
    val interactionSource = MutableInteractionSource()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scale = remember {
        Animatable(1f)
    }


    Box(
        modifier = modifier
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                modifier=Modifier.size(24.dp),
                painter = painterResource(id = option.icon),
                contentDescription = null,
                tint =BackColor
            )
            AnimatedVisibility(visible = isSelected, enter = slideInVertically(), exit = slideOutVertically () ) {
                Text(text = option.label,color=BackColor,
                    style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Medium, fontSize = 12.sp))

            }

        }

    }
}

