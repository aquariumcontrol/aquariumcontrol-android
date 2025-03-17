package com.laimburggasse.aquariumcontroller.feed

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class FeedProfileReader(private val url: String) {
    // This class reads the Feed profiles from the server using a GET request.
    // The data is read using class FeedProfileImport which needs to be converted to FeedProfile.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readFeedProfiles(): List<FeedProfileImport>? {
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    json.decodeFromString<List<FeedProfileImport>>(it)
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}