package com.example.friendupp.Home

import android.widget.ProgressBar
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.ActivityUi.activityItem
import com.example.friendupp.Categories.Category
import com.example.friendupp.Components.Calendar.rememberHorizontalDatePickerState2
import com.example.friendupp.Components.CalendarComponent
import com.example.friendupp.Components.FilterList
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.Pacifico
import com.example.friendupp.ui.theme.SocialTheme
import kotlinx.coroutines.launch
import com.example.friendupp.R
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response

import java.net.CookieHandler

sealed class HomeEvents {
    object OpenDrawer : HomeEvents()
    object CreateLive : HomeEvents()
    class ExpandActivity(val activityData: Activity) : HomeEvents()
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onEvent: (HomeEvents) -> Unit,
    activityViewModel: ActivityViewModel,
) {
    var calendarView by rememberSaveable {
        mutableStateOf(false)
    }
    var filterView by rememberSaveable {
        mutableStateOf(false)
    }
    val activities = remember { mutableStateListOf<Activity>() }

    Column() {
        TopBar(modifier = Modifier, onClick = {
            calendarView = !calendarView
            if (filterView) {
                filterView = !filterView
            }

        }, openFilter = {
            filterView = !filterView
            if (calendarView) {
                calendarView = !calendarView
            }
        }, calendarView, filterView, openDrawer = { onEvent(HomeEvents.OpenDrawer) })

        LazyColumn(
            modifier
                .weight(1f)
        ) {

            item {
                AnimatedVisibility(visible = calendarView) {
                    val state = rememberHorizontalDatePickerState2()
                    CalendarComponent(state)
                }
            }
            item {
                AnimatedVisibility(visible = filterView) {
                    FilterList(tags = SnapshotStateList(), onSelected = {}, onDeSelected = {})
                }
            }

            item {
                OptionPicker(onEvent = onEvent)
            }


            items(activities) { activity ->
                activityItem(
                    activity,
                    onClick = {
                        // Handle click event
                    },
                    onExpand = { onEvent(HomeEvents.ExpandActivity(it)) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))

            }
        }
    }
    activityViewModel.activitiesListState.value.let { response ->
        when (response) {
            is Response.Success -> {
                activities.clear()
                activities.addAll(response.data)
            }
            is Response.Failure -> {

                Toast.makeText(LocalContext.current, "FAiled", Toast.LENGTH_SHORT).show()

            }
            is Response.Loading -> {
                Toast.makeText(LocalContext.current, "LOAding", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

val image1 =
    "data:image/jpeg;base64,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"
val image2 =
    "data:image/jpeg;base64,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"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveUserItem(imageUrl: String, text: String = "") {

    Box(
        modifier = Modifier
            .height(72.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "stringResource(R.string.description)",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(
                    BorderStroke(1.dp, SocialTheme.colors.textInteractive),
                    CircleShape
                )
                .border(
                    BorderStroke(3.dp, SocialTheme.colors.uiBackground),
                    CircleShape
                )
        )
        if (text.isNotEmpty()) {
            Card(
                modifier = Modifier.align(Alignment.BottomCenter),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, SocialTheme.colors.uiBorder)
            ) {
                Box(
                    modifier = Modifier.background(SocialTheme.colors.uiBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                        text = text,
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = SocialTheme.colors.textPrimary.copy(0.8f)
                        )
                    )
                }
            }
        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLive(imageUrl: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick), contentAlignment = Alignment.Center

    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "stringResource(R.string.description)",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(
                    BorderStroke(1.dp, Color.Green),
                    CircleShape
                )
                .border(
                    BorderStroke(3.dp, SocialTheme.colors.uiBackground),
                    CircleShape
                )
        )
        Card(
            shape = RoundedCornerShape(100),
            colors = CardDefaults.cardColors(
                contentColor = Color.Transparent,
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(color = Color.Black.copy(0.8f)), contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    tint = Color.White,
                    contentDescription = null
                )
            }

        }

    }

}


@Composable
fun TopBar(
    modifier: Modifier,
    onClick: () -> Unit,
    openFilter: () -> Unit,
    calendarClicked: Boolean,
    filterClicked: Boolean, openDrawer: () -> Unit,
) {
    Column() {
        Box(
            modifier
                .fillMaxWidth()
                .background(SocialTheme.colors.uiBackground)
                .padding(vertical = 12.dp, horizontal = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SocialButtonNormal(icon = R.drawable.ic_menu_300, onClick = openDrawer)
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = "FriendUpp",
                    style = TextStyle(
                        fontFamily = Pacifico,
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        color = SocialTheme.colors.textPrimary.copy(0.8f)
                    )
                )
                Spacer(modifier = Modifier.weight(1f))

                SocialButtonNormal(
                    icon = R.drawable.ic_filte_300,
                    onClick = openFilter,
                    filterClicked
                )
                Spacer(modifier = Modifier.width(12.dp))
                SocialButtonNormal(
                    icon = R.drawable.ic_calendar_300,
                    onClick = onClick,
                    calendarClicked
                )

            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialButtonNormal(icon: Int, onClick: () -> Unit, clicked: Boolean = false) {
    val interactionSource = MutableInteractionSource()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scale = remember {
        Animatable(1f)
    }

    val FrontColor by animateColorAsState(
        if (clicked) {
            SocialTheme.colors.textInteractive
        } else {
            SocialTheme.colors.uiBackground
        }, tween(300)
    )
    var border = if (clicked) {
        null
    } else {
        BorderStroke(0.5.dp, SocialTheme.colors.uiBorder)
    }

    var iconColor = if (clicked) {
        Color.White
    } else {
        SocialTheme.colors.textPrimary.copy(0.8f)

    }
    var elevation = if (clicked) {
        10.dp
    } else {
        0.dp
    }
    Box(
        modifier = Modifier
            .background(Color.Transparent)
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
                .width(48.dp)
                .height(48.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            colors = CardDefaults.cardColors(
                contentColor = Color.Transparent,
                containerColor = FrontColor
            ),
            shape = RoundedCornerShape(8.dp),
            border = border
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = iconColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialButton(icon: Int, onClick: () -> Unit, clicked: Boolean = false) {
    val interactionSource = MutableInteractionSource()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scale = remember {
        Animatable(1f)
    }


    val FrontColor by animateColorAsState(
        if (clicked) {
            Color(0xFF88A2FF)
        } else {
            Color.White
        }, tween(300)
    )
    var border = if (clicked) {
        null
    } else {
        BorderStroke(1.dp, Color(0xFFD9D9D9))
    }
    val BackColor by animateColorAsState(
        targetValue = if (clicked) {
            Color(0xFF5E6FAB)
        } else {
            Color(0xFFB7B7B7)
        }, tween(300)
    )

    var iconColor = if (clicked) {
        Color.White
    } else {
        Color.Black.copy(0.8f)
    }
    Box(
        modifier = Modifier
            .background(Color.Transparent)
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
                .width(48.dp)
                .height(48.dp),
            colors = CardDefaults.cardColors(
                contentColor = Color.Transparent,
                containerColor = BackColor
            ),
            shape = RoundedCornerShape(8.dp)
        ) {

        }
        Card(
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .zIndex(2f)
                .graphicsLayer {
                    translationX = -10f
                    translationY = -10f
                },
            colors = CardDefaults.cardColors(
                contentColor = Color.Transparent,
                containerColor = FrontColor
            ),
            shape = RoundedCornerShape(8.dp),
            border = border
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = iconColor
                )
            }
        }
    }
}


enum class Option(val label: String, val icon: Int) {
    FRIENDS("Friends", R.drawable.ic_hand_300),
    PUBLIC("Public", R.drawable.ic_public_300)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionPicker(onEvent: (HomeEvents) -> Unit) {
    val context = LocalContext.current
    var selectedOption by rememberSaveable { mutableStateOf(Option.PUBLIC) }
    val dividerColor = SocialTheme.colors.uiBorder
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            )
    ) {
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(option = Option.FRIENDS,
            isSelected = selectedOption == Option.FRIENDS,
            onClick = { selectedOption = Option.FRIENDS })
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(option = Option.PUBLIC,
            isSelected = selectedOption == Option.PUBLIC,
            onClick = { selectedOption = Option.PUBLIC })
        Spacer(
            modifier = Modifier
                .width(64.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        CreateLive(
            onClick = { onEvent(HomeEvents.CreateLive) },
            imageUrl = "https://images.unsplash.com/photo-1587691592099-24045742c181?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2073&q=80"
        )
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        LiveUserItem(
            text = "Sports??",
            imageUrl = "https://images.unsplash.com/photo-1587691592099-24045742c181?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2073&q=80"
        )
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        LiveUserItem(imageUrl = "https://images.unsplash.com/photo-1587691592099-24045742c181?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2073&q=80")
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        LiveUserItem(imageUrl = "https://images.unsplash.com/photo-1587691592099-24045742c181?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2073&q=80")
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(dividerColor)
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionButton(option: Option, isSelected: Boolean, onClick: () -> Unit) {
    val backColor by animateColorAsState(
        targetValue = if (isSelected) {
            SocialTheme.colors.iconInteractive
        } else {
            SocialTheme.colors.uiBorder
        }, tween(300)
    )
    val frontColor by animateColorAsState(
        if (isSelected) {
            SocialTheme.colors.textInteractive
        } else {
            SocialTheme.colors.uiBackground
        }, tween(300)
    )
    var border = if (isSelected) {
        null

    } else {
        BorderStroke(1.dp, SocialTheme.colors.uiBorder)

    }

    val iconColor by animateColorAsState(
        if (isSelected) {
            Color.White
        } else {
            SocialTheme.colors.iconPrimary
        }, tween(300)
    )


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
                        painter = painterResource(id = option.icon),
                        contentDescription = null,
                        tint = iconColor
                    )
                }
            }
        }
    }
}


@Composable
fun buttonsRow(modifier: Modifier) {
    var bookmarked by remember { mutableStateOf(false) }
    val bookmarkColor: Color by animateColorAsState(
        if (bookmarked) Color(0xFF00CCDF) else SocialTheme.colors.iconPrimary,
        animationSpec = tween(1000, easing = LinearEasing)
    )
    var switch by remember { mutableStateOf(false) }
    val alpha: Float by animateFloatAsState(
        if (switch) 1f else 0f,
        animationSpec = tween(1000, easing = LinearEasing)
    )

    val bgColor: Color by animateColorAsState(
        if (switch) Color.Green else SocialTheme.colors.uiBorder,
        animationSpec = tween(1000, easing = LinearEasing)
    )
    val iconColor: Color by animateColorAsState(
        if (switch) Color.Green else SocialTheme.colors.iconPrimary,
        animationSpec = tween(1000, easing = LinearEasing)
    )
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .width(32.dp)
                .height(
                    if (switch) {
                        1.dp
                    } else {
                        0.5.dp
                    }
                )
                .background(color = bgColor)
        )
        eButtonSimple(icon = R.drawable.ic_check_300, onClick = {
            switch = !switch
        }, iconColor = iconColor, selected = switch, iconFilled = R.drawable.ic_check_filled)
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(
                    if (switch) {
                        1.dp
                    } else {
                        0.5.dp
                    }
                )
                .background(color = bgColor)
        )
        eButtonSimple(icon = R.drawable.ic_chat_300, onClick = {})
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(
                    if (switch) {
                        1.dp
                    } else {
                        0.5.dp
                    }
                )
                .background(color = bgColor)
        )
        eButtonSimple(
            icon = R.drawable.ic_bookmark_300,
            onClick = {
                bookmarked = !bookmarked
            },
            iconColor = bookmarkColor,
            selected = bookmarked,
            iconFilled = R.drawable.ic_bookmark_filled
        )


        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp), contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        if (switch) {
                            1.dp
                        } else {
                            0.5.dp
                        }
                    )
                    .background(SocialTheme.colors.uiBorder)
            )
            if (switch) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = alpha)
                        .height((alpha * 1).dp)
                        .background(bgColor)
                )

            }


        }


    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun eButtonSimple(
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    iconColor: Color = SocialTheme.colors.iconPrimary,
    selected: Boolean = false,
    iconFilled: Int = R.drawable.ic_bookmark_filled,
) {
    val backColor = if (selected) {
        Color.Green
    } else {
        SocialTheme.colors.uiBorder.copy(0.1f)
    }
    val iconColor = if (selected) {
        Color.White
    } else {
        SocialTheme.colors.iconPrimary
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backColor)
            .clickable(onClick = onClick), contentAlignment = Alignment.Center
    ) {

        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconColor
        )
        /* AnimatedVisibility(visible = selected, enter = scaleIn() , exit = scaleOut()) {
             Icon(
                 painter = painterResource(id = iconFilled),
                 contentDescription = null,
                 tint = iconColor
             )
         }
         AnimatedVisibility(visible =!selected, enter = scaleIn() , exit = scaleOut()) {
             Icon(
                 painter = painterResource(id = icon),
                 contentDescription = null,
                 tint = iconColor
             )
         }*/


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun eButtonSimpleBlue(onClick: () -> Unit, icon: Int, modifier: Modifier = Modifier) {
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
            .scale(scale = scale.value)
    ) {
        Card(
            modifier = Modifier
                .width(48.dp)
                .height(48.dp),
            colors = CardDefaults.cardColors(
                contentColor = Color.Transparent,
                containerColor = Color(0xff3E5DC9)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {

        }
        Card(
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .zIndex(2f)
                .graphicsLayer {
                    translationY = -8f
                },
            colors = CardDefaults.cardColors(
                contentColor = Color.Transparent,
                containerColor = Color(0xff6688FF)
            ),
            shape = RoundedCornerShape(12.dp),

            ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}