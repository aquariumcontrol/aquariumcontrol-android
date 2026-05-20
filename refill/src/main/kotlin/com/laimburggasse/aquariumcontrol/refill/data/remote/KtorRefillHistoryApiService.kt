package com.laimburggasse.aquariumcontrol.refill.data.remote

import com.laimburggasse.aquariumcontrol.common.data.remote.dto.TimestampJsonObject
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import javax.inject.Inject

class KtorRefillHistoryApiService @Inject constructor(
    private val client: HttpClient
) : RefillHistoryApiService {
    override suspend fun getHistory(url: String, authHeader: String, date: String): List<RefillHistoryLogEntry> {
        return client.get(url) {
            header("Authorization", authHeader)
            parameter("date", date)
        }.body()
    }

    override suspend fun getHistoryStart(url: String, authHeader: String): TimestampJsonObject {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }
}