package com.laimburggasse.aquariumcontrol.timedata

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class TimeDataRepository @Inject constructor(private val sessionManager: SessionManager) {
    private val _timeDataSamples = MutableStateFlow< DataFetchResult<List<TimeDataSample>>>(
        value = DataFetchResult.Success(data = emptyList())
    )

    open val timeDataSamples = _timeDataSamples.asStateFlow()

    open suspend fun fetchTimeData() {
        withContext(context = Dispatchers.IO) {
            val url: String =
                "http://" + sessionManager.getControllerAddress() + WebApiUrls.DATA_GET_START_STOP
            val reader = TimeDataReader(urlWithoutParameters = url)
            when (val result: DataFetchResult<List<TimeDataStringSample>> = reader.readTimeDataSeries()) {
                is DataFetchResult.Success -> {
                    val timeDataStringList: List<TimeDataStringSample> = result.data
                    val timeDataSamples: List<TimeDataSample> = if (timeDataStringList.isNotEmpty()) {
                        timeDataStringList.map { timeDataStringSample ->
                            timeDataStringSample.transformToTimeDataSample()
                        }
                    } else {
                        emptyList()
                    }
                    _timeDataSamples.value = DataFetchResult.Success(data = timeDataSamples)
                }
                is DataFetchResult.Error -> {
                    _timeDataSamples.value = DataFetchResult.Error(
                        errorMessage = result.errorMessage,
                        exception = null
                    )
                }
            }
        }
    }
}