package com.laimburggasse.aquariumcontrol.feed.data

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.controller.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedHistoryLogEntryReader
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.lib.data.remote.WebApiUrls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class FeedHistoryRepositoryImpl @Inject constructor(
    sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope
) {

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
        .flatMapLatest { (controller: ControllerProfile?, _, isFullWeek: Boolean) ->
            flow {
                // UI shall temporarily show loading state, while fetching data
                emit(value = DataFetchResult.Loading)

                val url: String = if (isFullWeek) {
                    "http://${controller!!.address}:${controller.port}${WebApiUrls.FEED_GET_EVENTS_7D}"
                } else {
                    "http://${controller!!.address}:${controller.port}${WebApiUrls.FEED_GET_EVENTS_24H}"
                }
                val result: DataFetchResult<List<FeedHistoryLogEntry>> =
                    withContext(context = Dispatchers.IO) {
                        val reader = FeedHistoryLogEntryReader(url)
                        reader.readFeedEvents()
                    }

                emit(value = result)
            }
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = DataFetchResult.Loading
        )

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