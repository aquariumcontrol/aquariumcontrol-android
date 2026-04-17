/*
 *  Copyright 2025 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.aquariumcontrol.balling

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingScreen
import com.laimburggasse.aquariumcontrol.balling.presentation.navigation.BallingNavigationEvent
import com.laimburggasse.aquariumcontrol.balling.testing.BallingMockData.mockBallingProfilesUiStateSuccess
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import com.laimburggasse.aquariumcontrol.balling.R

class BallingScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun tapping_24h_history_button_navigates_to_BallingHistory24h() {
        var capturedEvent: BallingNavigationEvent? = null
        val label24h = context.getString(R.string.view_balling_events_24h)

        composeTestRule.setContent {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateSuccess,
                onNavigateFromBalling = { event, _ -> capturedEvent = event },
                onRefresh = {}
            )
        }

        // Find the button for 24h history and click it
        composeTestRule.onNodeWithText(label24h).performClick()

        // Assert the callback was triggered with the expected enum value
        Assert.assertEquals(BallingNavigationEvent.ToBallingHistory24h, capturedEvent)
    }
    @Test

    fun tapping_7d_history_button_navigates_to_BallingHistory7d() {
        var capturedEvent: BallingNavigationEvent? = null
        val label7d = context.getString(R.string.view_balling_events_7d)

        composeTestRule.setContent {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateSuccess,
                onNavigateFromBalling = { event, _ -> capturedEvent = event },
                onRefresh = {}
            )
        }

        // Find the button for 24h history and click it
        composeTestRule.onNodeWithText(label7d).performClick()

        // Assert the callback was triggered with the expected enum value
        Assert.assertEquals(BallingNavigationEvent.ToBallingHistory7d, capturedEvent)
    }

    @Test
    fun tapping_BallingProfileSummary_navigates_to_BallingEditScreen() {
        var capturedEvent: BallingNavigationEvent? = null

        val targetProfile = mockBallingProfilesUiStateSuccess.ballingProfiles.first()

        composeTestRule.setContent {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateSuccess,
                onNavigateFromBalling = { event, _ ->
                    capturedEvent = event
                },
                onRefresh = {}
            )
        }

        // Find the profile summary by its label and tap it
        composeTestRule.onNodeWithText(targetProfile.label).performClick()

        // Assert navigation to BallingEditScreen occurred with the correct ID
        Assert.assertEquals(BallingNavigationEvent.ToBallingEdit, capturedEvent)
    }
}