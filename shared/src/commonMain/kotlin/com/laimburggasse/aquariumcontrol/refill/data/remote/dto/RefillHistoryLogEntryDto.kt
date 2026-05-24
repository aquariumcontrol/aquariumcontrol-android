package com.laimburggasse.aquariumcontrol.refill.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefillHistoryLogEntryDto(
    @SerialName(value = WebApiParams.TIMESTAMP)
    val timestamp: String,
    @SerialName(value = WebApiParams.REFILL_DURATION)
    val duration: Double,
    @SerialName(value = WebApiParams.REFILL_VOLUME)
    val volume: Double
) {
    fun toRefillHistoryLogEntry(): RefillHistoryLogEntry {
        
        val timestampMillis = try {
            DateTimeUtil.parseApiTimestamp(timestamp)
        } catch (_: Exception) {
            0L
        }
        return RefillHistoryLogEntry(
            timestamp = timestampMillis,
            duration = duration,
            volume = volume
        )
    }
}
