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

object WebApiUrls {
    private const val BASE_URL = "/api.php?action="
    const val OVERVIEW_GET_DATA = "${BASE_URL}load_overview_signals"
    const val LOAD_TIME_DATA = "${BASE_URL}load_time_data"
    const val REFILL_LOAD_EVENTS_24H = "${BASE_URL}load_refill_log&period=24h"
    const val REFILL_LOAD_EVENTS_7D = "${BASE_URL}load_refill_log&period=7d"
    const val REFILL_CONTROL_START = "${BASE_URL}set_refill_state&command=start"
    const val REFILL_CONTROL_STOP = "${BASE_URL}set_refill_state&command=stop"
    const val REFILL_CONTROL_RESET = "${BASE_URL}set_refill_state&command=stop"
    const val FEED_LOAD_PROFILES = "${BASE_URL}load_feed_profiles"
    const val FEED_LOAD_EVENTS_24H = "${BASE_URL}load_feed_log&period=24h"
    const val FEED_LOAD_EVENTS_7D = "${BASE_URL}load_feed_log&period=7d"
    const val FEED_DELETE_PROFILE = "${BASE_URL}delete_feed_profile&profileId="
    const val FEED_PROFILE_UPDATE = "${BASE_URL}update_feed_profile"
    const val FEED_PROFILE_CREATE = "${BASE_URL}create_feed_profile"
    const val FEED_PROFILE_SCHEDULE_NEW = "${BASE_URL}create_feed_schedule_entry"
    const val FEED_UPDATE_SCHEDULE = "${BASE_URL}update_feed_schedule_entry"
    const val FEED_LOAD_SCHEDULE = "${BASE_URL}load_feed_schedule"
    const val FEED_SCHEDULE_DELETE_ENTRY = "${BASE_URL}delete_feed_schedule_entry"
    const val BALLING_LOAD_SET_VALS = "${BASE_URL}load_balling_set_vals"
    const val BALLING_UPDATE_SET_VALS = "${BASE_URL}update_balling_set_vals"
    const val BALLING_LOAD_EVENTS_24H = "${BASE_URL}load_feed_log&period=24h"
    const val BALLING_LOAD_EVENTS_7D = "${BASE_URL}load_feed_log&period=7d"
    const val HEATING_LOAD_STATS = "${BASE_URL}load_heating_stats"
    const val HEATING_LOAD_SET_VALS = "${BASE_URL}load_heating_set_vals"
    const val HEATING_UPDATE_SET_VALS = "${BASE_URL}update_heating_set_vals"
    const val VENTILATION_LOAD_SET_VALS = "${BASE_URL}load_ventilation_set_vals"
    const val VENTILATION_UPDATE_SET_VALS = "${BASE_URL}update_ventilation_set_vals"
    const val ACTUATOR_SCHEDULE_UPDATE = "${BASE_URL}update_actuator_schedule"
    const val ACTUATOR_SCHEDULE_LOAD = "${BASE_URL}load_actuator_schedule"
}