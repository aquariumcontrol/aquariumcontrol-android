package com.laimburggasse.aquariumcontrol.overview.data.remote

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import okhttp3.Credentials

class OverviewDataReader(
    private val apiService: OverviewDataApiService,
    private val url: String,
    username: String,
    password: String
) {
    // This class reads the overview data from the server using a GET request.
    private val authHeaderValue: String = Credentials.basic(username, password)

    suspend fun readOverviewData(): DataFetchResult<OverviewData> {
        try {
            val response = apiService.getOverviewData(
                url = url, authHeader = authHeaderValue
            )
            if (response.isSuccessful) {
                val decodedData = response.body() ?: return DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_EMPTY_RESPONSE
                )
                return DataFetchResult.Success(decodedData)
            } else {
                return DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_ERR_CODE + response.code()
                )
            }
        } catch (e: Exception) { // recording exception in crashlytics
            Firebase.crashlytics.recordException(e)
            return DataFetchResult.Error(
                exception = e,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + e.message
            )
        }
    }
}