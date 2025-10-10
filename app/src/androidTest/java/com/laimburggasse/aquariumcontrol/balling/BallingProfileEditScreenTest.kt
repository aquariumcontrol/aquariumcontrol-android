package com.laimburggasse.aquariumcontrol.balling

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.balling.edit.BallingProfileEditScreen
import com.laimburggasse.aquariumcontrol.balling.edit.BallingProfileEditViewModelFactory
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BallingProfileEditScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "balling"

    val mockBallingProfile = BallingProfile(
        pumpId = 1,
        volume = 1.5,
        label = "MockDosingUnit 1",
    )

    val mockBallingProfiles = listOf(mockBallingProfile)

    val mockBallingRepositorySuccess = MockBallingRepository(
        mockData = DataFetchResult.Success(data = mockBallingProfiles)
    )
    val mockMainViewModel = MockMainViewModel()

    @Test
    fun ballingProfileEditScreenDarkTest() {
        composeTestRule.setContent {
            BallingProfileEditScreen(
                navHostController = null,
                mainViewModel = mockMainViewModel,
                ballingProfileEditViewModelFactory = BallingProfileEditViewModelFactory(
                    ballingProfileId = 1,
                    ballingRepository = mockBallingRepositorySuccess
                ),
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "balling_profile_edit_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun ballingProfileEditScreenLightTest() {
        composeTestRule.setContent {
            BallingProfileEditScreen(
                navHostController = null,
                mainViewModel = mockMainViewModel,
                ballingProfileEditViewModelFactory = BallingProfileEditViewModelFactory(
                    ballingProfileId = 1,
                    ballingRepository = mockBallingRepositorySuccess
                ),
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "balling_profile_edit_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
