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

package com.laimburggasse.aquariumcontrol.common.domain

object GlobalConstants {
    const val PREFS_NAME = "app_prefs"
    const val KEY_DEFAULT_PROFILE_ID = "default_profile_id"
    const val NO_DEFAULT_ID = -1L

    const val MAX_HISTORY_LOG_ENTRIES = 5000
    const val MAX_PROFILE_ENTRIES = 500
    const val MAX_TIME_DATA_SAMPLES = 10000
    const val MAX_STATS_ENTRIES = 2000
    const val LABEL_FEEDER = "Feeder"
    const val LABEL_SKIMMER = "Skimmer"
    const val LABEL_MAIN_PUMP_1 = "Main Pump 1"
    const val LABEL_MAIN_PUMP_2 = "Main Pump 2"
    const val LABEL_AUX_PUMP_1 = "Aux. Pump 1"
    const val LABEL_AUX_PUMP_2 = "Aux. Pump 2"
    const val LABEL_FEED = "Feed"
    const val LABEL_PAUSE = "Pause"
    const val LABEL_PHASE = "Phase"
    const val LABEL_TEMPERATURE = "Temperature"
    const val LABEL_TEMPERATURE_FILTERED = "Temperature filtered"
    const val LABEL_PH = "pH"
    const val LABEL_PH_FILTERED = "pH filtered"
    const val LABEL_CONDUCTIVITY = "Conductivity"
    const val LABEL_CONDUCTIVITY_FILTERED = "Conductivity filtered"
    const val LABEL_MAIN_TANK_LEVEL_SWITCH = "Main tank level switch"
    const val LABEL_REFILL_IN_PROGRESS = "Refill in progress"
    const val LABEL_AMBIENT_TEMPERATURE = "Ambient Temperature"
    const val LABEL_AMBIENT_HUMIDITY = "Ambient Humidity"
    const val LABEL_SURFACE_VENTILATION = "Surface Ventilation"
    const val LABEL_HEATING = "Heating"
    const val LABEL_UNIT_DEGREE_CELSIUS = " °C"
    const val LABEL_UNIT_MICROSIEVERT_PER_CM = " mS/cm"
    const val LABEL_UNIT_ON = "ON"
    const val LABEL_UNIT_OFF = "OFF"
    const val LABEL_UNIT_PERCENT = " %"
    const val ERR_MSG_SERVER_RESPONDED_TOO_MANY_ENTRIES = "Server responded with too many entries."
    const val ERR_MSG_UNEXPECTED_ERR_DURING_PREP = "An unexpected error occurred during preparation of server communication: "
    const val ERR_MSG_NO_CONTROLLER_SELECTED = "No controller selected."
    const val ERR_MSG_LOADING_HEATING_STATS_FAILED = "Loading heating stats data failed."
    const val ERR_MSG_RECEIVED_SET_VALS_OUT_OF_RANGE = "Incorrect data received from server: values are out of range"
    const val ERR_MSG_SERVER_RETURNED_EMPTY_LIST = "Server returned an empty list."
    const val ERR_MSG_VENTILATION_INCORRECT_SET_VALS = "Incorrect data received from server: switch off value must be smaller than switch on value"
    const val ERR_MSG_UPDATING_FEED_SCHEDULE_ENTRY_FAILED = "Updating feed schedule entry failed."
    const val ERR_MSG_ADDING_FEED_SCHEDULE_ENTRY_FAILED = "Adding feed schedule entry failed."
    const val ERR_MSG_DELETING_FEED_SCHEDULE_ENTRY_FAILED = "Deleting feed schedule entry failed."
    const val ERR_MSG_COULD_NOT_ADD_FEED_PROFILE = "Could not add the feed profile."
    const val ERR_MSG_COULD_NOT_DELETE_FEED_PROFILE = "Could not delete the feed profile."
    const val ERR_MSG_LOADING_FEED_SCHEDULE_ENTRIES_FAILED = "Loading feed schedule entries failed."
    const val ERR_MSG_LOADING_TIME_DATA_FAILED = "Loading time data failed."
    const val ERR_MSG_LOADING_FEED_PROFILES_FAILED = "Loading feed profiles failed."

    const val ERR_MSG_UPDATING_BALLING_PROFILE_FAILED = "Update of Balling profile on server failed."
    const val ERR_MSG_UPDATING_FEED_PROFILE_FAILED = "Update of feed profile on server failed."
    const val ERR_MSG_EXECUTING_FEED_PROFILE_FAILED = "Execution of feed profile on server failed."
    const val ERR_MSG_INVALID_CONTROLLER_ADDRESS = "No valid controller address."
    const val ERR_MSG_UPDATING_SET_VALS_FAILED = "Could not update set values."
    const val ERR_MSG_LOADING_SCHEDULE_INFO_FAILED = "Failed to load schedule information."
    const val ERR_MSG_LOADING_SET_VALS_FAILED = "Loading set values failed."
    const val ERR_MSG_HEATING_INCORRECT_SET_VALS = "Incorrect data received from server: switch off value must be smaller than switch on value"
}