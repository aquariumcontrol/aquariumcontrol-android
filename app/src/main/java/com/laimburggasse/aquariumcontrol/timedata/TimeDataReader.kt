package com.laimburggasse.aquariumcontrol.timedata

import android.util.Log
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.sql.Date
import java.sql.Time
import java.util.Calendar

class TimeDataReader(private val urlWithoutParameters: String) {
    // This class reads the time series from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readTimeDataSeries(): DataFetchResult<List<TimeDataStringSample>> {
        try {
            val calendar24hAgo = Calendar.getInstance()
            calendar24hAgo.add(Calendar.HOUR, -24)

            val calendarNow = Calendar.getInstance()

            val dateTimeInMillis24hAgo: Long = calendar24hAgo.time.time
            val date24hAgo = Date(dateTimeInMillis24hAgo)
            val time24hAgo = Time(dateTimeInMillis24hAgo)

            val dateTimeInMillisNow = calendarNow.time.time
            val dateNow = Date(dateTimeInMillisNow)
            val timeNow = Time(dateTimeInMillisNow)

            val optionalGetParams: Map<String, String> = mapOf(
                WebApiParams.START_DATE to date24hAgo.toString().replace(':', '-'),
                WebApiParams.STOP_DATE to dateNow.toString().replace(':', '-'),
                WebApiParams.START_TIME to time24hAgo.toString(),
                WebApiParams.STOP_TIME to timeNow.toString())

            var url = "$urlWithoutParameters?"

            for ((key, value) in optionalGetParams) {
                url += "$key=$value&"
            }

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("TimeDataReader", "Successful http request.")
                val responseBody = response.body?.string()
                responseBody?.let {
                    try {
                        val decodedData = json.decodeFromString<List<TimeDataStringSample>>(it)
                        Log.d("TimeDataReader", "Successfully decoded ${decodedData.size} items.")
                        return DataFetchResult.Success(decodedData)
                    } catch (e: Exception) {
                        Log.e("TimeDataReader", "Error decoding JSON: ${e.message}", e)
                        return DataFetchResult.Error(e, "Error decoding data sent from server: ${e.message}")
                    }
                }
            } else {
                Log.d("TimeDataReader", "Error: ${response.code}")
                return DataFetchResult.Error(null, "Server responded with error code: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("TimeDatReader", "Unexpected error during data fetching: ${e.message}")
            return DataFetchResult.Error(e, "An unexpected error occurred during preparation of server communication.")
        }
        return DataFetchResult.Error(null, "An unexpected error occurred during data fetching.")
    }
}