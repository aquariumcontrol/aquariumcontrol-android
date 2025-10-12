package com.laimburggasse.aquariumcontrol

object ViewNavigationRoutes {
    var feed = "feed"
    var feedHistory = "feedHistory"
    var feedHistory24h = "feedHistory24h"
    var feedHistory7d = "feedHistory7d"

    private const val FEED_ARG_PROFILE_ID = "feedProfileId"
    var feedEdit = "feedEdit/{$FEED_ARG_PROFILE_ID}"
    var feedAddProfile = "feedAdd"

    var feedSchedule = "feedSchedule/{$FEED_ARG_PROFILE_ID}"

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

    fun feedSchedule(feedProfileId: Int): String {
        return "feedSchedule/$feedProfileId"
    }

    fun ballingEdit(ballingProfileId: Int): String {
        return "ballingEdit/$ballingProfileId"
    }

    fun feedEdit(feedProfileId: Int): String {
        return "feedEdit/$feedProfileId"
    }
}