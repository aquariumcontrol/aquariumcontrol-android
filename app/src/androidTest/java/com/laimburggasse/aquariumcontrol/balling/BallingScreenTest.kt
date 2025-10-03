package com.laimburggasse.aquariumcontrol.balling

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BallingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private fun performScreenshotTest(
        goldenName: String,
        content: @Composable () -> Unit) {
        composeTestRule.setContent {
            content()
        }
        composeTestRule.waitForIdle()
        val imageBitmap = composeTestRule.onRoot().captureToImage()

        if (recordMode) {
            ScreenshotTestHelper.recordBitmap(imageBitmap, goldenName)
            // After recording, we might want to fail the test to remind the user to copy the file
            // and switch back to verify mode, or handle this in a more sophisticated way.
            // For this example, we'll let it pass but print a strong reminder.
            println("RECORD MODE: Screenshot for '$goldenName' captured. Ensure it's copied to assets and switch recordMode to false.")
        } else {

            val matches = ScreenshotTestHelper.verifyBitmap(
                imageBitmap,
                "balling",
                goldenName
            )

            assert(matches) {
                "Screenshot '$goldenName' does not match the golden image. " +
                        "If this is an intentional change, re-record the screenshot by setting recordMode=true. " +
                        "Debug images might be in: ${File(ScreenshotTestHelper.getDeviceOutputDirectory(), "debug_diffs").absolutePath}"
            }
        }
    }

    @Test
    fun BallingScreenDarkTest() {
        performScreenshotTest(
            goldenName = "balling_screen_dark"
        ) {
            val mockBallingProfile1 = BallingProfile(pumpId = 1, volume = 2.1, label = "mock1")
            val mockBallingProfile2 = BallingProfile(pumpId = 2, volume = 3.1, label = "mock2")
            val mockBallingProfiles = listOf(mockBallingProfile1, mockBallingProfile2)
            BallingScreen(navHostController = null, mockBallingProfiles)
        }
    }

    @Test
    fun BallingScreenLightTest() {
        performScreenshotTest(
            goldenName = "balling_screen_light"
        ) {
            val mockBallingProfile1 = BallingProfile(pumpId = 1, volume = 2.1, label = "mock1")
            val mockBallingProfile2 = BallingProfile(pumpId = 2, volume = 3.1, label = "mock2")
            val mockBallingProfiles = listOf(mockBallingProfile1, mockBallingProfile2)
            BallingScreen(navHostController = null, mockBallingProfiles)
        }
    }

}
