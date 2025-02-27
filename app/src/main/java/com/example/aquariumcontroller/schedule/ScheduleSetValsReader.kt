package com.example.aquariumcontroller.schedule

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class ScheduleSetValsReader(private val url: String) {
    // This class reads the schedule set values from the server using a GET request
    // These are later converted into one instance of ScheduleSetVals
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readScheduleSetVals(): List<ScheduleStringSetVal>? {
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
                    json.decodeFromString<List<ScheduleStringSetVal>>(it)
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