package com.laimburggasse.aquariumcontrol.feed.data.remote

import com.laimburggasse.aquariumcontrol.common.data.remote.dto.TimestampJsonObject
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedHistoryLogEntryDto
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import me.tatarka.inject.annotations.Inject

import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil

class KtorFeedHistoryApiService @Inject constructor(
    private val client: HttpClient
) : FeedHistoryApiService {
    override suspend fun getHistory(url: String, authHeader: String, date: Long): List<FeedHistoryLogEntry> {
        val dtos: List<FeedHistoryLogEntryDto> = client.get(url) {
            header("Authorization", authHeader)
            parameter("date", DateTimeUtil.formatDateToApi(date))
        }.body()
        return dtos.map { it.toFeedHistoryLogEntry() }
    }

    override suspend fun getHistoryStart(url: String, authHeader: String): TimestampJsonObject {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }
}