package com.example.friendupp.Home

import android.util.Log
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.friendupp.ActivityUi.ActivityEvents
import com.example.friendupp.ActivityUi.activityItem
import com.example.friendupp.Components.ActionButton
import com.example.friendupp.Components.ActionButtonDefault
import com.example.friendupp.Components.Calendar.rememberHorizontalDatePickerState2
import com.example.friendupp.Components.CalendarComponent
import com.example.friendupp.Components.FilterList
import com.example.friendupp.Create.Option
import com.example.friendupp.Create.rememberSelectedOptionState
import com.example.friendupp.Map.MapViewModel
import com.example.friendupp.R
import com.example.friendupp.di.ActiveUsersViewModel
import com.example.friendupp.di.ActivityViewModel
import com.example.friendupp.model.ActiveUser
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.UserData
import com.example.friendupp.ui.theme.Pacifico
import com.example.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

sealed class HomeEvents {
    object OpenDrawer : HomeEvents()
    object CreateLive : HomeEvents()
    class ExpandActivity(val activityData: Activity) : HomeEvents()
    class JoinActivity(val id: String) : HomeEvents()
    class LeaveActivity(val id: String) : HomeEvents()
    class OpenChat(val id: String) : HomeEvents()
    class GoToProfile(val id: String) : HomeEvents()
    class OpenLiveUser(val id: String) : HomeEvents()
}

val TAG = "HOMESCREENDEBUG"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onEvent: (HomeEvents) -> Unit,
    activityViewModel: ActivityViewModel,
    mapViewModel: MapViewModel,
    activeUserViewModel: ActiveUsersViewModel,
) {

    var calendarView by rememberSaveable {
        mutableStateOf(false)
    }
    var filterView by rememberSaveable {
        mutableStateOf(false)
    }
    val activities = remember { mutableStateListOf<Activity>() }
    val moreActivities = remember { mutableStateListOf<Activity>() }
    var activitiesExist = remember { mutableStateOf(false) }

    val locationFlow = mapViewModel.currentLocation.collectAsState()
    var currentLocation by rememberSaveable { mutableStateOf<LatLng?>(null) }
    locationFlow.value.let { latLng ->
        if (latLng != null) {
            currentLocation = latLng
        }
    }

    val publicActivities = remember { mutableStateListOf<Activity>() }
    val morePublicActivities = remember { mutableStateListOf<Activity>() }
    val activeUsers = remember { mutableStateListOf<ActiveUser>() }
    val moreActiveUsers = remember { mutableStateListOf<ActiveUser>() }
    var publicActivitiesExist = remember { mutableStateOf(false) }
    var activeUsersExist = remember { mutableStateOf(false) }

    // PUBLIC OR FRIENDS ACTIVITIEWS
    val selectedOption = rememberSelectedOptionState(
            Option.PUBLIC
    )
    var selectedTags = remember {
        mutableStateListOf<String>()
    }
    var datePicked = remember {
        mutableStateOf<String?>(null)
    }

    /**
    set the Date state and look for cahnges
     */
    val state = rememberHorizontalDatePickerState2()
    var year = state.selectedYear
    var month = state.selectedMonth
    var day = state.selectedDay
    var startDate = LocalDateTime.of(year, month, day, 0, 0)
    var startOfDay = startDate.with(LocalTime.MIN)

    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    var formattedDate = startOfDay.format(formatter)

    LaunchedEffect(state.selectedDay, state.selectedMonth, state.selectedYear) {
        Log.d(TAG, "changed date ")
        val startDate =
            LocalDateTime.of(state.selectedYear, state.selectedMonth, state.selectedDay, 0, 0)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = startOfDay.format(formatter)
        datePicked.value = formattedDate
    }
    loadActiveUsers(activeUserViewModel,activeUsers,moreActiveUsers,activeUsersExist)
    if (selectedOption.option == Option.FRIENDS) {
        loadFriendsActivities(activityViewModel, activities, activitiesExist = activitiesExist)
        loadMoreFriendsActivities(activityViewModel, moreActivities)
    } else {
        loadPublicActivities(
            activityViewModel,
            publicActivities,
            activitiesExist = publicActivitiesExist,
            currentLocation,
            selectedTags,
            date = datePicked.value
        )
        loadMorePublicActivities(activityViewModel, morePublicActivities)
    }
    val lazyListState = rememberLazyListState()

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableStateOf(15) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1000)
        /*
        todo call for acitivites
        */
        itemCount += 5
        refreshing = false
    }

    val pState = rememberPullRefreshState(refreshing, ::refresh)

    Column {
        TopBar(
            modifier = Modifier,
            onOptionSelected = { option -> selectedOption.option = option },
            selectedOption = selectedOption.option,
            openDrawer = { onEvent(HomeEvents.OpenDrawer) })
        Box(Modifier.pullRefresh(pState)) {
            Column() {
                LazyColumn(
                    modifier,
                    state = lazyListState
                ) {
                    item {
                        AnimatedVisibility(
                            visible = calendarView && selectedOption.option == Option.PUBLIC,
                            enter = slideInVertically(animationSpec = tween(800)),
                            exit = slideOutVertically(animationSpec = tween(0))
                        ) {
                            CalendarComponent(
                                state,
                                monthIncreased = { state.increaseMonth() },
                                monthDecreased = { state.decreaseMonth() },
                                yearIncreased = { state.increaseYear() },
                                yearDecreased = { state.decreaseYear() },
                                onDayClick = { state.setSelectedDay(it) },
                                onDayClick2= { state.setSelectedDay(it) })
                        }
                        AnimatedVisibility(
                            visible = filterView && selectedOption.option == Option.PUBLIC,
                            enter = slideInVertically(animationSpec = tween(800)),
                            exit = slideOutVertically(animationSpec = tween(0))
                        ) {
                            FilterList(tags = selectedTags, onSelected =
                            {

                                Log.d("HOMESCREEN", "addedd tags")
                                selectedTags.add(it)
                            }, onDeSelected = {
                                selectedTags.remove(it)
                            })

                        }
                        OptionPicker(
                            onEvent = onEvent,
                            onClick = {
                                calendarView = !calendarView
                                if (filterView) {
                                    filterView = !filterView
                                }

                            },
                            openFilter = {
                                filterView = !filterView
                                if (calendarView) {
                                    calendarView = !calendarView
                                }
                            },
                            calendarClicked = calendarView,
                            filterClicked = filterView,
                            displayFilters = selectedOption.option == Option.PUBLIC
                        , activeUsers = activeUsers,moreActiveUsers=moreActiveUsers)
                    }

                    if (selectedOption.option == Option.FRIENDS) {
                        items(activities) { activity ->
                            activityItem(
                                activity,
                                onClick = {
                                    // Handle click event
                                },
                                onEvent = { event ->
                                    handleActivityEvent(event,onEvent=onEvent)

                                }
                            )
                        }

                        items(moreActivities) { activity ->
                            activityItem(
                                activity,
                                onClick = {
                                    // Handle click event
                                },
                                onEvent = { event ->
                                    handleActivityEvent(event,onEvent=onEvent)
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
                    } else {
                        items(publicActivities) { activity ->
                            activityItem(
                                activity,
                                onClick = {
                                    // Handle click event
                                },
                                onEvent = { event ->
                                    handleActivityEvent(event,onEvent=onEvent)
                                }
                            )
                        }

                        items(morePublicActivities) { activity ->
                            activityItem(
                                activity,
                                onClick = {
                                    // Handle click event
                                },
                                onEvent = { event ->
                                    handleActivityEvent(event,onEvent=onEvent)
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(64.dp))

                        }
                        item {
                            LaunchedEffect(true) {
                                if (activitiesExist.value) {
                                    activityViewModel.location.value.let { location ->
                                        if (location != null) {
                                            activityViewModel.getMoreClosestActivities(
                                                location.latitude,
                                                location.longitude,
                                                50.0 * 1000.0
                                            )
                                        }

                                    }
                                }
                            }
                        }
                    }


                }
            }
            PullRefreshIndicator(
                refreshing,
                pState,
                Modifier.align(Alignment.TopCenter),
                backgroundColor = SocialTheme.colors.uiBackground,
                contentColor = SocialTheme.colors.textPrimary
            )
        }

    }


}

@Composable
 fun loadActiveUsers(activeUserViewModel: ActiveUsersViewModel, activeUsers: MutableList<ActiveUser>, moreActiveUsers: MutableList<ActiveUser>,activeUsersExist:MutableState<Boolean>) {
    //call active userse only once
    val activitiesFetched = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = activitiesFetched.value) {
        if (!activitiesFetched.value) {
            activeUserViewModel.getActiveUsersForUser(UserData.user!!.id)
            activitiesFetched.value = true
        }
    }
    activeUserViewModel.activeUsersListState.value.let { response ->
        when (response) {
            is Response.Success -> {
                activeUsers.clear()
                activeUsers.addAll(response.data)
                activeUsersExist.value = true

            }
            is Response.Failure -> {

            }
            is Response.Loading -> {
            }
            null -> {

            }
        }
    }
    activeUserViewModel.moreActiveUsers.value.let { response ->
        when (response) {
            is Response.Success -> {
                moreActiveUsers.clear()
                moreActiveUsers.addAll(response.data)

            }
            is Response.Failure -> {

            }
            is Response.Loading -> {
            }
            null -> {

            }
        }
    }
}


@Composable
fun TopBar(
    modifier: Modifier,
    selectedOption: Option, onOptionSelected: (Option) -> Unit,
    openDrawer: () -> Unit,
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(SocialTheme.colors.uiBackground)
            .padding(vertical = 12.dp, horizontal = 24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ActionButtonDefault(
                icon = R.drawable.ic_menu_300,
                isSelected = false,
                onClick = openDrawer
            )
            /*   SocialButtonNormal(icon = R.drawable.ic_menu_300, onClick = openDrawer)*/
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
            ActionButton(option = Option.FRIENDS,
                isSelected = selectedOption == Option.FRIENDS,
                onClick = { onOptionSelected(Option.FRIENDS) })
            Spacer(
                modifier = Modifier
                    .width(8.dp)
                    .height(1.dp)
                    .background(SocialTheme.colors.uiBorder)
            )
            ActionButton(option = Option.PUBLIC,
                isSelected = selectedOption == Option.PUBLIC,
                onClick = { onOptionSelected(Option.PUBLIC) })


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


@Composable
fun buttonsRow(
    modifier: Modifier,
    onEvent: (ActivityEvents) -> Unit,
    id: String,
    joined: Boolean = false,
    joinChanged: (Boolean) -> Unit,
    profilePictures: HashMap<String, String>,
) {
    var bookmarked by remember { mutableStateOf(false) }
    val bookmarkColor: Color by animateColorAsState(
        if (bookmarked) Color(0xFF00CCDF) else SocialTheme.colors.iconPrimary,
        animationSpec = tween(1000, easing = LinearEasing)
    )
    val alpha: Float by animateFloatAsState(
        if (joined) 1f else 0f,
        animationSpec = tween(1000, easing = LinearEasing)
    )

    val bgColor: Color = SocialTheme.colors.uiBorder

    val iconColor: Color by animateColorAsState(
        if (joined) SocialTheme.colors.textInteractive else SocialTheme.colors.iconPrimary,
        animationSpec = tween(1000, easing = LinearEasing)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SocialTheme.colors.uiBackground)
    ) {
        Row(
            modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .width(32.dp)
                    .height(

                        0.5.dp
                    )
                    .background(color = bgColor)
            )
            eButtonSimple(icon = R.drawable.ic_check_300, onClick = {
                if (joined) {
                    onEvent(ActivityEvents.Leave(id))
                    joinChanged(false)

                } else {
                    onEvent(ActivityEvents.Join(id))
                    joinChanged(true)

                }

            }, iconColor = iconColor, selected = joined, iconFilled = R.drawable.ic_check_filled)
            Spacer(
                modifier = Modifier
                    .width(12.dp)
                    .height(

                        0.5.dp
                    )
                    .background(color = bgColor)
            )
            eButtonSimple(
                icon = R.drawable.ic_chat_300,
                onClick = { onEvent(ActivityEvents.OpenChat(id)) })
            Spacer(
                modifier = Modifier
                    .width(12.dp)
                    .height(

                        0.5.dp
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
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .height(

                        0.5.dp
                    )
                    .background(color = bgColor)
            )
            Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                profilePictures.values.toList().take(4).reversed().forEachIndexed { index, it ->
                    AsyncImage(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .zIndex(profilePictures.values.toList().size - index.toFloat()),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_profile_300),
                        contentDescription = "participant picture",
                        contentScale = ContentScale.Crop,

                        )
                }
            }


            Spacer(
                modifier = Modifier
                    .width(24.dp)
                    .height(

                        0.5.dp
                    )
                    .background(color = bgColor)
            )

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
        SocialTheme.colors.textInteractive
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

private fun handleActivityEvent(event: ActivityEvents,    onEvent: (HomeEvents) -> Unit) {
    when (event) {
        is ActivityEvents.Expand -> {
            onEvent(HomeEvents.ExpandActivity(event.activity))
        }
        is ActivityEvents.Join -> {
            onEvent(HomeEvents.JoinActivity(event.id))
        }
        is ActivityEvents.Leave -> {
            onEvent(HomeEvents.LeaveActivity(event.id))
        }
        is ActivityEvents.OpenChat -> {
            onEvent(HomeEvents.OpenChat(event.id))
        }
        is ActivityEvents.GoToProfile->{
            onEvent(HomeEvents.GoToProfile(event.id))
        }
    }
}