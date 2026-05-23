package com.laimburggasse.aquariumcontrol.summary.data.remote

import com.laimburggasse.aquariumcontrol.summary.data.remote.dto.SummaryDataDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import me.tatarka.inject.annotations.Inject

class KtorSummaryDataApiService @Inject constructor(
    private val client: HttpClient
) : SummaryDataApiService {
    override suspend fun getSummaryData(url: String, authHeader: String): SummaryDataDto {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }
}