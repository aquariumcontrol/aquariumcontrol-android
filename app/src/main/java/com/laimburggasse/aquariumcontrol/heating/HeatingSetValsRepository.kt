package com.laimburggasse.aquariumcontrol.heating

import android.util.Log
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

const val heatingSetValsSwitchOnTempInitVal = 21.0
const val heatingSetValsSwitchOffTempInitVal = 29.0

@Singleton
open class HeatingSetValsRepository @Inject constructor(
    private val sessionManager: SessionManager
) {
    // @todo: unify scope for all repositories
    // Dedicated scope for the repository ensures the StateFlow lives as long as the app.
    private val repositoryScope = CoroutineScope(context = SupervisorJob() + Dispatchers.Default)

    // Triggers for manual refreshes and optimistic updates.
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val pendingUpdate = MutableStateFlow<HeatingSetVals?>(value = null)

    // Holds the last successfully fetched data from the server, used for the 'reset' function.
    private var heatingSetValsBackup: HeatingSetVals? = null

    // The private flow only fetches data from the network.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val networkDataFlow: StateFlow<DataFetchResult<HeatingSetVals>> = combine(
        flow = sessionManager.selectedControllerProfile,
        flow2 = refreshTrigger.onStart { emit(value = Unit) } // Initial trigger
    ) { controller, _ ->
        controller
    }.flatMapLatest { controller: ControllerProfile? ->
        flow {
            if (controller == null) {
                // If no controller, emit a success state with initial values
                emit(value = DataFetchResult.Success(data = HeatingSetVals(
                    heatingSwitchOnTemp = heatingSetValsSwitchOnTempInitVal,
                    heatingSwitchOffTemp = heatingSetValsSwitchOffTempInitVal,
                )))
                return@flow
            }
            // A controller is present, start the fetching process
            emit(value = DataFetchResult.Error(exception = null, errorMessage = "Loading"))
            Log.d("HeatingRepository", "Fetching heating setpoints from server.")
            val networkResult: DataFetchResult<HeatingSetVals> = withContext(context = Dispatchers.IO) {
                val url = "http://${controller.address}${WebApiUrls.HEATING_GET_SET_VALS}"
                val heatingSetValsReader = HeatingSetValsReader(url)
                when (val setValsList: DataFetchResult<List<HeatingSetVals>> =
                    heatingSetValsReader.readHeatingSetVals()) {
                    is DataFetchResult.Success -> {
                        if (setValsList.data.isNotEmpty()) {
                            val firstItem = setValsList.data.first()
                            heatingSetValsBackup = firstItem // Update the backup
                            DataFetchResult.Success(data = firstItem)
                        } else {
                            DataFetchResult.Error(exception = null, errorMessage = "Server returned an empty list.")
                        }
                    }
                    is DataFetchResult.Error -> setValsList
                }
            }
            emit(value = networkResult)
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Error(exception = null, errorMessage = "Initial loading")
    )

    // The public flow now combines the stable network data with the fast-changing local edits.
    open val heatingSetValsFlow: StateFlow<DataFetchResult<HeatingSetVals>> = combine(
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
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = DataFetchResult.Error(exception = null, errorMessage = "Initial loading")
    )
    /**
     * Triggers a manual refresh of the heating setpoints.
     */
    open suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }

    /**
     * Applies an optimistic update to the local state without committing to the server.
     * The UI will show this change instantly.
     */
    open fun updateLocalValues(heatingSetVals: HeatingSetVals) {
        pendingUpdate.value = heatingSetVals
    }

    /**
     * Commits the current (potentially local) values to the server.
     */
    open suspend fun updateValues() {
        // Get the most recent values, either from the pending update or the last fetched state
        val currentValues: HeatingSetVals? = pendingUpdate.value ?:
            (heatingSetValsFlow.value as? DataFetchResult.Success)?.data

        if (currentValues != null) {
            withContext(context = Dispatchers.IO) {
                webApiPostRequestExecution(
                    sessionManager = sessionManager,
                    apiUrl = WebApiUrls.HEATING_POST_SET_VALS,
                    webApiPostRequest = WebApiPostRequest(),
                    optionalGetParams = mapOf(
                        WebApiParams.HEATING_SWITCH_ON_TEMPERATURE to currentValues.heatingSwitchOnTemp.toString(),
                        WebApiParams.HEATING_SWITCH_OFF_TEMPERATURE to currentValues.heatingSwitchOffTemp.toString()
                    ),
                )
            }
        }
    }

    /**
     * Reverts any uncommitted local changes by clearing the pending update.
     * The flow will automatically revert to the last successfully fetched state.
     */
    fun reset() {
        pendingUpdate.value = null
    }
}
