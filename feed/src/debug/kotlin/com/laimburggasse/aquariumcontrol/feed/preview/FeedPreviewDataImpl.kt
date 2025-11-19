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

import com.laimburggasse.aquariumcontrol.feed.presentation.components.FeedPreviewData
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData

object FeedPreviewDataImpl: FeedPreviewData {
    override val mockFeedProfileForSimpleDisplayComposable = FeedMockData.mockFeedProfileForSimpleDisplayComposable
    override val mockChartData = FeedMockData.mockChartData
    override val mockInitialExpandedItemIds = FeedMockData.mockInitialExpandedItemIds
    override val mockInitialExpandedItemIdsEmpty = FeedMockData.mockInitialExpandedItemIdsEmpty
    override val mockFeedProfileEditNumericMenuItem = FeedMockData.mockFeedProfileEditNumericMenuItem
    override val mockFeedProfile = FeedMockData.mockFeedProfile
    override val mockFeedProfileEditUiStateSuccess = FeedMockData.mockFeedProfileEditUiStateSuccess
    override val mockFeedProfileEditUiStateFailure = FeedMockData.mockFeedProfileEditUiStateFailure
    override val mockFeedScheduleSelectionUiStateEmpty = FeedMockData.mockFeedScheduleSelectionUiStateEmpty
    override val mockFeedProfiles = FeedMockData.mockFeedProfiles
    override val mockFeedProfilesUiStateSuccess = FeedMockData.mockFeedProfilesUiStateSuccess
    override val mockFeedProfilesUiStateError = FeedMockData.mockFeedProfilesUiStateError
    override val mockFeedScheduleSelectionUiStateSuccess = FeedMockData.mockFeedScheduleSelectionUiStateSuccess
    override val mockFeedScheduleSelectionUiStateError = FeedMockData.mockFeedScheduleSelectionUiStateError
    override val mockEvent = FeedMockData.mockEvent
    override val mockFeedHistoryLogEntries = FeedMockData.mockFeedHistoryLogEntries
    override val mockFeedHistoryUiStateSuccess = FeedMockData.mockFeedHistoryUiStateSuccess
    override val mockFeedHistoryUiStateError = FeedMockData.mockFeedHistoryUiStateError
    override val mockFeedHistoryUiStateEmpty = FeedMockData.mockFeedHistoryUiStateEmpty
    override val mockFeedHistoryUiStateLoading = FeedMockData.mockFeedHistoryUiStateLoading
    override val mockFeedProfileImportForScheduleScreen = FeedMockData.mockFeedProfileImportForScheduleScreen
    override val mockFeedProfileForScheduleScreen = FeedMockData.mockFeedProfileForScheduleScreen
    override val mockFeedScheduleEntryImport = FeedMockData.mockFeedScheduleEntryImport
    override val mockFeedScheduleEntry = FeedMockData.mockFeedScheduleEntry
}