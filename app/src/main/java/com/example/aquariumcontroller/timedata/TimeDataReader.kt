package com.example.aquariumcontroller.timedata

import android.util.Log
import com.example.aquariumcontroller.refill.history.RefillEvent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class TimeDataReader(private val url: String) {
    // This class reads the time series from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readTimeDataSeries(): List<RefillEvent>? {
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
                    json.decodeFromString<List<RefillEvent>>(it)
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