package com.example.friendupp.Create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Components.Calendar.HorizontalDateState2
import com.example.friendupp.Components.TimePicker.TimeState
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import com.marosseleng.compose.material3.datetimepickers.time.domain.TimePickerDefaults
import com.marosseleng.compose.material3.datetimepickers.time.domain.TimePickerTypography
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeComponent(
                  modifier: Modifier,
                  dateState2: HorizontalDateState2,
                  startTimeState: TimeState, endTimeState: TimeState,
) {
    var showTimePickerStart by remember { mutableStateOf(false) }
    var showTimePickerEnd by remember { mutableStateOf(false) }
    var showTimePicker = remember {
        mutableStateOf(false)
    }
    val selectedDate =
        LocalDate.of(dateState2.selectedYear, dateState2.selectedMonth, dateState2.selectedDay)

    val formatter = DateTimeFormatter.ofPattern("E, d MMM yyyy", Locale.getDefault())

    val formattedDate = selectedDate.format(formatter)


    Column(modifier = modifier.padding(horizontal = 48.dp)) {


        Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "From:",
                color = SocialTheme.colors.textPrimary,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            Text(     modifier = Modifier
                .clickable(onClick = { showTimePickerStart = true })
                .padding(12.dp),

                text = startTimeState.hours.toString()+":"+startTimeState.minutes,
                color = SocialTheme.colors.textPrimary,
                style = TextStyle(fontFamily = Lexend, fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(painter = painterResource(id = com.example.friendupp.R.drawable.ic_long_right), contentDescription =null, tint = SocialTheme.colors.iconPrimary )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "To:",
                color = SocialTheme.colors.textPrimary,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )
            Text(
                modifier = Modifier
                    .clickable(onClick = { showTimePickerEnd = true })
                    .padding(12.dp),
                text = endTimeState.hours.toString()+":"+endTimeState.minutes,
                color = SocialTheme.colors.textPrimary,
                style = TextStyle(fontFamily = Lexend, fontSize = 18.sp)
            )
        }



    }

    val textStyle = TextStyle(fontFamily = Lexend, color = SocialTheme.colors.textPrimary)
    val textStylePicked = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        color = SocialTheme.colors.textPrimary
    )

    if (showTimePickerStart) {
        TimePickerDialog(
            onDismissRequest = { showTimePickerStart = false },
            onTimeChange = {

                startTimeState.hours=it.hour
                startTimeState.minutes=it.minute
                showTimePickerStart = false
            },
            is24HourFormat = true,
            containerColor = SocialTheme.colors.uiBackground,
            colors = TimePickerDefaults.colors(
                clockDigitsColonTextColor = SocialTheme.colors.textPrimary,
                dialNumberUnselectedTextColor = SocialTheme.colors.textPrimary,
                clockDigitsUnselectedTextColor = SocialTheme.colors.textPrimary,
                dialBackgroundColor = SocialTheme.colors.uiBorder.copy(0.5f)
                , clockDigitsSelectedBackgroundColor = SocialTheme.colors.textInteractive.copy(0.2f), clockDigitsUnselectedBackgroundColor =  SocialTheme.colors.uiBorder.copy(0.5f),
                dialNumberSelectedBackgroundColor = SocialTheme.colors.textInteractive,
                dialCenterColor = SocialTheme.colors.textInteractive,
                dialHandColor = SocialTheme.colors.textInteractive
            ),
            typography = TimePickerDefaults.typography(
                dialNumber = textStyle,
                dialogTitle = textStyle,
                digitsColon = textStylePicked,
                digitsHour = textStylePicked,
                digitsMinute = textStylePicked
            )
        )
    }

    if (showTimePickerEnd) {
        TimePickerDialog(
            onDismissRequest = { showTimePickerEnd = false },
            onTimeChange = {
                endTimeState.hours=it.hour
                endTimeState.minutes=it.minute
                showTimePickerEnd = false
            },
            is24HourFormat = true,
            containerColor = SocialTheme.colors.uiBackground,
            colors = TimePickerDefaults.colors(
                clockDigitsColonTextColor = SocialTheme.colors.textPrimary,
                dialNumberUnselectedTextColor = SocialTheme.colors.textPrimary,
                clockDigitsUnselectedTextColor = SocialTheme.colors.textPrimary,
                dialBackgroundColor = SocialTheme.colors.uiBorder.copy(0.5f)
                , clockDigitsSelectedBackgroundColor = SocialTheme.colors.textInteractive.copy(0.2f), clockDigitsUnselectedBackgroundColor =  SocialTheme.colors.uiBorder.copy(0.5f)
                ,                dialNumberSelectedBackgroundColor = SocialTheme.colors.textInteractive,
                dialCenterColor = SocialTheme.colors.textInteractive,
                dialHandColor = SocialTheme.colors.textInteractive
            ),
            typography = TimePickerDefaults.typography(
                dialNumber = textStyle,
                dialogTitle = textStyle,
                digitsColon = textStylePicked,
                digitsHour = textStylePicked,
                digitsMinute = textStylePicked
            )
        )
    }

}