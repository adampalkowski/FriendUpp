package com.palkowski.friendupp.Create

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.palkowski.friendupp.bottomBar.ActivityUi.ActivityState
import com.palkowski.friendupp.ChatUi.ButtonAdd
import com.palkowski.friendupp.Components.*
import com.palkowski.friendupp.Components.Calendar.HorizontalDateState2
import com.palkowski.friendupp.Components.TimePicker.TimeState
import com.palkowski.friendupp.Login.TextFieldState
import com.palkowski.friendupp.R
import com.palkowski.friendupp.model.Activity
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


sealed class CreateEvents {
    class Create(
        val title: String,
        val description: String,
        val public: Boolean,
        val startTime: String,
        val endTime: String,
    ) : CreateEvents()

    object OpenCamera : CreateEvents()
    object LocationPicker : CreateEvents()
    class Settings(
        val title: String,
        val description: String,
        val public: Boolean,
        val startTime: String,
        val endTime: String,
    ) : CreateEvents()

    object GoBack : CreateEvents()
}

data class Date(
    val year: String,
    val month: String,
    val day: String,
    val hour: String,
    val minute: String,
)

fun parseDateTime(dateTimeString: String): Date {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val dateTime = LocalDateTime.parse(dateTimeString, formatter)

    val year = dateTime.year.toString()
    val month = dateTime.monthValue.toString()
    val day = dateTime.dayOfMonth.toString()
    val hour = dateTime.hour.toString()
    val minute = dateTime.minute.toString()

    return Date(year, month, day, hour, minute)
}

@Composable
fun CreateScreen(modifier: Modifier, onEvent: (CreateEvents) -> Unit = {},
                 activity: Activity
                 ,activityState: ActivityState
) {
    val titleState = activityState.titleState
    val descriptionState = activityState.descriptionState
    val selectedOption = activityState.selectedOptionState
    val timeStartState = activityState.timeStartState
    val timeEndState = activityState.timeEndState
    val startDateState = activityState.startDateState
    val endDateState = activityState.endDateState
    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }
    var progressBlocked by rememberSaveable {
        mutableStateOf(false)
    }


    BackHandler(true) {
        onEvent(CreateEvents.GoBack)
    }


    var startTime = connectTimeAndDate(
        year = startDateState!!.selectedYear,
        month = startDateState.selectedMonth,
        day = startDateState.selectedDay,
        hour = timeStartState!!.hours,
        minute = timeStartState.minutes
    )
    var endTime = connectTimeAndDate(
        year = endDateState!!.selectedYear,
        month = endDateState.selectedMonth,
        day = endDateState.selectedDay,
        hour = timeEndState!!.hours,
        minute = timeEndState.minutes
    )

    /* CHECK IF START TIME IS AFTER NOW AND IF END TIME IS AFTER START TIME*/

    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    var startTimeCheck = LocalDateTime.parse(startTime, formatter)
    var endTimeCheck = LocalDateTime.parse(endTime, formatter)
    var now = LocalDateTime.now()

    var isEndTimeAfterStartTime = endTimeCheck.isAfter(startTimeCheck)
    if (!isEndTimeAfterStartTime) {
        errorMessage = "Invalid time: End time of activity is before the start time"
    }

    var isStartTimeInFuture = startTimeCheck.isAfter(now)
    if (!isStartTimeInFuture) {
        errorMessage = "Invalid time: Start time should take place in the future"
    }
    LaunchedEffect(selectedOption.option){
        if (selectedOption.option==Option.PUBLIC){
            if (!isEndTimeAfterStartTime || !isStartTimeInFuture|| !titleState!!.isValid||activityState.location==LatLng(0.0,0.0) ) {
                Log.d("DATEDEBUG", " progres blocked")
                Log.d("DATEDEBUG", " public")
                progressBlocked = true
            } else {
                progressBlocked = false
            }
            if (activityState.location==LatLng(0.0,0.0) ){
                errorMessage = "Pick location to create a public activity."
            }
        }else{
            Log.d("DATEDEBUG", " friends")
            if (!isEndTimeAfterStartTime || !isStartTimeInFuture|| !titleState.isValid) {
                progressBlocked = true
            } else {
                progressBlocked = false
            }

        }
    }
    LaunchedEffect(titleState.isValid ){
        if(!titleState.isValid){
            errorMessage="Please input the title."
        }
    }


    Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())

            ) {
                CreateTopBar(
                    onClick = {
                        onEvent(CreateEvents.GoBack)
                    },
                    selectedOption = selectedOption!!.option,
                    onPublic = {
                        selectedOption.option=Option.PUBLIC
                     },
                    onFriends = {          selectedOption!!.option=Option.FRIENDS
                    })
                TitleAndDescription(titleState!!, descriptionState!!)
                Spacer(modifier = Modifier.height(8.dp))

                DateAndTime(
                    startTimeState = timeStartState, endTimeState = timeEndState,
                    startDateState = startDateState,
                    endDateState = endDateState
                )
                if (progressBlocked) {
                    TextFieldError(textError = errorMessage)
                }

            Spacer(modifier = Modifier.weight(1f))

            BottomBarCreate(
                photo = activityState.imageUrl,
                onClick = {
                    onEvent(
                        CreateEvents.Settings(
                            description = descriptionState!!.text,
                            title = titleState!!.text,
                            startTime = startTime,
                            endTime = endTime,
                            public = selectedOption!!.option == Option.PUBLIC
                        ) ) },
                createClicked = {
                    Log.d("CREATESCREENEVENTS", "CREATE")
                    onEvent(
                        CreateEvents.Create(
                            description = descriptionState!!.text,
                            title = titleState!!.text,
                            startTime = startTime,
                            endTime = endTime,
                            public = selectedOption!!.option == Option.PUBLIC
                        )
                    )
                }, locationPicker = {onEvent(CreateEvents.LocationPicker)},
                openCamera = { onEvent(CreateEvents.OpenCamera) }, disabled = progressBlocked
            )

        }
    }

}

@Composable
fun TitleAndDescription(titleState: TextFieldState, descriptionState: TextFieldState) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading("Title & description", icon = com.palkowski.friendupp.R.drawable.ic_edit)
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Title", textState = titleState
        )
        Spacer(modifier = Modifier.height(12.dp))
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Description", textState = descriptionState
        )

    }
}

fun isToday(year: Int, month: Int, day: Int): Boolean {
    val currentDate = LocalDate.now()
    val inputDate = LocalDate.of(year, month, day)
    return currentDate == inputDate
}

@Composable
fun DateAndTime(
    startTimeState: TimeState, endTimeState: TimeState,
    lockStartTime: Boolean = false,
    focusRequester: FocusRequester = FocusRequester(),
    startDateState: HorizontalDateState2,
    endDateState: HorizontalDateState2,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading("Date & time", icon = com.palkowski.friendupp.R.drawable.ic_date)
        StartTimePicker(Modifier.fillMaxWidth(), startDateState, startTimeState)
        StartTimePicker(
            Modifier.fillMaxWidth(),
            endDateState,
            endTimeState,
            label = "End",
            endTime = true
        )

    }

}


@Composable
fun BottomBarCreate(
    onClick: () -> Unit,
    createClicked: () -> Unit = {},
    openCamera: () -> Unit = {},
    locationPicker: () -> Unit = {},
    photo: String,
    disabled: Boolean,
) {
    Row(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
        if (photo.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .size(48.dp)
                    .clickable(onClick = openCamera)
            )

        } else {
            ButtonAdd(onClick = openCamera, icon = com.palkowski.friendupp.R.drawable.ic_add_image)

        }
        Spacer(modifier = Modifier.width(12.dp))
        ButtonAdd(onClick = onClick, icon = com.palkowski.friendupp.R.drawable.ic_filte_300)
        Spacer(modifier = Modifier.width(12.dp))
        ButtonAdd(onClick = locationPicker, icon = com.palkowski.friendupp.R.drawable.ic_add_location)
        Spacer(modifier = Modifier.weight(1f))

        BlueButton(onClick = createClicked, icon = R.drawable.ic_long_right, disabled = disabled)

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateButton(
    text: String, createClicked: () -> Unit = {},
    modifier: Modifier = Modifier.width(150.dp), disabled: Boolean,
) {
    val cardColor by animateColorAsState(
        if (disabled) {
            SocialTheme.colors.uiBorder
        } else {
            SocialTheme.colors.textInteractive
        }

    )

    Card(
        modifier = modifier,
        onClick = {
            if (!disabled) {
                createClicked()
            }
        },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor, contentColor = cardColor)
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 48.dp),
            text = text,
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.White
            ), textAlign = TextAlign.Center // Center aligns the text

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoBackButton(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            1.dp,
            SocialTheme.colors.uiBorder
        )
    ) {
        Box(
            modifier = Modifier.background(SocialTheme.colors.uiBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 48.dp),
                text = text,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = SocialTheme.colors.textPrimary.copy(0.8f)
                )
            )
        }
    }
}


@Composable
fun BottomBarSettings(onClick: () -> Unit) {
    Row(
        Modifier
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.End
    ) {
        BlueButton(onClick = onClick, icon = com.palkowski.friendupp.R.drawable.ic_checkl)
    }
}


@Composable
fun SettingsTopBar(onClick: () -> Unit) {
    val dividerColor = SocialTheme.colors.uiBorder
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
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

        Text(
            modifier = Modifier.padding(bottom = 6.dp),
            text = "Additional settings",
            style = TextStyle(
                fontFamily = Lexend,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = SocialTheme.colors.textPrimary.copy(0.8f)
            )
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(dividerColor)
        )


    }
}

@Composable
fun CreateTopBar(
    onClick: () -> Unit,
    selectedOption: Option,
    onFriends: () -> Unit,
    onPublic: () -> Unit,
) {
    val context = LocalContext.current

    val dividerColor = SocialTheme.colors.uiBorder
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
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
        ButtonAdd(onClick = onClick, icon = com.palkowski.friendupp.R.drawable.ic_x)
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        Text(
            modifier = Modifier.padding(bottom = 6.dp),
            text = "Create",
            style = TextStyle(
                fontFamily = Lexend,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = SocialTheme.colors.textPrimary.copy(0.8f)
            )
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(
            option = Option.FRIENDS,
            isSelected = selectedOption == Option.FRIENDS,
            onClick = onFriends
        )
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(
            option = Option.PUBLIC,
            isSelected = selectedOption == Option.PUBLIC,
            onClick = onPublic
        )
        Spacer(
            modifier = Modifier
                .width(48.dp)
                .height(1.dp)
                .background(dividerColor)
        )

    }

}


