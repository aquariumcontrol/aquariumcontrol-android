package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleUiState
import com.laimburggasse.aquariumcontrol.feed.schedule.MockFeedScheduleViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "feed"

    @Test
    fun feedScreenDarkTest() {
        val mockFeedProfilesViewModel = MockFeedProfilesViewModel(
            feedProfilesUiState = FeedProfilesUiState.Error(errorMessage = "Error loading feed profiles"),
            isRefreshing = false,
            feedProfiles = emptyList()
        )

        val mockFeedScheduleViewModel = MockFeedScheduleViewModel(
            feedScheduleUiState = FeedScheduleUiState.Error(errorMessage = "Error loading feed schedule"),
            isRefreshing = false,
            feedScheduleEntries = emptyList()
        )

        mockMainViewModel.setMyFeedScheduleViewModel(mockFeedScheduleViewModel)
        mockMainViewModel.setMyFeedProfileViewModel(mockFeedProfilesViewModel)

        composeTestRule.setContent {
            FeedScreen(
                mainViewModel = mockMainViewModel,
                feedProfilesViewModel = mockFeedProfilesViewModel,
                feedScheduleViewModel = mockFeedScheduleViewModel,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun feedScreenLightTest() {
        val mockFeedProfilesViewModel = MockFeedProfilesViewModel(
            feedProfilesUiState = FeedProfilesUiState.Error(errorMessage = "Error loading feed profiles"),
            isRefreshing = false,
            feedProfiles = emptyList()
        )

        val mockFeedScheduleViewModel = MockFeedScheduleViewModel(
            feedScheduleUiState = FeedScheduleUiState.Error(errorMessage = "Error loading feed schedule"),
            isRefreshing = false,
            feedScheduleEntries = emptyList()
        )

        mockMainViewModel.setMyFeedScheduleViewModel(mockFeedScheduleViewModel)
        mockMainViewModel.setMyFeedProfileViewModel(mockFeedProfilesViewModel)

        composeTestRule.setContent {
            FeedScreen(
                mainViewModel = mockMainViewModel,
                feedProfilesViewModel = mockFeedProfilesViewModel,
                feedScheduleViewModel = mockFeedScheduleViewModel,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
