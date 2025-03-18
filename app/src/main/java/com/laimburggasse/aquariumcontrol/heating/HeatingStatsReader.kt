package com.laimburggasse.aquariumcontrol.heating

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class HeatingStatsReader(private val url: String) {
    // This class reads the heating stats from the server using a GET request.
    // The data is read using class HeatingStatsStringEntry which needs to be converted to HeatingStatsEntry.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readStatsEntry(): List<HeatingStatsStringEntry>? {
        val request = Request.Builder()
            .url(url)
            .build()

        Log.d(this::class.simpleName, "Request URL: $url")
        return try {
            Log.d(this::class.simpleName, "Trying to execute request")
            val response = client.newCall(request).execute()
            Log.d(this::class.simpleName, "Response Code: ${response.code}")
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d(this::class.simpleName, "ResponseBody=: $responseBody")
                responseBody?.let {
                    json.decodeFromString<List<HeatingStatsStringEntry>>(it)
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
