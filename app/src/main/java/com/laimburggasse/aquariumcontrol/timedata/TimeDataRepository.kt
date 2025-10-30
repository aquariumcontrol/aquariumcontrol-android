package com.laimburggasse.aquariumcontrol.timedata

import android.util.Log
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class TimeDataRepository @Inject constructor(private val sessionManager: SessionManager) {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    open val timeDataSamples: Flow<DataFetchResult<List<TimeDataSample>>> =
        combine (
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) }
        ) { controller, _ -> controller }
        .filterNotNull()
        .flatMapLatest { controller ->
            flow {
                // @todo: implement Loading state
                // emit(DataFetchResult.Error(exception = null, errorMessage = "still null"))

                // Perform the network request on a background thread.
                val readResult: DataFetchResult<List<TimeDataStringSample>> = withContext(context = Dispatchers.IO) {
                    val url = "http://" + sessionManager.getControllerAddress() + WebApiUrls.DATA_GET_START_STOP
                    val reader = TimeDataReader(urlWithoutParameters = url)
                    reader.readTimeDataSeries()
                }
                val processedResult: DataFetchResult<List<TimeDataSample>> = when (readResult) {
                    is DataFetchResult.Success -> {
                        val timeDataStringList: List<TimeDataStringSample> = readResult.data
                        val timeDataSamples: List<TimeDataSample> = if (timeDataStringList.isNotEmpty()) {
                            timeDataStringList.map { timeDataStringSample ->
                                timeDataStringSample.transformToTimeDataSample()
                            }
                        } else {
                            Log.d("TimeDataRepository", "readResult.data is empty")
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
                }

                // Emit the final result (Success or Error) from the network call.
                emit(value = processedResult)
                }
            }

    // @todo: implement pull-to-refresh
    /**
     * Public function to allow the ViewModel to trigger a manual refresh (e.g., for pull-to-refresh).
     */
    suspend fun triggerRefresh() {
        refreshTrigger.emit(Unit)
    }
}