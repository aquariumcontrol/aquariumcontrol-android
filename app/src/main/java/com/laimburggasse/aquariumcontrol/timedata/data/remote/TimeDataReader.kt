package com.laimburggasse.aquariumcontrol.timedata.data.remote

import com.laimburggasse.aquariumcontrol.timedata.data.remote.dto.TimeDataStringSample
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
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
            val calendar24hAgo: Calendar = Calendar.getInstance()
            calendar24hAgo.add(Calendar.HOUR, -24)

            val calendarNow: Calendar = Calendar.getInstance()

            val dateTimeInMillis24hAgo: Long = calendar24hAgo.time.time
            val date24hAgo = Date(dateTimeInMillis24hAgo)
            val time24hAgo = Time(dateTimeInMillis24hAgo)

            val dateTimeInMillisNow: Long = calendarNow.time.time
            val dateNow = Date(dateTimeInMillisNow)
            val timeNow = Time(dateTimeInMillisNow)

            val optionalGetParams: Map<String, String> = mapOf(
                WebApiParams.START_DATE to date24hAgo.toString().replace(oldChar = ':', newChar =  '-'),
                WebApiParams.STOP_DATE to dateNow.toString().replace(oldChar = ':', newChar = '-'),
                WebApiParams.START_TIME to time24hAgo.toString(),
                WebApiParams.STOP_TIME to timeNow.toString())

            var url = "$urlWithoutParameters?"

            for ((key: String, value: String) in optionalGetParams) {
                url += "$key=$value&"
            }
            val request: Request = Request.Builder().url(url).build()

            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                try {
                    val decodedData:List<TimeDataStringSample> = json.
                        decodeFromString<List<TimeDataStringSample>>(string = responseBody)
                    return DataFetchResult.Success(decodedData)
                } catch (exception: Exception) {
                    return DataFetchResult.Error(
                        exception,
                        errorMessage = "Error decoding data sent from server: ${exception.message}"
                    )
                }
            } else {
                return DataFetchResult.Error(
                    exception = null,
                    errorMessage = "Server responded with error code: ${response.code}"
                )
            }
        } catch (exception: Exception) {
            return DataFetchResult.Error(
                exception,
                errorMessage = "An unexpected error occurred during preparation of server communication."
            )
        }
    }
}