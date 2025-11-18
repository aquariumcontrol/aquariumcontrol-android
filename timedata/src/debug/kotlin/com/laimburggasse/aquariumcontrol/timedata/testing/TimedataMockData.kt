/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.timedata.testing

import android.os.Build
import androidx.annotation.RequiresApi
import com.laimburggasse.aquariumcontrol.timedata.domain.model.TimeDataSample
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataSets
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataUiState
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.sin
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
fun generateMockTimeData(): List<TimeDataSample> {
    val startTime: LocalDateTime = LocalDateTime.of(2024, 10, 24, 0, 0) // October 24th, 2024, 0:00 AM
    val samples: MutableList<TimeDataSample> = mutableListOf()
    val random = Random(seed = 12345) // arbitrary but fixed seed to have reproducible results

    // Helper function to add noise
    fun addNoise(value: Double, max: Double): Double {
        val noise: Double = random.nextDouble(from = -0.01 * max, until = 0.01 * max)
        return value + noise
    }

    // Low-pass filter (simple moving average)
    fun lowPassFilter(data: List<Double>, windowSize: Int): List<Double> {
        val filteredData: MutableList<Double> = mutableListOf()
        for (i: Int in data.indices) {
            val start: Int = maxOf(a = 0, b = i - windowSize)
            val end: Int = minOf(a = data.size, b = i + windowSize + 1)
            val window: List<Double> = data.subList(start, end)
            filteredData.add(window.average())
        }
        return filteredData
    }

    val temperatures: MutableList<Double> = mutableListOf()
    val pHs: MutableList<Double> = mutableListOf()
    val conductivities: MutableList<Double> = mutableListOf()
    val ambientTemperatures: MutableList<Double> = mutableListOf()
    val ambientHumidities: MutableList<Double> = mutableListOf()

    for (i: Int in 0 until 24 * 60) { // 24 hours * 60 minutes
        val currentTime = startTime.plusMinutes(i.toLong())
        val secondsOfDay: Long = currentTime.toEpochSecond(ZoneOffset.UTC)
        val timestamp = Timestamp(secondsOfDay * 1000)

        // Sinus profiles with noise
        val temperature: Double = 26.0 + sin(x = 2 * Math.PI * i / (24 * 60)) + addNoise(value = 0.0, max = 27.0)
        temperatures.add(temperature)

        val pH: Double = 7.5 + 0.5 * sin(x = 2 * Math.PI * i / (24 * 60)) + addNoise(value = 0.0, max = 8.0)
        pHs.add(pH)

        val conductivity: Double =
            51000.0 + 1000 * sin(x = 2 * Math.PI * i / (24 * 60)) + addNoise(value = 0.0, max = 52000.0)
        conductivities.add(conductivity)

        val ambientTemperature: Double =
            23.5 + 1.5 * sin(x = 2 * Math.PI * i / (24 * 60)) + addNoise(value = 0.0, max = 25.0)
        ambientTemperatures.add(ambientTemperature)

        val ambientHumidity: Double = 55.0 + 5 * sin(x = 2 * Math.PI * i / 60) + addNoise(value = 0.0, max = 60.0)
        ambientHumidities.add(ambientHumidity)

        // Other values
        val refillInProgress: Int = if (i % 70 == 0) 1 else 0
        val mainTankLevelSwitchStatus: Int = if (i % 60 == 0) 0 else 1
        val surfaceVentilation: Int = if (i in 12 * 60 until 17 * 60) 1 else 0 // 12:00 to 17:00
        val heater: Int = if (i in 9 * 60 until 20 * 60) 0 else 1 // 9:00 to 20:00

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
    val filteredTemperatures: List<Double> = lowPassFilter(data = temperatures, windowSize = 10)
    val filteredPHs: List<Double> = lowPassFilter(data = pHs, windowSize = 10)
    val filteredConductivities: List<Double> = lowPassFilter(data = conductivities, windowSize = 10)

    // Update samples with filtered values
    for (i: Int in samples.indices) {
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