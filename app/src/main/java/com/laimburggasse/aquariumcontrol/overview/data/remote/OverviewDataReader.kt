package com.laimburggasse.aquariumcontrol.overview.data.remote

import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
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
                json.decodeFromString<List<String>>(string = responseBody)
            } else {
                return DataFetchResult.Error(
                    exception = null,
                    errorMessage = "Server responded with error code: ${response.code}"
                )
            }
        } catch (exception: Exception) {
            return DataFetchResult.Error(
                exception,
                errorMessage = "An unexpected error occurred during preparation of server communication: ${exception.message}"
            )
        }
        if (jsonArray.size == 12) {
            val timestamp = jsonArray[0].replace(" ", "T")
            val temperatureWater = jsonArray[1].toDouble()
            val temperatureWaterFiltered = jsonArray[2].toDouble()
            val pH = jsonArray[3].toDouble()
            val pHFiltered = jsonArray[4].toDouble()
            val conductivity = jsonArray[5].toDouble()
            val conductivityFiltered = jsonArray[6].toDouble()
            val tankLevelSwitchPosition = jsonArray[7]
            val surfaceVentilation = jsonArray[8]
            val ambientTemperature = jsonArray[9].toDouble()
            val ambientHumidity = jsonArray[10].toDouble()
            val heater = jsonArray[11]

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
                errorMessage = "Invalid JSON array size: ${jsonArray.size}"
            )
        }
    }
}