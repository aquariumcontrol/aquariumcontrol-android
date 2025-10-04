package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditScreen
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

    fun generateMockFeedProfile(): FeedProfile {
        val mockFeedProfileImport = FeedProfileImport()

        mockFeedProfileImport.profileId = 1
        mockFeedProfileImport.profileName = "Test Profile"
        mockFeedProfileImport.profileId = 1
        mockFeedProfileImport.profileName = "Test Profile"
        mockFeedProfileImport.pause01duration = 1000
        mockFeedProfileImport.pause02duration = 1000
        mockFeedProfileImport.pause03duration = 1000
        mockFeedProfileImport.pause04duration = 1000
        mockFeedProfileImport.pause05duration = 1000
        mockFeedProfileImport.pause06duration = 1000
        mockFeedProfileImport.pause07duration = 1000
        mockFeedProfileImport.pause08duration = 1000
        mockFeedProfileImport.pause09duration = 1000
        mockFeedProfileImport.pause10duration = 1000
        mockFeedProfileImport.feed01duration = 1000
        mockFeedProfileImport.feed02duration = 1000
        mockFeedProfileImport.feed03duration = 1000
        mockFeedProfileImport.feed04duration = 1000
        mockFeedProfileImport.feed05duration = 1000
        mockFeedProfileImport.feed06duration = 1000
        mockFeedProfileImport.feed07duration = 1000
        mockFeedProfileImport.feed08duration = 1000
        mockFeedProfileImport.feed09duration = 1000
        mockFeedProfileImport.feed10duration = 1000
        mockFeedProfileImport.pause01skimmer = 1
        mockFeedProfileImport.pause02skimmer = 1
        mockFeedProfileImport.pause03skimmer = 1
        mockFeedProfileImport.pause04skimmer = 1
        mockFeedProfileImport.pause05skimmer = 1
        mockFeedProfileImport.pause06skimmer = 1
        mockFeedProfileImport.pause07skimmer = 1
        mockFeedProfileImport.pause08skimmer = 1
        mockFeedProfileImport.pause09skimmer = 1
        mockFeedProfileImport.pause10skimmer = 0
        mockFeedProfileImport.feed01skimmer = 1
        mockFeedProfileImport.feed02skimmer = 1
        mockFeedProfileImport.feed03skimmer = 1
        mockFeedProfileImport.feed04skimmer = 1
        mockFeedProfileImport.feed05skimmer = 1
        mockFeedProfileImport.feed06skimmer = 1
        mockFeedProfileImport.feed07skimmer = 1
        mockFeedProfileImport.feed08skimmer = 1
        mockFeedProfileImport.feed09skimmer = 1
        mockFeedProfileImport.feed10skimmer = 0
        mockFeedProfileImport.pause01mainPump1 = 1
        mockFeedProfileImport.pause02mainPump1 = 1
        mockFeedProfileImport.pause03mainPump1 = 1
        mockFeedProfileImport.pause04mainPump1 = 1
        mockFeedProfileImport.pause05mainPump1 = 1
        mockFeedProfileImport.pause06mainPump1 = 1
        mockFeedProfileImport.pause07mainPump1 = 1
        mockFeedProfileImport.pause08mainPump1 = 1
        mockFeedProfileImport.pause09mainPump1 = 0
        mockFeedProfileImport.pause10mainPump1 = 0
        mockFeedProfileImport.feed01mainPump1 = 1
        mockFeedProfileImport.feed02mainPump1 = 1
        mockFeedProfileImport.feed03mainPump1 = 1
        mockFeedProfileImport.feed04mainPump1 = 1
        mockFeedProfileImport.feed05mainPump1 = 1
        mockFeedProfileImport.feed06mainPump1 = 1
        mockFeedProfileImport.feed07mainPump1 = 1
        mockFeedProfileImport.feed08mainPump1 = 1
        mockFeedProfileImport.feed09mainPump1 = 0
        mockFeedProfileImport.feed10mainPump1 = 0
        mockFeedProfileImport.pause01mainPump2 = 1
        mockFeedProfileImport.pause02mainPump2 = 1
        mockFeedProfileImport.pause03mainPump2 = 1
        mockFeedProfileImport.pause04mainPump2 = 1
        mockFeedProfileImport.pause05mainPump2 = 1
        mockFeedProfileImport.pause06mainPump2 = 1
        mockFeedProfileImport.pause07mainPump2 = 1
        mockFeedProfileImport.pause08mainPump2 = 0
        mockFeedProfileImport.pause09mainPump2 = 0
        mockFeedProfileImport.pause10mainPump2 = 0
        mockFeedProfileImport.feed01mainPump2 = 1
        mockFeedProfileImport.feed02mainPump2 = 1
        mockFeedProfileImport.feed03mainPump2 = 1
        mockFeedProfileImport.feed04mainPump2 = 1
        mockFeedProfileImport.feed05mainPump2 = 1
        mockFeedProfileImport.feed06mainPump2 = 1
        mockFeedProfileImport.feed07mainPump2 = 1
        mockFeedProfileImport.feed08mainPump2 = 0
        mockFeedProfileImport.feed09mainPump2 = 0
        mockFeedProfileImport.feed10mainPump2 = 0
        mockFeedProfileImport.pause01auxPump1 = 1
        mockFeedProfileImport.pause02auxPump1 = 1
        mockFeedProfileImport.pause03auxPump1 = 1
        mockFeedProfileImport.pause04auxPump1 = 1
        mockFeedProfileImport.pause05auxPump1 = 1
        mockFeedProfileImport.pause06auxPump1 = 1
        mockFeedProfileImport.pause07auxPump1 = 0
        mockFeedProfileImport.pause08auxPump1 = 0
        mockFeedProfileImport.pause09auxPump1 = 0
        mockFeedProfileImport.pause10auxPump1 = 0
        mockFeedProfileImport.feed01auxPump1 = 1
        mockFeedProfileImport.feed02auxPump1 = 1
        mockFeedProfileImport.feed03auxPump1 = 1
        mockFeedProfileImport.feed04auxPump1 = 1
        mockFeedProfileImport.feed05auxPump1 = 1
        mockFeedProfileImport.feed06auxPump1 = 1
        mockFeedProfileImport.feed07auxPump1 = 0
        mockFeedProfileImport.feed08auxPump1 = 0
        mockFeedProfileImport.feed09auxPump1 = 0
        mockFeedProfileImport.feed10auxPump1 = 0
        mockFeedProfileImport.pause01auxPump2 = 1
        mockFeedProfileImport.pause02auxPump2 = 1
        mockFeedProfileImport.pause03auxPump2 = 1
        mockFeedProfileImport.pause04auxPump2 = 1
        mockFeedProfileImport.pause05auxPump2 = 1
        mockFeedProfileImport.pause06auxPump2 = 1
        mockFeedProfileImport.pause07auxPump2 = 0
        mockFeedProfileImport.pause08auxPump2 = 0
        mockFeedProfileImport.pause09auxPump2 = 0
        mockFeedProfileImport.pause10auxPump2 = 0
        mockFeedProfileImport.feed01auxPump2 = 1
        mockFeedProfileImport.feed02auxPump2 = 1
        mockFeedProfileImport.feed03auxPump2 = 1
        mockFeedProfileImport.feed04auxPump2 = 1
        mockFeedProfileImport.feed05auxPump2 = 1
        mockFeedProfileImport.feed06auxPump2 = 1
        mockFeedProfileImport.feed07auxPump2 = 0
        mockFeedProfileImport.feed08auxPump2 = 0
        mockFeedProfileImport.feed09auxPump2 = 0
        mockFeedProfileImport.feed10auxPump2 = 0

        return FeedProfile(mockFeedProfileImport)
    }

    @Test
    fun feedEditScreenDarkTest() {
        composeTestRule.setContent {
            FeedProfileEditScreen(
                navHostController = null,
                feedProfile = generateMockFeedProfile(),
                darkTheme = true
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
                navHostController = null,
                feedProfile = generateMockFeedProfile(),
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
