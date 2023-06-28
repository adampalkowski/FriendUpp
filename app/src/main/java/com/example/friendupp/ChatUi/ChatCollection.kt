package com.example.friendupp.ChatUi

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.R
import com.example.friendupp.TimeFormat.getFormattedDate
import com.example.friendupp.di.ChatViewModel
import com.example.friendupp.model.Chat
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.google.type.DateTime
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


sealed class ChatCollectionEvents {
    object GoToSearch : ChatCollectionEvents()
    object GoBack : ChatCollectionEvents()
    object GoToGroups : ChatCollectionEvents()
    class GoToChat (val chat:Chat): ChatCollectionEvents()
}
@Composable
fun ChatCollection(modifier: Modifier, chatEvent: (ChatCollectionEvents) -> Unit,chatViewModel: ChatViewModel) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    BackHandler(true) {
        chatEvent(ChatCollectionEvents.GoBack)
    }
    val chatCollectionsToBeDisplayed = remember { mutableStateOf(ArrayList<Chat>()) }

    LazyColumn(modifier=Modifier.background(SocialTheme.colors.uiBackground)){
        item {
            ScreenHeading(title = "Chats"){
                Row(Modifier,verticalAlignment = Alignment.CenterVertically){
                    ButtonAdd(icon = R.drawable.ic_group_add, onClick = {chatEvent(ChatCollectionEvents.GoToGroups)})
                    Spacer(modifier = Modifier
                        .background(SocialTheme.colors.uiBorder)
                        .width(16.dp))
                    ButtonAdd(icon = R.drawable.ic_person_add, onClick = {chatEvent(ChatCollectionEvents.GoToSearch)})
                    Spacer(modifier = Modifier
                        .background(SocialTheme.colors.uiBorder)
                        .width(24.dp))
                }

            }
        }

        items(chatCollectionsToBeDisplayed.value.reversed()) {chat->
            if (chat!=null){
                var chat_name = chat.name.toString()
                if (chat.type.equals("duo")){
                    if(chat.user_one_username== UserData.user!!.username){
                        chat_name=chat.user_two_username.toString()
                    }else{
                        chat_name=chat.user_one_username.toString()

                    }
                }
                var chat_image = chat.imageUrl.toString()
                if (chat.type.equals("duo")){
                    if(chat.user_one_profile_pic== UserData.user!!.pictureUrl){
                        chat_image=chat.user_two_profile_pic.toString()
                    }else{
                        chat_image=chat.user_one_profile_pic.toString()

                    }
                }

                ChatItem(
                image=chat_image,
                title=chat_name,
                subtitle=chat.recent_message.toString(),
                date=chat.recent_message_time.toString(),
                onClick = {
                    chatEvent(ChatCollectionEvents.GoToChat(chat))
                }
            )
        }
        }
    }
    chatViewModel.chatCollectionsState.value.let {response->
        when (response) {
            is Response.Success -> {
                chatCollectionsToBeDisplayed.value = response.data
            }
            is Response.Loading -> {}
            is Response.Failure -> {}
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonAdd( onClick: () -> Unit,disabled:Boolean=false,icon:Int) {

    val back =SocialTheme.colors.uiBorder
    val front = SocialTheme.colors.uiBackground

    val frontColor =  if(disabled) front.copy(0.2f )else front
    var backColor = if(disabled) back.copy(0.2f )else back




        var border =
            BorderStroke(1.dp,SocialTheme.colors.uiBorder)


        val iconColor=SocialTheme.colors.iconPrimary



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

@Composable
fun ChatItem(image: String, title: String, subtitle: String, date: String, onClick: () -> Unit) {
    val trunctuatedSubTitle =  if(subtitle.length>30){subtitle.substring(0, minOf(subtitle.length, 30))+"..."}else{subtitle}
    val trunctuatedTitle =  if(title.length>30){title.substring(0, minOf(title.length, 30))+"..."}else{title}

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)) {
        Column() {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_profile_300),
                    contentDescription = "stringResource(R.string.description)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column (modifier = Modifier.weight(1f)){
                    Text(
                        text = trunctuatedTitle,
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ), color = SocialTheme.colors.textPrimary
                    )
                    Text(
                        text = trunctuatedSubTitle,
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.Light,
                            fontSize = 12.sp
                        ), color = SocialTheme.colors.textPrimary
                    )
                }
                Text(
                    text = getFormattedDate(date),
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ), color = SocialTheme.colors.textPrimary
                )

            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SocialTheme.colors.uiBorder)
            )
        }


    }

}
