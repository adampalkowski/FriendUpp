package com.palkowski.friendupp
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import com.palkowski.friendupp.Activities.JoinedActivitiesViewModel
import com.palkowski.friendupp.Drawer.JoinedActivitiesScreen
import com.palkowski.friendupp.Drawer.JoinedActivitiesScreenEvents
import com.palkowski.friendupp.ui.theme.FriendUppTheme
import dagger.hilt.android.testing.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class LandingScreenTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var joinedActivitiesViewModel: JoinedActivitiesViewModel
    @Before
    fun init() {
        hiltRule.inject()
    }


    @Test
    fun MyTest() {
        composeTestRule.setContent {
            FriendUppTheme {
                 joinedActivitiesViewModel = hiltViewModel()
                JoinedActivitiesScreen(
                    modifier = Modifier.safeDrawingPadding(),
                    activitiesEvents = { event ->

                    },
                    onEvent = { event ->
                        when (event) {
                            is JoinedActivitiesScreenEvents.GetMoreJoinedActivities -> {
                                joinedActivitiesViewModel.getMoreJoinedActivities(event.id)
                            }
                        }
                    },
                    joinedActivitiesResponse = joinedActivitiesViewModel.joinedActivitiesResponse.value
                )
            }
        }

        // Your assertions here
    }
}
