package com.example.friendupp.Create

import android.util.Log
import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Components.Calendar.HorizontalDatePicker
import com.example.friendupp.Components.Calendar.HorizontalDateState2
import com.example.friendupp.Components.TimePicker.TimeState
import com.example.friendupp.Components.TimePicker.WheelPickerDefaults
import com.example.friendupp.Components.TimePicker.WheelTimePicker
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun StartTimePicker(modifier: Modifier, dateState: HorizontalDateState2, startTimeState: TimeState, label:String="Start",endTime:Boolean=false) {
    var showDatePickerStart by remember { mutableStateOf(false) }

    val backTextStyle= TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Light, fontSize = 16.sp)
    val timeTextStyle= TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    val selectedDate =
        LocalDate.of(dateState.selectedYear, dateState.selectedMonth, dateState.selectedDay)

    val formatter = if (selectedDate.year == Year.now().value) {
        DateTimeFormatter.ofPattern("E, d MMM", Locale.getDefault())
    } else {
        DateTimeFormatter.ofPattern("E, d MMM yyyy", Locale.getDefault())
    }

    val formattedDate = selectedDate.format(formatter)

    Column(modifier=modifier) {


        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.height(1.dp).background(SocialTheme.colors.uiBorder).width(24.dp))
            Text(     modifier = Modifier.padding(bottom = 4.dp).padding(horizontal = 4.dp),
                text =label,
                color = SocialTheme.colors.iconPrimary,
                style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Light)
            )
            Spacer(modifier = Modifier.height(1.dp).background(SocialTheme.colors.uiBorder).weight(1f))
            Text(     modifier = Modifier
                .clickable(onClick = { showDatePickerStart =!showDatePickerStart })
                .padding(12.dp),
                text =formattedDate,
                color = SocialTheme.colors.textPrimary,
                style = TextStyle(fontFamily = Lexend, fontSize = 16.sp)
            )


            Spacer(modifier = Modifier.height(1.dp).background(SocialTheme.colors.uiBorder).weight(1f))
            WheelTimePicker(
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    color = SocialTheme.colors.uiBackground,
                    border = null
                ),
                startTime = LocalTime.of(startTimeState.hours, startTimeState.minutes),
                textStyle = timeTextStyle,
                size = DpSize(100.dp, 100.dp)
            ) {
                startTimeState.hours=it.hour
                startTimeState.minutes=it.minute
            }

            Spacer(modifier = Modifier.height(1.dp).background(SocialTheme.colors.uiBorder).width(48.dp))
        }
        Spacer(Modifier.height(12.dp))
        AnimatedVisibility(visible = showDatePickerStart) {
            HorizontalDatePicker(dateState,monthDecreased= {  dateState.decreaseMonth()}, monthIncreased =  {  dateState.increaseMonth()} , yearDecreased =  {dateState.decreaseYear()  }  , yearIncreased = {dateState.increaseYear()  } , onDayClick =  {  dateState.setSelectedDay(it)
                showDatePickerStart=false})

        }

    }

}
