package com.example.friendupp.ChatUi

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.friendupp.Profile.ProfileDisplaySettingsItem
import com.example.friendupp.R
import com.example.friendupp.model.Chat
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


@Composable
fun ChatSettingsDialog(
    onCancel: () -> Unit,
    backgroundColor: Color = SocialTheme.colors.uiBackground,
    group: Chat,
    reportChat:()->Unit,
    shareGroupLink:()->Unit,
    turnOffChatNotification:(id:String)->Unit,
    goToGroup:()->Unit,
    goToActivity:()->Unit,
    goToUser:()->Unit,

){
    Dialog(onDismissRequest = onCancel,) {
        Box(
            Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(backgroundColor)){
            Column(horizontalAlignment = Alignment.CenterHorizontally,modifier= Modifier.verticalScroll(
                rememberScrollState()
            ) ) {
                    when(group.type){
                         "duo"->{
                             ProfileDisplaySettingsItem(label="Visit profile",icon= R.drawable.ic_profile_300, textColor = SocialTheme.colors.textPrimary, onClick = goToUser)

                         }
                         "group"->{
                             ProfileDisplaySettingsItem(label="Go to group",icon= R.drawable.ic_group, textColor = SocialTheme.colors.textPrimary, onClick = goToGroup)
                         }
                         "activity"->{
                             ProfileDisplaySettingsItem(label="Go to activity",icon= R.drawable.ic_event, textColor = SocialTheme.colors.textPrimary, onClick = goToActivity)

                         }
                    }
                    ProfileDisplaySettingsItem(label="Notifications",icon= R.drawable.ic_notify, textColor = SocialTheme.colors.textPrimary, onClick = {turnOffChatNotification(group.id!!)})
                    ProfileDisplaySettingsItem(label="Report",icon= R.drawable.ic_flag, textColor = SocialTheme.colors.error, onClick = reportChat)
                    ProfileDisplaySettingsItem(label="Cancel" , turnOffIcon = true, textColor = SocialTheme.colors.textPrimary.copy(0.5f), onClick = onCancel)
            }



        }


    }

}