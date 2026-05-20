package com.laimburggasse.aquariumcontrol.timedata.data.remote

import com.laimburggasse.aquariumcontrol.timedata.data.remote.dto.TimeDataStringSample
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import javax.inject.Inject

class KtorTimeDataApiService @Inject constructor(
    private val client: HttpClient
) : TimeDataApiService {
    override suspend fun getTimeData(url: String, authHeader: String): List<TimeDataStringSample> {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }
}