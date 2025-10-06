package com.laimburggasse.aquariumcontrol.overview

import com.laimburggasse.aquariumcontrol.MainViewModel

interface OverviewViewModelInterface {
    val overviewUiState: OverviewUiState
    val isRefreshing: Boolean
    fun loadData(mainViewModel: MainViewModel)
}