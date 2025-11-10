package com.laimburggasse.aquariumcontrol.heating.data.remote

import com.laimburggasse.aquariumcontrol.heating.data.remote.dto.HeatingStatsStringEntry
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class HeatingStatsReader(private val url: String) {
    // This class reads the heating stats from the server using a GET request.
    // The data is read using class HeatingStatsStringEntry which needs to be converted to HeatingStatsEntry.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readStatsEntry(): DataFetchResult<List<HeatingStatsStringEntry>> {
        val request: Request = Request.Builder().url(url).build()

        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                if (!responseBody.startsWith(prefix = "[")) {
                    return DataFetchResult.Error(
                        exception = null,
                        errorMessage = "Server responded with non-JSON data: $responseBody"
                    )
                }
                try {
                    val decodedData: List<HeatingStatsStringEntry> =
                        json.decodeFromString<List<HeatingStatsStringEntry>>(string = responseBody)
                    return DataFetchResult.Success(decodedData)
                }
                catch (e: Exception) {
                    return DataFetchResult.Error(
                        exception = e,
                        errorMessage = "Error decoding data sent from server: ${e.message}"
                    )
                }
            } else {
                return DataFetchResult.Error(
                    exception = null,
                    errorMessage = "Request failed with code ${response.code}"
                )
            }
        } catch (e: Exception) {
            return DataFetchResult.Error(
                exception = e,
                errorMessage = "Exception occurred: ${e.message}"
            )
        }
    }
}