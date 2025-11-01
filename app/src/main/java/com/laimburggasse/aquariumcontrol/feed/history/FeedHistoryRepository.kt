package com.laimburggasse.aquariumcontrol.feed.history

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class FeedHistoryRepository @Inject constructor(sessionManager: SessionManager) {

    // Trigger for manual refreshes (e.g., pull-to-refresh)
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    // State flow to hold the current time range choice (24h vs 7d)
    private val showFullWeek = MutableStateFlow(value = false)

    /**
     * A reactive Flow that provides the Feed History events.
     * It combines three sources:
     *  - The selected controller from SessionManager.
     *  - The manual refresh trigger.
     *  - The selected time range (24h or 7d).
     *
     * It automatically re-fetches data when any of these inputs change.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    open val feedHistoryEventFlow: Flow<DataFetchResult<List<FeedHistoryLogEntry>>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) }, // Trigger on first collection
            flow3 = showFullWeek
        ) { controller, _, isFullWeek ->
            Triple(first = controller, second = Unit, third = isFullWeek)
        }
            .filter { it.first != null } // Only proceed if a controller is selected
            .flatMapLatest { (controller, _, isFullWeek) ->
                flow {
                    // @todo: emit loading state
                    emit(value = DataFetchResult.Error(exception = null, errorMessage = "Loading"))

                    val url = if (isFullWeek) {
                        "http://${controller!!.address}${WebApiUrls.FEED_GET_EVENTS_7D}"
                    } else {
                        "http://${controller!!.address}${WebApiUrls.FEED_GET_EVENTS_24H}"
                    }

                    val result = withContext(context = Dispatchers.IO) {
                        val reader = FeedHistoryLogEntryReader(url)
                        reader.readFeedEvents()
                    }

                    emit(value = result)
                }
            }

    /**
     * Public function to tell the repository to fetch a different time range.
     * Setting the value will automatically trigger the flow to re-fetch the data.
     */
    suspend fun setHistoryTimeRange(fetchFullWeek: Boolean) {
        // Only emit if the value has changed to avoid unnecessary fetches.
        if (showFullWeek.value != fetchFullWeek) {
            showFullWeek.emit(value = fetchFullWeek)
        }
    }

    /**
     * Public function for the ViewModel to trigger a manual refresh.
     */
    suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }
}