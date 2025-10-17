package com.laimburggasse.aquariumcontrol.refill

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RefillScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "refill"

    @Test
    fun refillScreenDarkTest() {
        composeTestRule.setContent {
            RefillScreen(
                mainViewModel = mockMainViewModel,
                darkTheme = true,
                refillViewModelFactory = mockRefillViewModelFactory
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "refill_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun refillScreenLightTest() {
        composeTestRule.setContent {
            RefillScreen(
                mainViewModel = mockMainViewModel,
                darkTheme = false,
                refillViewModelFactory = mockRefillViewModelFactory
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "refill_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
