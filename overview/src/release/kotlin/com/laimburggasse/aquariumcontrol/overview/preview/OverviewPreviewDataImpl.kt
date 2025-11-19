/*
 *  Copyright 2025 Uwe Martin
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

package com.laimburggasse.aquariumcontrol.overview.preview

import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import com.laimburggasse.aquariumcontrol.overview.presentation.OverviewPreviewData
import com.laimburggasse.aquariumcontrol.overview.presentation.OverviewUiState

@Suppress("unused")
object OverviewPreviewDataImpl: OverviewPreviewData {
    override val mockOverviewData = OverviewData(
        timestamp = "",
        temperatureWater = 0.0,
        temperatureWaterFiltered = 0.0,
        pH = 0.0,
        pHFiltered = 0.0,
        conductivity = 0.0,
        conductivityFiltered = 0.0,
        tankLevelSwitchPosition = "",
        surfaceVentilation = "",
        temperatureAmbient = 0.0,
        humidityAmbient = 0.0,
        heater = ""
    )
    override val mockControllerProfile = ControllerProfile()
    override val mockOverviewUiStateSuccess = OverviewUiState.Success(mockOverviewData)
    override val mockOverviewUiStateError = OverviewUiState.Error(errorMessage = "Release build")
}