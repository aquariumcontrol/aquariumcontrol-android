package com.laimburggasse.aquariumcontrol.schedule

import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class ScheduleEntryReader(private val url: String) {
    // This class reads the schedule set values from the server using a GET request
    // These are later converted into one instance of ScheduleSetVals
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readScheduleEntryList(): DataFetchResult<List<ScheduleStringEntry>> {
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                try {
                    val decodedData = json.decodeFromString<List<ScheduleStringEntry>>(responseBody)
                    return DataFetchResult.Success(decodedData)
                } catch (e: Exception) {
                    return DataFetchResult.Error(e, "Error decoding data sent from server: ${e.message}")
                }
            } else {
                return DataFetchResult.Error(null, "Server responded with error code: ${response.code}")
            }
        } catch (e: Exception) {
            return DataFetchResult.Error(e, "An unexpected error occurred during preparation of server communication.")
        }
    }
}