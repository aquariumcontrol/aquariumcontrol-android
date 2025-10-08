package com.laimburggasse.aquariumcontrol.balling

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BallingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "balling"

    val mockBallingProfile1 = BallingProfile(pumpId = 1, volume = 2.1, label = "mock1")
    val mockBallingProfile2 = BallingProfile(pumpId = 2, volume = 3.1, label = "mock2")
    val mockBallingProfiles = listOf(mockBallingProfile1, mockBallingProfile2)

    val mockBallingUiState = BallingUiState.Success(ballingProfiles = mockBallingProfiles)

    val mockBallingViewModel = MockBallingViewModel(ballingUiState = mockBallingUiState, isRefreshing = false)

    val mockMainViewModel = MockMainViewModel()

    @Test
    fun ballingScreenDarkTest() {
        composeTestRule.setContent {
            BallingScreen(
                navHostController = null,
                mainViewModel = mockMainViewModel,
                ballingViewModel = mockBallingViewModel,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "balling_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun ballingScreenLightTest() {
        composeTestRule.setContent {
            BallingScreen(
                navHostController = null,
                mainViewModel = mockMainViewModel,
                ballingViewModel = mockBallingViewModel,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "balling_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
