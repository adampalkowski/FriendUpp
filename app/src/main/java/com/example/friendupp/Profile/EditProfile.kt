package com.example.friendupp.Profile

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.friendupp.Categories.Category
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Components.FilterList
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.*
import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.R
import com.example.friendupp.Settings.ChangeEmailDialog
import com.example.friendupp.Settings.ChangePasswordDialog
import com.example.friendupp.model.User
import com.example.friendupp.model.UserData
import com.example.friendupp.model.UserState
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

sealed class EditProfileEvents {
    object GoBack : EditProfileEvents()
    object ConfirmChanges : EditProfileEvents()
    object OpenCamera : EditProfileEvents()
    object openEditEmailDialog : EditProfileEvents()
    object openChangePasswordDialog : EditProfileEvents()
}

@Composable
fun EditProfile(
    modifier: Modifier, goBack: () -> Unit, userVa: MutableState<User?>,
    onEvent: (EditProfileEvents) -> Unit, userState: UserState,
) {
    BackHandler(true) {
        goBack()
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeading(title = "Edit profile", backButton = true,
            backIcon = com.example.friendupp.R.drawable.ic_back, onBack = { goBack() }) {
            Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
                ButtonAdd(
                    icon = R.drawable.ic_email,
                    onClick = { onEvent(EditProfileEvents.openEditEmailDialog) })
                Spacer(
                    modifier = Modifier
                        .background(SocialTheme.colors.uiBorder)
                        .width(16.dp)
                )
                ButtonAdd(
                    icon = R.drawable.ic_password,
                    onClick = { onEvent(EditProfileEvents.openChangePasswordDialog) })

            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        EditProfileInfo(
            name = userState.nameState,
            username = userState.usernameState,
            imageUrl = userState.imageUrl,
            onClick = { onEvent(EditProfileEvents.OpenCamera) })
        Spacer(modifier = Modifier.height(24.dp))
        EditInfoContent(
            name = userState.nameState,
            username = userState.usernameState,
            biography = userState.bioState
        )
        TagsSettings(
            tags = userState.tags,
            onSelected = { userState.tags.add(it) },
            onDeSelected = { userState.tags.remove(it) })

        CreateButton(
            "Confirm changes",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            disabled = false,
            createClicked = { onEvent(EditProfileEvents.ConfirmChanges) })
        Spacer(modifier = Modifier.height(120.dp))
    }


}

@Composable
fun ActivityPreferences() {
    Column() {
        CreateHeading(
            text = "Activity preferences",
            icon = R.drawable.ic_tag,
            tip = false,
            description = ""
        )
        FilterList(tags = SnapshotStateList(),
            onSelected = {},
            onDeSelected = {})
    }
}

@Composable
fun EditProfileInfo(
    name: TextFieldState,
    username: TextFieldState,
    imageUrl: String,
    onClick: () -> Unit,
) {
    val maxLength = 25
    val name = name.text
    val limitedName = if (name.length > maxLength) name.substring(0, maxLength) + "..." else name
    val username = username.text
    val maxusernameLength = 25
    val limitedUserName =
        if (username.length > maxLength) username.substring(0, maxLength) + "..." else username


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_profile_300),
                    contentDescription = "stringResource(R.string.description)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClick)
                        .background(color = Color.Black.copy(0.6f))
                , contentAlignment = Alignment.Center) {
                    Icon(painter = painterResource(id = R.drawable.ic_add_image), contentDescription =null,tint= Color.White )

                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = limitedName,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp
                    ),
                    color = SocialTheme.colors.textPrimary
                )
                Text(
                    text = limitedUserName,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    ),
                    color = SocialTheme.colors.textPrimary.copy(0.5f)
                )
            }

        }
    }
}

@Composable
fun EditInfoContent(name: TextFieldState, username: TextFieldState, biography: TextFieldState) {
    val focusRequester = remember { FocusRequester() }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading(
            text = "Information",
            icon = R.drawable.ic_badge,
            tip = false,
            description = ""
        )
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Name", textState = name
        )
        Spacer(modifier = Modifier.height(12.dp))
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Username", textState = username
        )
        Spacer(modifier = Modifier.height(12.dp))
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Biography", textState = biography
        )
        Spacer(modifier = Modifier.height(12.dp))

    }


}