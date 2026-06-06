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

package com.laimburggasse.aquariumcontrol.timedata.domain.model

/**
 * Domain model representing a single point-in-time telemetry sample.
 *
 * @property timestamp Time of the sample in epoch milliseconds.
 * @property temperature Water temperature.
 * @property temperatureFiltered Smoothed water temperature.
 * @property pH Water pH level.
 * @property pHFiltered Smoothed water pH level.
 * @property conductivity Water conductivity.
 * @property conductivityFiltered Smoothed water conductivity.
 * @property refillInProgress Status of the refill actuator.
 * @property mainTankLevelSwitchStatus Status of the water level switch.
 * @property surfaceVentilation Status of the ventilation actuator.
 * @property ambientTemperature Ambient room temperature.
 * @property ambientHumidity Ambient room humidity.
 * @property heater Status of the water heater actuator.
 */
data class TimeDataSample(
    val timestamp: Long,
    val temperature: Double,
    val temperatureFiltered: Double,
    val pH: Double,
    val pHFiltered: Double,
    val conductivity: Double,
    val conductivityFiltered: Double,
    val refillInProgress: Int,
    val mainTankLevelSwitchStatus: Int,
    val surfaceVentilation: Int,
    val ambientTemperature: Double,
    val ambientHumidity: Double,
    val heater: Int
) {
    /**
     * Converts the [timestamp] from milliseconds to seconds.
     *
     * @return The timestamp in seconds.
     */
    fun timeSeconds(): Long {
        return timestamp / 1000
    }
}