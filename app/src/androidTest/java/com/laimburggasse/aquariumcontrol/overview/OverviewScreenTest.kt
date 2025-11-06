package com.laimburggasse.aquariumcontrol.overview

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.overview.presentation.OverviewScreen
import com.laimburggasse.aquariumcontrol.overview.presentation.mockControllerProfile
import com.laimburggasse.aquariumcontrol.overview.presentation.mockOverviewUiStateSuccess
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OverviewScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "overview"

    @Test
    fun overviewScreenDarkTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.overView,
                screenContent = {
                    OverviewScreen(
                        controllerProfile = mockControllerProfile,
                        overviewUiState = mockOverviewUiStateSuccess,
                        onOverviewEvent = {},
                    )
                },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "overview_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun overviewScreenLightTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.overView,
                screenContent = {
                    OverviewScreen(
                        controllerProfile = mockControllerProfile,
                        overviewUiState = mockOverviewUiStateSuccess,
                        onOverviewEvent = {},
                    )
                },
                onNavigate = {},
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "overview_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
}
