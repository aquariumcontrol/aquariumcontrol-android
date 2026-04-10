package com.laimburggasse.aquariumcontrol.heating.data.remote

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.heating.data.remote.dto.HeatingStatsStringEntry
import okhttp3.Credentials

class HeatingStatsReader(
    private val apiService: HeatingStatsApiService,
    private val url: String,
    username: String,
    password: String
) {
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