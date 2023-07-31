package com.palkowski.friendupp.Create

/*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeComponent(
                  modifier: Modifier,
                  dateState2: HorizontalDateState2,
                  startTimeState: TimeState, endTimeState: TimeState,
) {
    var showTimePickerStart by remember { mutableStateOf(false) }
    var showTimePickerEnd by remember { mutableStateOf(false) }
    var showDatePickerStart by remember { mutableStateOf(false) }
    var showTimePicker = remember {
        mutableStateOf(false)
    }
    val selectedDate =
        LocalDate.of(dateState2.selectedYear, dateState2.selectedMonth, dateState2.selectedDay)

    val formatter = DateTimeFormatter.ofPattern("E, d MMM yyyy", Locale.getDefault())

    val formattedDate = selectedDate.format(formatter)


    Column(modifier = modifier) {

        Row(modifier = modifier.padding(horizontal = 48.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(     modifier = Modifier
                .clickable(onClick = { showDatePickerStart =!showDatePickerStart })
                .padding(12.dp),

                text =formattedDate,
                color = SocialTheme.colors.textPrimary,
                style = TextStyle(fontFamily = Lexend, fontSize = 18.sp)
            )


            Text(     modifier = Modifier
                .clickable(onClick = { showTimePickerStart =! showTimePickerStart })
                .padding(12.dp),

                text = startTimeState.hours.toString()+":"+startTimeState.minutes,
                color = SocialTheme.colors.textPrimary,
                style = TextStyle(fontFamily = Lexend, fontSize = 18.sp)
            )



        }
        AnimatedVisibility(visible = showTimePickerStart) {
            WheelTimePicker(selectorProperties = WheelPickerDefaults.selectorProperties(color = SocialTheme.colors.uiBackground, border = null)) {

            }
        }
        AnimatedVisibility(visible = showDatePickerStart) {
            HorizontalDatePicker(dateState2,monthDecreased= {  dateState2.decreaseMonth()}, monthIncreased =  {  dateState2.increaseMonth()} , yearDecreased =  {dateState2.decreaseYear()  }  , yearIncreased = {dateState2.increaseYear()  } , onDayClick =  {  dateState2.setSelectedDay(it)
            showDatePickerStart=false})

        }

        Row(modifier = modifier.padding(horizontal = 48.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(     modifier = Modifier
                .clickable(onClick = { showTimePickerStart = true })
                .padding(12.dp),

                text =formattedDate,
                color = SocialTheme.colors.textPrimary,
                style = TextStyle(fontFamily = Lexend, fontSize = 18.sp)
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

   /* if (showTimePickerStart) {
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
    }*/

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

}*/