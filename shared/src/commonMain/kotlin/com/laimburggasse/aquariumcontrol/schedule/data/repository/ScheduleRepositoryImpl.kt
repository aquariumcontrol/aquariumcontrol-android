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

package com.laimburggasse.aquariumcontrol.schedule.data.repository

import com.laimburggasse.aquariumcontrol.common.util.Base64



import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.schedule.data.remote.ScheduleApiService
import com.laimburggasse.aquariumcontrol.schedule.data.remote.ScheduleEntryReader
import com.laimburggasse.aquariumcontrol.schedule.data.remote.dto.ScheduleEntry
import com.laimburggasse.aquariumcontrol.schedule.data.remote.dto.ScheduleStringEntry
import com.laimburggasse.aquariumcontrol.schedule.domain.model.ScheduleEntries
import com.laimburggasse.aquariumcontrol.schedule.domain.model.ScheduleEntriesFactory
import com.laimburggasse.aquariumcontrol.schedule.domain.repository.ScheduleRepository
import io.ktor.util.encodeBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

/**
 * Implementation of [ScheduleRepository] for managing global actuator schedules.
 * Handles fetching, optimistic updates, and multi-actuator synchronization.
 *
 * @property logger Utility for recording exceptions.
 * @property sessionManager Manager providing the active controller profile.
 * @property apiService Network service for schedule management.
 * @property repositoryScope Coroutine scope for reactive flows.
 * @property context Dispatcher provider for IO operations.
 */
@ApplicationScope
open class ScheduleRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val sessionManager: SessionManager,
    private val apiService: ScheduleApiService,
    private val repositoryScope: CoroutineScope,
    private val context: IoDispatcher
): ScheduleRepository {
    /** Internal trigger flow for manually refreshing the schedule data. */
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    /** Internal state flow holding optimistic local modifications pending server confirmation. */
    private val pendingUpdate = MutableStateFlow<ScheduleEntries?>(value = null)

    /** Internal flow holding the last network or validation error, used to trigger retry logic. */
    private val failureResult = MutableStateFlow<DataFetchResult.Error?>(value = null)

    /**
     * Internal reactive flow that performs the actual network fetch.
     * Reacts to controller changes, manual refreshes, and failure resets.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val networkFlow: StateFlow<DataFetchResult<ScheduleEntries>> = combine(
        flow = sessionManager.selectedControllerProfile,
        flow2 = refreshTrigger.onStart { emit(value = Unit) }, // Triggers the first fetch
        flow3 = failureResult
    ) { controller, _, failure ->
        Pair(first = controller, second = failure)
    }
    .filter { it.first != null }
    .flatMapLatest { (controller: ControllerProfile?, failure: DataFetchResult.Error?) ->
        flow {
            if (failure != null) {
                emit(value = failure)
                return@flow
            }
            if (controller == null) {
                emit(value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_NO_CONTROLLER_SELECTED
                ))
                return@flow
            }

            // If there is no failure, and we have a controller, proceed with loading.
            println("ScheduleRepositoryImpl: flatMapLatest - start loading")
            emit(value = DataFetchResult.Loading)

            // A controller is present, so start fetching
            println("ScheduleRepositoryImpl: flatMapLatest - calling fetchScheduleSetVals")
            val networkResult: DataFetchResult<ScheduleEntries> = fetchScheduleSetVals(
                username = controller.user, password = controller.password
            )
            emit(value = networkResult)
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Loading
    )

    /**
     * Reactive state flow emitting the current combined schedule for all actuators.
     * Integrates network results with pending local modifications.
     */
    override val scheduleFlow: StateFlow<DataFetchResult<ScheduleEntries>> = combine(
        flow = networkFlow,
        flow2 = pendingUpdate
    ) { networkResult, pending ->
        if (pending != null) {
            // If there's a pending local update, show it instantly.
            DataFetchResult.Success(data = pending)
        } else {
            // Otherwise, show the latest result from the network.
            networkResult
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Loading
    )

    /**
     * Updates the local optimistic state of the entire schedule.
     *
     * @param newScheduleEntries The new schedule state to display immediately.
     */
    override fun updateLocalSchedule(newScheduleEntries: ScheduleEntries) {
        pendingUpdate.value = newScheduleEntries
    }

    /**
     * Commits all current schedule modifications to the remote server.
     */
    override suspend fun updateSchedule() {
        val controllerAddress: String? = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            return
        }

        if (scheduleFlow.value is DataFetchResult.Success) {
            val user = sessionManager.getControllerUser() ?: ""
            val password = sessionManager.getControllerPassword() ?: ""
            val port = sessionManager.getControllerPort()

            val url = "http://$controllerAddress:$port${WebApiUrls.ACTUATOR_SCHEDULE_UPDATE}"
            val authHeader = "Basic " + "$user:$password".encodeToByteArray().let { Base64.encode(it) }
            val scheduleEntries: ScheduleEntries =
                (scheduleFlow.value as DataFetchResult.Success).data

            val postParams = scheduleEntries.getApiParams()

            repositoryScope.launch(context.dispatcher) {
                apiService.updateScheduleData(url, authHeader, postParams)
            }
        }
    }

    /**
     * Discards any unsaved local modifications and reverts to the server state.
     */
    override fun reset() {
        pendingUpdate.value = null
    }

    /**
     * Internal helper to execute the network fetch and map raw DTOs into structured [ScheduleEntries].
     *
     * @param username The authentication username.
     * @param password The authentication password.
     * @return A [DataFetchResult] containing the processed [ScheduleEntries].
     */
    open suspend fun fetchScheduleSetVals(username: String, password: String): DataFetchResult<ScheduleEntries> {
        println("ScheduleRepositoryImpl: fetchScheduleSetVals - username: ")
        return withContext(context.dispatcher) {
            val url: String =
                "http://" + sessionManager.getControllerAddress() + ":" +
                sessionManager.getControllerPort() + WebApiUrls.ACTUATOR_SCHEDULE_LOAD
            val scheduleEntryReader = ScheduleEntryReader(logger, apiService, url, username, password)
            val result: DataFetchResult<List<ScheduleStringEntry>> =
                scheduleEntryReader.readScheduleEntryList()
            val processedResult: DataFetchResult<ScheduleEntries> = when (result) {
                is DataFetchResult.Success -> {
                    val scheduleStringEntryList: List<ScheduleStringEntry> = result.data
                    if (scheduleStringEntryList.isNotEmpty()) {
                        // Convert the list of ScheduleStringEntry to a list of ScheduleEntry
                        val scheduleEntryList: List<ScheduleEntry> =
                            scheduleStringEntryList.map { scheduleStringEntry ->
                                scheduleStringEntry.transformToSetVal()
                            }
                        val scheduleEntries: ScheduleEntries =
                            ScheduleEntriesFactory(scheduleEntryList).create()
                        DataFetchResult.Success(data = scheduleEntries)
                    } else {
                        val scheduleEntriesEmpty: ScheduleEntries = ScheduleEntriesFactory(
                            scheduleEntryList = emptyList()
                        ).create()
                        DataFetchResult.Success(data = scheduleEntriesEmpty)
                    }
                }
                is DataFetchResult.Error -> {
                    DataFetchResult.Error(
                        exception = result.exception,
                        errorMessage = result.errorMessage
                    )
                }
                is DataFetchResult.Loading -> {
                    DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_LOADING_SCHEDULE_INFO_FAILED
                    )
                }
            }
            return@withContext processedResult
        }
    }

    /**
     * Manually triggers a re-fetch of the schedule data from the server.
     */
    override suspend fun triggerRefresh() {
        // Check if the current state is a failure.
        if (failureResult.value != null) {
            // If it is, setting it to null is sufficient to trigger the combine/flatMapLatest chain again.
            failureResult.value = null
        } else {
            // If the system is NOT in a failure state, then we must emit the refreshTrigger
            // to force a new fetch, as nothing else has changed.
            refreshTrigger.emit(value = Unit)
        }
    }
}