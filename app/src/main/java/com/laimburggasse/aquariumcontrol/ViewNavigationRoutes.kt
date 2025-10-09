package com.laimburggasse.aquariumcontrol

import java.sql.Timestamp

object ViewNavigationRoutes {
    var feed = "feed"
    var feedHistory = "feedHistory"
    var feedHistory24h = "feedHistory24h"
    var feedHistory7d = "feedHistory7d"
    var feedEdit = "feedEdit"
    var feedAddProfile = "feedAdd"

    private const val FEED_SCHEDULE_ARG_PROFILE_NAME = "profileName"
    private const val FEED_SCHEDULE_ARG_TIMESTAMP = "timestamp"

    var feedSchedule = "feedSchedule/{$FEED_SCHEDULE_ARG_PROFILE_NAME}/{$FEED_SCHEDULE_ARG_TIMESTAMP}"

    var balling = "balling"
    var ballingHistory24h = "ballingHistory24h"
    var ballingHistory7d = "ballingHistory7d"
    var ballingEdit = "ballingEdit"
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

    fun feedSchedule(profileName: String, timestamp: Timestamp?): String {
        val milliSecondsSinceEpoch = timestamp?.time ?: 0
        return "feedSchedule/$profileName/$milliSecondsSinceEpoch"
    }
}