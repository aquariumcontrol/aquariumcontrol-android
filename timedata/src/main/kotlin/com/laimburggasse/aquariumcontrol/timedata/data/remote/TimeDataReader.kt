package com.laimburggasse.aquariumcontrol.timedata.data.remote

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.timedata.data.remote.dto.TimeDataStringSample
import okhttp3.Credentials

class TimeDataReader(
    private val apiService: TimeDataApiService,
    private val url: String,
    username: String,
    password: String
) {
    // This class reads the time series from the server using a GET request.
    private val authHeaderValue: String = Credentials.basic(username, password)

    suspend fun readTimeDataSeries(): DataFetchResult<List<TimeDataStringSample>> {
        return try {
            val response = apiService.getTimeData(
                url = url, authHeader = authHeaderValue
            )

            if (response.isSuccessful) {
                val decodedData = response.body()

                if (decodedData.isNullOrEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }

                if (decodedData.size > GlobalConstants.MAX_HISTORY_LOG_ENTRIES) {
                    return DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_TOO_MANY_ENTRIES
                    )
                }

                DataFetchResult.Success(decodedData)
            } else {
                DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_ERR_CODE + response.code()
                )
            }
        } catch (exception: Exception) {
            // Retrofit routes JSON mapping errors and network timeouts here
            DataFetchResult.Error(
                exception = exception,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + exception.message
            )
        }
    }
}