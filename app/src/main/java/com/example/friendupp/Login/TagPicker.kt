package com.example.friendupp.Login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.friendupp.Components.FilterList
import com.example.friendupp.Create.CreateButton
import com.example.friendupp.Create.TagsSettings

@Composable
fun TagPickerScreen(modifier: Modifier,SetTags:(List<String>)->Unit){
    val tags = remember {
       mutableListOf<String>() // Initialize with your desired value
    }
    Column() {
        TagsSettings(
            tags = tags,
            onSelected = { tags.add(it) },
            onDeSelected = { tags.remove(it) })
        Spacer(modifier = Modifier.height(24.dp))
        CreateButton(modifier = Modifier.padding(horizontal = 48.dp),text ="Set preferences" , disabled =false , createClicked = {SetTags(tags)})
    }

}