package com.laimburggasse.aquariumcontrol.timedata.data.remote

import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.timedata.data.remote.dto.TimeDataStringSample
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.sql.Date
import java.sql.Time
import java.util.Calendar
import kotlin.collections.iterator

class TimeDataReader(private val urlWithoutParameters: String) {
    // This class reads the time series from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readTimeDataSeries(): DataFetchResult<List<TimeDataStringSample>> {
        try {
            val url: String = urlWithoutParameters

            val request: Request = Request.Builder().url(url).build()

            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                if (!responseBody.startsWith(prefix = "[")) {
                    return DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_MALFORMED_JSON + responseBody
                    )
                }
                try {
                    val decodedData:List<TimeDataStringSample> = json.
                        decodeFromString<List<TimeDataStringSample>>(string = responseBody)
                    if (decodedData.size > GlobalConstants.MAX_TIME_DATA_SAMPLES) {
                        return DataFetchResult.Error(
                            exception = null,
                            errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_TOO_MANY_ENTRIES
                        )
                    }
                    return DataFetchResult.Success(decodedData)
                } catch (exception: Exception) {
                    return DataFetchResult.Error(
                        exception,
                        errorMessage = GlobalConstants.ERR_MSG_DECODING_DATA + exception.message
                    )
                }
            } else {
                return DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_ERR_CODE + response.code
                )
            }
        } catch (exception: Exception) {
            return DataFetchResult.Error(
                exception,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + exception.message
            )
        }
    }
}