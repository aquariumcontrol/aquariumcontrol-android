/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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