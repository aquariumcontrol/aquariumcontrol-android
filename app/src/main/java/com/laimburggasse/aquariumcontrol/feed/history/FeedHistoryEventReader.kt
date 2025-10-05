package com.laimburggasse.aquariumcontrol.feed.history

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class FeedHistoryEventReader(private val url: String) {
    // This class will read the feed events from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readFeedEvents(): List<FeedHistoryEvent>? {
        val request = Request.Builder()
            .url(url)
            .build()

        Log.d(this::class.simpleName, "Request URL: $url")
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body.string()
                Log.d(this::class.simpleName, "Response successful: $responseBody")
                if (responseBody.isEmpty()) {
                    Log.d(this::class.simpleName, "Response body is empty")
                    emptyList()
                }
                else {
                    json.decodeFromString<List<FeedHistoryEvent>>(responseBody)
                }
            } else {
                Log.d(this::class.simpleName, "Error: ${response.code} - ${response.message}")
                null
            }
        } catch (e: Exception) {
            Log.d(this::class.simpleName, "Exception: ${e.message}")
            null
        }
    }
}