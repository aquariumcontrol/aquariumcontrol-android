package com.laimburggasse.aquariumcontrol.balling

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.delay

class MockLongLoadingBallingRepository : BallingRepository(mainViewModel = MockMainViewModel()) {

    suspend fun getBallingProfiles(): DataFetchResult<List<BallingProfile>> {
        delay(timeMillis = Long.MAX_VALUE)
        return DataFetchResult.Error(exception = null, errorMessage = "Long wait is over")
    }

    override fun getBallingProfileById(id: Int): BallingProfile? {
        return null
    }
}