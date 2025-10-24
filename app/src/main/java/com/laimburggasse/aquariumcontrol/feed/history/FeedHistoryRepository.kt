package com.laimburggasse.aquariumcontrol.feed.history

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

open class FeedHistoryRepository(private val mainViewModel: MainViewModel) {
    private val _feedHistoryLogEntryFlow = MutableStateFlow<DataFetchResult<List<FeedHistoryLogEntry>>>(
        value = DataFetchResult.Success(data = emptyList())
    )

    open val feedHistoryEventFlow = _feedHistoryLogEntryFlow.asStateFlow()

    open suspend fun fetchFeedHistoryEvents(
        showFullWeek: Boolean
    ) {
        val url = when {
            showFullWeek -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.FEED_GET_EVENTS_7D
            else -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.FEED_GET_EVENTS_24H
        }
        val reader = FeedHistoryLogEntryReader(url)
        withContext(context = Dispatchers.IO) {
            val result: DataFetchResult<List<FeedHistoryLogEntry>> = reader.readFeedEvents(mainViewModel)
            _feedHistoryLogEntryFlow.value = result
        }
    }
}