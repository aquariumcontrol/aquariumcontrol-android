package com.laimburggasse.aquariumcontrol.refill.history

import com.laimburggasse.aquariumcontrol.MainViewModel

class MockRefillHistoryViewModel(mockRefillHistoryUiState: RefillHistoryUiState): RefillHistoryViewModelInterface {
    override val refillHistoryUiState: RefillHistoryUiState = mockRefillHistoryUiState
    override val isRefreshing: Boolean = mockRefillHistoryUiState == RefillHistoryUiState.Loading
    override fun loadData(mainViewModel: MainViewModel, showFullWeek: Boolean) {
        // do nothing
    }
}