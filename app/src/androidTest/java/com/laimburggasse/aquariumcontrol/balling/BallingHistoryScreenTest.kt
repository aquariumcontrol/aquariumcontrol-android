package com.laimburggasse.aquariumcontrol.balling

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryScreen
import com.laimburggasse.aquariumcontrol.balling.history.mockBallingHistoryViewModelFactoryEmpty
import com.laimburggasse.aquariumcontrol.balling.history.mockBallingHistoryViewModelFactorySuccess
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BallingHistoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "balling"

    val mockMainViewModel = MockMainViewModel()

    @Test
    fun ballingHistoryScreenDarkEmptyTest() {
        composeTestRule.setContent {
            MainScaffold(
                mainViewModel = mockMainViewModel,
                navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
                screenContent = {
                    BallingHistoryScreen(
                        showFullWeek = false,
                        mainViewModel = com.laimburggasse.aquariumcontrol.overview.mockMainViewModel,
                        ballingHistoryViewModelFactory = mockBallingHistoryViewModelFactoryEmpty,
                    )
                },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "balling_history_screen_empty_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun ballingHistoryScreenLightEmptyTest() {
        composeTestRule.setContent {
            MainScaffold(
                mainViewModel = mockMainViewModel,
                navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
                screenContent = {
                    BallingHistoryScreen(
                        showFullWeek = false,
                        mainViewModel = com.laimburggasse.aquariumcontrol.overview.mockMainViewModel,
                        ballingHistoryViewModelFactory = mockBallingHistoryViewModelFactoryEmpty,
                    )
                },
                onNavigate = {},
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "balling_history_screen_empty_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
    @Test
    fun ballingHistoryScreenDarkDataTest() {
        composeTestRule.setContent {
            MainScaffold(
                mainViewModel = mockMainViewModel,
                navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
                screenContent = {
                    BallingHistoryScreen(
                        showFullWeek = false,
                        mainViewModel = com.laimburggasse.aquariumcontrol.overview.mockMainViewModel,
                        ballingHistoryViewModelFactory = mockBallingHistoryViewModelFactorySuccess,
                    )
                },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "balling_history_screen_data_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun ballingHistoryScreenLightDataTest() {
        composeTestRule.setContent {
            MainScaffold(
                mainViewModel = mockMainViewModel,
                navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
                screenContent = {
                    BallingHistoryScreen(
                        showFullWeek = false,
                        mainViewModel = com.laimburggasse.aquariumcontrol.overview.mockMainViewModel,
                        ballingHistoryViewModelFactory = mockBallingHistoryViewModelFactorySuccess,
                    )
                },
                onNavigate = {},
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "balling_history_screen_data_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
}
