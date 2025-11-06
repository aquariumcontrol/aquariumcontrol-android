package com.laimburggasse.aquariumcontrol.feed.data

/**
 * A data class to act as a stable, unique identifier for a schedule entry.
 * It combines the profile ID and the exact timestamp.
 */
data class FeedScheduleEntryKey(val profileId: Int, val timestamp: Long)