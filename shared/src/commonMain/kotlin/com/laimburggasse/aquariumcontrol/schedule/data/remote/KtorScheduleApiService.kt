package com.laimburggasse.aquariumcontrol.schedule.data.remote

import com.laimburggasse.aquariumcontrol.schedule.data.remote.dto.ScheduleStringEntry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import me.tatarka.inject.annotations.Inject

class KtorScheduleApiService @Inject constructor(
    private val client: HttpClient
) : ScheduleApiService {
    override suspend fun getScheduleData(url: String, authHeader: String): List<ScheduleStringEntry> {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }

    override suspend fun updateScheduleData(url: String, authHeader: String, params: Map<String, String>): Boolean {
        return client.post(url) {
            header("Authorization", authHeader)
            contentType(ContentType.Application.Json)
            setBody(params)
        }.status.isSuccess()
    }
}