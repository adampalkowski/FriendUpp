package com.palkowski.friendupp.ChatUi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.palkowski.friendupp.Create.CustomizeItem
import com.palkowski.friendupp.Profile.ProfileDisplaySettingsItem
import com.palkowski.friendupp.R
import com.palkowski.friendupp.model.Chat
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme


@Composable
fun ChatSettingsDialog(
    onCancel: () -> Unit,
    backgroundColor: Color = SocialTheme.colors.uiBackground,
    group: Chat,
    reportChat:()->Unit,
    shareGroupLink:()->Unit,
    turnOffChatNotification:(String)->Unit,    turnOnChatNotification:(String)->Unit,
    goToGroup:()->Unit,
    goToActivity:()->Unit,
    goToUser:()->Unit,notificationTurnedOff:Boolean

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
                    NotificationExtendableItem(label="Notifications",icon= R.drawable.ic_notify, textColor = SocialTheme.colors.textPrimary,
                        onClick = {turnOffChatNotification(group.id!!)},notificationTurnedOff=notificationTurnedOff,
                        UpdateSharedPrefs={
                            if(it){
                                turnOffChatNotification(group.id.toString())
                            }else{
                                turnOnChatNotification(group.id.toString())
                            }
                        })
                    ProfileDisplaySettingsItem(label="Report",icon= R.drawable.ic_flag, textColor = SocialTheme.colors.error, onClick = reportChat)
                    ProfileDisplaySettingsItem(label="Cancel" , turnOffIcon = true, textColor = SocialTheme.colors.textPrimary.copy(0.5f), onClick = onCancel)
            }



        }


    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NotificationExtendableItem(turnOffIcon:Boolean=false,icon:Int=R.drawable.ic_x,label:String,textColor:androidx.compose.ui.graphics.Color=androidx.compose.ui.graphics.Color.Black,onClick: () -> Unit={}
                               ,notificationTurnedOff:Boolean,UpdateSharedPrefs:(Boolean)->Unit) {
    var userInviteNotification by rememberSaveable { mutableStateOf(notificationTurnedOff) }

    Column(Modifier.background(SocialTheme.colors.uiBackground)) {
        var extend by rememberSaveable {
            mutableStateOf(false)
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { extend = !extend })
            .padding(vertical = 16.dp, horizontal = 12.dp), horizontalArrangement =Arrangement.Center){
            if(!turnOffIcon){
                Icon(painter = painterResource(id = icon), contentDescription =null,tint=textColor )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(text =label, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, fontFamily = Lexend), color = textColor )
            if(!turnOffIcon){
                Spacer(modifier = Modifier.width(32.dp))

            }
        }
        AnimatedVisibility(visible = extend, enter = scaleIn(), exit = scaleOut()) {
            CustomizeItem(
                title = "Chat notifications",
                info = "Turn off this chat's notification.",
                switchValue = userInviteNotification,
                onSwitchValueChanged = {
                    userInviteNotification = it
                    UpdateSharedPrefs(it)
                }
            )

        }
        Box(modifier = Modifier
            .height(0.5.dp)
            .fillMaxWidth()
            .background(SocialTheme.colors.uiBorder))
    }
}

