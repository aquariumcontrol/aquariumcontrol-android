package com.laimburggasse.aquariumcontrol.refill

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.refill.history.RefillEvent
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.sql.Timestamp

@RunWith(AndroidJUnit4::class)
class RefillHistoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    val mockEvents = listOf(
        RefillEvent(
            timestamp = Timestamp(0L),
            duration = 60.0,
            volume = 10.0
        ),
        RefillEvent(
            timestamp = Timestamp(1000L),
            duration = 30.0,
            volume = 5.0
        )
    )

    @Test
    fun refillHistoryScreenDarkEmptyTest() {
        composeTestRule.setContent {
            RefillHistoryScreen(
                showFullWeek = true,
                mockRefillEvents = mockEvents,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "refill_history_screen_empty_dark",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun refillHistoryScreenLightEmptyTest() {
        composeTestRule.setContent {
            RefillHistoryScreen(
                showFullWeek = true,
                mockRefillEvents = mockEvents,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "refill_history_screen_empty_light",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
    @Test
    fun refillHistoryScreenDarkDataTest() {
        composeTestRule.setContent {
            RefillHistoryScreen(
                showFullWeek = true,
                mockRefillEvents = mockEvents,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "refill_history_screen_data_dark",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun refillHistoryScreenLightDataTest() {
        composeTestRule.setContent {
            RefillHistoryScreen(
                showFullWeek = true,
                mockRefillEvents = mockEvents,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "refill_history_screen_data_light",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
