package com.laimburggasse.aquariumcontrol.ventilation

import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class VentilationSetValsReader(private val url: String) {
    // This class reads the ventilation set values from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readVentilationSetVals(): DataFetchResult<List<VentilationSetVals>> {
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body.string()
                try {
                    val decodedData = json.decodeFromString<List<VentilationSetVals>>(responseBody)
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