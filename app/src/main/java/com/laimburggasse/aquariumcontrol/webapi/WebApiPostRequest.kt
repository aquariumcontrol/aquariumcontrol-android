package com.laimburggasse.aquariumcontrol.webapi

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class WebApiPostRequest {
    private val client = OkHttpClient()

    fun postData(
        url: String,
        username: String,
        password: String,
    ): Result<String> {

        val formBody = FormBody.Builder()
            .add(WebApiParams.POST_PARAM_USERNAME, username)
            .add(WebApiParams.POST_PARAM_PASSWORD, password)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        return try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body.string()
                Result.success(responseBody)
            } else {
                Result.failure(IOException("Request failed with code: ${response.code}"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}