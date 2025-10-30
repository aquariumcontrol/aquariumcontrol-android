package com.laimburggasse.aquariumcontrol.balling.common

import android.util.Log
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class BallingProfilesRepository @Inject constructor(private val sessionManager: SessionManager) {

    // For triggering manual refreshes
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    // A flow to hold the single pending optimistic update
    private val pendingUpdate = MutableStateFlow<BallingProfile?>(value = null)

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
            flow3 = pendingUpdate
        ) { controller, _, pending ->
            Triple(first = controller, second = Unit, third = pending)
        }
        // 2. We use `filterNotNull()` to IGNORE the initial `null` emission at app startup.
        //    This effectively pauses the flow until a controller is actually selected.
        .filter{ it.first != null }
        // 3. `flatMapLatest` will now ONLY execute when it receives a non-null controller.
        .flatMapLatest { triple ->
            val (controller, _, pending) = triple
            flow {
                // @todo: implement Loading state
                // Emit Loading state before the network call.
                //emit(value = DataFetchResult.Error(exception = null, errorMessage = "Loading"))

                Log.d("BallingRepository", "Fetching balling profiles from server.")
                // Perform the network request on a background thread.
                val networkResult: DataFetchResult<List<BallingProfile>> =
                    withContext(context = Dispatchers.IO) {
                        val url =
                            "http://${controller?.address}${WebApiUrls.BALLING_GET_SET_VALS}"
                        val reader = BallingProfileReader(url)
                        reader.readBallingProfiles()
                    }
                val finalResult = if (networkResult is DataFetchResult.Success) {
                    // If there is a pending update, merge it now.
                    if (pending != null) {
                        Log.d(
                            "BallingRepository",
                            "Merging pending update into network result."
                        )
                        val mergedList = networkResult.data.map { networkProfile ->
                                // If this profile's pumpId matches the pending update, use the updated version.
                                // Otherwise, use the one from the network.
                                if (networkProfile.pumpId == pending.pumpId)
                                    pending
                                else
                                    networkProfile
                            }
                        DataFetchResult.Success(data = mergedList)
                    } else {
                        // No pending update, just use the network result.
                        networkResult
                    }
                } else {
                    // If the network call failed, just pass on the error.
                    networkResult
                }
                // Emit the final result: local update merged into network result.
                emit(value = finalResult)
            }
        }

    suspend fun updateBallingProfile(ballingProfile: BallingProfile) {
        // set the updated profile as the pending update
        // this will trigger instant UI update
        pendingUpdate.value = ballingProfile
        Log.d("BallingRepository", "Updating balling profile: $ballingProfile")
        val controllerAddress: String? = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            Log.e("BallingRepository", "Update failed: No controller address.")
            pendingUpdate.value = null
            return
        }

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

        // 3. Instead of updating a dead flow, trigger a refresh of the live one.
        if (requestSuccess) {
            Log.d("BallingRepository", "Server update successful. Triggering refresh.")
            pendingUpdate.value = null
            refreshTrigger.emit(value = Unit) // This will cause the `profilesFlow` to re-fetch.
        } else {
            Log.e("BallingRepository", "Server update failed.")
            pendingUpdate.value = null
            // @todo: emit an event to the UI to show a failure.
        }
    }

    // @todo: implement pull-to-refresh
    /**
     * Public function to allow the ViewModel to trigger a manual refresh (e.g., for pull-to-refresh).
     */
    suspend fun triggerRefresh() {
        refreshTrigger.emit(Unit)
    }
}
