package com.laimburggasse.aquariumcontrol.heating.data

import com.laimburggasse.aquariumcontrol.GlobalConstants
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.controller.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import com.laimburggasse.aquariumcontrol.heating.data.remote.HeatingStatsReader
import com.laimburggasse.aquariumcontrol.heating.data.remote.dto.HeatingStatsStringEntry
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.lib.data.remote.WebApiUrls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class HeatingStatsRepositoryImpl @Inject constructor(
    sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope
) {
    // for triggering manual refreshes
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    open val heatingStatsFlow: StateFlow<DataFetchResult<List<HeatingStatsEntry>>> = combine(
        flow = sessionManager.selectedControllerProfile,
        flow2 = refreshTrigger.onStart { emit(value = Unit) } // Triggers the first fetch
    ) { controller, _ ->
        controller
    }
    .filterNotNull()
    .flatMapLatest { controller: ControllerProfile ->
        flow {
            // UI shall temporarily show loading state, while fetching data
            emit(value = DataFetchResult.Loading)

            val result: DataFetchResult<List<HeatingStatsEntry>> =
                withContext(context = Dispatchers.IO) {
                    val url = "http://${controller.address}:${controller.port}${WebApiUrls.HEATING_GET_STATS}"
                    val heatingStatsReader = HeatingStatsReader(url)
                    val stringResult: DataFetchResult<List<HeatingStatsStringEntry>> =
                        heatingStatsReader.readStatsEntry()

                    // Transform the result before returning it from the background thread
                    when (stringResult) {
                        is DataFetchResult.Success -> {
                            val heatingStats: List<HeatingStatsEntry> = stringResult.data.map {
                                it.transformToHeatingStatsEntry()
                            }
                            DataFetchResult.Success(data = heatingStats)
                        }
                        is DataFetchResult.Error -> {
                            // Propagate the error result
                            DataFetchResult.Error(
                                exception = stringResult.exception,
                                errorMessage = stringResult.errorMessage
                            )
                        }
                        is DataFetchResult.Loading -> DataFetchResult.Error(
                            exception = null,
                            errorMessage = GlobalConstants.ERR_MSG_LOADING_HEATING_STATS_FAILED
                        )
                    }
                }
            emit(value = result)
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.Companion.WhileSubscribed(5000L),
        initialValue = DataFetchResult.Loading
    )

    /**
     * Triggers a manual refresh of the heating stats.
     */
    open suspend fun triggerRefresh() {
        refreshTrigger.emit(Unit)
    }
}