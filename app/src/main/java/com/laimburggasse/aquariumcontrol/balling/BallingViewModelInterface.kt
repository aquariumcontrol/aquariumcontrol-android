package com.laimburggasse.aquariumcontrol.balling

import com.laimburggasse.aquariumcontrol.MainViewModel

interface BallingViewModelInterface {
    var ballingUiState: BallingUiState
    var isRefreshing: Boolean
    fun loadData(mainViewModel: MainViewModel)
}