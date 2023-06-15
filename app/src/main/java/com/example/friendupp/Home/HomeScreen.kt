package com.example.friendupp.Home

import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.friendupp.ActivityUi.ActivityEvents
import com.example.friendupp.ActivityUi.activityItem
import com.example.friendupp.ActivityUi.activityItemCard
import com.example.friendupp.Categories.Category
import com.example.friendupp.Components.Calendar.rememberHorizontalDatePickerState2
import com.example.friendupp.Components.CalendarComponent
import com.example.friendupp.Components.FilterList
import com.example.friendupp.Login.SplashScreen
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.Pacifico
import com.example.friendupp.ui.theme.SocialTheme
import kotlinx.coroutines.launch
import com.example.friendupp.R
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData

import java.net.CookieHandler

sealed class HomeEvents {
    object OpenDrawer : HomeEvents()
    object CreateLive : HomeEvents()
    class ExpandActivity(val activityData: Activity) : HomeEvents()
    class JoinActivity(val id: String) : HomeEvents()
    class LeaveActivity(val id: String) : HomeEvents()
    class OpenChat(val id: String) : HomeEvents()
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onEvent: (HomeEvents) -> Unit,
    activityViewModel: ActivityViewModel,
) {
    Log.d("ActivityLoadInDebug", "Screen load")
    var calendarView by rememberSaveable {
        mutableStateOf(false)
    }
    var filterView by rememberSaveable {
        mutableStateOf(false)
    }
    val activities = remember { mutableStateListOf<Activity>() }
    val moreActivities = remember { mutableStateListOf<Activity>() }
    var activitiesExist = remember { mutableStateOf(false) }



    val publicActivities = remember { mutableStateListOf<Activity>() }
    val morePublicActivities = remember { mutableStateListOf<Activity>() }
    var publicActivitiesExist = remember { mutableStateOf(false) }

    // PUBLIC OR FRIENDS ACTIVITIEWS
    var selectedOption by rememberSaveable { mutableStateOf(Option.FRIENDS) }
    if(selectedOption==Option.FRIENDS){
        loadFriendsActivities(activityViewModel,activities, activitiesExist = activitiesExist)
        loadMoreFriendsActivities(activityViewModel,moreActivities)

    }else{
        loadPublicActivities(activityViewModel,publicActivities, activitiesExist = publicActivitiesExist)
        loadMorePublicActivities(activityViewModel,morePublicActivities)
    }

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
        val lazyListState = rememberLazyListState()
        LazyColumn(
            modifier
              ,
            state = lazyListState
        ) {

            item {
                AnimatedVisibility(visible = calendarView, enter = slideInVertically(animationSpec = tween(800)), exit = slideOutVertically(animationSpec = tween(0)) ) {
                    val state = rememberHorizontalDatePickerState2()
                    CalendarComponent(state)

                }
                AnimatedVisibility(visible = filterView, enter = slideInVertically(animationSpec = tween(800)), exit = slideOutVertically(animationSpec = tween(0)) ) {
                    FilterList(tags = SnapshotStateList(), onSelected = {}, onDeSelected = {})

                }
                OptionPicker(onEvent = onEvent, onOptionSelected = {option->selectedOption=option}, selectedOption = selectedOption)
            }
            if(selectedOption==Option.FRIENDS){
                items(activities) { activity ->
                    activityItem(
                        activity,
                        onClick = {
                            // Handle click event
                        },
                        onEvent = { event->
                            when(event){
                                is ActivityEvents.Expand->{
                                    onEvent(HomeEvents.ExpandActivity(event.activity))
                                }
                                is ActivityEvents.Join->{  onEvent(HomeEvents.JoinActivity(event.id))}
                                is ActivityEvents.OpenChat->{  onEvent(HomeEvents.OpenChat(event.id))}
                            }
                        }
                    )
                }

                items(moreActivities) { activity ->
                    activityItem(
                        activity,
                        onClick = {
                            // Handle click event
                        },
                        onEvent = { event->
                            when(event){
                                is ActivityEvents.Expand->{
                                    onEvent(HomeEvents.ExpandActivity(event.activity))
                                }
                                is ActivityEvents.Join->{  onEvent(HomeEvents.JoinActivity(event.id))}
                                is ActivityEvents.OpenChat->{  onEvent(HomeEvents.OpenChat(event.id))}
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(64.dp))

                }
                item {
                    LaunchedEffect(true) {
                        if (activitiesExist.value) {
                            activityViewModel.getMoreActivitiesForUser(UserData.user!!.id)
                        }
                    }
                }
            }else{
                items(publicActivities) { activity ->
                    activityItem(
                        activity,
                        onClick = {
                            // Handle click event
                        },
                        onEvent = { event->
                            when(event){
                                is ActivityEvents.Expand->{
                                    onEvent(HomeEvents.ExpandActivity(event.activity))
                                }
                                is ActivityEvents.Join->{  onEvent(HomeEvents.JoinActivity(event.id))}
                                is ActivityEvents.OpenChat->{  onEvent(HomeEvents.OpenChat(event.id))}
                            }
                        }
                    )
                }

                items(morePublicActivities) { activity ->
                    activityItem(
                        activity,
                        onClick = {
                            // Handle click event
                        },
                        onEvent = { event->
                            when(event){
                                is ActivityEvents.Expand->{
                                    onEvent(HomeEvents.ExpandActivity(event.activity))
                                }
                                is ActivityEvents.Join->{  onEvent(HomeEvents.JoinActivity(event.id))}
                                is ActivityEvents.OpenChat->{  onEvent(HomeEvents.OpenChat(event.id))}
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(64.dp))

                }
                item {
                    LaunchedEffect(true) {
                        if (activitiesExist.value) {
                            activityViewModel.location.value.let {location->
                                if(location!=null){
                                    activityViewModel.getMoreClosestActivities(location.latitude,location.longitude, 50.0*1000.0)
                                }

                            }
                        }
                    }
                }
            }


        }
    }


}


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
fun OptionPicker(onEvent: (HomeEvents) -> Unit,selectedOption:Option,onOptionSelected:(Option)->Unit) {
    val context = LocalContext.current
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
            onClick = {onOptionSelected(Option.FRIENDS)})
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(option = Option.PUBLIC,
            isSelected = selectedOption == Option.PUBLIC,
            onClick = {onOptionSelected(Option.PUBLIC)})
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
fun buttonsRow(modifier: Modifier,
               onEvent:(ActivityEvents)->Unit,id:String,joined:Boolean=false) {
    var bookmarked by remember { mutableStateOf(false) }
    val bookmarkColor: Color by animateColorAsState(
        if (bookmarked) Color(0xFF00CCDF) else SocialTheme.colors.iconPrimary,
        animationSpec = tween(1000, easing = LinearEasing)
    )
    var switch by remember { mutableStateOf(joined) }
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
            onEvent(ActivityEvents.Join(id))
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
        eButtonSimple(icon = R.drawable.ic_chat_300, onClick = {onEvent(ActivityEvents.OpenChat(id))})
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