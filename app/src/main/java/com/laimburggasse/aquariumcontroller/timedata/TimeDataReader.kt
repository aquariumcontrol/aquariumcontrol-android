package com.laimburggasse.aquariumcontroller.timedata

import com.laimburggasse.aquariumcontroller.webapi.WebApiParams
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

    fun readTimeDataSeries(): List<TimeDataStringSample>? {
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

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    json.decodeFromString<List<TimeDataStringSample>>(it)
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}