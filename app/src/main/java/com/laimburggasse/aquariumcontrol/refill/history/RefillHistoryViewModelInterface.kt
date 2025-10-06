package com.laimburggasse.aquariumcontrol.refill.history

import com.laimburggasse.aquariumcontrol.MainViewModel

interface RefillHistoryViewModelInterface {
    val refillHistoryUiState: RefillHistoryUiState
    val isRefreshing: Boolean
    fun loadData(mainViewModel: MainViewModel, showFullWeek: Boolean)
}