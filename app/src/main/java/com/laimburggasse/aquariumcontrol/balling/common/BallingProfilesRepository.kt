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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class BallingProfilesRepository @Inject constructor(private val sessionManager: SessionManager) {

    // for triggering manual refreshes
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    /**
     * A single, reactive Flow that provides the Balling Profiles.
     * It combines two triggers: the selected controller and the manual refresh trigger.
     * Whenever EITHER of them emits, it re-fetches the data.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    open val ballingProfiles: Flow<DataFetchResult<List<BallingProfile>>> =
        combine(
            // 1. We still listen to the selected controller profile.
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) }
        ) { controller, _ -> controller }
            // 2. We use `filterNotNull()` to IGNORE the initial `null` emission at app startup.
            //    This effectively pauses the flow until a controller is actually selected.
            .filterNotNull()
            // 3. `flatMapLatest` will now ONLY execute when it receives a non-null controller.
            .flatMapLatest { controller ->
                flow {
                    // @todo: implement Loading state
                    // Emit Loading state before the network call.
                    // emit(DataFetchResult.Error(exception = null, errorMessage = "still null"))

                    // Perform the network request on a background thread.
                    val result: DataFetchResult<List<BallingProfile>> =
                        withContext(context = Dispatchers.IO) {
                            val url =
                                "http://${controller.address}${WebApiUrls.BALLING_GET_SET_VALS}"
                            val reader = BallingProfileReader(url)
                            reader.readBallingProfiles()
                        }

                    // Emit the final result (Success or Error) from the network call.
                    emit(value = result)
                }
            }

    suspend fun updateBallingProfile(ballingProfile: BallingProfile) {
        Log.d("BallingRepository", "Updating balling profile: $ballingProfile")
        val controllerAddress: String? = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            Log.e("BallingRepository", "Update failed: No controller address.")
            return
        }

        val requestSuccess: Boolean = withContext(Dispatchers.IO) {
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
            refreshTrigger.emit(value = Unit) // This will cause the `profilesFlow` to re-fetch.
        } else {
            Log.e("BallingRepository", "Server update failed.")
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
