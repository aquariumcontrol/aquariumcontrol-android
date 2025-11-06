package com.laimburggasse.aquariumcontrol.ventilation.data.remote

import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class VentilationSetValsReader(private val url: String) {
    // This class reads the ventilation set values from the server using a GET request.
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readVentilationSetVals(): DataFetchResult<List<VentilationSetVals>> {
        val request: Request = Request.Builder().url(url).build()

        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }
                try {
                    val decodedData: List<VentilationSetVals> =
                        json.decodeFromString<List<VentilationSetVals>>(string = responseBody)
                    return DataFetchResult.Success(decodedData)
                } catch (e: Exception) {
                    return DataFetchResult.Error(
                        exception = e,
                        errorMessage = "Error decoding data sent from server: ${e.message}"
                    )
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
                errorMessage = "An unexpected error occurred during preparation of server communication."
            )
        }
    }
}