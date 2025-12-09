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

package com.laimburggasse.aquariumcontrol.refill.data

import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.data.remote.webApiPostRequestExecution
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

open class RefillRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    @param:ApplicationScope private val repositoryScope: CoroutineScope,
): RefillRepository {
    private val _errorEvents = MutableSharedFlow<String>()
    override val errorEvents = _errorEvents.asSharedFlow()
    override suspend fun sendStartRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_CONTROL_START)
    }

    override suspend fun sendStopRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_CONTROL_STOP)
    }

    override suspend fun sendResetRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_CONTROL_RESET)
    }

    private fun executeRequest(apiUrl: String) {
        val webApiPostRequest = WebApiPostRequest()

        repositoryScope.launch {
            val requestSuccess: Boolean = webApiPostRequestExecution(
                apiUrl,
                sessionManager,
                webApiPostRequest,
            )
            if (!requestSuccess) {
                _errorEvents.emit(value = "Request failed. Please check connection and server configuration and try again.")
            }
        }
    }
}