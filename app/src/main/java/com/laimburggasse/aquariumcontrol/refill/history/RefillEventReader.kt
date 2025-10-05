package com.laimburggasse.aquariumcontrol.refill.history

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request


class RefillEventReader(private val url: String) {
    // This class reads the refill events from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readRefillEvents(mainViewModel: MainViewModel? = null): DataFetchResult<List<RefillEvent>> {
        val request = Request.Builder()
            .url(url)
            .build()

        val isControllerLoaded = mainViewModel?.isControllerLoaded() == true
        if (!isControllerLoaded) {
            return DataFetchResult.Error(null, "Controller not loaded.")
        }

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body.string()
                try {
                    val decodedData = json.decodeFromString<List<RefillEvent>>(responseBody)
                    return DataFetchResult.Success(decodedData)
                } catch (e: Exception) {
                    return DataFetchResult.Error(e, "Error decoding data sent from server: ${e.message}")
                }
            } else {
                return DataFetchResult.Error(null, "Server responded with error code: ${response.code}")
            }
        } catch (e: Exception) {
            return DataFetchResult.Error(e, "An unexpected error occurred during preparation of server communication.")
        }
    }
}