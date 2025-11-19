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

package com.laimburggasse.aquariumcontrol.feed

import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.presentation.components.FeedPreviewData
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileChartDataBundle
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileChartDataFactory
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditMenuItem
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditPhaseType
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditToggleButtonType
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedProfilesUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScheduleSelectionUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.history.FeedHistoryUiState

object FeedPreviewDataImpl: FeedPreviewData {
    override val mockFeedProfileForSimpleDisplayComposable: FeedProfile = FeedProfile().apply {
        feedProfilePhases[0].duration = 5.0
        feedProfilePhases[1].duration = 10.0
        feedProfilePhases[1].skimmer = true
    }
    override val mockChartData: FeedProfileChartDataBundle = FeedProfileChartDataFactory.create(
        mockFeedProfileForSimpleDisplayComposable
    )
    override val mockInitialExpandedItemIds: Set<Int> = setOf(0)
    override val mockInitialExpandedItemIdsEmpty: Set<Int> = emptySet()
    override val mockFeedProfileEditNumericMenuItem = FeedProfileEditMenuItem(
        id = 0,
        level = 0,
        text = "Relase build",
        isToggleButton = true,
        children = emptyList(),
        phaseType = FeedProfileEditPhaseType.FEED,
        toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
        isNumericInput = false,
    )
    override val mockFeedProfile = FeedProfile()
    override val mockFeedProfileEditUiStateSuccess = FeedProfileEditUiState(
        feedProfile = mockFeedProfile,
    )
    override val mockFeedProfileEditUiStateFailure = FeedProfileEditUiState(
        feedProfile = null,
    )
    override val mockFeedScheduleSelectionUiStateEmpty = FeedScheduleSelectionUiState.Empty
    override val mockFeedProfiles = emptyList<FeedProfile>()
    override val mockFeedProfilesUiStateSuccess = FeedProfilesUiState.Success(
        feedProfiles = mockFeedProfiles
    )
    override val mockFeedProfilesUiStateError = FeedProfilesUiState.Error(errorMessage = "Release build")
    override val mockFeedScheduleSelectionUiStateSuccess = FeedScheduleSelectionUiState.Success(
        feedScheduleEntries = emptyList()
    )
    override val mockFeedScheduleSelectionUiStateError = FeedScheduleSelectionUiState.Error(errorMessage = "Release build")
    override val mockEvent = FeedHistoryLogEntry(
        timestamp = "2525-01-18 12:00:00",
        feederOnTime = 0,
        profileName = "Mock feed profile 1",
        idx = 0
    )
    override val mockFeedHistoryLogEntries = emptyList<FeedHistoryLogEntry>()
    override val mockFeedHistoryUiStateSuccess = FeedHistoryUiState.Success(
        feedHistoryLogEntries = mockFeedHistoryLogEntries
    )
    override val mockFeedHistoryUiStateError = FeedHistoryUiState.Error(
        errorMessage = "Release build"
    )
    override val mockFeedHistoryUiStateEmpty = FeedHistoryUiState.Empty
    override val mockFeedHistoryUiStateLoading = FeedHistoryUiState.Loading
    override val mockFeedProfileImportForScheduleScreen = FeedProfileImport()
    override val mockFeedProfileForScheduleScreen: FeedProfile = FeedProfile.fromFeedProfileImportUpdateIdName(
        feedProfileImport = mockFeedProfileImportForScheduleScreen,
        profileId = 0,
        profileName = "Release build"
    )
    override val mockFeedScheduleEntryImport = FeedScheduleEntryImport(
        timestampString = "2525-09-01 12:00:00",
        profileId = 1,
        profileName = "Release build",
        isDaily = 1
    )
    override val mockFeedScheduleEntry = mockFeedScheduleEntryImport.transformToFeedScheduleEntry()
}