package com.laimburggasse.aquariumcontrol.refill

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.refill.presentation.history.RefillHistoryScreen
import com.laimburggasse.aquariumcontrol.refill.presentation.history.mockRefillHistoryUiStateEmpty
import com.laimburggasse.aquariumcontrol.refill.presentation.history.mockRefillHistoryUiStateSuccess
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
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
                screenContent = {
                    RefillHistoryScreen(
                        showFullWeek = true,
                        refillHistoryUiState = mockRefillHistoryUiStateEmpty,
                        onRefillHistoryEvent = {}
                    )
                },
                onNavigate = {},
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
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
                screenContent = {
                    RefillHistoryScreen(
                        showFullWeek = true,
                        refillHistoryUiState = mockRefillHistoryUiStateEmpty,
                        onRefillHistoryEvent = {}
                    )
                },
                onNavigate = {},
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
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
                screenContent = {
                    RefillHistoryScreen(
                        showFullWeek = true,
                        refillHistoryUiState = mockRefillHistoryUiStateSuccess,
                        onRefillHistoryEvent = {}
                    )
                },
                onNavigate = {},
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
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
                screenContent = {
                    RefillHistoryScreen(
                        showFullWeek = true,
                        refillHistoryUiState = mockRefillHistoryUiStateSuccess,
                        onRefillHistoryEvent = {}
                    )
                },
                onNavigate = {},
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
