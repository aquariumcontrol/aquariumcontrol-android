package com.laimburggasse.aquariumcontrol.common.util

import kotlinx.datetime.*
import kotlin.time.Instant

object DateTimeUtil {
    private val systemTz = TimeZone.currentSystemDefault()

    fun parseApiTimestamp(timestamp: String): Long {
        if (timestamp.isEmpty()) return 0L
        return try {
            val iso = timestamp.replace(" ", "T")
            if (iso.contains("T")) {
                LocalDateTime.parse(iso).toInstant(systemTz).toEpochMilliseconds()
            } else {
                // Try as LocalDate (yyyy-MM-dd or yyyy-M-d)
                val parts = iso.split("-")
                if (parts.size == 3) {
                    val year = parts[0].toInt()
                    val month = parts[1].toInt()
                    val day = parts[2].toInt()
                    LocalDate(year, month, day).atStartOfDayIn(systemTz).toEpochMilliseconds()
                } else {
                    0L
                }
            }
        } catch (e: Exception) {
            0L
        }
    }

    fun formatToApiTimestamp(epochMillis: Long): String {
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val localDateTime = instant.toLocalDateTime(systemTz)
        return localDateTime.toString().replace("T", " ").substringBefore(".")
    }
}
