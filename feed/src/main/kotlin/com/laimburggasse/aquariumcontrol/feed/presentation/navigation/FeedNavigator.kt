package com.laimburggasse.aquariumcontrol.feed.presentation.navigation

import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes

fun NavHostController.handleFeedNavigation(
    event: FeedNavigationEvent,
    feedProfileId: Int? = null,
    isNewScheduleEntry: Boolean? = null,
    millisSinceEpoch: Long? = null
) {
    when (event) {
        FeedNavigationEvent.ToFeedHistory7d -> this.navigate(route = ViewNavigationRoutes.feedHistory7d)
        FeedNavigationEvent.ToFeedHistory24h -> this.navigate(route = ViewNavigationRoutes.feedHistory24h)
        FeedNavigationEvent.ToFeedScheduling -> {
            if (feedProfileId != null && isNewScheduleEntry != null && millisSinceEpoch != null) {
                this.navigate(
                    route = ViewNavigationRoutes.feedSchedule(
                        feedProfileId = feedProfileId,
                        isNewScheduleEntry = isNewScheduleEntry,
                        millisSinceEpoch = millisSinceEpoch
                    )
                )
            }
        }
        FeedNavigationEvent.ToFeedProfileEdit -> {
            feedProfileId.let {
                this.navigate(route = ViewNavigationRoutes.feedEdit(feedProfileId = feedProfileId!!))
            }
        }
        FeedNavigationEvent.ToFeedProfileAdd -> this.navigate(route = ViewNavigationRoutes.feedAddProfile)
        FeedNavigationEvent.ToFeedMain -> this.navigate(route = ViewNavigationRoutes.feed)
    }
}