package com.laimburggasse.aquariumcontrol.heating

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.ventilation.VentilationScreen
import com.laimburggasse.aquariumcontrol.ventilation.VentilationSetVals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VentilationScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "ventilation"

    @Test
    fun heatingScreenDarkTest() {
        composeTestRule.setContent {
            VentilationScreen(
                mockVentilationSetVals = VentilationSetVals(
                    ventilationSwitchOffTemp = 23.5,
                    ventilationSwitchOnTemp = 27.5
                ),
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "ventilation_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun heatingScreenLightTest() {
        composeTestRule.setContent {
            VentilationScreen(
                mockVentilationSetVals = VentilationSetVals(
                    ventilationSwitchOffTemp = 23.5,
                    ventilationSwitchOnTemp = 27.5
                ),
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "ventilation_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
