package com.laimburggasse.aquariumcontrol.heating

import com.laimburggasse.aquariumcontrol.MainViewModel

class MockHeatingStatsViewModel(
    override var heatingStatsUiState: HeatingStatsUiState
) : HeatingStatsViewModelInterface {
    override fun loadData(mainViewModel: MainViewModel) {
        // do nothing
    }
}