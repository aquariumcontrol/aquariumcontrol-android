package com.laimburggasse.aquariumcontrol.feed.history

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

class FeedHistoryViewModel: ViewModel(), FeedHistoryViewModelInterface {
    override var feedHistoryUiState: FeedHistoryUiState by mutableStateOf(FeedHistoryUiState.Loading)
        private set

    override var isRefreshing by mutableStateOf(value = false)
        private set

    override fun loadData(mainViewModel: MainViewModel, showFullWeek: Boolean) {
        viewModelScope.launch {
            isRefreshing = true

            val result: DataFetchResult<List<FeedHistoryEvent>> = fetchFeedEvents(
                mainViewModel,
                showFullWeek
            )

            feedHistoryUiState = when(result) {
                is DataFetchResult.Success -> {
                    val feedHistoryEvents: List<FeedHistoryEvent> = result.data
                    if (feedHistoryEvents.isEmpty()) {
                        FeedHistoryUiState.Empty
                    }
                    else {
                        FeedHistoryUiState.Success(feedHistoryEvents)

                    }
                }
                is DataFetchResult.Error -> {
                    FeedHistoryUiState.Error(errorMessage = result.errorMessage)
                }
            }

            isRefreshing = false
        }
    }

    private suspend fun fetchFeedEvents(
        mainViewModel: MainViewModel,
        showFullWeek: Boolean
    ): DataFetchResult<List<FeedHistoryEvent>> {
        return withContext(context = Dispatchers.IO) {
            val url = when {
                showFullWeek -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.FEED_GET_EVENTS_7D
                else -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.FEED_GET_EVENTS_24H
            }
            val reader = FeedHistoryEventReader(url)
            reader.readFeedEvents()
        }
    }
}