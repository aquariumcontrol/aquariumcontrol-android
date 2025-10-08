package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryEvent
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryScreen
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryUiState
import com.laimburggasse.aquariumcontrol.feed.history.MockFeedHistoryViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedProfileHistoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "feed"

    val mockEvents = listOf(
        FeedHistoryEvent(
            timestamp = "2025-01-18 12:00:00",
            feederOnTime = 20000,
            profileName = "Mock feed profile 1",
            idx = 1
        ),
        FeedHistoryEvent(
            timestamp = "2025-01-18 12:00:00",
            feederOnTime = 10000,
            profileName = "Mock feed profile 2",
            idx = 2
        ),
    )

    val mockFeedHistoryUiStateSuccess = FeedHistoryUiState.Success(feedHistory = mockEvents)

    val mockFeedHistoryViewModelSuccess = MockFeedHistoryViewModel(mockFeedHistoryUiState = mockFeedHistoryUiStateSuccess)

    val mockMainViewModel = MockMainViewModel()

    val mockFeedHistoryUiStateEmpty = FeedHistoryUiState.Empty

    val mockFeedHistoryViewModelEmpty = MockFeedHistoryViewModel(mockFeedHistoryUiState = mockFeedHistoryUiStateEmpty)

    @Test
    fun feedProfileHistoryScreenDarkEmptyTest() {
        composeTestRule.setContent {
            FeedHistoryScreen(
                showFullWeek = false,
                mainViewModel = mockMainViewModel,
                feedHistoryViewModel = mockFeedHistoryViewModelEmpty,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_history_screen_empty_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun feedProfileHistoryScreenLightEmptyTest() {
        composeTestRule.setContent {
            FeedHistoryScreen(
                showFullWeek = false,
                mainViewModel = mockMainViewModel,
                feedHistoryViewModel = mockFeedHistoryViewModelEmpty,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_history_screen_empty_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
    @Test
    fun feedProfileHistoryScreenDarkDataTest() {
        composeTestRule.setContent {
            FeedHistoryScreen(
                showFullWeek = false,
                mainViewModel = mockMainViewModel,
                feedHistoryViewModel = mockFeedHistoryViewModelSuccess,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_history_screen_data_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun feedProfileHistoryScreenLightDataTest() {
        composeTestRule.setContent {
            FeedHistoryScreen(
                showFullWeek = false,
                mainViewModel = mockMainViewModel,
                feedHistoryViewModel = mockFeedHistoryViewModelSuccess,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_history_screen_data_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
