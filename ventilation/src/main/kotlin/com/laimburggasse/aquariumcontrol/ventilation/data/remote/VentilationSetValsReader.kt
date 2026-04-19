package com.laimburggasse.aquariumcontrol.ventilation.data.remote

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import okhttp3.Credentials

class VentilationSetValsReader(
    private val apiService: VentilationSetValsApiService,
    private val url: String,
    username: String,
    password: String
) {
    // This class reads the ventilation set values from the server using a GET request.
    private val authHeaderValue: String = Credentials.basic(username, password)

    suspend fun readVentilationSetVals(): DataFetchResult<List<VentilationSetVals>> {
        return try {
            val response = apiService.getVentilationSetVals(
                url = url, authHeader = authHeaderValue
            )

            if (response.isSuccessful) {
                val decodedData = response.body()

                if (decodedData.isNullOrEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }

                DataFetchResult.Success(decodedData)
            }
            else {
                DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_ERR_CODE + response.code()
                )
            }
        } catch (exception: Exception) { // recording exception in crashlytics
            Firebase.crashlytics.recordException(exception)
            DataFetchResult.Error(
                exception,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + exception.message
            )
        }
    }
}