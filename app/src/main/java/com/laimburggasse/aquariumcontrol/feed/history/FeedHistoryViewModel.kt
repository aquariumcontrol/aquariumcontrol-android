package com.laimburggasse.aquariumcontrol.feed.history

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
class FeedHistoryViewModel @Inject constructor(
    private val feedHistoryRepository: FeedHistoryRepository
): ViewModel() {
    val feedHistoryUiState: StateFlow<FeedHistoryUiState> = feedHistoryRepository.feedHistoryEventFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> FeedHistoryUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        FeedHistoryUiState.Empty
                    } else {
                        FeedHistoryUiState.Success(feedHistoryLogEntries = result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = FeedHistoryUiState.Loading
        )

    fun onEvent(event: FeedHistoryEvent) {
        when (event) {
            is FeedHistoryEvent.Refresh -> {
                viewModelScope.launch {
                    feedHistoryRepository.triggerRefresh()
                }
            }
            is FeedHistoryEvent.SetRange -> {
                viewModelScope.launch {
                    feedHistoryRepository.setHistoryTimeRange(fetchFullWeek = event.showFullWeek)
                }
            }
        }
    }
}