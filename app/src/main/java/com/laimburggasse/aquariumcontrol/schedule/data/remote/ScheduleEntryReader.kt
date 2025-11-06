package com.laimburggasse.aquariumcontrol.schedule.data.remote

import com.laimburggasse.aquariumcontrol.schedule.data.remote.dto.ScheduleStringEntry
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class ScheduleEntryReader(private val url: String) {
    // This class reads the schedule set values from the server using a GET request
    // These are later converted into one instance of ScheduleSetVals
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readScheduleEntryList(): DataFetchResult<List<ScheduleStringEntry>> {
        val request: Request = Request.Builder().url(url).build()
        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                try {
                    val decodedData: List<ScheduleStringEntry> =
                        json.decodeFromString<List<ScheduleStringEntry>>(string = responseBody)
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