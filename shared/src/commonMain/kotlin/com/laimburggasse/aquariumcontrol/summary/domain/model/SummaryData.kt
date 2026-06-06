/*
 *  Copyright 2026 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.summary.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing the complete current state of the aquarium.
 *
 * @property timestamp Time of the reading in epoch milliseconds.
 * @property waterTemperature Current water temperature in Celsius.
 * @property filteredWaterTemperature Smoothed water temperature value.
 * @property phValue Current water pH value.
 * @property filteredPhValue Smoothed water pH value.
 * @property conductivity Current water conductivity.
 * @property filteredConductivity Smoothed water conductivity.
 * @property tankLevelSwitchPosition Descriptive status of the level switch.
 * @property surfaceVentilationStatus Descriptive status of surface ventilation.
 * @property ambientTemperature Current ambient room temperature.
 * @property ambientHumidity Current ambient room humidity percentage.
 * @property heatingStatus Descriptive status of the water heater.
 */
@Serializable
data class SummaryData(
    val timestamp: Long,
    val waterTemperature: Double,
    val filteredWaterTemperature: Double,
    val phValue: Double,
    val filteredPhValue: Double,
    val conductivity: Double,
    val filteredConductivity: Double,
    val tankLevelSwitchPosition: String,
    val surfaceVentilationStatus: String,
    val ambientTemperature: Double,
    val ambientHumidity: Double,
    val heatingStatus: String,
)