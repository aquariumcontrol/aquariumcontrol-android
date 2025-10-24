package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.entry.mockMainViewModel
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedProfileScheduleScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedProfileScheduleScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "feed"

    @Test
    fun feedScheduleScreenDarkTest() {
        val mockFeedProfileImport = FeedProfileImport()
        mockFeedProfileImport.profileId = 1
        mockFeedProfileImport.profileName = "Test Profile 1"

        composeTestRule.setContent {
            FeedProfileScheduleScreen(
                mainViewModel = mockMainViewModel,
                feedProfileId = 1,
                feedScheduleViewModelFactory = mockFeedScheduleViewModelFactory,
                feedProfilesViewModelFactory = mockFeedProfilesViewModelFactory,
                isNewScheduleEntry = false,
                millisSinceEpoch = 0,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_schedule_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun feedScheduleScreenLightTest() {
        val mockFeedProfileImport = FeedProfileImport()
        mockFeedProfileImport.profileId = 1
        mockFeedProfileImport.profileName = "Test Profile 1"

        composeTestRule.setContent {
            FeedProfileScheduleScreen(
                mainViewModel = mockMainViewModel,
                feedProfileId = 1,
                feedScheduleViewModelFactory = mockFeedScheduleViewModelFactory,
                feedProfilesViewModelFactory = mockFeedProfilesViewModelFactory,
                isNewScheduleEntry = false,
                millisSinceEpoch = 0,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_schedule_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
