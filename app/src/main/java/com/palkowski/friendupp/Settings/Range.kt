package com.palkowski.friendupp.Settings

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkowski.friendupp.Components.ScreenHeading
import com.palkowski.friendupp.Create.CreateButton
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme
import java.lang.String.format

sealed class RangeEvents{
    object GoBack:RangeEvents()
}

@Composable
fun RangeScreen(onEvent:(RangeEvents)->Unit){
    var sliderValue by remember {
        mutableStateOf(0f)
    }
    Column() {
        ScreenHeading(title = "Range", backButton = true, onBack = {onEvent(RangeEvents.GoBack)}) {

        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(modifier=Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,text = "Take control of your local activities feed and select the maximum range from which displayed activities should be found.",
            style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Light),
            color = SocialTheme.colors.textPrimary.copy(0.5f))
        Spacer(modifier = Modifier.height(24.dp))
        Text(modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center,text = format("%.1f", sliderValue)+" km", style = TextStyle(fontFamily = Lexend, fontSize = 20.sp, fontWeight = FontWeight.SemiBold), color = SocialTheme.colors.textPrimary)


        Slider(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
            value = sliderValue,
            onValueChange = { sliderValue_ ->
                sliderValue = sliderValue_
            },
            onValueChangeFinished = {
                // this is called when the user completed selecting the value
                Log.d("MainActivity", "sliderValue = $sliderValue")
            },
            valueRange = 5f..150f,
            colors = SliderDefaults.colors(
                thumbColor = SocialTheme.colors.textInteractive,
                activeTrackColor = SocialTheme.colors.textInteractive.copy(1f),
                inactiveTrackColor = SocialTheme.colors.uiBorder
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        CreateButton(modifier = Modifier.fillMaxWidth().padding(48.dp),text = "Confirm range", disabled =false, createClicked = {})

    }
}