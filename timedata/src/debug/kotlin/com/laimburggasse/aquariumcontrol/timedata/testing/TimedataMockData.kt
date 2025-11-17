package com.laimburggasse.aquariumcontrol.timedata.testing

import com.laimburggasse.aquariumcontrol.timedata.domain.model.TimeDataSample
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataSets
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataUiState
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.sin
import kotlin.random.Random

fun generateMockTimeData(): List<TimeDataSample> {
    val startTime = LocalDateTime.of(2024, 10, 24, 0, 0) // October 24th, 2024, 0:00 AM
    val samples = mutableListOf<TimeDataSample>()
    val random = Random(12345) // arbitrary but fixed seed to have reproducible results

    // Helper function to add noise
    fun addNoise(value: Double, max: Double): Double {
        val noise = random.nextDouble(-0.01 * max, 0.01 * max)
        return value + noise
    }

    // Low-pass filter (simple moving average)
    fun lowPassFilter(data: List<Double>, windowSize: Int): List<Double> {
        val filteredData = mutableListOf<Double>()
        for (i in data.indices) {
            val start = maxOf(0, i - windowSize)
            val end = minOf(data.size, i + windowSize + 1)
            val window = data.subList(start, end)
            filteredData.add(window.average())
        }
        return filteredData
    }

    val temperatures = mutableListOf<Double>()
    val pHs = mutableListOf<Double>()
    val conductivities = mutableListOf<Double>()
    val ambientTemperatures = mutableListOf<Double>()
    val ambientHumidities = mutableListOf<Double>()

    for (i in 0 until 24 * 60) { // 24 hours * 60 minutes
        val currentTime = startTime.plusMinutes(i.toLong())
        val secondsOfDay = currentTime.toEpochSecond(ZoneOffset.UTC)
        val timestamp = Timestamp(secondsOfDay * 1000)

        // Sinus profiles with noise
        val temperature = 26.0 + sin(2 * Math.PI * i / (24 * 60)) + addNoise(0.0, 27.0)
        temperatures.add(temperature)

        val pH = 7.5 + 0.5 * sin(2 * Math.PI * i / (24 * 60)) + addNoise(0.0, 8.0)
        pHs.add(pH)

        val conductivity =
            51000.0 + 1000 * sin(2 * Math.PI * i / (24 * 60)) + addNoise(0.0, 52000.0)
        conductivities.add(conductivity)

        val ambientTemperature =
            23.5 + 1.5 * sin(2 * Math.PI * i / (24 * 60)) + addNoise(0.0, 25.0)
        ambientTemperatures.add(ambientTemperature)

        val ambientHumidity = 55.0 + 5 * sin(2 * Math.PI * i / 60) + addNoise(0.0, 60.0)
        ambientHumidities.add(ambientHumidity)

        // Other values
        val refillInProgress = if (i % 70 == 0) 1 else 0
        val mainTankLevelSwitchStatus = if (i % 60 == 0) 0 else 1
        val surfaceVentilation = if (i in 12 * 60 until 17 * 60) 1 else 0 // 12:00 to 17:00
        val heater = if (i in 9 * 60 until 20 * 60) 0 else 1 // 9:00 to 20:00

        val timeDataSample = TimeDataSample(
            timestamp = timestamp,
            temperature = temperature,
            temperatureFiltered = temperature,
            pH = pH,
            pHFiltered = pH,
            conductivity = conductivity,
            conductivityFiltered = conductivity,
            ambientTemperature = ambientTemperature,
            ambientHumidity = ambientHumidity,
            refillInProgress = refillInProgress,
            mainTankLevelSwitchStatus = mainTankLevelSwitchStatus,
            surfaceVentilation = surfaceVentilation,
            heater = heater,
        )

        samples.add(
            timeDataSample
        )
    }

    // Apply low-pass filter
    val filteredTemperatures = lowPassFilter(temperatures, 10)
    val filteredPHs = lowPassFilter(pHs, 10)
    val filteredConductivities = lowPassFilter(conductivities, 10)

    // Update samples with filtered values
    for (i in samples.indices) {
        samples[i] = samples[i].copy(
            temperatureFiltered = filteredTemperatures[i],
            pHFiltered = filteredPHs[i],
            conductivityFiltered = filteredConductivities[i]
        )
    }

    return samples
}

object TimedataMockData {
    val mockTimeDataSets =
        TimeDataSets(timeData = generateMockTimeData())

    val mockTimeDataUiStateSuccess = TimeDataUiState.Success(timeDataSets = mockTimeDataSets)
    val mockTimeDataUiStateError = TimeDataUiState.Error(errorMessage = "Mock Error loading data")

    val mockTimeDataUiStateEmpty = TimeDataUiState.Empty
}