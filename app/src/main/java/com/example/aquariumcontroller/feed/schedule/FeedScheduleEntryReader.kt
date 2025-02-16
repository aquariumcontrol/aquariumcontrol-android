package com.example.aquariumcontroller.feed.schedule

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class FeedScheduleEntryReader(private val url: String) {
    // This class reads the Feed schedule entries from the server using a GET request.
    // The data is read using class FeedScheduleEntryImport which needs to be converted to FeedScheduleEntry
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readFeedScheduleEntries(): List<FeedScheduleEntryImport>? {
        val request = Request.Builder()
            .url(url)
            .build()

        Log.d(this::class.simpleName, "Request URL: $url")
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d(this::class.simpleName, "Response successful: $responseBody")
                responseBody?.let {
                    json.decodeFromString<List<FeedScheduleEntryImport>>(it)
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