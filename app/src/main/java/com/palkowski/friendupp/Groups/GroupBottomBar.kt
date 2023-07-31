package com.palkowski.friendupp.Groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.palkowski.friendupp.ChatUi.ButtonAdd
import com.palkowski.friendupp.Components.BlueButton
import com.palkowski.friendupp.R

@Composable
fun GroupBottomBar(
    onClick: () -> Unit,
    createClicked: () -> Unit = {},
    openCamera: () -> Unit = {},
    photo: String,
    disabled: Boolean,
) {
    Row(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
        if (photo.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .size(48.dp)
                    .clickable(onClick = openCamera)
            )

        } else {
            ButtonAdd(onClick = openCamera, icon = R.drawable.ic_add_image)

        }
        Spacer(modifier = Modifier.width(12.dp))
     /*   ButtonAdd(onClick = onClick, icon = R.drawable.ic_filte_300)*/
        Spacer(modifier = Modifier.weight(1f))

        BlueButton(onClick = createClicked, icon = R.drawable.ic_long_right, disabled = disabled)

    }
}