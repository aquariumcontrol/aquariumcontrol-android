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

package com.laimburggasse.aquariumcontrol.heating.testing

import com.laimburggasse.aquariumcontrol.heating.data.remote.dto.HeatingStatsStringEntry
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingSetValsUiState
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingStatsUiState

fun createMockStatsData(): List<HeatingStatsEntry> {
    val mockHeatingStatsStringEntries: List<HeatingStatsStringEntry> = listOf(
        HeatingStatsStringEntry(
            dateString = "2025-01-12 00:00:00",
            energy = 1988.17,
            ambientTemperatureAverage = 22.6869,
            waterTemperatureAverage = 25.4176,
            heatingControlRuntime = 36235
        ),
        HeatingStatsStringEntry(
            dateString = "2025-01-25 00:00:00",
            energy = 18.7222,
            ambientTemperatureAverage = 18.9717,
            waterTemperatureAverage = 25.4007,
            heatingControlRuntime = 389
        )
    )

    return mockHeatingStatsStringEntries.map {
        it.transformToHeatingStatsEntry()
    }
}

object HeatingMockData {
    val mockHeatingStatsData = createMockStatsData()
    val mockHeatingStatsUiState = HeatingStatsUiState.Success(
        heatingStats = mockHeatingStatsData
    )
    val mockHeatingSetVals = HeatingSetVals(
        heatingSwitchOffTemp = 27.5,
        heatingSwitchOnTemp = 23.5
    )
    val mockHeatingSetValsUiState = HeatingSetValsUiState.Success(
        heatingSetVals = mockHeatingSetVals
    )
}
