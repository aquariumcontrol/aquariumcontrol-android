package com.laimburggasse.aquariumcontrol.balling

import com.laimburggasse.aquariumcontrol.MockMainViewModel

class MockLongLoadingBallingRepository : BallingRepository(mainViewModel = MockMainViewModel()) {

    override fun getBallingProfileById(id: Int): BallingProfile? {
        return null
    }
}