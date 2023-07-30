package com.example.friendupp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.friendupp.Components.Calendar.rememberHorizontalDatePickerState2
import com.example.friendupp.Components.CalendarComponent
import com.example.friendupp.Components.FilterList
import com.example.friendupp.Create.Option
import com.example.friendupp.Create.rememberSelectedOptionState
import com.example.friendupp.Home.SocialButtonNormal
import com.example.friendupp.Home.SocialButtonNormalMedium
import com.example.friendupp.Home.TAG
import com.example.friendupp.Map.MapActivityItem
import com.example.friendupp.Settings.getSavedRangeValue
import com.example.friendupp.bottomBar.ActivityUi.ActivityEvents
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


sealed class MapEvent {
    class PreviewActivity(val activity: Activity) : MapEvent()
    class GoToProfile(val id: String) : MapEvent()
    object GetMorePublicActivities : MapEvent()
    object GetPublicActivities : MapEvent()
    class GetClosestFilteredActivities(val tags:ArrayList<String>) : MapEvent()
    class GetMorePublicActivitiesWithTags(val tags: java.util.ArrayList<String>) : MapEvent()
    class GetPublicActivitiesWithDate(val date: String) : MapEvent()
    class GetMorePublicActivitiesWithDate(val date: String) : MapEvent()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreen(

    onEvent: (MapEvent) -> Unit,
    activityEvents: (ActivityEvents) -> Unit,
    currentLocation:LatLng,
    publicActivitiesResponse:Response<List<Activity>>
) {
    var publicActivitiesExist = remember { mutableStateOf(false) }

    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }


    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 14f)
    }
    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true,
                indoorLevelPickerEnabled = true
            )
        )
    }


    var isMapLoaded by remember { mutableStateOf(false) }
    var markerOptions by remember { mutableStateOf<MarkerOptions?>(null) }

    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val activityToScroll = remember { mutableStateOf<Activity?>(null) }
    var calendarView by rememberSaveable {
        mutableStateOf(false)
    }
    var filterView by rememberSaveable {
        mutableStateOf(false)
    }
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

    if(calendarView){
        LaunchedEffect(state.selectedDay, state.selectedMonth, state.selectedYear) {
            Log.d(TAG, "changed date ")
            val startDate =
                LocalDateTime.of(state.selectedYear, state.selectedMonth, state.selectedDay, 0, 0)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = startOfDay.format(formatter)
            datePicked.value = formattedDate
        }
    }else{
        datePicked.value=null
    }

    Box(modifier = Modifier.consumeWindowInsets(WindowInsets.ime)) {




        var hideActivities by rememberSaveable {
            mutableStateOf(true)
        }

        val scaffoldState = rememberBottomSheetScaffoldState()

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 140.dp,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetBackgroundColor = Color.Transparent,
            sheetContentColor =Color.Transparent,
            backgroundColor = Color.Transparent,
            sheetElevation = 0.dp,
            sheetContent = {
                Column(Modifier.background(Color.Transparent)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {

                        Card(elevation = 5.dp) {
                            Box(
                                Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SocialTheme.colors.uiBackground)
                                    .clickable(onClick = {
                                        cameraPositionState.position =
                                            CameraPosition.fromLatLngZoom(currentLocation, 14f)
                                    }), contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_my_location),
                                    contentDescription = null,
                                    tint = SocialTheme.colors.textInteractive
                                )
                            }
                        }


                        Spacer(modifier = Modifier.width(12.dp))

                        Card(elevation = 5.dp) {
                            Box(
                                Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SocialTheme.colors.uiBackground)
                                    .clickable(onClick = {
                                        cameraPositionState.position =
                                            CameraPosition.fromLatLngZoom(currentLocation, 11f)
                                    }), contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_refresh_map),
                                    contentDescription = null,
                                    tint =  SocialTheme.colors.textInteractive

                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))
                        SocialButtonNormalMedium(
                            icon = R.drawable.ic_filte_300,
                            onClick = {
                                filterView = !filterView
                                if (calendarView) {
                                    calendarView = !calendarView
                                }
                            },
                            filterView
                        )
                        Spacer(modifier = Modifier
                            .width(12.dp)
                            .height(1.dp) )
                        SocialButtonNormalMedium(
                            icon = R.drawable.ic_calendar_300,
                            onClick = {
                                calendarView = !calendarView
                                if (filterView) {
                                    filterView = !filterView
                                }
                            },
                            calendarView
                        )


/*
            Spacer(modifier = Modifier.weight(1f))
            Card(elevation = 5.dp) {
                SocialButtonNormal(
                    icon = R.drawable.ic_filte_300,
                    onClick = { },
                    false
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Card(elevation = 5.dp) {
                SocialButtonNormal(
                    icon = R.drawable.ic_calendar_300,
                    onClick = { },
                    false
                )


            }
*/
                    }

                    Column(modifier=Modifier.background(SocialTheme.colors.uiBackground),horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedVisibility(
                            visible = calendarView && selectedOption.option == Option.PUBLIC,
                            enter = expandVertically (animationSpec = tween(400)),
                            exit = shrinkVertically(animationSpec = tween(400))
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
                            enter = expandVertically (animationSpec = tween(400)),
                            exit = shrinkVertically(animationSpec = tween(400))
                        ) {
                            FilterList(tags = selectedTags, onSelected =
                            {

                                Log.d("HOMESCREEN", "addedd tags")
                                selectedTags.add(it)
                            }, onDeSelected = {
                                selectedTags.remove(it)
                            })

                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(SocialTheme.colors.uiBorder)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        when (publicActivitiesResponse) {
                            is Response.Success -> {
                                MapActivitiesDisplay(
                                    modifier = Modifier,
                                    publicActivities = publicActivitiesResponse.data,
                                    CenterOnPoint = { latLng ->
                                        cameraPositionState.position =
                                            CameraPosition.fromLatLngZoom(latLng, 15f)

                                    },
                                    activityToScroll = activityToScroll,
                                    onEvent = onEvent,
                                    hideActivities = hideActivities,
                                    GetMoreActivities = {
                                        if(selectedTags.toList().isNotEmpty()){
                                            val tags: ArrayList<String> = arrayListOf()
                                            tags.addAll(selectedTags)
                                            onEvent(MapEvent.GetMorePublicActivitiesWithTags(tags))
                                        }else if(datePicked.value!=null){
                                            onEvent(MapEvent.GetMorePublicActivitiesWithDate(datePicked.value.toString()))
                                        }else{
                                            onEvent(MapEvent.GetMorePublicActivities)
                                        }
                                       },
                                    activityEvents=activityEvents)
                            }
                            is Response.Loading -> {
                                androidx.compose.material3.CircularProgressIndicator(color = SocialTheme.colors.textPrimary)
                            }
                            else -> {}
                        }

                    }

                }


            }) { innerPadding ->
            GoogleMap(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp), cameraPositionState,
                properties = properties, onMapLoaded = {
                    isMapLoaded = true
                }, onMapLongClick = { latlng ->
                    selectedLocation = latlng
                }, onMapClick = {
                },
                uiSettings = uiSettings
            ) {

                val bitmap = getBitmapDescriptor(context, R.drawable.ic_puck)
                selectedLocation.let { selectedLatLng ->
                    if (selectedLatLng != null) {

                        MarkerInfoWindow(
                            state = MarkerState(
                                position = selectedLatLng
                            )
                        ) {

                        }
                    }
                }
                when(publicActivitiesResponse){
                    is Response.Success->{
                        publicActivitiesResponse.data.forEach { activity ->
                            if (activity.lat != null) {
                                val latLng = LatLng(activity.lat!!, activity.lng!!)
                                MarkerInfoWindow(
                                    alpha = 0.8f,
                                    state = MarkerState(
                                        position = latLng,
                                    )
                                ) {
                                    activityToScroll.value = activity
                                }
                            }

                        }
                    }
                    else->{}
                }


                currentLocation.let { latLng ->
                    MarkerInfoWindow(
                        alpha = 0.8f,
                        state = MarkerState(
                            position = latLng
                        ), icon = bitmap
                    ) {

                    }
                }

            }

        }






    }
    /*on inital compose this may be called 3 times*/
    LaunchedEffect(selectedTags.toList()) {
        if (selectedTags.isNotEmpty()) {
            val tags: ArrayList<String> = arrayListOf()
            tags.addAll(selectedTags)
            if (currentLocation != null) {
                Log.d(TAG,"CALLED FOR CLOSEST WITH TAGS")
                onEvent(MapEvent.GetClosestFilteredActivities(tags))

            }
        } else {
            if (currentLocation != null) {
                onEvent(MapEvent.GetPublicActivities)
            }
        }
    }
    LaunchedEffect(datePicked.value) {
        if(datePicked.value!=null){
            if(currentLocation!=null){
               onEvent(MapEvent.GetPublicActivitiesWithDate(datePicked.value.toString()))
            }

        }else{
            onEvent(MapEvent.GetPublicActivities)

        }
    }

}

@Composable
fun MapActivitiesDisplay(
    activityToScroll: MutableState<Activity?>,
    modifier: Modifier, publicActivities: List<Activity>,
    CenterOnPoint: (LatLng) -> Unit, onEvent: (MapEvent) -> Unit, activityEvents: (ActivityEvents) -> Unit, GetMoreActivities: () -> Unit, hideActivities:Boolean
) {
    val lazyListState = rememberLazyListState()
    val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
    val totalItems = publicActivities.size
    Column(horizontalAlignment = Alignment.Start,                modifier = modifier.fillMaxWidth(),
    ) {
        if(!hideActivities){
            Spacer(modifier = Modifier.height(24.dp))
        }
        AnimatedVisibility(visible = hideActivities) {
            LazyRow(
                state = lazyListState
            ) {
                items(publicActivities) { activity ->
                        MapActivityItem(onClick = {
                            val latLng = LatLng(activity.lat!!, activity.lng!!)

                            CenterOnPoint(latLng)
                        }, activity = activity, onEvent = onEvent,activityEvents=activityEvents)
                        Spacer(modifier = Modifier.width(16.dp))

                }
                item{
                    Spacer(modifier = Modifier.width(80.dp))
                }
                item{
                }
            }
        }
    Spacer(modifier = Modifier.height(12.dp))
    }
    // Trigger scrolling when the activityToScroll value changes
    LaunchedEffect(activityToScroll.value) {
        val index = publicActivities.indexOf(activityToScroll.value)
        if (index != -1) {
            lazyListState.animateScrollToItem(index)
        }
    }
    LaunchedEffect(lazyListState.firstVisibleItemIndex, lazyListState.layoutInfo.totalItemsCount) {
        val endReached = visibleItems.isNotEmpty() && visibleItems.last().index == totalItems - 1
        if (endReached) {
            GetMoreActivities()
        }
    }

}


fun getBitmapDescriptor(context: Context, id: Int): BitmapDescriptor? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val vectorDrawable = getDrawable(context, id) as VectorDrawable
        val h = vectorDrawable.intrinsicHeight
        val w = vectorDrawable.intrinsicWidth
        vectorDrawable.setBounds(0, 0, w, h)
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        vectorDrawable.draw(canvas)
        BitmapDescriptorFactory.fromBitmap(bm)
    } else {
        BitmapDescriptorFactory.fromResource(id)
    }
}

fun loadIcon(
    context: Context,
    url: String?,
    placeHolder: Int,
): BitmapDescriptor? {
    try {
        var bitmap: Bitmap? = null
        Glide.with(context)
            .asBitmap()
            .load(url)
            .error(placeHolder)
            // to show a default icon in case of any errors
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?,
                ) {

                    bitmap = resource

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
        return BitmapDescriptorFactory.fromBitmap(bitmap!!)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreenTablet(
    currentLocation:LatLng,
    onEvent: (MapEvent) -> Unit,       activityEvents: (ActivityEvents) -> Unit,    publicActivitiesResponse:Response<List<Activity>>
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 11f)
    }
    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true,
                indoorLevelPickerEnabled = true
            )
        )
    }


    var isMapLoaded by remember { mutableStateOf(false) }
    var markerOptions by remember { mutableStateOf<MarkerOptions?>(null) }

    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val activityToScroll = remember { mutableStateOf<Activity?>(null) }

    Box(modifier = Modifier.consumeWindowInsets(WindowInsets.ime)) {


        var hideActivities by rememberSaveable {
            mutableStateOf(true)
        }

        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val icon = if (hideActivities) {
            painterResource(id = R.drawable.ic_down)
        } else {
            painterResource(id = R.drawable.ic_up)
        }
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 40.dp,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetBackgroundColor = SocialTheme.colors.uiBackground,
            sheetContentColor = SocialTheme.colors.uiBackground,
            backgroundColor = SocialTheme.colors.uiBackground,
            sheetContent = {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(SocialTheme.colors.uiBorder)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when (publicActivitiesResponse) {
                        is Response.Success -> {
                            MapActivitiesDisplay(
                                modifier = Modifier,
                                publicActivities = publicActivitiesResponse.data,
                                CenterOnPoint = { latLng ->
                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(latLng, 13f)

                                },
                                activityToScroll = activityToScroll,
                                onEvent = onEvent,
                                hideActivities = hideActivities,
                                GetMoreActivities = { onEvent(MapEvent.GetMorePublicActivities) },
                            activityEvents = activityEvents)
                        }
                        is Response.Loading -> {
                            androidx.compose.material3.CircularProgressIndicator(color = SocialTheme.colors.textPrimary)
                        }
                        else -> {}
                    }

                }


            }) { innerPadding ->

            Column() {


                GoogleMap(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f), cameraPositionState,
                    properties = properties, onMapLoaded = {
                        isMapLoaded = true
                    }, onMapLongClick = { latlng ->
                        selectedLocation = latlng
                    }, onMapClick = {
                    },
                    uiSettings = uiSettings
                ) {

                    val bitmap = getBitmapDescriptor(context, R.drawable.ic_puck)
                    selectedLocation.let { selectedLatLng ->
                        if (selectedLatLng != null) {

                            MarkerInfoWindow(
                                state = MarkerState(
                                    position = selectedLatLng
                                )
                            ) {

                            }
                        }
                    }
                    when (publicActivitiesResponse) {
                        is Response.Success -> {
                            publicActivitiesResponse.data.forEach { activity ->
                                if (activity.lat != null) {
                                    val latLng = LatLng(activity.lat!!, activity.lng!!)
                                    MarkerInfoWindow(
                                        alpha = 0.8f,
                                        state = MarkerState(
                                            position = latLng,
                                        )
                                    ) {
                                        activityToScroll.value = activity
                                    }
                                }

                            }
                        }
                        else -> {}
                    }


                    currentLocation.let { latLng ->
                        MarkerInfoWindow(
                            alpha = 0.8f,
                            state = MarkerState(
                                position = latLng
                            ), icon = bitmap
                        ) {

                        }
                    }

                }


            Spacer(modifier = Modifier.height(200.dp))
            }
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(24.dp)
                    .padding(top = 24.dp)
            ) {

                Card(elevation = 5.dp) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SocialTheme.colors.uiBackground)
                            .clickable(onClick = {
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(currentLocation, 11f)
                            }), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_my_location),
                            contentDescription = null,
                            tint = SocialTheme.colors.textInteractive
                        )
                    }
                }


                Spacer(modifier = Modifier.height(12.dp))

                Card(elevation = 5.dp) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SocialTheme.colors.uiBackground)
                            .clickable(onClick = {
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(currentLocation, 11f)
                            }), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_refresh_map),
                            contentDescription = null,
                            tint = SocialTheme.colors.textInteractive

                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                ) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SocialTheme.colors.textInteractive)
                            .clickable(onClick = {
                                hideActivities = !hideActivities
                            }), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
        }


/*
            Spacer(modifier = Modifier.weight(1f))
            Card(elevation = 5.dp) {
                SocialButtonNormal(
                    icon = R.drawable.ic_filte_300,
                    onClick = { },
                    false
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Card(elevation = 5.dp) {
                SocialButtonNormal(
                    icon = R.drawable.ic_calendar_300,
                    onClick = { },
                    false
                )


            }
*/
        }


    }

}
