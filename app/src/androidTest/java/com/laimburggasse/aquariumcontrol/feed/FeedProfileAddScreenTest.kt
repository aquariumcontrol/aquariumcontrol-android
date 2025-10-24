package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddScreen
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.entry.mockFeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.entry.mockMainViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

val mockFeedProfileAddViewModelFactory = FeedProfileAddViewModelFactory(
    feedProfilesRepository = mockFeedProfilesRepository
)

@RunWith(value = AndroidJUnit4::class)
class FeedProfileAddScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "feed"

    @Test
    fun feedAddScreenDarkTest() {
        composeTestRule.setContent {
            FeedProfileAddScreen(
                darkTheme = true,
                mainViewModel = mockMainViewModel,
                feedProfileAddViewModelFactory = mockFeedProfileAddViewModelFactory
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_add_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun feedAddScreenLightTest() {
        composeTestRule.setContent {
            FeedProfileAddScreen(
                darkTheme = false,
                mainViewModel = mockMainViewModel,
                feedProfileAddViewModelFactory = mockFeedProfileAddViewModelFactory
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_add_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
}
