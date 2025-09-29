package com.laimburggasse.aquariumcontrol.balling

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
    private val recordMode = true

    @Composable
    private fun TestSurface(applyTheme: Boolean = true, content: @Composable () -> Unit) {
        if (applyTheme) {
            AquariumControllerTheme { // Use your app's actual theme
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

    private fun performScreenshotTest(goldenName: String, content: @Composable () -> Unit) {
        composeTestRule.setContent {
            TestSurface {
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
            val matches = ScreenshotTestHelper.verifyBitmap(imageBitmap, goldenName)
            assert(matches) {
                "Screenshot '$goldenName' does not match the golden image. " +
                        "If this is an intentional change, re-record the screenshot by setting recordMode=true. " +
                        "Debug images might be in: ${File(ScreenshotTestHelper.getDeviceOutputDirectory(), "debug_diffs").absolutePath}"
            }
        }
    }

    /**
     * Test for the default appearance, typically how it might look in a Column (portrait-like).
     * The Composable itself is a Row of Buttons, so we'll wrap it in a Column for this test.
     */
    @Test
    fun selectionButtons_defaultAppearance_looksCorrect() {
        performScreenshotTest(goldenName = "balling_selection_buttons_default") {
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

    /**
     * Test for how it might look in a Row (landscape-like), where the buttons might have weights.
     * Your composable already contains Buttons in a Row internally.
     * To test the 'modifier' part, we'll pass a modifier as if it's part of a larger Row.
     *
     * Note: Your current BallingDosingHistorySelectionButtonsComposable directly creates a Row of buttons.
     * If the intention of the `modifier.weight(0.5f)` in your landscape preview was to make *this entire composable*
     * take up half the space of an outer parent Row, then we test that specific modifier.
     * If the individual buttons inside your composable were meant to have weights, your composable's
     * internal structure would need to change. I'm assuming the former based on your preview.
     */
    @Test
    fun selectionButtons_weightedInParentRow_looksCorrect() {
        performScreenshotTest(goldenName = "balling_selection_buttons_weighted") {
            // Simulate it being one item in a parent Row, taking half the space
            Row(modifier = Modifier.fillMaxWidth()) {
                BallingDosingHistorySelectionButtonsComposable(
                    modifier = Modifier.weight(0.5f), // Modifier applied to the whole component
                    navHostController = null
                )
                // Optionally, add another element to see the weight effect, or test in isolation
                Box(modifier = Modifier.weight(0.5f).background(Color.LightGray)) {}
            }
        }
    }

    @Test
    fun selectionButtons_withCustomPadding_looksCorrect() {
        performScreenshotTest(goldenName = "balling_selection_buttons_custom_padding") {
            BallingDosingHistorySelectionButtonsComposable(
                modifier = Modifier.padding(16.dp), // Apply a different padding
                navHostController = null
            )
        }
    }
}
