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

package com.laimburggasse.aquariumcontrol.feed.preview

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileChartDataBundle
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditMenuItem
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedProfilesUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScheduleSelectionUiState

@Suppress("unused")
object FeedPreviewData {
    val mockChartData: FeedProfileChartDataBundle get() = error("Preview only")
    val mockInitialExpandedItemIds: Set<Int> get() = error("Preview only")
    val mockInitialExpandedItemIdsEmpty: Set<Int> get() = error("Preview only")
    val mockFeedProfileEditNumericMenuItem: FeedProfileEditMenuItem get() = error("Preview only")
    val mockFeedProfile: FeedProfile get() = error("Preview only")
    val mockFeedProfileEditUiStateSuccess: FeedProfileEditUiState get() = error("Preview only")
    val mockFeedProfileEditUiStateFailure: FeedProfileEditUiState get() = error("Preview only")
    val mockFeedScheduleSelectionUiStateEmpty: FeedScheduleSelectionUiState.Empty get() = error("Preview only")
    val mockFeedProfilesUiStateSuccess: FeedProfilesUiState.Success get() = error("Preview only")
    val mockFeedProfilesUiStateError: FeedProfilesUiState.Error get() = error("Preview only")
    val mockFeedScheduleSelectionUiStateSuccess: FeedScheduleSelectionUiState.Success get() = error("Preview only")
    val mockFeedScheduleSelectionUiStateError: FeedScheduleSelectionUiState.Error get() = error("Preview only")
    val mockEvent: FeedHistoryLogEntry get() = error("Preview only")
    val mockFeedHistoryLogEntries: List<FeedHistoryLogEntry> get() = error("Preview only")
    val mockFeedProfileForScheduleScreen: FeedProfile get() = error("Preview only")
    val mockFeedScheduleEntry: FeedScheduleEntry get() = error("Preview only")
}
