package com.laimburggasse.aquariumcontrol.refill.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for parsing refill history log entries from the remote API.
 *
 * @property timestamp The raw string representation of the event time from the server.
 * @property duration The duration of the refill event in seconds.
 * @property volume The volume of water added during the refill event in liters.
 */
@Serializable
data class RefillHistoryLogEntryDto(
    @SerialName(value = WebApiParams.TIMESTAMP)
    val timestamp: String,
    @SerialName(value = WebApiParams.REFILL_DURATION)
    val duration: Double,
    @SerialName(value = WebApiParams.REFILL_VOLUME)
    val volume: Double
) {
    /**
     * Converts this DTO into a domain-level [RefillHistoryLogEntry].
     * Parses the string timestamp into epoch milliseconds using [DateTimeUtil].
     *
     * @return A domain model representation of the refill history log entry.
     */
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
