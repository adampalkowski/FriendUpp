package com.example.friendupp.Profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.friendupp.R
import com.example.friendupp.model.User
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme

sealed class EditProfileEvents{
    object GoBack:EditProfileEvents()
    object ConfirmChanges:EditProfileEvents()
    object OpenCamera:EditProfileEvents()
}

@Composable
fun EditProfile(modifier: Modifier, goBack: () -> Unit,user:User, onEvent: (EditProfileEvents) -> Unit) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    BackHandler(true) {
        goBack()
    }

   Column(modifier = modifier
       .fillMaxSize()
       .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            ScreenHeading(title = "Edit profile", backButton = true,
                backIcon = com.example.friendupp.R.drawable.ic_back, onBack = {goBack()}) {
                Row(Modifier,verticalAlignment = Alignment.CenterVertically){
                    ButtonAdd(icon = R.drawable.ic_email, onClick = {})
                    Spacer(modifier = Modifier
                        .background(SocialTheme.colors.uiBorder)
                        .width(16.dp))
                    ButtonAdd(icon = R.drawable.ic_password, onClick = {})

                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            EditProfileInfo(name=user.name?: "",username=user.username?: "",imageUrl=user.pictureUrl?: "",onClick={onEvent(EditProfileEvents.OpenCamera)})
            Spacer(modifier = Modifier.height(24.dp))
            EditInfoContent(name=user.name?: "",username=user.username?: "", biography =user.biography, location =user.location )
            ActivityPreferences()



            CreateButton("Confirm changes", modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp), disabled = false, createClicked = {onEvent(EditProfileEvents.ConfirmChanges)})
            Spacer(modifier = Modifier.height(120.dp)) }


}

@Composable
fun ActivityPreferences() {
    Column() {
        CreateHeading(text = "Activity preferences", icon = R.drawable.ic_tag,tip=false, description = "")
        FilterList(    tags= SnapshotStateList(),
        onSelected={},
        onDeSelected={})
    }
}

@Composable
fun EditProfileInfo(name: String, username: String, imageUrl: String, onClick: () -> Unit) {
    val maxLength = 25
    val limitedName = if (name.length > maxLength) name.substring(0, maxLength) +"..." else name

    val maxusernameLength = 25
    val limitedUserName = if (username.length > maxLength) username.substring(0, maxLength)+"..." else username


    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)) {
        Row (verticalAlignment = Alignment.CenterVertically){
            Box(modifier = Modifier){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "stringResource(R.string.description)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
                Box(modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape).clickable(onClick = onClick)
                    .background(color = Color.Black.copy(0.6f))){
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(text =limitedName , style = TextStyle(fontFamily = Lexend , fontWeight = FontWeight.SemiBold , fontSize =22.sp ), color = SocialTheme.colors.textPrimary)
                Text(text = limitedUserName, style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Normal, fontSize =16.sp ), color = SocialTheme.colors.textPrimary.copy(0.5f))
            }

        }
    }
}

@Composable
fun EditInfoContent(name:String,username:String,biography:String,location:String){
    val focusRequester = remember { FocusRequester() }
    val nameState by rememberSaveable(stateSaver = NameStateSaver) {
        mutableStateOf(NameState())
    }
    nameState.text=name
    val usernameState by rememberSaveable(stateSaver = UsernameStateSaver) {
        mutableStateOf(UsernameState())
    }
    usernameState.text=username
    val locationState by rememberSaveable(stateSaver = LocationStateSaver) {
        mutableStateOf(LocationState())
    }
    locationState.text=location
    val biographyState by rememberSaveable(stateSaver = BiographyStateSaver) {
        mutableStateOf(BiographyState())
    }
    biographyState.text=biography
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading(text = "Information", icon = R.drawable.ic_badge,tip=false, description = "")
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Name", textState = nameState
        )
        Spacer(modifier = Modifier.height(12.dp))
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Username", textState = usernameState
        )
        Spacer(modifier = Modifier.height(12.dp))
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Biography", textState = biographyState
        )
        Spacer(modifier = Modifier.height(12.dp))
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Location", textState = locationState
        )
    }



}