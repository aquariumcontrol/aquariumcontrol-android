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

package com.laimburggasse.aquariumcontrol.common.presentation

object ViewNavigationRoutes {
    var feed = "feed"
    var feedHistory = "feedHistory"

    const val FEED_ARG_PROFILE_ID = "feedProfileId"
    const val FEED_ARG_IS_NEW_SCHEDULE_ENTRY = "isNewScheduleEntry"
    const val FEED_ARG_MILLIS_SINCE_EPOCH = "millisSinceEpoch"
    var feedEdit = "feedEdit/{$FEED_ARG_PROFILE_ID}"
    var feedAddProfile = "feedAdd"

    var feedSchedule = "feedSchedule/{$FEED_ARG_PROFILE_ID}/{$FEED_ARG_IS_NEW_SCHEDULE_ENTRY}/{$FEED_ARG_MILLIS_SINCE_EPOCH}"

    var balling = "balling"
    var ballingHistory = "ballingHistory"

    private const val BALLING_PROFILE_ARG_ID = "ballingProfileId"
    var ballingEdit = "ballingEdit/{$BALLING_PROFILE_ARG_ID}"

    var refill = "refill"
    var refillHistory = "refillHistory"
    var ventilation = "ventilation"
    var heating = "heating"
    var schedule = "schedule"
    var timeData = "timeData"

    const val CONTROLLER_ARG_ID = "controllerProfileId"
    var controllerSelection = "controllerSelection/{$CONTROLLER_ARG_ID}"
    var info = "info"
    var controllerAdd = "controllerAdd"
    var controllerEdit = "controllerEdit/{$CONTROLLER_ARG_ID}"
    var summary = "summary/{$CONTROLLER_ARG_ID}"

    fun feedSchedule(feedProfileId: Int, isNewScheduleEntry: Boolean, millisSinceEpoch: Long): String {
        return "feedSchedule/$feedProfileId/$isNewScheduleEntry/$millisSinceEpoch"
    }

    fun ballingEdit(ballingProfileId: Int): String {
        return "ballingEdit/$ballingProfileId"
    }

    fun feedEdit(feedProfileId: Int): String {
        return "feedEdit/$feedProfileId"
    }

    fun controllerEdit(controllerProfileId: Long): String {
        return "controllerEdit/$controllerProfileId"
    }

    fun controllerSelection(controllerProfileId: Long): String {
        return "controllerSelection/$controllerProfileId"
    }

    fun summary(controllerProfileId: Long): String {
        return "summary/$controllerProfileId"
    }
}