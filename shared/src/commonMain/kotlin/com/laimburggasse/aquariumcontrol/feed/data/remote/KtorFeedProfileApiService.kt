package com.laimburggasse.aquariumcontrol.feed.data.remote

import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
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

class KtorFeedProfileApiService @Inject constructor(
    private val client: HttpClient
) : FeedProfileApiService {
    override suspend fun getFeedProfiles(url: String, authHeader: String): List<FeedProfileImport> {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }

    override suspend fun updateFeedProfile(url: String, authHeader: String, params: Map<String, String>): Boolean {
        return client.post(url) {
            header("Authorization", authHeader)
            contentType(ContentType.Application.Json)
            setBody(params)
        }.status.isSuccess()
    }

    override suspend fun addFeedProfile(url: String, authHeader: String, params: Map<String, String>): Boolean {
        return client.post(url) {
            header("Authorization", authHeader)
            contentType(ContentType.Application.Json)
            setBody(params)
        }.status.isSuccess()
    }

        override suspend fun executeFeedProfile(url: String, authHeader: String): Boolean {
        return client.post(url) {
            header("Authorization", authHeader)
        }.status.isSuccess()
    }

    override suspend fun deleteFeedProfile(url: String, authHeader: String): Boolean {
        return client.post(url) {
            header("Authorization", authHeader)
        }.status.isSuccess()
    }
}