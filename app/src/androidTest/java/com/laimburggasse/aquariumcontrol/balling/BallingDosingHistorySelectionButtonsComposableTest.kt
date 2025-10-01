package com.laimburggasse.aquariumcontrol.balling

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BallingDosingHistorySelectionButtonsComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    @Composable
    private fun TestSurface(
        applyTheme: Boolean = true,
        useDarkTheme: Boolean = true,
        content: @Composable () -> Unit) {
        if (applyTheme) {
            AquariumControllerTheme(darkTheme = useDarkTheme) { // Use your app's actual theme
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background) // Use theme background
                        .padding(8.dp)
                ) {
                    content()
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .background(Color.White) // Fallback background if no theme
                    .padding(8.dp)
            ) {
                content()
            }
        }
    }

    private fun performScreenshotTest(
        goldenName: String,
        useDarkTheme: Boolean,
        content: @Composable () -> Unit) {
        composeTestRule.setContent {
            TestSurface(useDarkTheme = useDarkTheme) {
                content()
            }
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
                "balling_dosing_history_selection_buttons_composable_test", goldenName
            )

            assert(matches) {
                "Screenshot '$goldenName' does not match the golden image. " +
                        "If this is an intentional change, re-record the screenshot by setting recordMode=true. " +
                        "Debug images might be in: ${File(ScreenshotTestHelper.getDeviceOutputDirectory(), "debug_diffs").absolutePath}"
            }
        }
    }

    @Test
    fun defaultAppearance_darkMode() { // Renamed for clarity
        performScreenshotTest(
            goldenName = "balling_selection_buttons_default_dark", // Unique name for dark mode
            useDarkTheme = true
        ) {
            // Simulate a common parent layout (like your portrait preview)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BallingDosingHistorySelectionButtonsComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    navHostController = null // NavHostController is not used for visual output
                )
            }
        }
    }

    @Test
    fun defaultAppearance_lightMode() {
        performScreenshotTest(
            goldenName = "balling_selection_buttons_default_light", // Unique name for light mode
            useDarkTheme = false
        ) {
            // Simulate a common parent layout (like your portrait preview)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BallingDosingHistorySelectionButtonsComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    navHostController = null // NavHostController is not used for visual output
                )
            }
        }
    }

    // You can add more tests for different states (e.g., selected button) in both themes
    @Test
    fun selectedButton_darkMode() {
        performScreenshotTest(
            goldenName = "balling_selection_buttons_selected_dark",
            useDarkTheme = true
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // You might need to adjust your Composable or pass parameters
                // to simulate a button being selected for the screenshot.
                // This is just a placeholder for the content.
                BallingDosingHistorySelectionButtonsComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    navHostController = null,
                    // example: initiallySelectedButton = SomeButtonType.HISTORY (if your composable supports this)
                )
            }
        }
    }

    @Test
    fun selectedButton_lightMode() {
        performScreenshotTest(
            goldenName = "balling_selection_buttons_selected_light",
            useDarkTheme = false
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BallingDosingHistorySelectionButtonsComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    navHostController = null,
                    // example: initiallySelectedButton = SomeButtonType.HISTORY
                )
            }
        }
    }
}
