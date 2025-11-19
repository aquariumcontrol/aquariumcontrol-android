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

package com.laimburggasse.aquariumcontrol.schedule

import com.laimburggasse.aquariumcontrol.schedule.domain.model.ScheduleEntries
import com.laimburggasse.aquariumcontrol.schedule.presentation.SchedulePreviewData
import com.laimburggasse.aquariumcontrol.schedule.presentation.ScheduleUiState

object SchedulePreviewDataImpl: SchedulePreviewData {
    override val mockScheduleEntries = ScheduleEntries(
        ballingStartTime = 0.0F,
        ballingStopTime = 0.0F,
        ballingLimitationActive = false,
        ballingScheduleLoaded = false,
        refillStartTime = 0.0F,
        refillStopTime = 0.0F,
        refillLimitationActive = false,
        refillScheduleLoaded = false,
        ventilationStartTime = 0.0F,
        ventilationStopTime = 0.0F,
        ventilationLimitationActive = false,
        ventilationScheduleLoaded = false,
        heatingStartTime = 0.0F,
        heatingStopTime = 0.0F,
        heatingLimitationActive = false,
        heatingScheduleLoaded = false
    )
    override val mockScheduleUiStateSuccess = ScheduleUiState.Success(scheduleEntries = mockScheduleEntries)
    override val mockScheduleUiStateError = ScheduleUiState.Error(errorMessage = "Release build")
}