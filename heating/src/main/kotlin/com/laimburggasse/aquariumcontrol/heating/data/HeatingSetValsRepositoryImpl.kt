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

package com.laimburggasse.aquariumcontrol.heating.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.domain.model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.heating.data.remote.HeatingSetValsApiService
import com.laimburggasse.aquariumcontrol.heating.data.remote.HeatingSetValsReader
import com.laimburggasse.aquariumcontrol.heating.domain.heatingSetValsSanityCheck
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingSetValsRepository
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


@ApplicationScope
@RequiresApi(Build.VERSION_CODES.O)
open class HeatingSetValsRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: HeatingSetValsApiService,
    private val repositoryScope: CoroutineScope,
    private val context: IoDispatcher
): HeatingSetValsRepository {
    // Triggers for manual refreshes and optimistic updates.
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val pendingUpdate = MutableStateFlow<HeatingSetVals?>(value = null)

    // Holds the last successfully fetched data from the server, used for the 'reset' function.
    private var heatingSetValsBackup: HeatingSetVals? = null

    // A flow to hold the failure value
    private val failureResult = MutableStateFlow<DataFetchResult.Error?>(value = null)

    // The private flow only fetches data from the network.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val networkDataFlow: StateFlow<DataFetchResult<HeatingSetVals>> = combine(
        flow = sessionManager.selectedControllerProfile,
        flow2 = refreshTrigger.onStart { emit(value = Unit) }, // Initial trigger
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
            emit(value = DataFetchResult.Loading)

            val networkResult: DataFetchResult<HeatingSetVals> = withContext(context.dispatcher) {
                val url = "http://${controller.address}:${controller.port}${WebApiUrls.HEATING_LOAD_SET_VALS}"
                val heatingSetValsReader = HeatingSetValsReader(
                    apiService, url, username = controller.user, password = controller.password
                )
                when (val setValsList: DataFetchResult<List<HeatingSetVals>> =
                    heatingSetValsReader.readHeatingSetVals()) {
                    is DataFetchResult.Success -> {
                        if (setValsList.data.isNotEmpty()) {
                            val firstItem: HeatingSetVals = setValsList.data.first()
                            val checkResult: SetValsSanityCheckResult<Unit> =
                                heatingSetValsSanityCheck(heatingSetVals = firstItem)
                            when (checkResult) {
                                is SetValsSanityCheckResult.Error -> {
                                    DataFetchResult.Error(
                                        exception = null,
                                        errorMessage = checkResult.errorMessage
                                    )
                                }
                                is SetValsSanityCheckResult.Success<*> -> {
                                    heatingSetValsBackup = firstItem // Update the backup
                                    DataFetchResult.Success(data = firstItem)
                                }
                            }
                        } else {
                            DataFetchResult.Error(
                                exception = null,
                                errorMessage = GlobalConstants.ERR_MSG_SERVER_RETURNED_EMPTY_LIST
                            )
                        }
                    }
                    is DataFetchResult.Error -> setValsList
                    is DataFetchResult.Loading -> DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_LOADING_SET_VALS_FAILED
                    )
                }
            }
            emit(value = networkResult)
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Loading
    )

    // The public flow now combines the stable network data with the fast-changing local edits.
    override val heatingSetValsFlow: StateFlow<DataFetchResult<HeatingSetVals>> = combine(
        flow = networkDataFlow,
        flow2 = pendingUpdate
    ) { networkResult, pending ->
        if (pending != null) {
            // Use local data when the user has modified something.
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
     * Applies an optimistic update to the local state without committing to the server.
     * The UI will show this change instantly.
     */
    override fun updateLocalValues(heatingSetVals: HeatingSetVals) {
        pendingUpdate.value = heatingSetVals
    }

    /**
     * Commits the current (potentially local) values to the server.
     */
    override suspend fun updateValues() {
        // Get the most recent values, either from the pending update or the last fetched state
        val currentValues: HeatingSetVals? = pendingUpdate.value ?:
            (heatingSetValsFlow.value as? DataFetchResult.Success)?.data

        val controllerAddress: String? = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            pendingUpdate.value = null
            failureResult.value = DataFetchResult.Error(
                exception = null,
                errorMessage = GlobalConstants.ERR_MSG_INVALID_CONTROLLER_ADDRESS
            )
            return
        }

        if (currentValues != null) {
            repositoryScope.launch(context.dispatcher) {
                val user = sessionManager.getControllerUser() ?: ""
                val password = sessionManager.getControllerPassword() ?: ""
                val port = sessionManager.getControllerPort()

                val url = "http://$controllerAddress:$port${WebApiUrls.HEATING_UPDATE_SET_VALS}"
                val authHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("$user:$password".toByteArray())

                val postParams = mapOf(
                    WebApiParams.HEATING_SWITCH_ON_TEMPERATURE to currentValues.heatingSwitchOnTemp.toString(),
                    WebApiParams.HEATING_SWITCH_OFF_TEMPERATURE to currentValues.heatingSwitchOffTemp.toString()
                )

                // Make the Ktor call
                val requestSuccess = try {
                    apiService.updateHeatingSetVals(url, authHeader, postParams)
                } catch (e: Exception) { // recording exception in crashlytics
                    Firebase.crashlytics.recordException(e)
                    false // Catch network timeouts or connection errors
                }

                if (requestSuccess) {
                    pendingUpdate.value = null
                    failureResult.value = null
                    refreshTrigger.emit(value = Unit) // This will cause the `profilesFlow` to re-fetch.
                } else {
                    pendingUpdate.value = null
                    failureResult.value = DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_UPDATING_SET_VALS_FAILED
                    )
                }
            }
        }
    }

    /**
     * Reverts any uncommitted local changes by clearing the pending update.
     * The flow will automatically revert to the last successfully fetched state.
     */
    override fun reset() {
        pendingUpdate.value = null
    }

    /**
     * Triggers a manual refresh of the heating setpoints.
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
