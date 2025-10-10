package com.laimburggasse.aquariumcontrol.refill.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryUiState
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                        RefillHistoryUiState.Success(refillHistoryEvents = result.data)
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