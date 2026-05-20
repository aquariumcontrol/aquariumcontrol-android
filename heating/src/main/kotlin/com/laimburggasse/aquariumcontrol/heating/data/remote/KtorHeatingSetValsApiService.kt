package com.laimburggasse.aquariumcontrol.heating.data.remote

import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
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

class KtorHeatingSetValsApiService @Inject constructor(
    private val client: HttpClient
) : HeatingSetValsApiService {
    override suspend fun getHeatingSetVals(
        url: String,
        authHeader: String
    ): List<HeatingSetVals> {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }

    override suspend fun updateHeatingSetVals(
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