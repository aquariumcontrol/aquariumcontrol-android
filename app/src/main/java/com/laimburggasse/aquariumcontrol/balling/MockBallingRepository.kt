package com.laimburggasse.aquariumcontrol.balling

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult

class MockBallingRepository(
    private val result: DataFetchResult<List<BallingProfile>>
) : BallingRepository(mainViewModel = MockMainViewModel()) {

    override suspend fun fetchBallingProfiles() {
        // do nothing
    }

    override fun getBallingProfileById(id: Int): BallingProfile? {
        return null
    }
}