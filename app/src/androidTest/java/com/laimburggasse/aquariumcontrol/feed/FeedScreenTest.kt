package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    @Test
    fun feedScreenDarkTest() {
        composeTestRule.setContent {
            FeedScreen(
                mockFeedProfiles = createMockFeedProfiles(),
                mockFeedScheduleEntries = createMockFeedSchedule(),
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_screen_dark",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun feedScreenLightTest() {
        composeTestRule.setContent {
            FeedScreen(
                mockFeedProfiles = createMockFeedProfiles(),
                mockFeedScheduleEntries = createMockFeedSchedule(),
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_screen_light",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
