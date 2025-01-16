package com.example.aquariumcontroller.refill

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request


class RefillEventReader(private val url: String) {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readRefillEvents(): List<RefillEvent>? {
        val request = Request.Builder()
            .url(url)
            .build()

        Log.d("RefillEventReader", "Request URL: $url")
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("RefillEventReader", "Response successful: $responseBody")
                responseBody?.let {
                    json.decodeFromString<List<RefillEvent>>(it)
                }
            } else {
                Log.d("RefillEventReader", "Error: ${response.code} - ${response.message}")
                null
            }
        } catch (e: Exception) {
            Log.d("RefillEventReader", "Exception: ${e.message}")
            null
        }
    }
}