package com.laimburggasse.aquariumcontrol.overview

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
open class OverviewRepository(sessionManager: SessionManager) {
    // 1. Create a "trigger" flow.
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
            sessionManager.selectedControllerProfile,
            refreshTrigger
        ) { controller, _ ->
            controller
        }.flatMapLatest { controller ->
            flow {
                if (controller == null) {
                    emit(value = DataFetchResult.Error(exception = null, errorMessage = "No controller selected."))
                    return@flow
                }

                // @todo: update after introducing loading state
                // emit(DataFetchResult.Loading)

                val result: DataFetchResult<OverviewData> = withContext(context = Dispatchers.IO) {
                    val url = "http://${controller.address}${WebApiUrls.OVERVIEW_GET_DATA}"
                    val reader = OverviewDataReader(url)
                    reader.readOverviewData()
                }

                emit(value = result)
            }
        }

    /**
     * This function acts as a manual trigger for the reactive `overviewData` flow.
     * It simply emits a new signal on the `refreshTrigger` flow, which causes the
     * `flatMapLatest` block above to re-execute.
     */
    open suspend fun fetchOverviewData() {
        refreshTrigger.emit(value = Unit)
    }

    /**
     * We need to emit an initial trigger so that data loads the first time
     * the repository is created and a controller is available.
     */
    init {
        // @todo: trigger initial load by view model
        kotlinx.coroutines.GlobalScope.launch {
            refreshTrigger.emit(value = Unit)
        }
    }
}