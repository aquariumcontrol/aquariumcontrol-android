package com.laimburggasse.aquariumcontrol.ventilation.data.remote

import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
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

class KtorVentilationSetValsApiService @Inject constructor(
    private val client: HttpClient
) : VentilationSetValsApiService {
    override suspend fun getVentilationSetVals(url: String, authHeader: String): List<VentilationSetVals> {
        return client.get(url) {
            header("Authorization", authHeader)
        }.body()
    }

    override suspend fun updateVentilationSetVals(url: String, authHeader: String, params: Map<String, String>): Boolean {
        return client.post(url) {
            header("Authorization", authHeader)
            contentType(ContentType.Application.Json)
            setBody(params)
        }.status.isSuccess()
    }
}