package com.laimburggasse.aquariumcontrol.refill.data.remote

import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillStateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import me.tatarka.inject.annotations.Inject

class KtorRefillStateApiService @Inject constructor(
    private val client: HttpClient
) : RefillStateApiService {
    override suspend fun loadRefillState(url: String, authHeader: String): RefillStateResponse {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }

    override suspend fun resetRefillErrors(url: String, authHeader: String): Boolean {
        return client.get(url) {
            header("Authorization", authHeader)
        }.status.isSuccess()
    }
}