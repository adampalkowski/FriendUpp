package com.example.friendupp.Create

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.friendupp.ChatUi.ButtonAdd
import com.example.friendupp.Components.CalendarComponent
import com.example.friendupp.Components.NameEditText
import com.example.friendupp.Home.ActionButton
import com.example.friendupp.Home.Option
import com.example.friendupp.Login.TextFieldState
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.ui.theme.SocialTheme


sealed class CreateEvents{
    class Create(val title:String,val description: String,val public:Boolean,val startTime:String,val endTime: String) :CreateEvents()
    object OpenCamera :CreateEvents()
    object Settings :CreateEvents()
    object GoBack :CreateEvents()
}

@Composable
fun CreateScreen(modifier: Modifier, onEvent:(CreateEvents)->Unit={},photo:String?) {

    val titleState by rememberSaveable(stateSaver = TitleStateSaver) {
        mutableStateOf(TitleState())
    }
    val descriptionState by rememberSaveable(stateSaver = DescriptionStateSaver) {
        mutableStateOf(DescriptionState())
    }
    var selectedOption by rememberSaveable { mutableStateOf(Option.PUBLIC) }
    BackHandler(true) {
        onEvent(CreateEvents.GoBack)
    }

        Box(modifier = Modifier.fillMaxSize()) {
            Column() {
                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)) {

                    CreateTopBar(onClick = {        onEvent(CreateEvents.GoBack)
                    },selectedOption=selectedOption,onPublic={selectedOption=Option.PUBLIC},onFriends={selectedOption=Option.FRIENDS})
                    TitleAndDescription(titleState,descriptionState)
                    Spacer(modifier = Modifier.height(8.dp))

                    DateAndTime()

                }
                BottomBarCreate(photo=photo,onClick = {onEvent(CreateEvents.Settings)},
                    createClicked = { onEvent(CreateEvents.Create(description =descriptionState.text, title = titleState.text, startTime = "", endTime = "", public = false )) },openCamera={onEvent(CreateEvents.OpenCamera)})
            }
        }

}

@Composable
fun TitleAndDescription(titleState: TextFieldState,descriptionState: TextFieldState) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        CreateHeading("Title & description", icon = com.example.friendupp.R.drawable.ic_edit)
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Title", textState = titleState
        )
        Spacer(modifier = Modifier.height(12.dp))
        NameEditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            focusRequester = focusRequester,
            focus = false,
            onFocusChange = { focusState ->

            }, label = "Description", textState = descriptionState
        )

    }
}

@Composable
fun DateAndTime() {
    Column {
        CreateHeading("Date", icon = com.example.friendupp.R.drawable.ic_date,)
        CalendarComponent(onDatePicked={
           })
            TimePicker(startTime = "20:00", endTime = "22:00")

    }
}

@Composable
fun BottomBarCreate(onClick: () -> Unit,createClicked:()->Unit={},openCamera:()->Unit={},photo: String?) {
    Row(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
        if (photo==null || photo.isNotEmpty()){
            ButtonAdd(onClick = openCamera, icon = com.example.friendupp.R.drawable.ic_add_image)


        }else{
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo)
                    .crossfade(true)
                    .build(),
                contentDescription =null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .size(48.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        ButtonAdd(onClick = onClick, icon = com.example.friendupp.R.drawable.ic_filte_300)
        Spacer(modifier = Modifier.weight(1f))
        CreateButton("Create", createClicked = createClicked)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateButton(text: String,createClicked:()->Unit={},modifier:Modifier=Modifier) {
    Card(modifier = Modifier, onClick = createClicked, shape = RoundedCornerShape(8.dp)) {
        Box(
            modifier = Modifier.background(SocialTheme.colors.textInteractive),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 48.dp),
                text = text,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoBackButton(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            1.dp,
            SocialTheme.colors.uiBorder
        )
    ) {
        Box(
            modifier = Modifier.background(SocialTheme.colors.uiBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 48.dp),
                text = text,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = SocialTheme.colors.textPrimary.copy(0.8f)
                )
            )
        }
    }
}


@Composable
fun BottomBarSettings(onClick: () -> Unit) {
    Row(
        Modifier
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.End
    ) {

        CreateButton("Create")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(startTime: String, endTime: String,lockStartTime:Boolean=false) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "From",
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            color = SocialTheme.colors.textPrimary.copy(0.25f)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SocialTheme.colors.uiBorder)
        ) {
            Box(
                modifier = Modifier.background(SocialTheme.colors.uiBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    text = startTime,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Lexend
                    ),
                    color = SocialTheme.colors.textPrimary
                )
            }
        }
        Spacer(modifier = Modifier.width(36.dp))
        Icon(
            painter = painterResource(id = com.example.friendupp.R.drawable.ic_long_right),
            contentDescription = null,
            tint = SocialTheme.colors.iconPrimary
        )
        Spacer(modifier = Modifier.width(36.dp))

        Text(
            text = "To",
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            color = SocialTheme.colors.textPrimary.copy(0.25f)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SocialTheme.colors.uiBorder)
        ) {
            Box(
                modifier = Modifier.background(SocialTheme.colors.uiBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    text = endTime,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Lexend
                    ),
                    color = SocialTheme.colors.textPrimary
                )
            }
        }

    }
}


@Composable
fun CreateHeading(text: String, icon: Int, tip: Boolean = false, description: String = "") {
    var displayDesription by remember {
        mutableStateOf(false)
    }

    var color = SocialTheme.colors
        .textPrimary.copy(0.8f)
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {


        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = color
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = color,
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            if (tip) {
                IconButton(onClick = { displayDesription = !displayDesription }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = com.example.friendupp.R.drawable.ic_ligh),
                        contentDescription = null,
                        tint = SocialTheme.colors.iconPrimary.copy(0.5f)
                    )

                }
            }
        }
        AnimatedVisibility(visible = displayDesription, enter = slideInVertically(), exit = slideOutVertically  ()) {
            Text(
                text = description, color = SocialTheme.colors.textPrimary.copy(0.5f),
                style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                )
            )
        }

    }
}

@Composable
fun SettingsTopBar(onClick: () -> Unit) {
    val dividerColor = SocialTheme.colors.uiBorder
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .horizontalScroll(
                rememberScrollState()
            )
    ) {
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(dividerColor)
        )

        Text(
            modifier = Modifier.padding(bottom = 6.dp),
            text = "Additional settings",
            style = TextStyle(
                fontFamily = Lexend,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = SocialTheme.colors.textPrimary.copy(0.8f)
            )
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(dividerColor)
        )


    }
}

@Composable
fun CreateTopBar(onClick: () -> Unit,selectedOption:Option,onFriends: () -> Unit,onPublic: () -> Unit) {
    val context = LocalContext.current

    val dividerColor = SocialTheme.colors.uiBorder
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .horizontalScroll(
                rememberScrollState()
            )
    ) {
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        ButtonAdd(onClick = onClick, icon = com.example.friendupp.R.drawable.ic_x)
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        Text(
            modifier = Modifier.padding(bottom = 6.dp),
            text = "Create",
            style = TextStyle(
                fontFamily = Lexend,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = SocialTheme.colors.textPrimary.copy(0.8f)
            )
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(option = Option.FRIENDS,
            isSelected = selectedOption == Option.FRIENDS,
            onClick = onFriends)
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(1.dp)
                .background(dividerColor)
        )
        ActionButton(option = Option.PUBLIC,
            isSelected = selectedOption == Option.PUBLIC,
            onClick = onPublic)
        Spacer(
            modifier = Modifier
                .width(48.dp)
                .height(1.dp)
                .background(dividerColor)
        )

    }

}


