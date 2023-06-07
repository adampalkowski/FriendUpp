package com.example.friendupp.ChatUi

import android.widget.EditText
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Home.eButtonSimple
import com.example.friendupp.Home.eButtonSimpleBlue
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.R
import com.example.friendupp.ui.theme.SocialTheme
import kotlinx.coroutines.delay


sealed class ChatEvents {
    object GoBack : ChatEvents()
    object OpenChatSettings : ChatEvents()
    object Reply : ChatEvents()
    object Report : ChatEvents()
    object Delete : ChatEvents()
    object Copy : ChatEvents()
    object Share : ChatEvents()
}


@Composable
fun ChatContent(modifier: Modifier, onEvent: (ChatEvents) -> Unit) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    BackHandler(true) {
        onEvent(ChatEvents.GoBack)
    }

    Column(Modifier.background(SocialTheme.colors.uiBackground)) {
        TopChatBar(
            title = "Adam",
            image = "https://developer.android.com/static/images/jetpack/compose/graphics-sourceimagesmall.jpg",
            chatEvents = onEvent
        )
        Box(Modifier.weight(1f)){
            Column(Modifier.zIndex(5f)) {
                LazyColumn(
                    modifier.weight(1f),
                    reverseLayout = true,
                ) {

                    items(5) {
                        ChatButtonItemLeft(
                            icon = R.drawable.ic_location,
                            "Shared location",
                            onClick = {})

                        ChatItemLeft(modifier=Modifier,"How was your day ???/ ",
                            onLongClick={}, onEvent = onEvent)
                        ChatItemRight("WHERE  are ywe hoing ouy ??",onEvent=onEvent)
                    }

                }
                BottomChatBar(modifier = Modifier)
            }

        }


    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatButtonItemLeft(icon: Int, label: String, onClick: () -> Unit) {
    val bgColor = SocialTheme.colors.uiBorder.copy(0.2f)
    val color =SocialTheme.colors.textPrimary.copy(0.8f)




    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(
                shape = RoundedCornerShape(
                    topEnd = 8.dp,
                    topStart = 8.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 8.dp
                )
            )
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        Icon(painter = painterResource(id = icon), contentDescription =null,tint=color)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold
            , fontSize = 14.sp ),color=color)

    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatItemLeft(modifier: Modifier=Modifier,text: String,timeSent: String="12:12",onLongClick:()->Unit={},onEvent:(ChatEvents)->Unit) {
    var  clicked by remember {
        mutableStateOf(false)
    }
    var selected by remember {
        mutableStateOf(false)
    }

    var elevation = if(selected){4.dp}else{0.dp}

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 12.dp)
    ) {
        AnimatedVisibility(visible = clicked) {
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                , style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Normal)
                , text = timeSent,color=SocialTheme.colors.iconPrimary)
            Spacer(modifier = Modifier.height(4.dp))
        }

        /*todo add emoji reaction to text message
        AnimatedVisibility(visible = selected) {
            Row(
                Modifier

                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            }
        }*/


            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(
                        shape = RoundedCornerShape(
                            topEnd = 8.dp,
                            topStart = 8.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .combinedClickable(
                        onClick = {
                            clicked = !clicked
                        },
                        onLongClick = {
                            selected = !selected
                        },
                    )
                    .background(color = SocialTheme.colors.uiBackground)

                    .border(
                        border = BorderStroke(1.dp, SocialTheme.colors.uiBorder),
                        shape = RoundedCornerShape(
                            topEnd = 8.dp,
                            topStart = 8.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = SocialTheme.colors.textPrimary
                    )
                )
            }
        AnimatedVisibility(visible = selected) {
            Row(
                Modifier

                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                ChatSettingItem(label ="Reply" , icon =R.drawable.ic_reply , onClick = {onEvent(ChatEvents.Reply)})
                ChatSettingItem(label ="Copy" , icon =R.drawable.ic_copy , onClick = {onEvent(ChatEvents.Copy)})
                ChatSettingItem(label ="Share" , icon =R.drawable.ic_share , onClick = {onEvent(ChatEvents.Share)})
                ChatSettingItem(label ="Delete" , icon =R.drawable.ic_delete , onClick = {onEvent(ChatEvents.Delete)})
                ChatSettingItem(label ="Report" , icon =R.drawable.ic_flag , onClick = {onEvent(ChatEvents.Report)})
            }
        }

    }
}

@Composable
fun ChatSettingItem(label:String,icon: Int,onClick: () -> Unit){
    val color = SocialTheme.colors.uiBorder.copy(0.2f)
    Column (horizontalAlignment = Alignment.CenterHorizontally){
        Box(modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick=onClick)
            .background(color)
            .padding(12.dp)){
            Icon(painter = painterResource(id = icon), contentDescription =null, tint = SocialTheme.colors.textPrimary.copy(0.8f) )

        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = label, style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Normal), color = SocialTheme.colors.textPrimary.copy(0.8f))
    }

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatItemRight(text: String,timeSent:String="12:12",onEvent:(ChatEvents)->Unit) {
    var  clicked by remember {
        mutableStateOf(false)
    }

    var selected by remember {
        mutableStateOf(false)
    }

    Column(horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 12.dp)
    ) {
        AnimatedVisibility(visible = clicked) {
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Normal)
                , text = timeSent,color=SocialTheme.colors.iconPrimary)
            Spacer(modifier = Modifier.height(4.dp))
        }
            Box(
                modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(
                            topEnd = 8.dp,
                            topStart = 8.dp,
                            bottomStart = 8.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .combinedClickable(
                        onClick = {
                            clicked = !clicked
                        },
                        onLongClick = {
                            selected=!selected
                        },
                    )

                    .fillMaxHeight()
                    .background(color = SocialTheme.colors.textPrimary.copy(alpha = 0.8f))

                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = SocialTheme.colors.uiBackground.copy(alpha = 0.9f)
                    )
                )
            }
        AnimatedVisibility(visible = selected) {
            Row(
                Modifier

                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                ChatSettingItem(label ="Reply" , icon =R.drawable.ic_reply , onClick = {onEvent(ChatEvents.Reply)})
                ChatSettingItem(label ="Copy" , icon =R.drawable.ic_copy , onClick = {onEvent(ChatEvents.Copy)})
                ChatSettingItem(label ="Share" , icon =R.drawable.ic_share , onClick = {onEvent(ChatEvents.Share)})
                ChatSettingItem(label ="Delete" , icon =R.drawable.ic_delete , onClick = {onEvent(ChatEvents.Delete)})

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialEditText(modifier: Modifier, focus: Boolean, onFocusChange: (Boolean) -> Unit) {

    val focusRequester = remember { FocusRequester() }


    var text by remember {
        mutableStateOf("")
    }
    var focus by remember {
        mutableStateOf(false)
    }
    var borderColor = if (focus) {
        Color(0xFFF3DB86)
    } else {
        SocialTheme.colors.uiBorder
    }
    Card(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .wrapContentHeight(align = Alignment.Top),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SocialTheme.colors.uiBackground)
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        onFocusChange(it.isFocused)
                    }
                    .wrapContentHeight(align = Alignment.Top)
                    .fillMaxWidth(),
                value = text, onValueChange = { text = it },
                textStyle = TextStyle(
                    fontFamily = Lexend, fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                ),
                singleLine = false,
                maxLines = Int.MAX_VALUE,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { /* Perform action on Done button press */ }
                ),
            )
            if (text.isEmpty()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 0.dp),
                    text = "Send a message",
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Light
                    )
                )
            }
        }
    }

}

@Composable
fun chatButtonsRow(modifier: Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .width(32.dp)
                .height(1.dp)
                .background(color = Color.Transparent)
        )
        eButtonSimple(icon = R.drawable.ic_pindrop_300)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
        eButtonSimple(icon = R.drawable.ic_highlight_300)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
        eButtonSimple(icon = R.drawable.ic_image_300)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
        eButtonSimple(icon = R.drawable.ic_wave_300)

        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )

        Spacer(
            modifier = Modifier
                .width(56.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )
    }

}

@Composable
fun BottomChatBar(modifier: Modifier = Modifier) {
    var focused by remember { mutableStateOf(false) }
    Column(modifier.background(color = SocialTheme.colors.uiBackground.copy(0.7f))) {
        chatButtonsRow(modifier = Modifier)
        //todo set appropirate color to theme
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp, top = 6.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            SocialEditText(
                modifier = Modifier.weight(1f),
                focus = false,
                onFocusChange = { focusState ->
                    focused = focusState
                })
            Spacer(
                modifier = Modifier
                    .width(12.dp)
            )
            eButtonSimpleBlue(icon = R.drawable.ic_send, onClick = {})
        }

    }
}

@Composable
fun TopChatBar(title: String, image: String, chatEvents: (ChatEvents) -> Unit) {
    Column {

        //todo set appropirate color to theme
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { chatEvents(ChatEvents.GoBack) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconPrimary
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SocialTheme.colors.textPrimary
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { chatEvents(ChatEvents.OpenChatSettings) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconPrimary
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = SocialTheme.colors.uiBorder)
        )

    }
}
