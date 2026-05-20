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

package com.laimburggasse.aquariumcontrol.summary.testing

import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.summary.domain.model.SummaryData
import com.laimburggasse.aquariumcontrol.summary.presentation.SummaryUiState

object SummaryMockData {
    val mockSummaryData = SummaryData(
        timestamp = "2023-05-01 12:00:00",
        waterTemperature = 25.0,
        filteredWaterTemperature = 25.5,
        phValue = 7.0,
        filteredPhValue = 7.5,
        conductivity = 1000.0,
        filteredConductivity = 1005.0,
        tankLevelSwitchPosition = "HIGH",
        surfaceVentilationStatus = "ON",
        ambientTemperature = 22.0,
        ambientHumidity = 60.0,
        heatingStatus = "OFF"
    )
    val mockControllerProfile = ControllerProfile(
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
    )
    val mockSummaryUiStateSuccess = SummaryUiState.Success(mockSummaryData)
    val mockSummaryUiStateError = SummaryUiState.Error(errorMessage = "Mock Error loading data")
}