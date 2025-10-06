package com.laimburggasse.aquariumcontrol.balling.history

import com.laimburggasse.aquariumcontrol.MainViewModel

class MockBallingHistoryViewModel(mockBallingHistoryUiState: BallingHistoryUiState): BallingHistoryViewModelInterface {
    override val ballingHistoryUiState: BallingHistoryUiState = mockBallingHistoryUiState
    override val isRefreshing: Boolean = mockBallingHistoryUiState == BallingHistoryUiState.Loading
    override fun loadData(mainViewModel: MainViewModel, showFullWeek: Boolean) {
        // do nothing
    }
}