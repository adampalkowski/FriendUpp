package com.example.friendupp
import android.app.Application
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.friendupp.Activities.JoinedActivitiesViewModel
import com.example.friendupp.Drawer.JoinedActivitiesScreen
import com.example.friendupp.Drawer.JoinedActivitiesScreenEvents
import com.example.friendupp.di.ActivityRepository
import com.example.friendupp.model.Activity
import com.example.friendupp.model.Response
import com.example.friendupp.model.SocialException
import com.example.friendupp.ui.theme.FriendUppTheme
import com.google.ar.core.Config
import dagger.hilt.android.testing.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


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
