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

package com.laimburggasse.aquariumcontrol.balling.data

import androidx.paging.Pager
import androidx.paging.PagingData
import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingHistoryRemoteMediator
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingHistoryRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import okhttp3.Credentials
import javax.inject.Inject

open class BallingHistoryRepositoryImpl @Inject constructor(
    sessionManager: SessionManager,
    private val pager: Pager<Int, BallingHistoryLogEntry>,
    private val remoteMediator: BallingHistoryRemoteMediator
): BallingHistoryRepository {

    // Trigger for manual refreshes (e.g., pull-to-refresh)
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    /**
     * A reactive Flow that provides the Balling History events using Paging 3.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override val ballingHistoryDosingEventFlow: Flow<PagingData<BallingHistoryLogEntry>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) }, // Trigger on first collection
        ) { controller, _ ->
            controller
        }
        .filter { it != null } // Only proceed if a controller is selected
        .flatMapLatest { controller ->
            val address = controller!!.address
            val port = controller.port
            val apiPath = WebApiUrls.BALLING_LOAD_EVENTS
            
            // Construct the URL ensuring no double slashes if apiPath already starts with /
            val baseUrl = "http://$address:$port"
            val dynamicUrl = if (apiPath.startsWith("/")) {
                "$baseUrl$apiPath="
            } else {
                "$baseUrl/$apiPath="
            }

            val authHeader = Credentials.basic(controller.user, controller.password)
            remoteMediator.updateConfig(dynamicUrl, authHeader)
            pager.flow
        }

    /**
     * Public function for the ViewModel to trigger a manual refresh.
     */
    override suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }
}