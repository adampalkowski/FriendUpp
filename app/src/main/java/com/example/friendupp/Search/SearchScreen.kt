package com.example.friendupp.Search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.CreateHeading
import com.example.friendupp.Home.eButtonSimpleBlue
import com.example.friendupp.Profile.UsernameState
import com.example.friendupp.Profile.UsernameStateSaver
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(goBack: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val usernameState by rememberSaveable(stateSaver = UsernameStateSaver) {
        mutableStateOf(UsernameState())
    }
    BackHandler(true) {
        goBack()
    }

    Column() {


        LazyColumn {
            item {
                ScreenHeading(title = "Search", backButton = true, onBack = goBack) {

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NameEditText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        focusRequester = focusRequester,
                        focus = false,
                        onFocusChange = { focusState ->

                        }, label = "Search by username", textState = usernameState
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    eButtonSimpleBlue(icon = R.drawable.ic_search, onClick = {}, modifier = Modifier.padding(top=8.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                DiscoverComponent()
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                CreateHeading(text = "Invites", icon = R.drawable.ic_invites, tip = false)
            }
            items(5) {
                var visibility by remember {
                    mutableStateOf(true)
                }
                AnimatedVisibility(visible = visibility) {
                    InviteItem(
                        name = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                        username = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                        profilePictureUrl = "https://images.unsplash.com/photo-1529665253569-6d01c0eaf7b6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cHJvZmlsZXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60",
                        onInvite = {
                            visibility = !visibility
                        })
                }

            }

        }
    }
}

@Composable
fun InvitesComponent() {
    Column() {
        CreateHeading(text = "Invites", icon = R.drawable.ic_invites, tip = false)
        LazyColumn {
            item {
                InviteItem(
                    name = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                    username = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                    profilePictureUrl = "https://images.unsplash.com/photo-1529665253569-6d01c0eaf7b6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cHJvZmlsZXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60"
                )
            }
            item {
                InviteItem(
                    name = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                    username = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                    profilePictureUrl = "https://images.unsplash.com/photo-1529665253569-6d01c0eaf7b6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cHJvZmlsZXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60"
                )
            }

            item {
                InviteItem(
                    name = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                    username = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                    profilePictureUrl = "https://images.unsplash.com/photo-1529665253569-6d01c0eaf7b6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cHJvZmlsZXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60"
                )
            }
            item {
                InviteItem(
                    name = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                    username = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                    profilePictureUrl = "https://images.unsplash.com/photo-1529665253569-6d01c0eaf7b6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cHJvZmlsZXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60"
                )
            }
            item {
                InviteItem(
                    name = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                    username = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                    profilePictureUrl = "https://images.unsplash.com/photo-1529665253569-6d01c0eaf7b6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cHJvZmlsZXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60"
                )
            }
        }
    }
}

@Composable
fun InviteItem(
    profilePictureUrl: String,
    username: String,
    name: String,
    onInvite: () -> Unit = {}
) {

    val truncatedName = if (name.length > 15) name.substring(0, 15) + "..." else name
    val truncatedUsername =
        if (username.length > 15) username.substring(0, 15) + "..." else username

    Column {
        Box(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(SocialTheme.colors.iconInteractiveInactive)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(SocialTheme.colors.uiBorder.copy(0.1f))
                .padding(horizontal = 24.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(com.example.friendupp.R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = truncatedName,
                    color = SocialTheme.colors.textPrimary,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = truncatedUsername,
                    color = SocialTheme.colors.textPrimary.copy(0.6f),
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp
                    )
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                ActionButton(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SocialTheme.colors.textInteractive)
                        .padding(horizontal = 18.dp, vertical = 8.dp), onClick = onInvite
                )
                Spacer(modifier = Modifier.height(4.dp))
                InActionButton(
                    label = "Remove", modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            BorderStroke(1.dp, SocialTheme.colors.uiBorder),
                            RoundedCornerShape(8.dp)
                        )
                        .background(SocialTheme.colors.uiBackground)
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    discardIcon = R.drawable.ic_delete
                )
            }
        }
        Box(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(SocialTheme.colors.iconInteractiveInactive)
        )

    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiscoverComponent() {
    Column() {
        CreateHeading(text = "You may want to know", icon = R.drawable.ic_wave, tip = false)
        LazyRow {
            item {
                Spacer(modifier = Modifier.width(24.dp))
            }
            items(5) {
                var visibility by remember {
                    mutableStateOf(true)
                }
                AnimatedVisibility(visible = visibility, exit = scaleOut()) {
                    DiscoverItem(
                        name = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                        username = "<MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                        pictureUrl = "https://images.unsplash.com/photo-1529665253569-6d01c0eaf7b6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cHJvZmlsZXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60",
                        onAccept = { visibility = !visibility })

                }
                Spacer(modifier = Modifier.width(16.dp))

            }

        }
    }
}

@Composable
fun DiscoverItem(pictureUrl: String, name: String, username: String, onAccept: () -> Unit) {
    val truncatedName = if (name.length > 15) name.substring(0, 15) + "..." else name
    val truncatedUsername =
        if (username.length > 15) username.substring(0, 15) + "..." else username

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color = SocialTheme.colors.uiBorder.copy(0.15f))
            .border(
                BorderStroke(1.dp, SocialTheme.colors.uiBorder.copy(0.6f)),
                RoundedCornerShape(12.dp)
            )
            .widthIn(100.dp, 200.dp)
            .padding(top = 6.dp)
    ) {
        Box(
            modifier = Modifier.border(
                BorderStroke(1.dp, SocialTheme.colors.uiBorder)
            )
        ) {

        }


        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = truncatedName,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = SocialTheme.colors.textPrimary
            )
            Text(
                text = truncatedUsername,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp
                ),
                color = SocialTheme.colors.textPrimary.copy(0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row() {
                InActionButton(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 8.dp))
                        .border(
                            BorderStroke(1.dp, SocialTheme.colors.uiBorder),
                            RoundedCornerShape(bottomStart = 8.dp)
                        )
                        .background(SocialTheme.colors.uiBackground)
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    discardIcon = R.drawable.ic_visibility_off
                )
                ActionButton(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomEnd = 8.dp))
                        .background(SocialTheme.colors.textInteractive)
                        .padding(horizontal = 18.dp, vertical = 8.dp), onClick = onAccept
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    modifier: Modifier,
    label: String = "Invite",
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val textVisible = remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                coroutineScope.launch {
                    scale.animateTo(0.8f, animationSpec = tween(300))
                    scale.animateTo(1f, animationSpec = tween(100))
                    textVisible.value = false
                    delay(1000)
                    textVisible.value = true
                    onClick()
                }
            }
            .scale(scale = scale.value),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = textVisible.value) {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = Color.White
            )
        }
        AnimatedVisibility(visible = !textVisible.value) {
            Icon(
                painter = painterResource(R.drawable.ic_check_300),
                contentDescription = "Icon",
                tint = Color.White
            )
        }

    }
}

@Composable
fun InActionButton(modifier: Modifier, label: String = "Hide", onClick: () -> Unit = {},discardIcon:Int) {
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val textVisible = remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                coroutineScope.launch {
                    scale.animateTo(0.8f, animationSpec = tween(300))
                    scale.animateTo(1f, animationSpec = tween(100))
                    textVisible.value = false
                    delay(1000)
                    textVisible.value = true
                    onClick()
                }
            }
            .scale(scale = scale.value), contentAlignment = Alignment.Center
    ) {

        AnimatedVisibility(visible = textVisible.value) {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = SocialTheme.colors.textPrimary.copy(0.6f)
            )
        }
        AnimatedVisibility(visible = !textVisible.value) {
            Icon(
                painter = painterResource(discardIcon),
                contentDescription = "Icon",
                tint = SocialTheme.colors.textPrimary.copy(0.6f)
            )
        }

    }
}



