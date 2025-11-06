package com.laimburggasse.aquariumcontrol.refill.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.refill.data.RefillHistoryRepository
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RefillHistoryViewModel @Inject constructor(
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

    // @todo: add functionality to limit the number of events displayed
    fun onEvent(event: RefillHistoryEvent) {
        when (event) {
            is RefillHistoryEvent.Refresh -> {
                viewModelScope.launch {
                    refillHistoryRepository.triggerRefresh()
                }
            }
            is RefillHistoryEvent.SetRange -> {
                viewModelScope.launch {
                    refillHistoryRepository.setHistoryTimeRange(fetchFullWeek = event.showFullWeek)
                }
            }
        }
    }

}