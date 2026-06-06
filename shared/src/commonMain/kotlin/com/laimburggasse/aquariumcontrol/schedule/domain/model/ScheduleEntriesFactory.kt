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

import com.laimburggasse.aquariumcontrol.schedule.data.remote.dto.ScheduleEntry

/**
 * Factory class responsible for aggregating a list of individual [ScheduleEntry] items
 * into a single unified [ScheduleEntries] domain model.
 *
 * @property scheduleEntryList The list of individual actuator entries to aggregate.
 */
class ScheduleEntriesFactory (
    private val scheduleEntryList: List<ScheduleEntry>
) {
    /**
     * Orchestrates the aggregation of entries into the final [ScheduleEntries] object.
     * Handles mapping individual actuator keys to their respective properties.
     *
     * @return A complete [ScheduleEntries] instance.
     */
    fun create(): ScheduleEntries {
        // initialize with default values
        var ballingStartTime = 0f
        var ballingStopTime = 24f
        var ballingLimitationActive = false
        var ballingScheduleLoaded = false
        var refillStartTime = 0f
        var refillStopTime = 24f
        var refillLimitationActive = false
        var refillScheduleLoaded = false
        var ventilationStartTime = 0f
        var ventilationStopTime = 24f
        var ventilationLimitationActive = false
        var ventilationScheduleLoaded = false
        var heatingStartTime = 0f
        var heatingStopTime = 24f
        var heatingLimitationActive = false
        var heatingScheduleLoaded = false
        
        if (scheduleEntryList.isNotEmpty()) {
            for (scheduleSetVal: ScheduleEntry in scheduleEntryList) {
                when (scheduleSetVal.scheduleType) {
                    ScheduleType.BALLING.key -> {
                        ballingStartTime = scheduleSetVal.startTime
                        ballingStopTime = scheduleSetVal.stopTime
                        ballingLimitationActive = scheduleSetVal.isLimitationActive
                        ballingScheduleLoaded = true
                    }

                    ScheduleType.REFILL.key -> {
                        refillStartTime = scheduleSetVal.startTime
                        refillStopTime = scheduleSetVal.stopTime
                        refillLimitationActive = scheduleSetVal.isLimitationActive
                        refillScheduleLoaded = true
                    }

                    ScheduleType.VENTILATION.key-> {
                        ventilationStartTime = scheduleSetVal.startTime
                        ventilationStopTime = scheduleSetVal.stopTime
                        ventilationLimitationActive = scheduleSetVal.isLimitationActive
                        ventilationScheduleLoaded = true
                    }

                    ScheduleType.HEATING.key -> {
                        heatingStartTime = scheduleSetVal.startTime
                        heatingStopTime = scheduleSetVal.stopTime
                        heatingLimitationActive = scheduleSetVal.isLimitationActive
                        heatingScheduleLoaded = true
                    }
                }
            }
        }

        return ScheduleEntries(
            ballingStartTime,
            ballingStopTime,
            ballingLimitationActive,
            ballingScheduleLoaded,
            refillStartTime,
            refillStopTime,
            refillLimitationActive,
            refillScheduleLoaded,
            ventilationStartTime,
            ventilationStopTime,
            ventilationLimitationActive,
            ventilationScheduleLoaded,
            heatingStartTime,
            heatingStopTime,
            heatingLimitationActive,
            heatingScheduleLoaded
        )
    }
}