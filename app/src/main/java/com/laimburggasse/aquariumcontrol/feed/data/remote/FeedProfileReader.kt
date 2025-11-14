package com.laimburggasse.aquariumcontrol.feed.data.remote

import com.laimburggasse.aquariumcontrol.GlobalConstants
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class FeedProfileReader(private val url: String) {
    // This class reads the Feed profiles from the server using a GET request.
    // The data is read using class FeedProfileImport which needs to be converted to FeedProfile.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readFeedProfiles(): DataFetchResult<List<FeedProfileImport>> {
        val request: Request = Request.Builder().url(url).build()

        return try {
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
                    val decodedData: List<FeedProfileImport> =
                        json.decodeFromString<List<FeedProfileImport>>(string = responseBody)
                    if (decodedData.size > GlobalConstants.MAX_PROFILE_ENTRIES) {
                        return DataFetchResult.Error(
                            exception = null,
                            errorMessage = "Server responded with more than ${GlobalConstants.MAX_PROFILE_ENTRIES} entries."
                        )
                    }
                    DataFetchResult.Success(data = decodedData)
                } catch (exception: Exception) {
                    return DataFetchResult.Error(
                        exception,
                        errorMessage = "Error decoding data sent from server: ${exception.message}"
                    )
                }
            } else {
                DataFetchResult.Error(
                    exception = null,
                    errorMessage = "Server responded with error code: ${response.code}"
                )
            }
        } catch (e: Exception) {
            DataFetchResult.Error(
                exception = e,
                errorMessage = "An unexpected error occurred during preparation of server communication."
            )
        }
    }
}