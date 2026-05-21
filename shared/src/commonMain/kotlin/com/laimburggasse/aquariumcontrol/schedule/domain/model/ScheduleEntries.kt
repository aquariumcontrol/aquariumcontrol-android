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

package com.laimburggasse.aquariumcontrol.schedule.domain.model

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams

data class ScheduleEntries(
    var ballingStartTime: Float,
    var ballingStopTime: Float,
    var ballingLimitationActive: Boolean,
    var ballingScheduleLoaded: Boolean,
    var refillStartTime: Float,
    var refillStopTime: Float,
    var refillLimitationActive: Boolean,
    var refillScheduleLoaded: Boolean,
    var ventilationStartTime: Float,
    var ventilationStopTime: Float,
    var ventilationLimitationActive: Boolean,
    var ventilationScheduleLoaded: Boolean,
    var heatingStartTime: Float,
    var heatingStopTime: Float,
    var heatingLimitationActive: Boolean,
    var heatingScheduleLoaded: Boolean
) {
    private fun formatTime(value: Float): String {
        val hours: Int = value.toInt()
        val minutes: Int = ((value - hours) * 60).toInt()
        return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
    }

    fun getApiParams(): Map<String, String> {
        return mapOf(
            WebApiParams.SCHEDULE_BALLING_START_TIME to formatTime(value = ballingStartTime),
            WebApiParams.SCHEDULE_BALLING_STOP_TIME to formatTime(value = ballingStopTime),
            WebApiParams.SCHEDULE_BALLING_IS_ACTIVE to if(ballingLimitationActive) "1" else "0",
            WebApiParams.SCHEDULE_REFILL_START_TIME to formatTime(value = refillStartTime),
            WebApiParams.SCHEDULE_REFILL_STOP_TIME to formatTime(value = refillStopTime),
            WebApiParams.SCHEDULE_REFILL_IS_ACTIVE to if(refillLimitationActive) "1" else "0",
            WebApiParams.SCHEDULE_VENTILATION_START_TIME to formatTime(value = ventilationStartTime),
            WebApiParams.SCHEDULE_VENTILATION_STOP_TIME to formatTime(value = ventilationStopTime),
            WebApiParams.SCHEDULE_VENTILATION_IS_ACTIVE to if(ventilationLimitationActive) "1" else "0",
            WebApiParams.SCHEDULE_HEATING_START_TIME to formatTime(value = heatingStartTime),
            WebApiParams.SCHEDULE_HEATING_STOP_TIME to formatTime(value = heatingStopTime),
            WebApiParams.SCHEDULE_HEATING_IS_ACTIVE to if(heatingLimitationActive) "1" else "0",
        )
    }
}