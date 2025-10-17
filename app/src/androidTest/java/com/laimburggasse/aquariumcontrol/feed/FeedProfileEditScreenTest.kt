package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditScreen
import com.laimburggasse.aquariumcontrol.feed.edit.mockFeedProfileEditViewModelFactory
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedProfileEditScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "feed"

    val mockMainViewModel = MockMainViewModel()

    @Test
    fun feedEditScreenDarkTest() {
        composeTestRule.setContent {
            FeedProfileEditScreen(
                mainViewModel = mockMainViewModel,
                darkTheme = true,
                feedProfileEditViewModelFactory = mockFeedProfileEditViewModelFactory,
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_edit_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun feedEditScreenLightTest() {
        composeTestRule.setContent {
            FeedProfileEditScreen(
                mainViewModel = mockMainViewModel,
                feedProfileEditViewModelFactory = mockFeedProfileEditViewModelFactory,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_edit_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
