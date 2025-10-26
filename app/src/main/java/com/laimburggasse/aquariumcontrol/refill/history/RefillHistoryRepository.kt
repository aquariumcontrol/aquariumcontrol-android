package com.laimburggasse.aquariumcontrol.refill.history

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

open class RefillHistoryRepository(private val sessionManager: SessionManager) {
    private val _refillHistoryLogEntryFlow = MutableStateFlow<DataFetchResult<List<RefillHistoryLogEntry>>>(
        value = DataFetchResult.Success(data = emptyList())
    )

    open val refillHistoryEventFlow = _refillHistoryLogEntryFlow.asStateFlow()

    open suspend fun fetchRefillHistoryEvents(
        showFullWeek: Boolean
    ) {
        val url = when {
            showFullWeek -> "http://" + sessionManager.getControllerAddress() + WebApiUrls.REFILL_GET_EVENTS_7D
            else -> "http://" + sessionManager.getControllerAddress() + WebApiUrls.REFILL_GET_EVENTS_24H
        }
        val reader = RefillHistoryLogEntryReader(url)
        withContext(context = Dispatchers.IO) {
            val result: DataFetchResult<List<RefillHistoryLogEntry>> = reader.readRefillEvents(sessionManager)
            _refillHistoryLogEntryFlow.value = result
        }
    }
}