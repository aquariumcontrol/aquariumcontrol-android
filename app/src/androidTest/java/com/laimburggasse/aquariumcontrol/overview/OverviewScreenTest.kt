package com.laimburggasse.aquariumcontrol.overview

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OverviewScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "overview"

    @Test
    fun overviewScreenDarkTest() {
        composeTestRule.setContent {
            OverviewScreen(
                mainViewModel = mockMainViewModel,
                overviewViewModelFactory = mockOverviewViewModelFactorySuccess,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "overview_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun overviewScreenLightTest() {
        composeTestRule.setContent {
            OverviewScreen(
                mainViewModel = mockMainViewModel,
                overviewViewModelFactory = mockOverviewViewModelFactorySuccess,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "overview_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
}
