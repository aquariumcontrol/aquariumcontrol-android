package com.laimburggasse.aquariumcontrol

object ViewNavigationRoutes {
    var feed = "feed"
    var feedHistory = "feedHistory"
    var feedHistory24h = "feedHistory24h"
    var feedHistory7d = "feedHistory7d"

    const val FEED_ARG_PROFILE_ID = "feedProfileId"
    const val FEED_ARG_IS_NEW_SCHEDULE_ENTRY = "isNewScheduleEntry"
    const val FEED_ARG_MILLIS_SINCE_EPOCH = "millisSinceEpoch"
    var feedEdit = "feedEdit/{$FEED_ARG_PROFILE_ID}"
    var feedAddProfile = "feedAdd"

    var feedSchedule = "feedSchedule/{$FEED_ARG_PROFILE_ID}/{$FEED_ARG_IS_NEW_SCHEDULE_ENTRY}/{$FEED_ARG_MILLIS_SINCE_EPOCH}"

    var balling = "balling"
    var ballingHistory24h = "ballingHistory24h"
    var ballingHistory7d = "ballingHistory7d"

    private const val BALLING_PROFILE_ARG_ID = "ballingProfileId"
    var ballingEdit = "ballingEdit/{$BALLING_PROFILE_ARG_ID}"

    var refill = "refill"
    var refillHistory24h = "refillHistory24h"
    var refillHistory7d = "refillHistory7d"
    var ventilation = "ventilation"
    var heating = "heating"
    var schedule = "schedule"
    var timeData = "timeData"
    var controllerSelection = "controllerSelection"
    var info = "info"
    var controllerAdd = "controllerAdd"
    var controllerEdit = "controllerEdit"
    var overView = "overView"

    fun feedSchedule(feedProfileId: Int, isNewScheduleEntry: Boolean, millisSinceEpoch: Long): String {
        return "feedSchedule/$feedProfileId/$isNewScheduleEntry/$millisSinceEpoch"
    }

    fun ballingEdit(ballingProfileId: Int): String {
        return "ballingEdit/$ballingProfileId"
    }

    fun feedEdit(feedProfileId: Int): String {
        return "feedEdit/$feedProfileId"
    }
}