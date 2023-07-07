package com.example.friendupp.ActivityPreview

import android.widget.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.friendupp.ChatUi.ChatSettingsEvents
import com.example.friendupp.Components.ScreenHeading
import com.example.friendupp.Create.Customize
import com.example.friendupp.Create.CustomizeItem
import com.example.friendupp.R
import com.example.friendupp.Settings.SettingsItem
import com.example.friendupp.Settings.SettingsLabel
import com.example.friendupp.model.Activity


sealed class CreatorSettingsEvent {
    object GoBack : CreatorSettingsEvent()
    object AddUsers : CreatorSettingsEvent()
    object DeleteActivity : CreatorSettingsEvent()
    object DeleteImage : CreatorSettingsEvent()
    object Share : CreatorSettingsEvent()
    object RemoveParticipant : CreatorSettingsEvent()
    object ChangeLocation : CreatorSettingsEvent()
    object EditDescription : CreatorSettingsEvent()
}

@Composable
fun CreatorSettingsScreen(onEvent: (CreatorSettingsEvent) -> Unit, activity: Activity,updateCutomization:(Boolean,Boolean,Boolean)->Unit) {
    BackHandler(true) {
        onEvent(CreatorSettingsEvent.GoBack)
    }
    var activitySharing by remember { mutableStateOf(activity.enableActivitySharing) }
    var disableChat by remember { mutableStateOf(activity.disableChat) }
    var participantConfirmation by remember { mutableStateOf(activity.participantConfirmation) }
    DisposableEffect(true) {
        onDispose {
            if (activitySharing!=activity.enableActivitySharing||disableChat!=activity.disableChat||participantConfirmation!=activity.participantConfirmation)
            {
                updateCutomization(activitySharing,disableChat,participantConfirmation)
            }
        }
    }


    Column {
        ScreenHeading(
            title = "Activity settings",
            onBack = { onEvent(CreatorSettingsEvent.GoBack) },
            backButton = true
        ) {}
        SettingsItem(label = "Add users", icon = R.drawable.ic_person_add) {
            onEvent(CreatorSettingsEvent.AddUsers)
        }

        SettingsItem(label = "Change location", icon = R.drawable.ic_custom_location) {
            onEvent(CreatorSettingsEvent.ChangeLocation)

        }
        SettingsItem(label = "Edit description", icon = R.drawable.ic_edit) {
            onEvent(CreatorSettingsEvent.EditDescription)
        }
        SettingsItem(label = "Share", icon = R.drawable.ic_share) {
            onEvent(CreatorSettingsEvent.Share)
        }
        SettingsItem(label = "Remove participant", icon = R.drawable.ic_person_remove) {
            onEvent(CreatorSettingsEvent.RemoveParticipant)

        }

        SettingsItem(label = "Delete activity", icon = R.drawable.ic_delete) {
            onEvent(CreatorSettingsEvent.DeleteActivity)
        }
        Spacer(modifier = Modifier.height(24.dp))
        SettingsLabel("Customize")

        CustomizeItem(
            title = "Activity sharing", info = "Enable or Disable Activity Sharing",
            switchValue = activitySharing,
            onSwitchValueChanged = {
                activitySharing = it
            }
        )
        CustomizeItem(
            title = "Chat", info = "Enable or disable chat visibility.",
            switchValue = disableChat,
            onSwitchValueChanged = {
                disableChat = it

            }
        )

        CustomizeItem(
            title = "Participant Confirmation",
            info = "Require confirmation from the activity creator for users who want to join the activity.",
            switchValue = participantConfirmation,
            onSwitchValueChanged = {
                participantConfirmation = it

            }
        )


    }

}