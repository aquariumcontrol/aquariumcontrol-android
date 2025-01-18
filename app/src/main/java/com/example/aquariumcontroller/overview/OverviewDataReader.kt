package com.example.aquariumcontroller.overview

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class OverviewDataReader(private val url: String) {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    @RequiresApi(Build.VERSION_CODES.O)
    fun readOverviewData(): OverviewData? {
        val request = Request.Builder()
            .url(url)
            .build()

        Log.d("OverviewDataReader", "Request URL: $url")
        val jsonArray = try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("OverviewDataReader", "Response successful: $responseBody")
                responseBody?.let {
                    json.decodeFromString<List<String>>(it)
                }
            } else {
                Log.d("OverviewDataReader", "Error: ${response.code} - ${response.message}")
                null
            }
        } catch (e: Exception) {
            Log.d("OverviewDataReader", "Exception: ${e.message}")
            null
        }
        if (jsonArray != null) {
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

                return OverviewData(
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
            } else {
                Log.d("OverviewDataReader", "Invalid JSON array size: ${jsonArray.size}")
                return null
            }
        } else {
            return null
        }
    }
}