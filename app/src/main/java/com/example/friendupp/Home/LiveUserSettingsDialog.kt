package com.example.friendupp.Home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.friendupp.ActivityUi.ActivityPreviewEvents
import com.example.friendupp.ActivityUi.ActivityPreviewSettings
import com.example.friendupp.Profile.ProfileDisplaySettingsItem
import com.example.friendupp.R
import com.example.friendupp.ui.theme.SocialTheme

@Composable
fun LiveUserSettingsDialog(onDismissRequest:()->Unit,deleteActiveUser:()->Unit){
    Dialog(onDismissRequest =onDismissRequest) {
        Column(Modifier.clip(RoundedCornerShape(24.dp))) {
            ProfileDisplaySettingsItem(label="Delete",icon= R.drawable.ic_delete, textColor = SocialTheme.colors.error, onClick = { deleteActiveUser() })
            ProfileDisplaySettingsItem(label="Cancel" , turnOffIcon = true, textColor = SocialTheme.colors.textPrimary.copy(0.5f), onClick = onDismissRequest)
        }
    }
}