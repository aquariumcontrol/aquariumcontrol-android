package com.laimburggasse.aquariumcontrol.overview.data.remote

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Credentials
class OverviewDataReader(
    private val url: String,
    username: String,
    password: String
) {
    // This class reads the overview data from the server using a GET request.

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val authHeaderValue: String = Credentials.basic(username, password)

    fun readOverviewData(): DataFetchResult<OverviewData> {
        val request: Request = Request.Builder().url(url).header(
            name = "Authorization", authHeaderValue
        ).get().build()
        val overviewData: OverviewData = try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_EMPTY_RESPONSE
                    )
                }
                if (!responseBody.startsWith(prefix = "{")) {
                    return DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_MALFORMED_JSON + responseBody
                    )
                }
                try {
                    json.decodeFromString<OverviewData>(string = responseBody)
                }
                catch (exception: Exception) {
                    return DataFetchResult.Error(
                        exception,
                        errorMessage = GlobalConstants.ERR_MSG_DECODING_DATA + exception.message
                    )
                }
            } else {
                return DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_ERR_CODE + response.code
                )
            }
        } catch (exception: Exception) {
            return DataFetchResult.Error(
                exception,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + exception.message
            )
        }
        // return the successfully deserialized data
        return DataFetchResult.Success(data = overviewData)
    }
}