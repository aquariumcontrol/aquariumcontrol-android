package com.laimburggasse.aquariumcontrol.balling.data

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingProfileReader
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.controller.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class BallingProfilesRepository @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope
) {

    // For triggering manual refreshes
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    // A flow to hold the single pending optimistic update
    private val pendingUpdate = MutableStateFlow<BallingProfile?>(value = null)

    // A flow to hold the failure value
    private val failureResult = MutableStateFlow<DataFetchResult.Error?>(value = null)

    /**
     * A single, reactive Flow that provides the Balling Profiles.
     * It combines two triggers: the selected controller and the manual refresh trigger.
     * Whenever EITHER of them emits, it re-fetches the data.
     */
    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalCoroutinesApi::class)
    open val ballingProfiles: Flow<DataFetchResult<List<BallingProfile>>> =
        combine(
            // 1. We still listen to the selected controller profile.
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) },
            flow3 = pendingUpdate,
            flow4 = failureResult
        ) { controller, _, pending, failure ->
            Triple(first = controller, second = pending, third = failure)
        }
        .filter{ it.first != null }
        .flatMapLatest { (controller: ControllerProfile?, pending: BallingProfile?, failure: DataFetchResult.Error?) ->
            flow {
                if (failure != null) {
                    emit(value = failure)
                    return@flow
                }
                if (controller == null) {
                    emit(value = DataFetchResult.Error(
                        exception = null,
                        errorMessage = "No controller selected."
                    ))
                    return@flow
                }

                // If there is no failure and we have a controller, proceed with loading.
                emit(value = DataFetchResult.Loading)

                val url = "http://${controller.address}:${controller.port}${WebApiUrls.BALLING_GET_SET_VALS}"
                val reader = BallingProfileReader(url)
                // Perform the network request on a background thread.
                val networkResult: DataFetchResult<List<BallingProfile>> =
                    withContext(context = Dispatchers.IO) {
                        reader.readBallingProfiles()
                    }
                val finalResult: DataFetchResult<List<BallingProfile>> = if (
                        networkResult is DataFetchResult.Success
                ) {
                    // If there is a pending update, merge it now.
                    if (pending != null) {
                        val mergedList: List<BallingProfile> = networkResult.data.map { networkProfile ->
                            // If this profile's pumpId matches the pending update, use the updated version.
                            // Otherwise, use the one from the network.
                            if (networkProfile.pumpId == pending.pumpId)
                                pending
                            else
                                networkProfile
                        }
                        DataFetchResult.Success(data = mergedList)
                    } else { networkResult} // No pending update, just use the network result.
                } else { networkResult} // If the network call failed, just pass on the error.
                // Emit the final result: local update merged into network result.
                emit(value = finalResult)
            }
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = DataFetchResult.Loading
        )

    fun updateBallingProfile(ballingProfile: BallingProfile) {
        // set the updated profile as the pending update
        // this will trigger instant UI update
        pendingUpdate.value = ballingProfile
        val controllerAddress: String? = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            pendingUpdate.value = null
            failureResult.value = DataFetchResult.Error(
                exception = null,
                errorMessage = "No controller address."
            )
            return
        }

        repositoryScope.launch(context = Dispatchers.IO) {
            val requestSuccess: Boolean = withContext(context = Dispatchers.IO) {
                webApiPostRequestExecution(
                    sessionManager = sessionManager,
                    apiUrl = WebApiUrls.BALLING_POST_SET_VALS,
                    webApiPostRequest = WebApiPostRequest(),
                    optionalGetParams = mapOf(
                        WebApiParams.PUMP_ID to ballingProfile.pumpId.toString(),
                        WebApiParams.DOSING_VOLUME to ballingProfile.volume.toString()
                    )
                )
            }

            if (requestSuccess) {
                pendingUpdate.value = null
                failureResult.value = null
                refreshTrigger.emit(value = Unit) // This will cause the `profilesFlow` to re-fetch.
            } else {
                pendingUpdate.value = null
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = "Update of feed profile on server failed."
                )
            }
        }
    }

    /**
     * Public function to allow the ViewModel to trigger a manual refresh (e.g., for pull-to-refresh).
     */
    suspend fun triggerRefresh() {
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
