package com.laimburggasse.aquariumcontrol.balling.data.remote

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
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

class KtorBallingProfileApiService @Inject constructor(
    private val client: HttpClient
) : BallingProfileApiService {
    override suspend fun getBallingProfiles(
        url: String,
        authHeader: String
    ): List<BallingProfile> {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }

    override suspend fun updateBallingProfile(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Boolean {
        val response = client.post(url) {
            header("Authorization", authHeader)
            contentType(ContentType.Application.Json)
            setBody(params)
        }
        return response.status.isSuccess()
    }
}