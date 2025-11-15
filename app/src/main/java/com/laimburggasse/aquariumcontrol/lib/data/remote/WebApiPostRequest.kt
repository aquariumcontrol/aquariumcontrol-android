package com.laimburggasse.aquariumcontrol.lib.data.remote

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
        val formBody: FormBody = FormBody.Builder()
            .add(name = WebApiParams.POST_PARAM_USERNAME, value = username)
            .add(name = WebApiParams.POST_PARAM_PASSWORD, value = password)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        return try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody: String = response.body.string()
                Result.success(value = responseBody)
            } else {
                Result.failure(exception = IOException("Request failed with code: ${response.code}"))
            }
        } catch (e: IOException) {
            Result.failure(exception = e)
        }
    }
}