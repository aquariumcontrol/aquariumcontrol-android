package com.laimburggasse.aquariumcontrol.overview.data.remote

import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class OverviewDataReader(private val url: String) {
    // This class reads the overview data from the server using a GET request.

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun readOverviewData(): DataFetchResult<OverviewData> {
        val request: Request = Request.Builder().url(url).build()

        val jsonArray: List<String> = try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                if (responseBody.isEmpty()) {
                    return DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_EMPTY_RESPONSE
                    )
                }
                if (!responseBody.startsWith(prefix = "[")) {
                    return DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_MALFORMED_JSON + responseBody
                    )
                }
                try {
                    json.decodeFromString<List<String>>(string = responseBody)
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
        if (jsonArray.size == 12) {
            val timestamp: String = jsonArray[0].replace(oldValue = " ", newValue = "T")
            val temperatureWater: Double = jsonArray[1].toDouble()
            val temperatureWaterFiltered: Double = jsonArray[2].toDouble()
            val pH: Double = jsonArray[3].toDouble()
            val pHFiltered: Double = jsonArray[4].toDouble()
            val conductivity: Double = jsonArray[5].toDouble()
            val conductivityFiltered: Double = jsonArray[6].toDouble()
            val tankLevelSwitchPosition: String = jsonArray[7]
            val surfaceVentilation: String = jsonArray[8]
            val ambientTemperature: Double = jsonArray[9].toDouble()
            val ambientHumidity: Double = jsonArray[10].toDouble()
            val heater: String = jsonArray[11]

            return DataFetchResult.Success(data = OverviewData(
                timestamp = timestamp,
                temperatureWater = temperatureWater,
                temperatureWaterFiltered = temperatureWaterFiltered,
                pH = pH,
                pHFiltered = pHFiltered,
                conductivity = conductivity,
                conductivityFiltered = conductivityFiltered,
                tankLevelSwitchPosition = tankLevelSwitchPosition,
                surfaceVentilation = surfaceVentilation,
                temperatureAmbient = ambientTemperature,
                humidityAmbient = ambientHumidity,
                heater = heater
            )
            )
        } else {
            return DataFetchResult.Error(
                exception = null,
                errorMessage = GlobalConstants.ERR_MSG_INVALID_JSON_ARRAY_SIZE + jsonArray.size
            )
        }
    }
}