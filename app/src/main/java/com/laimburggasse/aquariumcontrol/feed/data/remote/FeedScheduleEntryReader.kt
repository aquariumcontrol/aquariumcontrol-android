package com.laimburggasse.aquariumcontrol.feed.data.remote

import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class FeedScheduleEntryReader(private val url: String) {
    // This class reads the Feed schedule entries from the server using a GET request.
    // The data is read using class FeedScheduleEntryImport which needs to be converted to FeedScheduleEntry
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readFeedScheduleEntries(): DataFetchResult<List<FeedScheduleEntryImport>> {
        val request: Request = Request.Builder().url(url).build()

        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                else {
                    val decodedData: List<FeedScheduleEntryImport> =
                        json.decodeFromString<List<FeedScheduleEntryImport>>(string = responseBody)
                    return DataFetchResult.Success(data = decodedData)
                }
            } else {
                return DataFetchResult.Error(
                    exception = null,
                    errorMessage = "Server responded with error code: ${response.code}"
                )
            }
        } catch (e: Exception) {
            return DataFetchResult.Error(
                exception = e,
                errorMessage = "An unexpected error occurred during preparation of server communication: ${e.message}"
            )
        }
    }
}