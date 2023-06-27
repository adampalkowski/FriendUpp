package com.example.friendupp.Components.Calendar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.Companion.days

private val defaultCardShape = RoundedCornerShape(8.dp)


class HorizontalDateState2(
    selectedDay: Int,
    selectedMonth: Int,
    selectedYear: Int,
    shouldScrollToSelectedDate: Boolean = true,
) {
    private var _selectedDay by mutableStateOf(selectedDay, structuralEqualityPolicy())
    private var _selectedMonth by mutableStateOf(selectedMonth, structuralEqualityPolicy())
    private var _selectedYear by mutableStateOf(selectedYear, structuralEqualityPolicy())


    val selectedDay: Int
        get() = _selectedDay

    val selectedMonth: Int
        get() = _selectedMonth

    val selectedYear: Int
        get() = _selectedYear

    fun increaseMonth() {
        _selectedMonth += 1
        if (_selectedMonth > Calendar.DECEMBER+1) {
            _selectedMonth = Calendar.JANUARY+1
            _selectedYear += 1
        }
    }

    fun setSelectedDay(day:Int) {
        _selectedDay =day

    }

    fun decreaseMonth() {
        _selectedMonth -= 1
        if (_selectedMonth < Calendar.JANUARY+1) {
            _selectedMonth = Calendar.DECEMBER+1
            _selectedYear -= 1
        }
    }

    fun increaseYear(){
        _selectedYear+=1
    }
    fun decreaseYear(){
        _selectedYear-=1
    }
    companion object {
        val Saver: Saver<HorizontalDateState2, *> = listSaver(
            save = {
                listOf(
                    it.selectedDay,
                    it.selectedMonth,
                    it.selectedYear,
                )
            },
            restore = {
                HorizontalDateState2(
                    selectedDay=it[0],
                    selectedMonth=it[1],
                    selectedYear=it[2],
                )
            }
        )
    }
}

@Composable
fun rememberHorizontalDatePickerState2(  initialCalendar: Calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())) =
    rememberSaveable(saver = HorizontalDateState2.Saver) { HorizontalDateState2(
        selectedDay = initialCalendar.get(Calendar.DAY_OF_MONTH),
        selectedMonth = initialCalendar.get(Calendar.MONTH)+1,
        selectedYear = initialCalendar.get(Calendar.YEAR)) }


@Composable
fun HorizontalDatePicker(state: HorizontalDateState2 = rememberHorizontalDatePickerState2() , monthIncreased: () -> Unit,
                         monthDecreased: () -> Unit,
                         yearDecreased: () -> Unit,
                         yearIncreased: () -> Unit, onDayClick: (Int) -> Unit,) {
    val locale = Locale.getDefault()
    //month year
        Column() {
            MonthYearPicker(
                modifier=Modifier.fillMaxWidth(),
                monthText = Month.of(state.selectedMonth).toString(),
                yearText = state.selectedYear,
                monthIncreased = monthIncreased,
                monthDecreased =monthDecreased,
                yearIncreased =yearIncreased,
                yearDecreased = yearDecreased)
            //LOCALE SET TO ENGLISH
            Spacer(modifier = Modifier.height(6.dp))
            DayPicker( selectedDay = state.selectedDay,
                year = state.selectedYear,
                month = state.selectedMonth,
                locale = locale,
                onDayClick =onDayClick
            )
            Spacer(modifier = Modifier.height(6.dp))

        }

}

fun daysOfMonth(year: Int, month: Int): List<LocalDate> {
    val start = LocalDate.of(year, month, 1)
    val end = start.plusMonths(1).minusDays(1)
    return (0 until end.dayOfMonth).map { start.plusDays(it.toLong()) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPicker(
    modifier:Modifier=Modifier,
    selectedDay: Int,
    year: Int, month: Int, locale: Locale = LocalConfiguration.current.locale,
    onDayClick: (Int) -> Unit,
    dayCardShape: Shape = defaultCardShape,
    dayCardBorder: BorderStroke = BorderStroke(1.dp, Color.Black.copy(0.1f)),
    dayCardBackgroundColor: Color = Color.White,
    dayCardTextColor: Color = SocialTheme.colors.textPrimary,
    selectedDayCardBackgroundColor: Color = SocialTheme.colors.textInteractive,
    selectedDayCardBackgroundDeptColor: Color = Color(0xFF5E6FAB),
    selectedDayCardTextColor: Color = Color.White,
    dayOfWeekTextStyle: TextStyle = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Medium, fontSize = 14.sp,color=Color.Black),
    dayOfMonthTextStyle: TextStyle =  TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,color=Color.Black),
    dayOfWeekFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E", locale)
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedDay - 1,
        initialFirstVisibleItemScrollOffset = 0,
    )
    val days: List<LocalDate> = daysOfMonth(year, month)

    LazyRow(modifier=Modifier.padding(horizontal = 0.dp),state = listState) {
        items(days) { day ->
            if (day.dayOfMonth == selectedDay){
                Column(
                    modifier = Modifier
                        ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day.format(dayOfWeekFormatter),
                        style = dayOfWeekTextStyle,
                        color= dayCardTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))


                        Card(
                            modifier = Modifier
                                .size(48.dp)
                                .zIndex(2f)
                                .graphicsLayer {
                                    translationY = -10f
                                },
                            colors = CardDefaults.cardColors(
                                contentColor = Color.Transparent,
                                containerColor = if (day.dayOfMonth == selectedDay) selectedDayCardBackgroundColor else dayCardBackgroundColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            onClick = { onDayClick(day.dayOfMonth) }
                        ) {
                            Box(modifier =Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    style = dayOfMonthTextStyle,
                                    fontSize = 14.sp,
                                    fontWeight = if (day.dayOfMonth == selectedDay) FontWeight.SemiBold else FontWeight.Normal,
                                    color=if (day.dayOfMonth == selectedDay) selectedDayCardTextColor else dayCardTextColor
                                )
                            }

                    }
                }


            }else{
                Column(
                    modifier = Modifier
                        .clickable(onClick = { onDayClick(day.dayOfMonth) })
                        .background(Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day.format(dayOfWeekFormatter),
                        style = dayOfWeekTextStyle,
                        color=if (day.dayOfMonth == selectedDay) selectedDayCardTextColor else dayCardTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = day.dayOfMonth.toString(),
                        style = dayOfMonthTextStyle,
                        fontWeight = if (day.dayOfMonth == selectedDay) FontWeight.SemiBold else FontWeight.Normal,
                        color=if (day.dayOfMonth == selectedDay) selectedDayCardTextColor else dayCardTextColor

                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

        }
    }

}

@Composable
fun MonthYearPicker(
    modifier:Modifier=Modifier,
    monthText: String,
    yearText: Int,
    monthIncreased: () -> Unit,
    monthDecreased: () -> Unit,
    yearDecreased: () -> Unit,
    yearIncreased: () -> Unit,
    monthTextStyle: TextStyle = TextStyle.Default.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = Lexend,
        color = SocialTheme.colors.textPrimary
    ),
    yearTextStyle: TextStyle = TextStyle.Default.copy(
        fontSize =12.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = Lexend,
        color = SocialTheme.colors.textPrimary
    )

) {
    Row(modifier=modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CardButton(
            onClick = yearDecreased,
            icon = R.drawable.double_arrow_left,
            backgroundColor = Color.Transparent,
            borderColor =SocialTheme.colors.uiBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        CardButton(
            onClick = monthDecreased,
            icon = R.drawable.arrow_left,
            backgroundColor =  Color.Transparent,
            borderColor = SocialTheme.colors.uiBorder
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = monthText, style = monthTextStyle)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = yearText.toString(), style = yearTextStyle)
        Spacer(modifier = Modifier.width(12.dp))
        CardButton(
            onClick = monthIncreased,
            icon = R.drawable.arrow_right,
            backgroundColor =  Color.Transparent,
            borderColor =SocialTheme.colors.uiBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        CardButton(
            onClick = yearIncreased,
            icon = R.drawable.double_arrow_right,
            backgroundColor = Color.Transparent,
            borderColor = SocialTheme.colors.uiBorder
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    iconTint: Color =SocialTheme.colors.iconPrimary,
    cardShape: Shape = RoundedCornerShape(100.dp),
    borderColor: Color =Color(0xFFEAEAEA),
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    cardPadding: Dp = 8.dp
) {
    androidx.compose.material3.Card(
        modifier = modifier,
        onClick = onClick,
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor =  SocialTheme.colors.uiBorder.copy(0.1f), contentColor = Color.Transparent)
        ) {
        Box(
            modifier = modifier
                .background(color = backgroundColor)
                .padding(cardPadding)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "arrow",
                tint = iconTint
            )

        }
    }
}
