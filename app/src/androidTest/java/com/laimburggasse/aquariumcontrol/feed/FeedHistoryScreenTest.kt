package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryScreen
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.history.MockFeedHistoryRepository
import com.laimburggasse.aquariumcontrol.feed.history.mockFeedHistoryEvents
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedHistoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "feed"

    val mockFeedHistoryRepositorySuccess = MockFeedHistoryRepository(
        mockData = DataFetchResult.Success(data = mockFeedHistoryEvents)
    )

    val mockFeedHistoryRepositoryEmpty = MockFeedHistoryRepository(
        mockData = DataFetchResult.Success(data = emptyList())
    )

    val mockMainViewModel = MockMainViewModel()

    @Test
    fun feedProfileHistoryScreenDarkEmptyTest() {
        composeTestRule.setContent {
            FeedHistoryScreen(
                showFullWeek = false,
                mainViewModel = mockMainViewModel,
                feedHistoryViewModelFactory = FeedHistoryViewModelFactory(
                    feedHistoryRepository = mockFeedHistoryRepositoryEmpty
                ),
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
                feedHistoryViewModelFactory = FeedHistoryViewModelFactory(
                    feedHistoryRepository = mockFeedHistoryRepositoryEmpty
                ),
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
                feedHistoryViewModelFactory = FeedHistoryViewModelFactory(
                    feedHistoryRepository = mockFeedHistoryRepositorySuccess
                ),
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
                feedHistoryViewModelFactory = FeedHistoryViewModelFactory(
                    feedHistoryRepository = mockFeedHistoryRepositorySuccess
                ),
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
