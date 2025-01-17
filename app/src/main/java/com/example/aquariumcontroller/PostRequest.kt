package com.example.aquariumcontroller

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class PostRequest {
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun postData(url: String, username: String, password: String): Result<String> {
        val jsonBody = """
            {
                "username": "$username",
                "password": "$password"
            }
        """.trimIndent()

        val requestBody = jsonBody.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                Result.success(responseBody)
            } else {
                Result.failure(IOException("Request failed with code: ${response.code}"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}