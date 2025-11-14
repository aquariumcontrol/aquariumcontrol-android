package com.laimburggasse.aquariumcontrol.feed.data.remote

import com.laimburggasse.aquariumcontrol.GlobalConstants
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class FeedHistoryLogEntryReader(private val url: String) {
    // This class will read the feed events from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readFeedEvents(): DataFetchResult<List<FeedHistoryLogEntry>> {
        val request: Request = Request.Builder().url(url).build()

        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                if (!responseBody.startsWith(prefix = "[")) {
                    return DataFetchResult.Error(
                        exception = null,
                        errorMessage = "Server responded with non-JSON data: $responseBody"
                    )
                }
                try {
                    val decodedData: List<FeedHistoryLogEntry> =
                        json.decodeFromString<List<FeedHistoryLogEntry>>(string = responseBody)
                    if (decodedData.size > GlobalConstants.MAX_HISTORY_LOG_ENTRIES) {
                        return DataFetchResult.Error(
                            exception = null,
                            errorMessage = "Server responded with more than ${GlobalConstants.MAX_HISTORY_LOG_ENTRIES} entries."
                        )
                    }
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
        } catch (e: Exception) {
            return DataFetchResult.Error(
                exception = e,
                errorMessage = "An unexpected error occurred during preparation of server communication."
            )
        }
    }
}