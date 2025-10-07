package com.laimburggasse.aquariumcontrol.feed.history

import com.laimburggasse.aquariumcontrol.MainViewModel

interface FeedHistoryViewModelInterface {
    val feedHistoryUiState: FeedHistoryUiState
    val isRefreshing: Boolean
    fun loadData(mainViewModel: MainViewModel, showFullWeek: Boolean)
}