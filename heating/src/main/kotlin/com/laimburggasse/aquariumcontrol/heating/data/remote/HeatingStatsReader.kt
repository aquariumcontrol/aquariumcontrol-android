package com.laimburggasse.aquariumcontrol.heating.data.remote

import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.heating.data.remote.dto.HeatingStatsStringEntry
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class HeatingStatsReader(
    private val apiService: HeatingStatsApiService,
    private val url: String,
    username: String,
    password: String
) {
    // This class reads the heating stats from the server using a GET request.
    // The data is read using class HeatingStatsStringEntry which needs to be converted to HeatingStatsEntry.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val authHeaderValue: String = Credentials.basic(username, password)

    suspend fun readStatsEntry(): DataFetchResult<List<HeatingStatsStringEntry>> {
        return try {
            val response = apiService.getHeatingStats(
                url = url, authHeader = authHeaderValue
            )

            if (response.isSuccessful) {
                val decodedData = response.body()

                if (decodedData.isNullOrEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                if (decodedData.size > GlobalConstants.MAX_STATS_ENTRIES) {
                    return DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_TOO_MANY_ENTRIES
                    )
                }

                DataFetchResult.Success(decodedData)
            }
            else {
                DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_ERR_CODE + response.code()
                )
            }
        } catch (exception: Exception) {
            DataFetchResult.Error(
                exception,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + exception.message
            )
        }
    }
}