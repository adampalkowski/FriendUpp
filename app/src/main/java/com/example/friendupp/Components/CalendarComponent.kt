package com.example.friendupp.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.friendupp.Components.Calendar.HorizontalDatePicker
import com.example.friendupp.ui.theme.SocialTheme


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarComponent(onDatePicked:(Int)->Unit) {
    Box(modifier = Modifier.padding(horizontal = 0.dp, vertical = 4.dp))
    {
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(
                    contentColor = SocialTheme.colors.uiBackground,
                    containerColor =SocialTheme.colors.uiBackground,
                ),
                modifier = Modifier.fillMaxWidth(),

                ) {
                HorizontalDatePicker(onDatePicked)

            }

    }

}
