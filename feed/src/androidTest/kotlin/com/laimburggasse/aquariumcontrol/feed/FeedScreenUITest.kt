/*
 *  Copyright 2026 Uwe Martin
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

package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScreen
import com.laimburggasse.aquariumcontrol.feed.presentation.navigation.FeedNavigationEvent
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfilesUiStateSuccess
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedScheduleSelectionUiStateSuccess
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class FeedScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun tapping_history_button_navigates_to_FeedHistory() {
        var capturedEvent: FeedNavigationEvent? = null
        val label24h = context.getString(R.string.view_feed_events)

        composeTestRule.setContent {
            FeedScreen(
                feedProfilesUiState = mockFeedProfilesUiStateSuccess,
                feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                onNavigateFromFeed = { event, _, _, _ -> capturedEvent = event },
                onFeedProfilesUiEvent = {},
                onFeedScheduleEntryUiEvent = { _ -> return@FeedScreen null }
            )
        }

        // Find the button for 24h history and click it
        composeTestRule.onNodeWithText(label24h).performClick()

        // Assert the callback was triggered with the expected enum value
        Assert.assertEquals(FeedNavigationEvent.ToFeedHistory, capturedEvent)
    }

    @Test
    fun tapping_FeedProfileSummary_Edit_icon_navigates_to_FeedProfileEditScreen() {
        var capturedEvent: FeedNavigationEvent? = null

        val targetProfile = mockFeedProfilesUiStateSuccess.feedProfiles.first()

        composeTestRule.setContent {
            FeedScreen(
                feedProfilesUiState = mockFeedProfilesUiStateSuccess,
                feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                onNavigateFromFeed = { event, _, _, _ -> capturedEvent = event },
                onFeedProfilesUiEvent = {},
                onFeedScheduleEntryUiEvent = { _ -> return@FeedScreen null }
            )
        }

        // Find the profile summary edit icon by specific tag and tap it
        composeTestRule.onNodeWithTag("feed_profile_edit_${targetProfile.profileID}").performClick()

        // Assert navigation to FeedEditScreen occurred with the correct ID
        Assert.assertEquals(FeedNavigationEvent.ToFeedProfileEdit, capturedEvent)
    }

    @Test
    fun tapping_FeedProfileSummary_Schedule_icon_navigates_to_FeedProfileScheduleScreen() {
        var capturedEvent: FeedNavigationEvent? = null

        val targetProfile = mockFeedProfilesUiStateSuccess.feedProfiles.first()

        composeTestRule.setContent {
            FeedScreen(
                feedProfilesUiState = mockFeedProfilesUiStateSuccess,
                feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                onNavigateFromFeed = { event, _, _, _ -> capturedEvent = event },
                onFeedProfilesUiEvent = {},
                onFeedScheduleEntryUiEvent = { _ -> return@FeedScreen null }
            )
        }

        // Find the profile summary schedule icon by specific tag and tap it
        composeTestRule.onNodeWithTag("feed_profile_schedule_${targetProfile.profileID}").performClick()

        // Assert navigation to FeedScheduleScreen occurred with the correct ID
        Assert.assertEquals(FeedNavigationEvent.ToFeedScheduling, capturedEvent)
    }

    @Test
    fun tapping_FeedScheduleEntryComposable_Edit_icon_navigates_to_FeedScheduleScreen() {
        var capturedEvent: FeedNavigationEvent? = null

        val targetScheduleEntry = mockFeedScheduleSelectionUiStateSuccess.feedScheduleEntries.first()

        composeTestRule.setContent {
            FeedScreen(
                feedProfilesUiState = mockFeedProfilesUiStateSuccess,
                feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                onNavigateFromFeed = { event, _, _, _ -> capturedEvent = event },
                onFeedProfilesUiEvent = {},
                onFeedScheduleEntryUiEvent = { _ -> return@FeedScreen null }
            )
        }

        // Find the profile summary edit icon by specific tag and tap it
        composeTestRule.onNodeWithTag("feed_schedule_edit_${targetScheduleEntry.profileId}").performClick()

        // Assert navigation to FeedEditScreen occurred with the correct ID
        Assert.assertEquals(FeedNavigationEvent.ToFeedScheduling, capturedEvent)
    }
}