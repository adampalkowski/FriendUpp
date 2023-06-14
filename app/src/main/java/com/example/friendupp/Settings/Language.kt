package com.example.friendupp.Settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.RoundedCheckView
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme
import java.util.*

sealed class LanguageEvents{
    object GoBack:LanguageEvents()
}

data class Language(val label: String, val code: String)
@Composable
fun LanguageScreen(onEvent:(LanguageEvents)->Unit){
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

    Column() {
        ScreenHeading(title = "Language", backButton = true, onBack = {onEvent(LanguageEvents.GoBack)}) { }
        Spacer(modifier = Modifier.height(24.dp))
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