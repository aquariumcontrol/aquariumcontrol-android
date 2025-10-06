package com.laimburggasse.aquariumcontrol.overview

import com.laimburggasse.aquariumcontrol.MainViewModel

class MockOverviewViewModel(mockOverviewUiState: OverviewUiState): OverviewViewModelInterface {
    override val overviewUiState: OverviewUiState = mockOverviewUiState
    override val isRefreshing: Boolean = mockOverviewUiState == OverviewUiState.Loading
    override fun loadData(mainViewModel: MainViewModel) {
        // do nothing
    }
}