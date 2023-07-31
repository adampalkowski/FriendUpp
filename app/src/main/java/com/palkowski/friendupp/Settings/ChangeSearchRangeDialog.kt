package com.palkowski.friendupp.Settings

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme



@Composable
fun ChangeSearchRangeDialog(
    label: String,
    icon: Int,
    onCancel: () -> Unit,
    onConfirm: (Float) -> Unit,
    confirmLabel: String = "Confirm",
    cancelLabel: String = "Cancel",
    iconTint: Color = SocialTheme.colors.iconPrimary,
    textColor: Color = SocialTheme.colors.textPrimary,
    backgroundColor: Color = SocialTheme.colors.uiBackground,
    confirmTextColor: Color = SocialTheme.colors.textInteractive,
    cancelTextColor: Color = SocialTheme.colors.iconPrimary,
    disableConfirmButton: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    var sliderValue by rememberSaveable {
        mutableStateOf(getSavedRangeValue(context))
    }
    LaunchedEffect(Unit) {
        sliderValue = getSavedRangeValue(context)
    }

    Dialog(onDismissRequest = onCancel) {
        Box(
            Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(backgroundColor)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                )
            ) {
                Column(
                    Modifier.padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = cancelTextColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = label,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            fontFamily = Lexend
                        ),
                        color = cancelTextColor
                    )
                }


                Spacer(modifier = Modifier.height(24.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material.Text(
                        textAlign = TextAlign.Center,
                        text = java.lang.String.format("%.1f", sliderValue) + " km",
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = SocialTheme.colors.textPrimary
                    )
                    Slider(modifier= Modifier
                        .padding(horizontal = 6.dp),
                        value = sliderValue,
                        onValueChange = { sliderValue=it },
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
                }

                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Transparent
                    ), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column() {


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    onConfirm(sliderValue)
                                })
                                .border(BorderStroke(0.5.dp, SocialTheme.colors.uiBorder))
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = confirmLabel,
                                style = TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    fontFamily = Lexend
                                ),
                                color =     confirmTextColor
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onCancel)
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = cancelLabel,
                                style = TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    fontFamily = Lexend
                                ),
                                color = cancelTextColor
                            )

                        }
                    }
                }
            }


        }


    }

}
// Function to get the saved range value from SharedPreferences
fun getSavedRangeValue(context: Context): Float {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val rangeValueKey = "rangeValue"
    return preferences.getFloat(rangeValueKey, 50f)
}

// Function to save the range value to SharedPreferences
fun saveRangeValue(value: Float,context: Context) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val rangeValueKey = "rangeValue"
    preferences.edit().putFloat(rangeValueKey, value).apply()
}