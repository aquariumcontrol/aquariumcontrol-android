package com.laimburggasse.aquariumcontrol.balling.data.remote.dto

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for parsing Balling history log entries from the remote API.
 *
 * @property timestamp The raw string representation of the log entry's time.
 * @property pumpId The unique identifier of the dosing pump.
 * @property volume The volume dosed in milliliters.
 * @property label A descriptive label for the fluid being dosed (e.g., "Calcium").
 */
@Serializable
data class BallingHistoryLogEntryDto(
    @SerialName(value = WebApiParams.TIMESTAMP)
    val timestamp: String,
    @SerialName(value = WebApiParams.PUMP_ID)
    val pumpId: Int,
    @SerialName(value = WebApiParams.DOSING_VOLUME)
    val volume: Double,
    @SerialName(value = WebApiParams.LABEL)
    val label: String
) {
    /**
     * Converts this DTO into a domain-level [BallingHistoryLogEntry].
     * Parses the string timestamp into epoch milliseconds using [DateTimeUtil].
     *
     * @return A domain model representation of the history log entry.
     */
    fun toBallingHistoryLogEntry(): BallingHistoryLogEntry {
        
        val timestampMillis = try {
            DateTimeUtil.parseApiTimestamp(timestamp)
        } catch (_: Exception) {
            0L
        }
        return BallingHistoryLogEntry(
            timestamp = timestampMillis,
            pumpId = pumpId,
            volume = volume,
            label = label
        )
    }
}
