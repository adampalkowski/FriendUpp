package com.palkowski.friendupp.ChatUi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.palkowski.friendupp.R
import com.palkowski.friendupp.model.ChatMessage
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatItemRight(
    text_type: String,
    text: String,
    timeSent: String = "12:12",
    onEvent: (ChatEvents) -> Unit, chat: ChatMessage,
    onClick: () -> Unit,
    displayLocation:(LatLng)->Unit,
    highlite_message:Boolean,
    isReply:Boolean=false,
    replyTo: String?,
    groupId: String?,
    displayImage:(String)->Unit
) {
    var clicked by remember {
        mutableStateOf(false)
    }

    var selected by remember {
        mutableStateOf(false)
    }

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .padding(horizontal = 12.dp)
    ) {
        AnimatedVisibility(visible = clicked) {
            Text(
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ), text = timeSent, color = SocialTheme.colors.iconPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (text_type.equals("uri")) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(chat.text)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_image_300),
                contentDescription = "image sent",
                contentScale = ContentScale.Crop,
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
                            if (!isReply) {
                                if (highlite_message) {
                                    clicked = !clicked

                                } else {
                                    displayImage(chat.text)

                                }
                            }


                        },
                        onLongClick = {
                            if (!isReply) {
                                clicked = !clicked
                                selected = !selected
                            }

                        },               interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = Color.Black),
                    )

            )
        } else if (text_type.equals("text")) {
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
                            if (!isReply) {
                                if (highlite_message) {
                                    onClick()
                                } else {
                                    onClick()
                                    clicked = !clicked
                                }
                            }

                        },
                        onLongClick = {
                            if (!isReply) {
                                selected = !selected
                            }

                        },
                    )

                    .background(color = SocialTheme.colors.textPrimary.copy(alpha = 0.8f))

                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,

                        ),
                    color = SocialTheme.colors.textSecondary
                )
            }
        } else if (text_type.equals("latLng")) {
            Box(modifier = Modifier
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
                        if (!isReply) {
                            val latLng = createLatLngFromString(text)
                            if (latLng != null) {
                                displayLocation(latLng)
                            }
                        }


                    },
                    onLongClick = {
                        if (!isReply) {
                            clicked = !clicked
                            onClick()
                            selected = !selected
                        }

                    },
                )

                .background(color = SocialTheme.colors.textPrimary.copy(alpha = 0.8f))
                .padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        tint =  SocialTheme.colors.textSecondary,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Shared location",
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,

                            ),                     color = SocialTheme.colors.uiBackground

                    )
                }

            }
        } else if (text_type.equals("live")) {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        tint = SocialTheme.colors.iconPrimary,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ClickableText(
                        text = AnnotatedString("Live activity shared"),
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = SocialTheme.colors.textPrimary,
                            textDecoration = TextDecoration.Underline,
                        ),
                        onClick = {
                            /*todo join live*/
                        }
                    )
                }

            }
        }else if(text_type.equals("reply")){

            Column(horizontalAlignment = Alignment.End) {

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
                                if (!isReply) {
                                    if (highlite_message) {
                                        onClick()
                                    } else {
                                        onClick()
                                        clicked = !clicked
                                    }
                                }

                            },
                            onLongClick = {
                                if (!isReply) {
                                    selected = !selected
                                }

                            },
                        )

                        .background(color = SocialTheme.colors.textPrimary.copy(alpha = 0.1f))

                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .padding(bottom = 20.dp)
                ) {
                    Text(
                        text = replyTo!!,
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,

                            ),
                        color = SocialTheme.colors.textPrimary.copy(0.4f)
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(y = (-20).dp)
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
                                if (!isReply) {
                                    if (highlite_message) {
                                        onClick()
                                    } else {
                                        onClick()
                                        clicked = !clicked
                                    }
                                }

                            },
                            onLongClick = {
                                if (!isReply) {
                                    selected = !selected
                                }

                            },
                        )
                        .background(color = SocialTheme.colors.uiBackground)
                        .background(color = SocialTheme.colors.textPrimary.copy(alpha = 0.8f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = text,
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,

                            ),
                        color = SocialTheme.colors.uiBackground
                    )
                }
            }





        }


        AnimatedVisibility(visible = selected) {
            Row(
                Modifier

                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChatSettingItem(
                    label = "Reply",
                    icon = R.drawable.ic_reply,
                    onClick = { onEvent(ChatEvents.Reply(chat)) })
                ChatSettingItem(
                    label = "Copy",
                    icon = R.drawable.ic_copy,
                    onClick = { onEvent(ChatEvents.Copy(chat.text)) })
                ChatSettingItem(
                    label = "Share",
                    icon = R.drawable.ic_share,
                    onClick = { onEvent(ChatEvents.Share) })
                ChatSettingItem(
                    label = "Delete",
                    icon = R.drawable.ic_delete,
                    onClick = {
                        if(groupId!=null){
                            onEvent(ChatEvents.Delete(groupId,chat.id))
                        }
                      })

            }
        }
    }
}