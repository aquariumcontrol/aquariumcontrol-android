package com.laimburggasse.aquariumcontroller.feed.schedule

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

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    json.decodeFromString<List<FeedScheduleEntryImport>>(it)
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}