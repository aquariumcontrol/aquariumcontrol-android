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

package com.laimburggasse.aquariumcontrol.common.data.remote

object WebApiParams {
    const val PROFILE_NAME = "profileName"
    const val PROFILE_ID = "profileId"
    const val PUMP_ID = "pumpId"
    const val DOSING_VOLUME = "dosingVolume"
    const val DURATION = "Duration"
    const val SKIMMER = "Skimmer"
    const val MAIN_PUMP1 = "MainPump1"
    const val MAIN_PUMP2 = "MainPump2"
    const val AUX_PUMP1 = "AuxPump1"
    const val AUX_PUMP2 = "AuxPump2"
    const val HEATING_SWITCH_OFF_TEMPERATURE = "heatingSwitchOffTemp"
    const val HEATING_SWITCH_ON_TEMPERATURE = "heatingSwitchOnTemp"
    const val VENTILATION_SWITCH_OFF_TEMPERATURE = "ventilationSwitchOffTemp"
    const val VENTILATION_SWITCH_ON_TEMPERATURE = "ventilationSwitchOnTemp"
    const val IS_DAILY = "isDaily"
    const val IS_WEEKLY = "isWeekly"
    const val TIMESTAMP = "timestamp"
    const val TIMESTAMP_NEW = "TimeStampNew" // @todo: update API
    const val SCHEDULE_BALLING_START_TIME = "ballingRangeStartTime"
    const val SCHEDULE_BALLING_STOP_TIME = "ballingRangeFinishTime"
    const val SCHEDULE_BALLING_IS_ACTIVE = "ballingRangeIsActive"
    const val SCHEDULE_REFILL_START_TIME = "refillRangeStartTime"
    const val SCHEDULE_REFILL_STOP_TIME = "refillRangeFinishTime"
    const val SCHEDULE_REFILL_IS_ACTIVE = "refillRangeIsActive"
    const val SCHEDULE_VENTILATION_START_TIME = "ventilationRangeStartTime"
    const val SCHEDULE_VENTILATION_STOP_TIME = "ventilationRangeFinishTime"
    const val SCHEDULE_VENTILATION_IS_ACTIVE = "ventilationRangeIsActive"
    const val SCHEDULE_HEATING_START_TIME = "heatingRangeStartTime"
    const val SCHEDULE_HEATING_STOP_TIME = "heatingRangeFinishTime"
    const val SCHEDULE_HEATING_IS_ACTIVE = "heatingRangeIsActive"
}