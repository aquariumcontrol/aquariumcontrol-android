package com.laimburggasse.aquariumcontrol.feed.history

import com.laimburggasse.aquariumcontrol.MainViewModel

class MockFeedHistoryViewModel(mockFeedHistoryUiState: FeedHistoryUiState): FeedHistoryViewModelInterface {
    override val feedHistoryUiState: FeedHistoryUiState = mockFeedHistoryUiState
    override val isRefreshing: Boolean = mockFeedHistoryUiState == FeedHistoryUiState.Loading
    override fun loadData(mainViewModel: MainViewModel, showFullWeek: Boolean) {
        // do nothing
    }
}