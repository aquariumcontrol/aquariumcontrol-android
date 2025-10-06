package com.laimburggasse.aquariumcontrol.refill.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RefillHistoryViewModel: ViewModel(), RefillHistoryViewModelInterface {
    override var refillHistoryUiState: RefillHistoryUiState by mutableStateOf(RefillHistoryUiState.Loading)
        private set

    override var isRefreshing by mutableStateOf(value = false)
        private set

    override fun loadData(mainViewModel: MainViewModel, showFullWeek: Boolean) {
        viewModelScope.launch {
            isRefreshing = true

            val result: DataFetchResult<List<RefillEvent>> = fetchRefillEvents(mainViewModel, showFullWeek)

            refillHistoryUiState = when(result) {
                is DataFetchResult.Success -> {
                    val refillEvents: List<RefillEvent> = result.data
                    if (refillEvents.isEmpty()) {
                        RefillHistoryUiState.Empty
                    }
                    else {
                        RefillHistoryUiState.Success(refillHistory = refillEvents)
                    }
                }
                is DataFetchResult.Error -> {
                    RefillHistoryUiState.Error(result.errorMessage)
                }
            }

            isRefreshing = false
        }
    }

    private suspend fun fetchRefillEvents(
        mainViewModel: MainViewModel,
        showFullWeek: Boolean,
    ): DataFetchResult<List<RefillEvent>> {
        return withContext(context = Dispatchers.IO) {
            val url = when {
                showFullWeek -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.REFILL_GET_EVENTS_7D
                else -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.REFILL_GET_EVENTS_24H
            }
            val reader = RefillEventReader(url)
            reader.readRefillEvents(mainViewModel)
        }
    }
}