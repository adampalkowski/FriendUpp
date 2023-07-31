package com.palkowski.friendupp.ChatUi

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.palkowski.friendupp.Components.FriendUppDialog
import com.palkowski.friendupp.Components.ScreenHeading
import com.palkowski.friendupp.R
import com.palkowski.friendupp.Settings.SettingsItem
import com.palkowski.friendupp.di.ChatViewModel
import com.palkowski.friendupp.model.Chat
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme

sealed class TYPE {
    object DUO : TYPE()
    object GROUP : TYPE()
    object ACTIVITY : TYPE()
}

sealed class ChatSettingsEvents {
    object GoBack : ChatSettingsEvents()
    object Notification : ChatSettingsEvents()
    class Block(val id:String)  : ChatSettingsEvents()
    object Limit : ChatSettingsEvents()
    class Report(val id:String) : ChatSettingsEvents()
    object Share : ChatSettingsEvents()
    object CrateGroup : ChatSettingsEvents()
    object ChangeGroupName : ChatSettingsEvents()
    object ChangeGroupImage : ChatSettingsEvents()
    object AddUsers : ChatSettingsEvents()
    object LeaveGroup : ChatSettingsEvents()
    object DisplayUsers : ChatSettingsEvents()
    class GoToUserProfile(val userId: String, val chat: Chat) : ChatSettingsEvents()
}

@Composable
fun ChatSettingsScreen(
    modifier: Modifier,
    chatSettingsEvents: (ChatSettingsEvents) -> Unit,
    chatViewModel: ChatViewModel,
    chatResponse: Response<Chat>,
    context: Context,
) {
    // Handle null safety and loading state
    when (chatResponse) {
        is Response.Success -> {

            // The data has been successfully fetched, and group is not null
            ChatSettings(
                context = context,
                modifier = modifier,
                chatSettingsEvents = chatSettingsEvents,
                chatViewModel = chatViewModel,
                chat = chatResponse.data
            )

        }
        is Response.Loading -> {
            // Show a loading indicator while data is being fetched
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is Response.Failure -> {
            // Show an error message or navigate back on failure
            Toast.makeText(
                context,
                "Failed to load chat. Please try again later.",
                Toast.LENGTH_LONG
            ).show()
            // You can also navigate back using the onEvent callback
            chatSettingsEvents(ChatSettingsEvents.GoBack)
        }
        else -> {
            // Show a loading indicator or some placeholder content
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

    }
}

@Composable
fun ChatSettings(
    modifier: Modifier,
    chatSettingsEvents: (ChatSettingsEvents) -> Unit,
    chatViewModel: ChatViewModel,
    chat: Chat,
    context: Context,
) {
    var type = remember {
        mutableStateOf<TYPE?>(null)
    }
    var openReportDialog by remember { mutableStateOf(false) }
    var openBlockDialog by remember { mutableStateOf(false) }
    BackHandler(true) {
        chatSettingsEvents(ChatSettingsEvents.GoBack)
    }
    Box(modifier = modifier.fillMaxSize()) {

        when (chat.type) {
            "duo" -> {
                type.value = TYPE.DUO
            }
            "activity" -> {
                type.value = TYPE.ACTIVITY
            }
            "group" -> {
                type.value = TYPE.GROUP
            }
        }

        val (chat_name, chat_image) = getChatNameAndImage(chat)

        when (type.value) {
            is TYPE.DUO -> {
                ChatSettingsSingle(
                    name = chat_name,
                    username = "",
                    profilePictureUrl = chat_image,
                    chatSettingsEvents = chatSettingsEvents,
                    chat = chat,
                    openBlockDialog = { openBlockDialog=true },
                    openReportDialog = {openReportDialog=true}
                )
            }
            is TYPE.GROUP -> {

                ChatSettingsGroup(
                    name = chat_name, profilePictureUrl = chat_image,
                    chatSettingsEvents = chatSettingsEvents, chat = chat,
                    openBlockDialog = { openBlockDialog=true },
                    openReportDialog = {openReportDialog=true}
                )
            }
            is TYPE.ACTIVITY -> {

                ChatSettingsActivity(
                    name = chat_name, profilePictureUrl = chat_image,
                    chatSettingsEvents = chatSettingsEvents, chat = chat,
                    openBlockDialog = { openBlockDialog=true },
                    openReportDialog = {openReportDialog=true}
                )
            }
            else -> {}
        }
    }

    if (openReportDialog) {
        FriendUppDialog(
            label = "If the chat contains any violations, inappropriate content, or any other concerns that violate community guidelines, please report it.",
            icon = R.drawable.ic_flag,
            onCancel = { openReportDialog = false },
            onConfirm = {
                chatSettingsEvents(ChatSettingsEvents.Report(chat.id.toString()))

                openReportDialog = false
            }, confirmLabel = "Report"
        )
    }
    if (openBlockDialog) {
        FriendUppDialog(
            label = "Enabling the chat block feature restricts the ability of users to send messages. This action is reversible, allowing for the restoration of message-sending capabilities.",
            icon = R.drawable.ic_block,
            onCancel = { openBlockDialog = false },
            onConfirm = {
                chatSettingsEvents(ChatSettingsEvents.Block(chat.id.toString()))

                openBlockDialog = false
            }, confirmLabel = "Block", confirmTextColor = SocialTheme.colors.error
        )
    }

}

@Composable
fun ChatSettingsGroup(
    profilePictureUrl: String,
    name: String,
    chatSettingsEvents: (ChatSettingsEvents) -> Unit,

    chat: Chat,
    openReportDialog: () -> Unit,
    openBlockDialog: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
            rememberScrollState()
        )
    ) {
        ScreenHeading(
            title = "Chat settings",
            backButton = true,
            onBack = { chatSettingsEvents(ChatSettingsEvents.GoBack) }) {

        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = {
                chatSettingsEvents(
                    ChatSettingsEvents.GoToUserProfile(chat = chat, userId = "")
                )
            })
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = SocialTheme.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        SettingsItem(label = "Go to group", icon = R.drawable.ic_group) {
            chatSettingsEvents(ChatSettingsEvents.DisplayUsers)

        }

        SettingsItem(label = "Share group", icon = R.drawable.ic_share) {
            chatSettingsEvents(ChatSettingsEvents.Share)
        }

        SettingsItem(label = "Report", icon = R.drawable.ic_flag) {
            openReportDialog()
        }
    }
}

@Composable
fun ChatSettingsSingle(
    profilePictureUrl: String,
    name: String,
    username: String,
    chatSettingsEvents: (ChatSettingsEvents) -> Unit,
    chat: Chat,
    openReportDialog: () -> Unit,
    openBlockDialog: () -> Unit,

    ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
            rememberScrollState()
        )
    ) {
        ScreenHeading(
            title = "Chat settings",
            backButton = true,
            onBack = { chatSettingsEvents(ChatSettingsEvents.GoBack) }) {

        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = {
                chatSettingsEvents(
                    ChatSettingsEvents.GoToUserProfile("", chat)
                )
            })
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_profile_300),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = SocialTheme.colors.textPrimary
            )
            Text(
                text = username,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = SocialTheme.colors.textPrimary
            )

        }
        Spacer(modifier = Modifier.height(24.dp))
      /*  SettingsItem(label = "Notifications", icon = R.drawable.ic_notify) {
            chatSettingsEvents(ChatSettingsEvents.Notification)
        }*/
        /* SettingsItem(label = "Create group with user", icon = R.drawable.ic_group_add) {
             chatSettingsEvents(ChatSettingsEvents.CrateGroup)
         }*/
        /* SettingsItem(label = "Limit", icon = R.drawable.ic_limit) {
             chatSettingsEvents(ChatSettingsEvents.Limit)
         }*/
        SettingsItem(label = "Block", icon = R.drawable.ic_block) {
            openBlockDialog()
        }
        SettingsItem(label = "Report", icon = R.drawable.ic_flag) {
            openReportDialog()
        }
    }
}

@Composable
fun ChatSettingsActivity(
    profilePictureUrl: String,
    name: String,
    chatSettingsEvents: (ChatSettingsEvents) -> Unit,
    chat: Chat,
    openReportDialog: () -> Unit,
    openBlockDialog: () -> Unit,

    ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
            rememberScrollState()
        )
    ) {
        ScreenHeading(
            title = "Chat settings",
            backButton = true,
            onBack = { chatSettingsEvents(ChatSettingsEvents.GoBack) }) {

        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = {
                chatSettingsEvents(
                    ChatSettingsEvents.GoToUserProfile("", chat)
                )
            })
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "stringResource(R.string.description)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = SocialTheme.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        SettingsItem(label = "Go to activity", icon = R.drawable.ic_group) {
            chatSettingsEvents(ChatSettingsEvents.DisplayUsers)

        }
        SettingsItem(label = "Notifications", icon = R.drawable.ic_notify) {
            chatSettingsEvents(ChatSettingsEvents.Notification)
        }
        SettingsItem(label = "Share activity", icon = R.drawable.ic_share) {
            chatSettingsEvents(ChatSettingsEvents.Share)
        }

        SettingsItem(label = "Report", icon = R.drawable.ic_flag) {
            openReportDialog()
        }
    }
}
