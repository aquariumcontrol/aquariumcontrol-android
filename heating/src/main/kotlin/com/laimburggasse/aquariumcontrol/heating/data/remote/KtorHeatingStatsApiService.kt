package com.laimburggasse.aquariumcontrol.heating.data.remote

import com.laimburggasse.aquariumcontrol.heating.data.remote.dto.HeatingStatsStringEntry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import javax.inject.Inject

class KtorHeatingStatsApiService @Inject constructor(
    private val client: HttpClient
) : HeatingStatsApiService {
    override suspend fun getHeatingStats(
        url: String,
        authHeader: String
    ): List<HeatingStatsStringEntry> {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }
}