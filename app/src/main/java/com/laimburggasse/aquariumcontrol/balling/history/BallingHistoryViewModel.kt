package com.laimburggasse.aquariumcontrol.balling.history

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

class BallingHistoryViewModel: ViewModel(), BallingHistoryViewModelInterface {
    override var ballingHistoryUiState: BallingHistoryUiState by mutableStateOf(BallingHistoryUiState.Loading)
        private set

    override var isRefreshing by mutableStateOf(value = false)
        private set

    override fun loadData(mainViewModel: MainViewModel, showFullWeek: Boolean) {
        viewModelScope.launch {
            isRefreshing = true

            val result: DataFetchResult<List<BallingHistoryDosingEvent>> = fetchBallingEvents(
                mainViewModel,
                showFullWeek
            )

            ballingHistoryUiState = when(result) {
                is DataFetchResult.Success -> {
                    val ballingHistoryDosingEvents: List<BallingHistoryDosingEvent> = result.data
                    if (ballingHistoryDosingEvents.isEmpty()) {
                        BallingHistoryUiState.Empty
                    }
                    else {
                        BallingHistoryUiState.Success(ballingHistoryDosingEvents)

                    }
                }
                is DataFetchResult.Error -> {
                    BallingHistoryUiState.Error(errorMessage = result.errorMessage)
                }
            }

            isRefreshing = false
        }
    }

    private suspend fun fetchBallingEvents(
        mainViewModel: MainViewModel,
        showFullWeek: Boolean
    ): DataFetchResult<List<BallingHistoryDosingEvent>> {
        return withContext(context = Dispatchers.IO) {
            val url = when {
                showFullWeek -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.BALLING_GET_EVENTS_7D
                else -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.BALLING_GET_EVENTS_24H
            }
            val reader = BallingHistoryEventReader(url)
            reader.readBallingEvents(mainViewModel)
        }
    }
}