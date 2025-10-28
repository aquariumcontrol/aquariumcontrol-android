package com.laimburggasse.aquariumcontrol.balling.common

import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class BallingProfileReader(private val url: String) {
    // This class reads the Balling profiles from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readBallingProfiles(): DataFetchResult<List<BallingProfile>> {
        val request: Request = Request.Builder().url(url).build()

        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                else {
                    val decodedData: List<BallingProfile> =
                        json.decodeFromString<List<BallingProfile>>(string = responseBody)
                    return DataFetchResult.Success(decodedData)
                }
            } else {
                return DataFetchResult.Error(
                    exception = null,
                    errorMessage = "Server responded with error code: ${response.code}"
                )
            }
        } catch (e: Exception) {
            return DataFetchResult.Error(
                exception = e,
                errorMessage = "An unexpected error occurred during preparation of server communication: ${e.message}"
            )
        }
    }
}