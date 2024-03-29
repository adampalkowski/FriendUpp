package com.palkowski.friendupp.Settings

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkowski.friendupp.Components.ScreenHeading
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme
import com.palkowski.friendupp.R
import kotlinx.coroutines.launch

sealed class SettingsEvents {
    object GoBack : SettingsEvents()
    object ChangePassword : SettingsEvents()
    object UpdateEmail : SettingsEvents()
    object Notifications : SettingsEvents()
    object Rules : SettingsEvents()
    object LogOut : SettingsEvents()
    object DeleteAccount : SettingsEvents()
    object ChangeSearchRange : SettingsEvents()
    object ChangeLanguage : SettingsEvents()
    object SocialMedia : SettingsEvents()
    object FAQ : SettingsEvents()
    object Support : SettingsEvents()
    object AppVersionInformation : SettingsEvents()
    object TermsAndPrivacy : SettingsEvents()
    object DarkMode : SettingsEvents()
    object OpenLogOutDialog : SettingsEvents()
    object OpenDeleteAccountDialog : SettingsEvents()
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(modifier: Modifier, settingsEvents: (SettingsEvents) -> Unit) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val confirmAction = remember { mutableStateOf<SettingsEvents?>(null) }
    val context = LocalContext.current


    BackHandler(true) {
        settingsEvents(SettingsEvents.GoBack)
    }

    ModalBottomSheetLayout(
        sheetContent = {
            val confirmType = confirmAction.value

            if (confirmType != null) {
                BottomSheetContent(
                    confirmColor = when (confirmType) {
                        SettingsEvents.LogOut -> SocialTheme.colors.error
                        SettingsEvents.DeleteAccount -> SocialTheme.colors.error

                        else -> SocialTheme.colors.textInteractive
                    },
                    description = when (confirmType) {
                        SettingsEvents.LogOut -> "Are you sure you want to log out?"
                        SettingsEvents.DeleteAccount -> "Are you sure you want to delete your account?"
                        SettingsEvents.DarkMode ->
                            "To enable the dark mode, kindly navigate to the settings on your phone and adjust the theme settings to the 'dark' option. This will activate the dark mode feature for a more visually comfortable and immersive experience."
                        else -> ""
                    },
                    title = when (confirmType) {
                        SettingsEvents.LogOut -> "Log out"
                        SettingsEvents.DeleteAccount -> "Delete account"
                        SettingsEvents.DarkMode -> "Dark mode"
                        else -> ""
                    },
                    icon = when (confirmType) {
                        SettingsEvents.LogOut -> R.drawable.ic_logout
                        SettingsEvents.DeleteAccount -> R.drawable.ic_delete
                        SettingsEvents.DarkMode -> R.drawable.ic_darkmode
                        else -> R.drawable.ic_logout // Set a default icon if needed
                    },
                    onCancel = {
                        scope.launch { sheetState.hide() }
                        confirmAction.value = null
                    },
                    onConfirm = {
                        // Handle the confirmation logic here, based on the confirmType
                        settingsEvents(confirmType)
                        confirmAction.value = null
                    },
                    cancelLabel = when (confirmType) {
                        SettingsEvents.LogOut -> "Cancel"
                        SettingsEvents.DeleteAccount -> "Cancel"
                        else -> ""
                    },
                    confirmLabel = when (confirmType) {
                        SettingsEvents.LogOut -> "Log out"
                        SettingsEvents.DeleteAccount -> "Delete"
                        else -> ""
                    }, disableButtons = when (confirmType) {
                        SettingsEvents.DarkMode -> true
                        else -> false
                    }
                )
            }

        },
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp),
        scrimColor = Color.Black.copy(0.8f),
        sheetElevation = 16.dp
    ) {


        Column(modifier.verticalScroll(rememberScrollState())) {
            ScreenHeading(
                title = "Settings",
                backButton = true,
                onBack = { settingsEvents(SettingsEvents.GoBack) }) {

            }
            SettingsLabel("Search Range")


            SettingsItem(
                label = "Change range",
                icon = R.drawable.ic_ruler,
                onClick = { settingsEvents(SettingsEvents.ChangeSearchRange) })
            SettingsLabel("Account")
            SettingsItem(
                label = "Language",
                icon = R.drawable.ic_language,
                onClick = { settingsEvents(SettingsEvents.ChangeLanguage) })
            SettingsItem(
                label = "Password",
                icon = R.drawable.ic_password,
                onClick = { settingsEvents(SettingsEvents.ChangePassword) })
            SettingsItem(
                label = "Email",
                icon = R.drawable.ic_email,
                onClick = { settingsEvents(SettingsEvents.UpdateEmail) })
            SettingsItem(
                label = "Dark mode",
                icon = R.drawable.ic_darkmode,
                onClick = {
                    scope.launch {
                        settingsEvents(SettingsEvents.DarkMode)
                    }
                })
            SettingsItem(
                label = "Notifications",
                icon = R.drawable.ic_notify,
                onClick = { settingsEvents(SettingsEvents.Notifications) })
            SettingsItem(
                label = "Rules and regulations",
                icon = R.drawable.ic_law,
                onClick = { settingsEvents(SettingsEvents.Rules) })



            SettingsLabel("Login")
            SettingsItem(
                label = "Log out",
                icon = R.drawable.ic_logout,
                onClick = {
                    scope.launch {
                        settingsEvents(SettingsEvents.OpenLogOutDialog)
                    }

                })
            SettingsItem(
                label = "Delete account",
                icon = R.drawable.ic_delete,
                onClick = {
                    scope.launch {

                        settingsEvents(SettingsEvents.OpenDeleteAccountDialog)
                    }
                })

/*
            SettingsLabel("About")
            SettingsItem(
                label = "FAQ",
                icon = R.drawable.ic_faq,
                onClick = { settingsEvents(SettingsEvents.FAQ) })
            SettingsItem(
                label = "Support",
                icon = R.drawable.ic_support,
                onClick = { settingsEvents(SettingsEvents.Support) })
            SettingsItem(
                label = "Terms of service and privacy policy",
                icon = R.drawable.ic_terms,
                onClick = { settingsEvents(SettingsEvents.TermsAndPrivacy) })
*/
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Beta Version 1.0",
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.ExtraLight,
                    fontSize = 12.sp
                ),
                color = SocialTheme.colors.textPrimary.copy(0.5f)
            )

        }

    }


}

@Composable
fun RangeItem(
    label: String,
    icon: Int,
    onClick: () -> Unit,
    sliderValue: Float,
    onValueChange: (Float) -> Unit,
) {

    val interactionSource = remember { MutableInteractionSource() }
    val rippleColor = SocialTheme.colors.iconPrimary.copy(0.2f)

    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true, color = rippleColor),
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = SocialTheme.colors.iconPrimary
        )
        Spacer(modifier = Modifier.width(16.dp))

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
            Slider(
                modifier = Modifier
                    .padding(horizontal = 6.dp),
                value = sliderValue,
                onValueChange = onValueChange,
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

        Spacer(modifier = Modifier.width(12.dp))


    }
}

@Composable
fun BottomSheetContent(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    confirmLabel: String,
    cancelLabel: String,
    confirmColor: Color,
    description: String,
    icon: Int,
    title: String,
    disableButtons: Boolean = false,
) {
    Column(
        Modifier
            .background(SocialTheme.colors.uiBackground)
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Row(Modifier.padding(end = 32.dp)) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = SocialTheme.colors.iconPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.padding(bottom = 4.dp), text = title,
                color = SocialTheme.colors.textPrimary.copy(0.8f), style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
        Spacer(Modifier.height(16.dp))

        Text(
            style = TextStyle(
                fontFamily = Lexend, fontSize = 16.sp,
                fontWeight = FontWeight.Light
            ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
            text = description
        )
        Spacer(Modifier.height(24.dp))
        if (disableButtons) {

        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {


                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(6.dp)
                        )
                        .clickable(onClick = onCancel)
                        .background(SocialTheme.colors.uiBackground)
                        .border(
                            BorderStroke(1.dp, SocialTheme.colors.uiBorder),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        cancelLabel,
                        style = TextStyle(
                            fontFamily = Lexend, fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ), color = SocialTheme.colors.textPrimary.copy(0.8f)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(6.dp)
                        )
                        .clickable(onClick = onConfirm)
                        .background(confirmColor)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        confirmLabel,
                        style = TextStyle(
                            fontFamily = Lexend, fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White
                    )
                }

            }
        }


    }
}

@Composable
fun SettingsItem(label: String, icon: Int, disabled: Boolean = false,number:Int?=null, onClick: () -> Unit) {


    val interactionSource = remember { MutableInteractionSource() }
    val rippleColor = SocialTheme.colors.iconPrimary.copy(0.2f)

    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true, color = rippleColor),
                onClick = {
                    if (disabled) {
                    } else {
                        onClick()
                    }
                }
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = SocialTheme.colors.iconPrimary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Row() {


        Column(horizontalAlignment = Alignment.Start) {

            Text(
                modifier = Modifier.padding(bottom = 4.dp), text = label,
                color =if(disabled){SocialTheme.colors.textPrimary.copy(0.4f)}else{SocialTheme.colors.textPrimary.copy(0.8f)} , style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            if(disabled){
                Text(
                    modifier = Modifier.padding(bottom = 4.dp), text = "Coming soon",
                    color = SocialTheme.colors.textPrimary.copy(0.8f), style = TextStyle(
                        fontFamily = Lexend,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light
                    )
                )
            }

        }
            if(number!=null){
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier
                    .clip(CircleShape)
                    .background(SocialTheme.colors.textInteractive)
                    .padding(4.dp)){
                    Text(text = number.toString(), style = TextStyle(fontFamily = Lexend, fontSize = 10.sp), color = Color.White)
                }
            }
        }
    }

}

@Composable
fun SettingsLabel(label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .width(24.dp)
                .background(SocialTheme.colors.uiBorder)
        )
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = label,
            color = SocialTheme.colors.textPrimary.copy(0.8f),
            style = TextStyle(
                fontFamily = Lexend,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(SocialTheme.colors.uiBorder)
        )

    }
}
