package com.laimburggasse.aquariumcontrol.refill.history

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

open class RefillHistoryRepository(private val mainViewModel: MainViewModel) {
    private val _refillHistoryEventFlow = MutableStateFlow<DataFetchResult<List<RefillHistoryEvent>>>(
        value = DataFetchResult.Success(data = emptyList())
    )

    open val refillHistoryEventFlow = _refillHistoryEventFlow.asStateFlow()

    open suspend fun fetchRefillHistoryEvents(
        showFullWeek: Boolean
    ) {
        val url = when {
            showFullWeek -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.REFILL_GET_EVENTS_7D
            else -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.REFILL_GET_EVENTS_24H
        }
        val reader = RefillHistoryEventReader(url)
        withContext(context = Dispatchers.IO) {
            val result: DataFetchResult<List<RefillHistoryEvent>> = reader.readRefillEvents(mainViewModel)
            _refillHistoryEventFlow.value = result
        }
    }
}