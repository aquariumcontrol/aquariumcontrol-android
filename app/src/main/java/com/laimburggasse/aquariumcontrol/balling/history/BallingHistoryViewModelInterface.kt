package com.laimburggasse.aquariumcontrol.balling.history

import com.laimburggasse.aquariumcontrol.MainViewModel

interface BallingHistoryViewModelInterface {
    val ballingHistoryUiState: BallingHistoryUiState
    val isRefreshing: Boolean
    fun loadData(mainViewModel: MainViewModel, showFullWeek: Boolean)
}