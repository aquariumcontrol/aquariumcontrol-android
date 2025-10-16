package com.laimburggasse.aquariumcontrol.refill

import com.laimburggasse.aquariumcontrol.MockMainViewModel

class MockRefillRepository: RefillRepository(mainViewModel = MockMainViewModel()) {
    override suspend fun sendStartRequest() {
        // do nothing
    }

    override suspend fun sendStopRequest() {
        // do nothing
    }

    override suspend fun sendResetRequest() {
        // do nothing
    }
}