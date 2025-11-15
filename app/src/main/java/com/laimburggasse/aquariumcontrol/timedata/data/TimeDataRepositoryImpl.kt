package com.laimburggasse.aquariumcontrol.timedata.data

import com.laimburggasse.aquariumcontrol.GlobalConstants
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.timedata.data.remote.TimeDataReader
import com.laimburggasse.aquariumcontrol.timedata.data.remote.dto.TimeDataStringSample
import com.laimburggasse.aquariumcontrol.timedata.domain.model.TimeDataSample
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.lib.data.remote.WebApiUrls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class TimeDataRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope,
) {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    open val timeDataSamples: Flow<DataFetchResult<List<TimeDataSample>>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) }
        ) { controller, _ -> controller }
        .filterNotNull()
        .flatMapLatest { controller ->
            flow {
                // UI shall temporarily show loading state, while fetching data
                emit(value = DataFetchResult.Loading)

                // Perform the network request on a background thread.
                val readResult: DataFetchResult<List<TimeDataStringSample>> =
                    withContext(context = Dispatchers.IO) {
                        val url: String =
                            "http://" + sessionManager.getControllerAddress() + ":" +
                            sessionManager.getControllerPort() + WebApiUrls.DATA_GET_START_STOP
                        val reader = TimeDataReader(urlWithoutParameters = url)
                        reader.readTimeDataSeries()
                    }
                val processedResult: DataFetchResult<List<TimeDataSample>> = when (readResult) {
                    is DataFetchResult.Success -> {
                        val timeDataStringList: List<TimeDataStringSample> = readResult.data
                        val timeDataSamples: List<TimeDataSample> =
                            if (timeDataStringList.isNotEmpty()) {
                                timeDataStringList.map { timeDataStringSample ->
                                    timeDataStringSample.transformToTimeDataSample()
                                }
                            } else {
                                emptyList()
                            }
                        DataFetchResult.Success(data = timeDataSamples)
                    }
                    is DataFetchResult.Error -> {
                        DataFetchResult.Error(
                            errorMessage = readResult.errorMessage,
                            exception = null
                        )
                    }
                    is DataFetchResult.Loading -> DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_LOADING_TIME_DATA_FAILED
                    )
                }

                // Emit the final result (Success or Error) from the network call.
                emit(value = processedResult)
            }
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = DataFetchResult.Loading
        )

    /**
     * Public function to allow the ViewModel to trigger a manual refresh (e.g., for pull-to-refresh).
     */
    suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }
}