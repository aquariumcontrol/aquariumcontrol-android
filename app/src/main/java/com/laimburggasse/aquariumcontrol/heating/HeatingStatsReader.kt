package com.laimburggasse.aquariumcontrol.heating

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
                    return DataFetchResult.Success(emptyList())
                }
                val decodedData: List<HeatingStatsStringEntry> =
                    json.decodeFromString<List<HeatingStatsStringEntry>>(string = responseBody)
                return DataFetchResult.Success(decodedData)
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
