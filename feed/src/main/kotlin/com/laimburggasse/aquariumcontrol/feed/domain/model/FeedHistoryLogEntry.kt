package com.laimburggasse.aquariumcontrol.feed.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedHistoryLogEntry(
    // This data class represents a feed event.
    @SerialName("Timestamp")
    val timestamp: String,
    @SerialName("FeederOnTime")
    val feederOnTime: Int,
    @SerialName("ProfileName")
    val profileName: String,
    @SerialName("ProfileIdx")
    val idx: Int
)