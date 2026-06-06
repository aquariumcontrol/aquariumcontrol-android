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

import com.laimburggasse.aquariumcontrol.summary.data.remote.SummaryDataApiService
import com.laimburggasse.aquariumcontrol.summary.domain.model.SummaryData
import com.laimburggasse.aquariumcontrol.summary.data.remote.dto.SummaryDataDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class MockSummaryDataApiServiceImpl(
    private var summaryData: SummaryData?
): SummaryDataApiService {
    override suspend fun getSummaryData(
        url: String,
        authHeader: String
    ): SummaryDataDto {
        delay(10.milliseconds) // Small virtual delay to avoid conflation in flow
        val data = summaryData ?: throw Exception("API ERROR")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        return SummaryDataDto(
            timestamp = dateFormat.format(Date(data.timestamp)),
            waterTemperature = data.waterTemperature,
            filteredWaterTemperature = data.filteredWaterTemperature,
            phValue = data.phValue,
            filteredPhValue = data.filteredPhValue,
            conductivity = data.conductivity,
            filteredConductivity = data.filteredConductivity,
            tankLevelSwitchPosition = data.tankLevelSwitchPosition,
            surfaceVentilationStatus = data.surfaceVentilationStatus,
            ambientTemperature = data.ambientTemperature,
            ambientHumidity = data.ambientHumidity,
            heatingStatus = data.heatingStatus
        )
    }

    fun setSummaryData(newSummaryData: SummaryData?) {
        summaryData = newSummaryData
    }
}