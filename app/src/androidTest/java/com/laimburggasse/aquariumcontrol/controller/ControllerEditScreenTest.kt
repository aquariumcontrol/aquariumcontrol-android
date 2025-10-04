package com.laimburggasse.aquariumcontrol.controller

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ControllerEditScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    @Test
    fun controllerEditScreenDarkTest() {
        composeTestRule.setContent {
            ControllerEditScreen(state = ControllerState(), onEvent = {}, darkTheme = true)
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "controller_edit_screen_dark",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun controllerEditScreenLightTest() {
        composeTestRule.setContent {
            ControllerEditScreen(state = ControllerState(), onEvent = {}, darkTheme = false)
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "controller_edit_screen_light",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
