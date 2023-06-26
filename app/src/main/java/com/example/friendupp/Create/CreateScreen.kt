package com.example.friendupp.Create

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Components.*
import com.example.friendupp.Components.Calendar.HorizontalDateState2
import com.example.friendupp.Components.Calendar.rememberHorizontalDatePickerState2
import com.example.friendupp.Components.TimePicker.TimeState
import com.example.friendupp.Components.TimePicker.WheelPickerDefaults
import com.example.friendupp.Components.TimePicker.WheelTimePicker
import com.example.friendupp.Components.TimePicker.rememberTimeState
import com.example.friendupp.Home.Option
import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.R
import com.example.friendupp.model.Activity
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import java.time.LocalDate
import java.time.LocalTime
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
    class Settings(
        val title: String,
        val description: String,
        val public: Boolean,
        val startTime: String,
        val endTime: String,
    ) : CreateEvents()

    object GoBack : CreateEvents()
}

@Composable
fun CreateScreen(modifier: Modifier, onEvent: (CreateEvents) -> Unit = {}, activity: Activity) {

    val titleState by rememberSaveable(stateSaver = TitleStateSaver) {
        mutableStateOf(TitleState())
    }
    titleState.text = activity.title
    val descriptionState by rememberSaveable(stateSaver = DescriptionStateSaver) {
        mutableStateOf(DescriptionState())
    }
    descriptionState.text = activity.description
    var selectedOption by rememberSaveable {
        mutableStateOf(
            if (activity.public) {
                Option.PUBLIC
            } else {
                Option.FRIENDS
            }
        )
    }
    BackHandler(true) {
        onEvent(CreateEvents.GoBack)
    }

    val dateState = rememberHorizontalDatePickerState2()
    val timeStartState = rememberTimeState(initialHours =LocalTime.now().hour, initialMinutes = LocalTime.now().minute)
    val timeEndState = rememberTimeState(initialHours =LocalTime.now().hour.plus(1), initialMinutes = LocalTime.now().minute)


    val startTime = connectTimeAndDate(
        year = dateState.selectedYear,
        month = dateState.selectedMonth,
        day = dateState.selectedDay,
        hour = timeStartState.hours,
        minute = timeStartState.minutes
    )
    val endTime = connectTimeAndDate(
        year = dateState.selectedYear,
        month = dateState.selectedMonth,
        day = dateState.selectedDay,
        hour = timeEndState.hours,
        minute = timeEndState.minutes
    )

    var progressBlocked by rememberSaveable {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column() {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
            ) {

                CreateTopBar(
                    onClick = {
                        onEvent(CreateEvents.GoBack)
                    },
                    selectedOption = selectedOption,
                    onPublic = { selectedOption = Option.PUBLIC },
                    onFriends = { selectedOption = Option.FRIENDS })
                TitleAndDescription(titleState, descriptionState)
                Spacer(modifier = Modifier.height(8.dp))

                DateAndTime(startTimeState=timeStartState,endTimeState=timeEndState,
                    dateState = dateState,
                    onProgressBlocked = {
                    })

            }


            BottomBarCreate(photo = activity.image,
                onClick = {
                    onEvent(
                        CreateEvents.Settings(
                            description = descriptionState.text,
                            title = titleState.text,
                            startTime = startTime.toString(),
                            endTime = endTime,
                            public = selectedOption == Option.PUBLIC
                        )
                    )
                },
                createClicked = {
                    Log.d("CREATESCREENEVENTS", "CREATE")
                    onEvent(
                        CreateEvents.Create(
                            description = descriptionState.text,
                            title = titleState.text,
                            startTime = startTime,
                            endTime = endTime,
                            public = selectedOption == Option.PUBLIC
                        )
                    )
                },
                openCamera = { onEvent(CreateEvents.OpenCamera) }, disabled = progressBlocked
            )
        }
    }

}

@Composable
fun TitleAndDescription(titleState: TextFieldState, descriptionState: TextFieldState) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading("Title & description", icon = com.example.friendupp.R.drawable.ic_edit)
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
fun DateAndTime(startTimeState: TimeState,endTimeState: TimeState,
    lockStartTime: Boolean = false,
    focusRequester: FocusRequester = FocusRequester(),
    onProgressBlocked: (Boolean) -> Unit,
    dateState: HorizontalDateState2,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading("Date", icon = com.example.friendupp.R.drawable.ic_date)
        CalendarComponent(dateState    ,
            monthIncreased = { dateState.increaseMonth() },
            monthDecreased = { dateState.decreaseMonth() },
            yearIncreased = { dateState.increaseYear() },
            yearDecreased = { dateState.decreaseYear() },
            onDayClick = { dateState.setSelectedDay(it) })
        TimeComponent(Modifier.fillMaxWidth(),dateState,startTimeState,endTimeState)

    }

}



@Composable
fun BottomBarCreate(
    onClick: () -> Unit,
    createClicked: () -> Unit = {},
    openCamera: () -> Unit = {},
    photo: String?,
    disabled: Boolean,
) {
    Row(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
        Log.d("CreateGraphActivity", photo.toString())
        if (photo != null) {
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
            Log.d("CreateGraphActivity", "add button")
            ButtonAdd(onClick = openCamera, icon = com.example.friendupp.R.drawable.ic_add_image)

        }
        Spacer(modifier = Modifier.width(16.dp))
        ButtonAdd(onClick = onClick, icon = com.example.friendupp.R.drawable.ic_filte_300)
        Spacer(modifier = Modifier.weight(1f))
        CreateButton("Create", createClicked = createClicked, disabled = disabled)
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

        ButtonAdd(onClick = onClick, icon = com.example.friendupp.R.drawable.ic_checkl)
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
        ButtonAdd(onClick = onClick, icon = com.example.friendupp.R.drawable.ic_x)
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


