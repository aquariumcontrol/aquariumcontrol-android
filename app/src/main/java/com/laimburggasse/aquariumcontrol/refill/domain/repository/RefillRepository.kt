package com.laimburggasse.aquariumcontrol.refill.domain.repository

import kotlinx.coroutines.flow.SharedFlow

interface RefillRepository {
    val errorEvents: SharedFlow<String>
    suspend fun sendStartRequest()
    suspend fun sendStopRequest()
    suspend fun sendResetRequest()
}