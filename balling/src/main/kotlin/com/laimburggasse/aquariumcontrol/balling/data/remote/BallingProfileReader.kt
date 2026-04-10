package com.laimburggasse.aquariumcontrol.balling.data.remote

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import okhttp3.Credentials

class BallingProfileReader(
    private val apiService: BallingProfileApiService,
    private val url: String,
    username: String,
    password: String
) {
    // This class reads the Balling profiles from the server using a GET request.
    private val authHeaderValue: String = Credentials.basic(username, password)

    suspend fun readBallingProfiles(): DataFetchResult<List<BallingProfile>> {
        try {
            val response = apiService.getBallingProfiles(
                url = url, authHeader = authHeaderValue
            )
            if (response.isSuccessful) {
                val decodedData = response.body()

                if (decodedData.isNullOrEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }

                return DataFetchResult.Success(decodedData)
            } else {
                return DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_ERR_CODE + response.code()
                )
            }
        } catch (e: Exception) {
            return DataFetchResult.Error(
                exception = e,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + e.message
            )
        }
    }
}