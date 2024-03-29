package com.palkowski.friendupp.Groups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.palkowski.friendupp.Create.*
import com.palkowski.friendupp.Login.TextFieldState
import com.palkowski.friendupp.Login.textFieldStateSaver

class GroupState(
    val groupName: TextFieldState,
    val descriptionState: TextFieldState,
    val selectedOptionState: SelectedOptionState,
    var imageUrl: String,
    val tags: ArrayList<String>,

) {
    companion object {
        val Saver: Saver<GroupState, *> = Saver<GroupState, String>(
            save = { state ->
                listOf(
                    state.groupName.text,
                    state.descriptionState.text,
                    state.selectedOptionState.option,
                    state.imageUrl,
                    state.tags.joinToString(",")
                ).joinToString(",")
            },
            restore = { savedStateString ->
                val savedStates = savedStateString.split(",")
                GroupState(
                    groupName = TitleState().apply { text=savedStates[0] },
                    descriptionState = DescriptionState().apply { text=savedStates[1] },
                    selectedOptionState = SelectedOptionState(
                        initialOption = Option.valueOf(savedStates[2])
                    ),
                    imageUrl = savedStates[3],
                    tags = ArrayList( savedStates.drop(4)) // Retrieve the tags from the saved state
                )
            }
        )
    }
}
@Composable
fun rememberGroupState(
    initialName: String,
    initialOption: Option,
    initialDescription:String,
    initialImageUrl:String,
    initialTags: ArrayList<String>

): GroupState {
    val GroupNameSaver = textFieldStateSaver(TitleState())
    val titleState = rememberSaveable(saver = GroupNameSaver){ TitleState() }
    val GroupDescSaver = textFieldStateSaver(TitleState())

    val descriptionState = rememberSaveable(saver = GroupDescSaver){DescriptionState()}
    val selectedOptionState = rememberSelectedOptionState(initialOption)

    val groupState = rememberSaveable(saver = GroupState.Saver) {
        GroupState(
            groupName = titleState,
            selectedOptionState = selectedOptionState,
            descriptionState = descriptionState,
            imageUrl = initialImageUrl,
            tags = initialTags,
        )
    }

    return groupState
}