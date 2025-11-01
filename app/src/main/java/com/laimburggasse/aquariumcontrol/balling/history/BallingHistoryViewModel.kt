package com.laimburggasse.aquariumcontrol.balling.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BallingHistoryViewModel @Inject constructor(
    private val ballingHistoryRepository: BallingHistoryRepository
): ViewModel() {
    val ballingHistoryUiState: StateFlow<BallingHistoryUiState> =
        ballingHistoryRepository.ballingHistoryDosingEventFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> BallingHistoryUiState.Error(
                    errorMessage = result.errorMessage
                )
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
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = BallingHistoryUiState.Loading
        )

    fun onEvent(event: BallingHistoryEvent) {
        when (event) {
            is BallingHistoryEvent.Refresh -> {
                viewModelScope.launch {
                    ballingHistoryRepository.triggerRefresh()
                }
            }
            is BallingHistoryEvent.SetRange -> {
                viewModelScope.launch {
                    ballingHistoryRepository.setHistoryTimeRange(fetchFullWeek = event.showFullWeek)
                }
            }
        }
    }
}