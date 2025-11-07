package com.laimburggasse.aquariumcontrol.overview.data

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.overview.data.remote.OverviewDataReader
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
open class OverviewRepository @Inject constructor(
    sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope
) {
    // This SharedFlow doesn't hold state; it just emits a signal when we call emit().
    // We use replay=1 so it emits the last signal to new subscribers if needed.
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    /**
     * This is the main reactive data source for the UI.
     * It combines two triggers:
     *  1. `sessionManager.selectedControllerProfile`: Automatically fetches when the controller changes.
     *  2. `refreshTrigger`: Manually fetches when `fetchOverviewData()` is called.
     *
     * It uses `flatMapLatest` to ensure that whenever EITHER trigger emits,
     * it cancels any ongoing fetch and starts a new one.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    open val overviewData: Flow<DataFetchResult<OverviewData>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger
        ) { controller, _ ->
            controller
        }
        .filterNotNull()
        .flatMapLatest { controller ->
            flow {
                // UI shall temporarily show loading state, while fetching data
                emit(value = DataFetchResult.Loading)

                val result: DataFetchResult<OverviewData> = withContext(context = Dispatchers.IO) {
                    val url = "http://${controller.address}${WebApiUrls.OVERVIEW_GET_DATA}"
                    val reader = OverviewDataReader(url)
                    reader.readOverviewData()
                }

                emit(value = result)
            }
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Companion.Eagerly,
            initialValue = DataFetchResult.Loading
        )

    /**
     * This function acts as a manual trigger for the reactive `overviewData` flow.
     * It simply emits a new signal on the `refreshTrigger` flow, which causes the
     * `flatMapLatest` block above to re-execute.
     */
    open suspend fun fetchOverviewData() {
        refreshTrigger.emit(value = Unit)
    }
}