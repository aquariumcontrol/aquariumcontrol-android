package com.laimburggasse.aquariumcontrol.balling.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BallingHistoryViewModel(
    private val ballingHistoryRepository: BallingHistoryRepository
): ViewModel() {
    val ballingHistoryUiState: StateFlow<BallingHistoryUiState> = ballingHistoryRepository.ballingHistoryDosingEventFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> BallingHistoryUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        BallingHistoryUiState.Empty
                    } else {
                        BallingHistoryUiState.Success(ballingHistoryLogEntries = result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = BallingHistoryUiState.Loading
        )

    init {
        loadData(showFullWeek = false)
    }

    fun loadData(showFullWeek: Boolean) {
        viewModelScope.launch {
            ballingHistoryRepository.fetchBallingHistoryDosingEvents(showFullWeek)
        }
    }

    fun onEvent(event: BallingHistoryEvent) {
        when (event) {
            is BallingHistoryEvent.Refresh -> {
                loadData(showFullWeek = event.showFullWeek)
            }
        }
    }
}