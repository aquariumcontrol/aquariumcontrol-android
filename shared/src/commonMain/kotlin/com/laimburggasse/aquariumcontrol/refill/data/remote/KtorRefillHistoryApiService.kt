package com.laimburggasse.aquariumcontrol.refill.data.remote

import com.laimburggasse.aquariumcontrol.common.data.remote.dto.TimestampJsonObject
import com.laimburggasse.aquariumcontrol.refill.data.remote.dto.RefillHistoryLogEntryDto
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import me.tatarka.inject.annotations.Inject

import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil

class KtorRefillHistoryApiService @Inject constructor(
    private val client: HttpClient
) : RefillHistoryApiService {
    override suspend fun getHistory(url: String, authHeader: String, date: Long): List<RefillHistoryLogEntry> {
        val dtos: List<RefillHistoryLogEntryDto> = client.get(url) {
            header("Authorization", authHeader)
            parameter("date", DateTimeUtil.formatDateToApi(date))
        }.body()
        return dtos.map { it.toRefillHistoryLogEntry() }
    }

    override suspend fun getHistoryStart(url: String, authHeader: String): TimestampJsonObject {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }
}