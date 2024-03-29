package com.palkowski.friendupp.Settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkowski.friendupp.Components.ScreenHeading
import com.palkowski.friendupp.Create.RoundedCheckView
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme
import java.util.*

sealed class LanguageEvents{
    object GoBack:LanguageEvents()
}

data class Language(val label: String, val code: String)
@Composable
fun LanguageScreen(modifier:Modifier,onEvent:(LanguageEvents)->Unit){
    val currentLanguage = Locale.getDefault().getDisplayLanguage()
    val languages = listOf(
        Language("Use device language - $currentLanguage", currentLanguage),
        Language("Espanol", "es"),
        Language("English", "en"),
        Language("Deutsch", "de"),
        Language("Francais", "fr"),
        Language("Italiano", "it"),
        Language("Svenska", "sv"),
        Language("Portugues", "pt"),
        Language("Nederlands", "nl")
    )

    val selectedLanguage = remember { mutableStateOf<Language?>(languages.get(0)) }

    Column(modifier=modifier) {
        ScreenHeading(title = "Language", backButton = true, onBack = {onEvent(LanguageEvents.GoBack)}) { }
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxSize()){
            Column() {
                languages.forEach { language ->
                    LanguageItem(
                        label = language.label,
                        onClick = {},
                        selected = selectedLanguage.value == language,
                        onSelected = { isSelected ->
                            if (isSelected) {
                                selectedLanguage.value = language
                            }
                        }
                    )
                }
            }

            Box(modifier = Modifier
                .fillMaxSize()
                .background(SocialTheme.colors.uiBorder.copy(0.4f))){
                Text(text = "Different languages coming soon", style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 18.sp), modifier = Modifier.align(Alignment.Center))
            }
        }

    }
}


@Composable
fun LanguageItem(label:String,onClick:()->Unit,selected:Boolean,onSelected:(Boolean)->Unit){

    var textColor = if (selected){
        SocialTheme.colors.textLink
    }else{
        SocialTheme.colors.textPrimary.copy(0.5f)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier

            .clickable(onClick = {
                if (selected) {
                    onSelected(false)
                } else {
                    onSelected(true)
                }

            })
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(modifier = Modifier,text = label,
            style= TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Medium, fontSize = 16.sp),color=textColor)
        Spacer(modifier = Modifier.weight(1f))

        RoundedCheckView(selected = selected)
    }
}