package com.laimburggasse.aquariumcontrol.refill

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.overview.mockMainViewModel
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryScreen
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryViewModelFactory
import com.laimburggasse.aquariumcontrol.refill.history.mockRefillHistoryRepositoryEmpty
import com.laimburggasse.aquariumcontrol.refill.history.mockRefillHistoryRepositorySuccess
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RefillHistoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "refill"

    @Test
    fun refillHistoryScreenDarkEmptyTest() {
        composeTestRule.setContent {
            RefillHistoryScreen(
                showFullWeek = true,
                refillHistoryViewModelFactory = RefillHistoryViewModelFactory(
                    refillHistoryRepository = mockRefillHistoryRepositoryEmpty
                ),
                mainViewModel = mockMainViewModel,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "refill_history_screen_empty_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun refillHistoryScreenLightEmptyTest() {
        composeTestRule.setContent {
            RefillHistoryScreen(
                showFullWeek = true,
                refillHistoryViewModelFactory = RefillHistoryViewModelFactory(
                    refillHistoryRepository = mockRefillHistoryRepositoryEmpty
                ),
                mainViewModel = mockMainViewModel,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "refill_history_screen_empty_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
    @Test
    fun refillHistoryScreenDarkDataTest() {
        composeTestRule.setContent {
            RefillHistoryScreen(
                showFullWeek = true,
                refillHistoryViewModelFactory = RefillHistoryViewModelFactory(
                    refillHistoryRepository = mockRefillHistoryRepositorySuccess
                ),
                mainViewModel = mockMainViewModel,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "refill_history_screen_data_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun refillHistoryScreenLightDataTest() {
        composeTestRule.setContent {
            RefillHistoryScreen(
                showFullWeek = true,
                refillHistoryViewModelFactory = RefillHistoryViewModelFactory(
                    refillHistoryRepository = mockRefillHistoryRepositorySuccess
                ),
                mainViewModel = mockMainViewModel,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "refill_history_screen_data_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
