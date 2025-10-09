package com.laimburggasse.aquariumcontrol.heating

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult

interface HeatingStatsViewModelInterface {
    var heatingStatsUiState: HeatingStatsUiState

    fun loadData(mainViewModel: MainViewModel)
}