package com.palkowski.friendupp.bottomBar.ActivityUi

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.palkowski.friendupp.Create.FriendPickerItem
import com.palkowski.friendupp.Groups.SelectedUsersState
import com.palkowski.friendupp.model.Activity
import com.palkowski.friendupp.model.Participant
import com.palkowski.friendupp.ui.theme.Lexend
import com.palkowski.friendupp.ui.theme.SocialTheme




@Composable
fun RemoveUsersDialog(
    label: String,
    icon: Int,
    onCancel: () -> Unit,
    onConfirm: (List<String>) -> Unit,
    confirmLabel: String = "Confirm",
    cancelLabel: String = "Cancel",
    iconTint: Color = SocialTheme.colors.iconPrimary,
    textColor: Color = SocialTheme.colors.textPrimary,
    backgroundColor: Color = SocialTheme.colors.uiBackground,
    confirmTextColor: Color = SocialTheme.colors.textInteractive,
    cancelTextColor: Color = SocialTheme.colors.iconPrimary,
    disableConfirmButton: Boolean = false,
    activity: Activity,
    participantsList:List<Participant>
) {
    val selectedIds= remember{ mutableStateListOf<String>() }
    val focusRequester = remember { FocusRequester() }
    var disableConfirm by remember {
        mutableStateOf(false)
    }
    val selectedListIds = rememberSaveable(saver = SelectedUsersState.Saver) {
        SelectedUsersState(mutableStateListOf())
    }
    Dialog(onDismissRequest = onCancel) {
        Box(
            Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(backgroundColor)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                Column(
                    Modifier.padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = cancelTextColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = label,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            fontFamily = Lexend
                        ),
                        color = cancelTextColor
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                activity.participants_usernames
                activity.participants_profile_pictures

                LazyColumn{
                    items(participantsList){participant->

                            var selected by rememberSaveable {
                                mutableStateOf(false)
                            }
                            FriendPickerItem(
                                id =participant.id,
                                username =participant.username?: "",
                                onClick = {
                                },
                                imageUrl = participant.profile_picture ?: "",
                                onUserSelected = {
                                }, onUserDeselected = {
                                },
                                addUserName = { selected=true
                                    selectedListIds.list.add(participant.id)},
                                removeUsername = {  selected=false
                                    selectedListIds.list.remove(participant.id)},
                                selected =selected
                            )


                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Transparent
                    ), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    onConfirm(selectedListIds.list.toList())
                                })
                                .border(BorderStroke(0.5.dp, SocialTheme.colors.uiBorder))
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = confirmLabel,
                                style = TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    fontFamily = Lexend
                                ),
                                color = if (disableConfirm)  confirmTextColor.copy(0.2f) else confirmTextColor
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onCancel)
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = cancelLabel,
                                style = TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    fontFamily = Lexend
                                ),
                                color = cancelTextColor
                            )

                        }
                    }
                }
            }


        }


    }

}