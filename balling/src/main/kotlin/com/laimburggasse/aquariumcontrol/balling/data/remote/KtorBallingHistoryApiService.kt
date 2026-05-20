package com.laimburggasse.aquariumcontrol.balling.data.remote

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.data.remote.dto.TimestampJsonObject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import me.tatarka.inject.annotations.Inject

class KtorBallingHistoryApiService @Inject constructor(
    private val client: HttpClient
) : BallingHistoryApiService {
    override suspend fun getHistory(
        url: String,
        authHeader: String,
        date: String
    ): List<BallingHistoryLogEntry> {
        return client.get(url) {
            header("Authorization", authHeader)
            parameter("date", date)
        }.body()
    }

    override suspend fun getHistoryStart(
        url: String,
        authHeader: String
    ): TimestampJsonObject {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }
}