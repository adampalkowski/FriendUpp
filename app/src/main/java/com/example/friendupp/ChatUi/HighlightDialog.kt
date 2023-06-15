package com.example.friendupp.ChatUi

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.R
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HighLightDialog(modifier: Modifier, onEvent: (ChatEvents) -> Unit, highlitedMessage: String) {
    Box(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        Card(shape = RoundedCornerShape(12.dp), elevation = 8.dp, onClick = {

            onEvent(ChatEvents.CloseDialog)
        }) {

            Box(
                modifier = Modifier
                    .background(color = SocialTheme.colors.uiBackground)
                    .padding(
                        if (highlitedMessage.isValidUrl()) {
                            0.dp
                        } else {
                            12.dp
                        }
                    )
            ) {
                if (highlitedMessage.isValidUrl()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(highlitedMessage)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_add_image),
                        contentDescription = "image sent",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                    )

                } else {
                    Text(
                        text = highlitedMessage.toString(),
                        color = SocialTheme.colors.textPrimary,
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

            }
        }
    }
}

fun String.isValidUrl(): Boolean =
    Patterns.WEB_URL.matcher(this).matches() && this.contains("firebasestorage.googleapis")

