package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedProfileScheduleScreen
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntryImport
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

    fun generateMockFeedProfile(): FeedProfile {
        val mockFeedProfileImport = FeedProfileImport()
        mockFeedProfileImport.profileId = 1
        mockFeedProfileImport.profileName = "Test Profile 1"

        return FeedProfile(mockFeedProfileImport)
    }

    fun generateMockFeedScheduleEntry(): FeedScheduleEntry {
        return FeedScheduleEntryImport(
            timestampString = "2024-02-16 12:00:00",
            profileName = "MockProfile",
            isDaily = 1
        ).transformToFeedScheduleEntry()
    }

    fun generateMockMainViewModel(): MockMainViewModel {
        val mockMainViewModel = MockMainViewModel()
        mockMainViewModel.updateSelectedFeedProfile(feedProfile = generateMockFeedProfile())
        mockMainViewModel.updateSelectedFeedScheduleEntry(feedScheduleEntry = generateMockFeedScheduleEntry())
        return mockMainViewModel
    }

    @Test
    fun feedScheduleScreenDarkTest() {
        composeTestRule.setContent {
            FeedProfileScheduleScreen(
                mainViewModel = generateMockMainViewModel(),
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
        composeTestRule.setContent {
            FeedProfileScheduleScreen(
                mainViewModel = generateMockMainViewModel(),
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
