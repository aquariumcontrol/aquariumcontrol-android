package com.laimburggasse.aquariumcontrol.common.data.remote

import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
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
                Result.failure(exception = IOException(
                    GlobalConstants.ERR_MSG_SERVER_RESPONDED_ERR_CODE + response.code)
                )
            }
        } catch (exception: IOException) {
            Result.failure(exception)
        }
    }
}