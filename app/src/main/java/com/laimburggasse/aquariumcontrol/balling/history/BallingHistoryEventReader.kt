package com.laimburggasse.aquariumcontrol.balling.history

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class BallingHistoryEventReader(private val url: String) {
    // This class will read the Balling dosing events from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readBallingEvents(mainViewModel: MainViewModel? = null): DataFetchResult<List<BallingHistoryDosingEvent>> {
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
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                try {
                    val decodedData: List<BallingHistoryDosingEvent> =
                        json.decodeFromString<List<BallingHistoryDosingEvent>>(string = responseBody)
                    return DataFetchResult.Success(decodedData)
                } catch (e: Exception) {
                    return DataFetchResult.Error(
                        exception = e,
                        errorMessage = "Error decoding data sent from server: ${e.message}"
                    )
                }
            } else {
                return DataFetchResult.Error(exception = null, errorMessage = "Server responded with error code: ${response.code}")
            }
        } catch (e: Exception) {
            return DataFetchResult.Error(exception = e, errorMessage = "An unexpected error occurred during preparation of server communication.")
        }
    }
}