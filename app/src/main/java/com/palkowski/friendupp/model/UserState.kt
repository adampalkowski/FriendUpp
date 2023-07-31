package com.palkowski.friendupp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.palkowski.friendupp.Login.TextFieldState
import com.palkowski.friendupp.Profile.*

class UserState(
        val nameState: TextFieldState,
        val usernameState: TextFieldState,
        val bioState: TextFieldState,
        var imageUrl: String,
       val tags: ArrayList<String>
) {
        companion object {
            val Saver: Saver<UserState, *> = Saver<UserState, String>(
                save = { state ->
                    listOf(
                        state.nameState.text,
                        state.usernameState.text,
                        state.bioState.text,
                        state.imageUrl,
                        state.tags.joinToString(",")
                    ).joinToString(",")
                },
                restore = { savedStateString ->
                    val savedStates = savedStateString.split(",")
                    UserState(
                        nameState = NameState().apply { text=savedStates[0] },
                        usernameState = UsernameState().apply { text=savedStates[1] },
                        bioState = BiographyState().apply { text=savedStates[2] },
                        imageUrl = savedStates[3],
                        tags = ArrayList( savedStates.drop(4)) // Retrieve the tags from the saved state
                    )
                }
            )
        }
}

    @Composable
    fun rememberUserState(
        initialName: String,
        initialUsername: String,
        initialBio: String,
        initialImageUrl: String,
        initialTags: ArrayList<String>

    ): UserState {
        val nameState = rememberSaveable(saver = NameStateSaver){ NameState().apply { text=initialName } }
        val usernameState = rememberSaveable(saver = UsernameStateSaver){ UsernameState().apply { text=initialUsername } }
        val bioState = rememberSaveable(saver = BiographyStateSaver){ BiographyState().apply { text=initialBio } }


        val userState = rememberSaveable(saver = UserState.Saver) {
            UserState(
                nameState=nameState,
                usernameState=usernameState,
                bioState=bioState,
                imageUrl = initialImageUrl,
                tags = initialTags,
            )
        }

        return userState
    }