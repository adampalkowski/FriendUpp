package com.palkowski.friendupp.Components

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
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme

@Composable
fun FriendUppDialog(
    label: String,
    icon: Int,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    confirmLabel: String = "Confirm",
    cancelLabel: String = "Cancel",
    iconTint: Color = SocialTheme.colors.iconPrimary,
    textColor: Color = SocialTheme.colors.textPrimary,
    backgroundColor: Color=SocialTheme.colors.uiBackground,
    confirmTextColor:Color=SocialTheme.colors.textInteractive,
    cancelTextColor:Color=SocialTheme.colors.iconPrimary,
    disableConfirmButton:Boolean=false

){
        Dialog(onDismissRequest = onCancel,) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(backgroundColor)){
                Column(horizontalAlignment = Alignment.CenterHorizontally,modifier=Modifier.verticalScroll(
                    rememberScrollState()) ) {
                    Column(Modifier.padding(horizontal = 12.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Icon(painter = painterResource(icon), contentDescription =null,tint=cancelTextColor )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text =label, textAlign = TextAlign.Center, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, fontFamily = Lexend), color = cancelTextColor )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color.Transparent, contentColor = Color.Transparent), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                        Column() {
                            if(!disableConfirmButton){
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = onConfirm)
                                    .border(BorderStroke(0.5.dp, SocialTheme.colors.uiBorder))
                                    .padding(vertical = 16.dp, horizontal = 12.dp), horizontalArrangement = Arrangement.Center){
                                    Text(text =confirmLabel, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, fontFamily = Lexend), color = confirmTextColor )
                                }
                            }else{
                                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(SocialTheme.colors.uiBorder))
                            }

                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onCancel)
                                .padding(vertical = 16.dp, horizontal = 12.dp), horizontalArrangement = Arrangement.Center){
                                Text(text =cancelLabel, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, fontFamily = Lexend), color = cancelTextColor )

                            }
                        }
                    }
                }
         
              

            }


        }

}