package com.example.friendupp.Create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
fun TimeSelection(modifier: Modifier,startTimeState: TimeState,endTimeState: TimeState , label:String="Start", endTime:Boolean=false) {
    var showDatePickerStart by remember { mutableStateOf(false) }

    val backTextStyle= TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Light, fontSize = 16.sp)
    val timeTextStyleDisabled= TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,color=SocialTheme.colors.iconPrimary.copy(0.7f))
    val timeTextStyle= TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)


    Column(modifier=modifier) {


        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.height(1.dp).background(SocialTheme.colors.uiBorder).width(24.dp))
            Text(     modifier = Modifier.padding(bottom = 4.dp).padding(horizontal = 4.dp),
                text ="From",
                color = SocialTheme.colors.iconPrimary,
                style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Light)
            )
            Spacer(modifier = Modifier.height(1.dp).background(SocialTheme.colors.uiBorder).weight(1f))
            Text(     modifier = Modifier.padding(bottom = 4.dp).padding(horizontal = 4.dp),
                text ="12:30",
               style=timeTextStyleDisabled
            )
            Spacer(modifier = Modifier.height(1.dp).background(SocialTheme.colors.uiBorder).weight(1f))
            Text(     modifier = Modifier.padding(bottom = 4.dp).padding(horizontal = 4.dp),
                text ="To",
                color = SocialTheme.colors.iconPrimary,
                style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Light)
            )
            Spacer(modifier = Modifier.height(1.dp).background(SocialTheme.colors.uiBorder).weight(1f))
            WheelTimePicker(
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    color = SocialTheme.colors.uiBackground,
                    border = null
                ),
                startTime = LocalTime.of(endTimeState.hours, endTimeState.minutes),
                textStyle = timeTextStyle,
                size = DpSize(100.dp, 100.dp)
            ) {
                endTimeState.hours=it.hour
                endTimeState.minutes=it.minute
            }

            Spacer(modifier = Modifier.height(1.dp).background(SocialTheme.colors.uiBorder).width(48.dp))
        }
        Spacer(Modifier.height(12.dp))

    }
}