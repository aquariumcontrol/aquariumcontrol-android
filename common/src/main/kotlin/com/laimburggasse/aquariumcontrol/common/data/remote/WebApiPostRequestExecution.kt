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

import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun webApiPostRequestExecution(
    apiUrl: String,
    sessionManager: SessionManager,
    webApiPostRequest: WebApiPostRequest,
    optionalGetParams: Map<String, String> = emptyMap()
): Boolean {
    val user: String? = sessionManager.getControllerUser()
    val password: String? = sessionManager.getControllerPassword()

    if (user == null || password == null) {
        return false
    }
    var url: String = "http://" + sessionManager.getControllerAddress() + ":" +
                        sessionManager.getControllerPort() + apiUrl + "?"

    for ((index: Int, entry: Map.Entry<String, String>) in optionalGetParams.entries.withIndex()) {
        val (key: String, value: String) = entry
        url += "$key=$value"
        if (index != optionalGetParams.size - 1) {
            url += "&"
        }
    }

    return withContext(context = Dispatchers.IO) {
        webApiPostRequest.postData(url, username = user, password).isSuccess
    }
}