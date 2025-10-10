package com.laimburggasse.aquariumcontrol.balling.history

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

open class BallingHistoryRepository(private val mainViewModel: MainViewModel) {
    private val _ballingHistoryDosingEventFlow = MutableStateFlow<DataFetchResult<List<BallingHistoryDosingEvent>>>(
        value = DataFetchResult.Success(data = emptyList())
    )

    open val ballingHistoryDosingEventFlow = _ballingHistoryDosingEventFlow.asStateFlow()

    open suspend fun fetchBallingHistoryDosingEvents(
        showFullWeek: Boolean
    ) {
        val url = when {
            showFullWeek -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.BALLING_GET_EVENTS_7D
            else -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.BALLING_GET_EVENTS_24H
        }
        val reader = BallingHistoryEventReader(url)
        withContext(context = Dispatchers.IO) {
            val result: DataFetchResult<List<BallingHistoryDosingEvent>> = reader.readBallingEvents(mainViewModel)
            _ballingHistoryDosingEventFlow.value = result
        }
    }
}