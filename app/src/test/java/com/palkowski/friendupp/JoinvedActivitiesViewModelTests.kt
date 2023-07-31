package com.palkowski.friendupp

import com.palkowski.friendupp.Activities.JoinedActivitiesViewModel
import com.palkowski.friendupp.di.ActivityRepository
import com.palkowski.friendupp.model.Activity
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.model.SocialException
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.lang.Exception

// Reusable JUnit4 TestRule to override the Main dispatcher
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class JoinedActivitiesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Mock
    private lateinit var activitiesRepo: ActivityRepository

    private lateinit var viewModel: JoinedActivitiesViewModel
    private lateinit var testCoroutineDispatcher: TestCoroutineDispatcher
    private lateinit var testCoroutineScope: TestCoroutineScope

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        testCoroutineDispatcher = TestCoroutineDispatcher()
        testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

        viewModel = JoinedActivitiesViewModel(activitiesRepo)
    }

    @After
    fun teardown() {
        testCoroutineScope.cleanupTestCoroutines()
    }

    @Test
    fun `test getJoinedActivities success`() = testCoroutineScope.runBlockingTest {
        // Given
        val activityId = "activity_id"
        val mockActivities = listOf(Activity(image=null,
            id = "",
            title = "Hiking Adventure",
            start_time="",
            creator_id="",
            description="Join us for an exciting hiking adventure in the mountains!",
            creator_username="",
            creator_name="",
            creator_profile_picture="",
            end_time="",
            geoHash=null,
            lat=null,
            lng=null,
            custom_location=null,
            minUserCount= 0,
            maxUserCount=100,
            disableChat = false,
            likes=0,
            invited_users=ArrayList(),
            participants_profile_pictures=HashMap(),
            participants_usernames=HashMap(),
            creation_time="",
            location=null,
            pictures=HashMap(),
            enableActivitySharing = false,
            disableNotification = false,
            privateChat = false,
            public=false,
            participants_ids =ArrayList(),
            requests_ids =ArrayList(),
            participantConfirmation=false,
            requests=ArrayList(),
            reports=0,
            tags=ArrayList(),
            date =null,
            bookmarked=ArrayList()))

        val mockResponse = Response.Success(mockActivities)

        // Stub the repository's behavior
        Mockito.`when`(activitiesRepo.getJoinedActivities(activityId)).thenReturn(flowOf(mockResponse))
        // When
        viewModel.getJoinedActivities(activityId)


        // Then
        assertEquals(mockActivities, viewModel.joinedActivitiesListState.value)
        assertEquals(mockResponse, viewModel.joinedActivitiesResponse.value)
    }

    @Test
    fun `test getJoinedActivities failure`() = runBlockingTest {
        // Given
        val activityId = "activity_id"
        val mockResponse = Response.Failure(e = SocialException(message = "Error",e= Exception("Error")))

        // Stub the repository's behavior
        Mockito.`when`(activitiesRepo.getJoinedActivities(activityId)).thenReturn(flow { emit(mockResponse) })

        // When
        viewModel.getJoinedActivities(activityId)

        // Then
        assertEquals(emptyList<Activity>(), viewModel.joinedActivitiesListState.value)
        assertEquals(Response.Success<List<Activity>>(emptyList()), viewModel.joinedActivitiesResponse.value)
    }

}
