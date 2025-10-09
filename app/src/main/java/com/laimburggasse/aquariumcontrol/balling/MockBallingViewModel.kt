package com.laimburggasse.aquariumcontrol.balling

import com.laimburggasse.aquariumcontrol.MainViewModel

class MockBallingViewModel(
    override var ballingUiState: BallingUiState,
    override var isRefreshing: Boolean
) : BallingViewModelInterface {
    override fun loadData() {
        // do nothing
    }
}