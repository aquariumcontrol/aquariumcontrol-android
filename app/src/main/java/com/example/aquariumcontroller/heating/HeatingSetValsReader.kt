package com.example.aquariumcontroller.heating

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class HeatingSetValsReader(private val url: String) {
    // This class reads the Balling profiles from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readHeatingSetVals(): List<HeatingSetVals>? {
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    json.decodeFromString<List<HeatingSetVals>>(it)
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}