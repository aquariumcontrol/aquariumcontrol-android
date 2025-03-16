package com.example.aquariumcontroller.schedule

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

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    json.decodeFromString<List<ScheduleStringSetVal>>(it)
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}