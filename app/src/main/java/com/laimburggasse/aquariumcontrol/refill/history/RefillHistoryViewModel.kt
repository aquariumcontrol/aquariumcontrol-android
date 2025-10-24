package com.laimburggasse.aquariumcontrol.refill.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RefillHistoryViewModel(
    private val refillHistoryRepository: RefillHistoryRepository
): ViewModel() {
    
    val refillHistoryUiState: StateFlow<RefillHistoryUiState> = refillHistoryRepository.refillHistoryEventFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> RefillHistoryUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        RefillHistoryUiState.Empty
                    } else {
                        RefillHistoryUiState.Success(refillHistoryLogEntries = result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = RefillHistoryUiState.Loading
        )

    init {
        loadData(showFullWeek = false)
    }

    fun loadData(showFullWeek: Boolean) {
        viewModelScope.launch {
            refillHistoryRepository.fetchRefillHistoryEvents(showFullWeek)
        }
    }
}