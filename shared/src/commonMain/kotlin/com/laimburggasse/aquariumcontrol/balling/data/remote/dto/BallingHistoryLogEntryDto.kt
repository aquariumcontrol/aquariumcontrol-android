package com.laimburggasse.aquariumcontrol.balling.data.remote.dto

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
