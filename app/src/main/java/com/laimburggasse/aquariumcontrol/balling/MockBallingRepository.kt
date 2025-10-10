package com.laimburggasse.aquariumcontrol.balling

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockBallingRepository(
    mockData: DataFetchResult<List<BallingProfile>>
) : BallingRepository(mainViewModel = MockMainViewModel()) {

    private val _profilesFlow = MutableStateFlow(value = mockData)

    override val profilesFlow = _profilesFlow.asStateFlow()

    override suspend fun fetchBallingProfiles() {
        // do nothing
    }

    override fun getBallingProfileById(id: Int): BallingProfile? {
        return null
    }
}