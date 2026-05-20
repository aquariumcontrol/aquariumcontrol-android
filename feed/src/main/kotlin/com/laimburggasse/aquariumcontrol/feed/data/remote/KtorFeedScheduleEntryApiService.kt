package com.laimburggasse.aquariumcontrol.feed.data.remote

import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedScheduleEntryImport
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject

class KtorFeedScheduleEntryApiService @Inject constructor(
    private val client: HttpClient
) : FeedScheduleEntryApiService {
    override suspend fun getFeedScheduleEntries(url: String, authHeader: String): List<FeedScheduleEntryImport> {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }

    override suspend fun updateFeedScheduleEntry(url: String, authHeader: String, params: Map<String, String>): Boolean {
        return client.post(url) {
            header("Authorization", authHeader)
            contentType(ContentType.Application.Json)
            setBody(params)
        }.status.isSuccess()
    }

    override suspend fun addFeedScheduleEntry(url: String, authHeader: String, params: Map<String, String>): Boolean {
        return client.post(url) {
            header("Authorization", authHeader)
            contentType(ContentType.Application.Json)
            setBody(params)
        }.status.isSuccess()
    }

    override suspend fun deleteFeedScheduleEntry(url: String, authHeader: String, params: Map<String, String>): Boolean {
        return client.post(url) {
            header("Authorization", authHeader)
            contentType(ContentType.Application.Json)
            setBody(params)
        }.status.isSuccess()
    }
}