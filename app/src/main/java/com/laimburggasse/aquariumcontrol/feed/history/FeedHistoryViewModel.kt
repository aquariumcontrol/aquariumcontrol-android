package com.laimburggasse.aquariumcontrol.feed.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedHistoryViewModel(
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

    init {
        loadData(showFullWeek = false)
    }

    fun loadData(showFullWeek: Boolean) {
        viewModelScope.launch {
            feedHistoryRepository.fetchFeedHistoryEvents(showFullWeek)
        }
    }
}