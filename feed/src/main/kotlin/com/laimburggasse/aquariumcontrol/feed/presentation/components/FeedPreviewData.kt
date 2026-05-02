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

package com.laimburggasse.aquariumcontrol.feed.presentation.components

import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileChartDataBundle
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditMenuItem
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedProfilesUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScheduleSelectionUiState

interface FeedPreviewData {
    val mockFeedProfileForSimpleDisplayComposable: FeedProfile
    val mockChartData: FeedProfileChartDataBundle
    val mockInitialExpandedItemIds: Set<Int>
    val mockInitialExpandedItemIdsEmpty: Set<Int>
    val mockFeedProfileEditNumericMenuItem: FeedProfileEditMenuItem
    val mockFeedProfile: FeedProfile
    val mockFeedProfileEditUiStateSuccess: FeedProfileEditUiState
    val mockFeedProfileEditUiStateFailure: FeedProfileEditUiState
    val mockFeedScheduleSelectionUiStateEmpty: FeedScheduleSelectionUiState.Empty
    val mockFeedProfiles: List<FeedProfile>
    val mockFeedProfilesUiStateSuccess: FeedProfilesUiState.Success
    val mockFeedProfilesUiStateError: FeedProfilesUiState.Error
    val mockFeedScheduleSelectionUiStateSuccess: FeedScheduleSelectionUiState.Success
    val mockFeedScheduleSelectionUiStateError: FeedScheduleSelectionUiState.Error
    val mockEvent: FeedHistoryLogEntry
    val mockFeedHistoryLogEntries: List<FeedHistoryLogEntry>
    val mockFeedProfileImportForScheduleScreen: FeedProfileImport
    val mockFeedProfileForScheduleScreen: FeedProfile
    val mockFeedScheduleEntryImport: FeedScheduleEntryImport
    val mockFeedScheduleEntry: FeedScheduleEntry
}