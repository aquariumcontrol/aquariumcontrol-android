package com.laimburggasse.aquariumcontrol.balling

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryDosingEvent
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryScreen
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryUiState
import com.laimburggasse.aquariumcontrol.balling.history.MockBallingHistoryViewModel
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

    val mockBallingHistoryEvents = listOf(
        BallingHistoryDosingEvent(
            timestamp = "2025-01-18 12:00:00",
            pumpId = 1,
            volume = 1.5,
            label = "MockDosingUnit 1"
        ),
        BallingHistoryDosingEvent(
            timestamp = "2025-01-18 12:00:00",
            pumpId = 2,
            volume = 2.5,
            label = "MockDosingUnit 2"
        ),
    )

    val mockBallingHistoryUiStateEmpty = BallingHistoryUiState.Empty

    val mockBallingHistoryUiStateSuccess = BallingHistoryUiState.Success(
        ballingHistoryDosingEvents = mockBallingHistoryEvents
    )

    val mockBallingHistoryViewModelSuccess = MockBallingHistoryViewModel(
        mockBallingHistoryUiState = mockBallingHistoryUiStateSuccess
    )

    val mockBallingHistoryViewModelEmpty = MockBallingHistoryViewModel(
        mockBallingHistoryUiState = mockBallingHistoryUiStateEmpty
    )

    val mockMainViewModel = MockMainViewModel()

    @Test
    fun ballingHistoryScreenDarkEmptyTest() {
        composeTestRule.setContent {
            BallingHistoryScreen(
                showFullWeek = false,
                mainViewModel = mockMainViewModel,
                ballingHistoryViewModel = mockBallingHistoryViewModelEmpty,
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
            BallingHistoryScreen(
                showFullWeek = false,
                mainViewModel = mockMainViewModel,
                ballingHistoryViewModel = mockBallingHistoryViewModelEmpty,
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
            BallingHistoryScreen(
                showFullWeek = false,
                mainViewModel = mockMainViewModel,
                ballingHistoryViewModel = mockBallingHistoryViewModelSuccess,
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
            BallingHistoryScreen(
                showFullWeek = false,
                mainViewModel = mockMainViewModel,
                ballingHistoryViewModel = mockBallingHistoryViewModelSuccess,
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
