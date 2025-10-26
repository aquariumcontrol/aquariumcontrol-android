package com.laimburggasse.aquariumcontrol.feed.history

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class FeedHistoryLogEntryReader(private val url: String) {
    // This class will read the feed events from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readFeedEvents(sessionManager: SessionManager? = null): DataFetchResult<List<FeedHistoryLogEntry>> {
        val request = Request.Builder()
            .url(url)
            .build()

        val isControllerLoaded = sessionManager?.isControllerLoaded() == true
        if (!isControllerLoaded) {
            return DataFetchResult.Error(exception = null, errorMessage = "Controller not loaded.")
        }

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                try {
                    val decodedData: List<FeedHistoryLogEntry> =
                        json.decodeFromString<List<FeedHistoryLogEntry>>(responseBody)
                    return DataFetchResult.Success(decodedData)
                } catch (e: Exception) {
                    return DataFetchResult.Error(
                        exception = e,
                        errorMessage = "Error decoding data sent from server: ${e.message}"
                    )
                }
            } else {
                return DataFetchResult.Error(exception = null, errorMessage = "Server responded with error code: ${response.code}")
            }
        } catch (e: Exception) {
            return DataFetchResult.Error(exception = e, errorMessage = "An unexpected error occurred during preparation of server communication.")
        }
    }
}