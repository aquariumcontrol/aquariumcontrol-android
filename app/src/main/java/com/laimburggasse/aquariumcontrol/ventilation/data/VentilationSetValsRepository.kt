package com.laimburggasse.aquariumcontrol.ventilation.data

import com.laimburggasse.aquariumcontrol.GlobalConstants
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.controller.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.lib_composables.domain.model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.ventilation.data.remote.VentilationSetValsReader
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import com.laimburggasse.aquariumcontrol.ventilation.domain.ventilationSetValsSanityCheck
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
open class VentilationSetValsRepository @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope
) {
    // Triggers for manual refreshes and optimistic updates.
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val pendingUpdate = MutableStateFlow<VentilationSetVals?>(value = null)

    // Holds the last successfully fetched data from the server, used for the 'reset' function.
    private var ventilationSetValsBackup: VentilationSetVals? = null

    // A flow to hold the failure value
    private val failureResult = MutableStateFlow<DataFetchResult.Error?>(value = null)

    // The private flow only fetches data from the network.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val networkDataFlow: StateFlow<DataFetchResult<VentilationSetVals>> = combine(
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

            // UI shall temporarily show loading state, while fetching data
            emit(value = DataFetchResult.Loading)

            val networkResult: DataFetchResult<VentilationSetVals> = withContext(context = Dispatchers.IO) {
                val url = "http://${controller.address}:${controller.port}${WebApiUrls.VENTILATION_GET_SET_VALS}"
                val ventilationSetValsReader = VentilationSetValsReader(url)
                when (val setValsList: DataFetchResult<List<VentilationSetVals>> =
                    ventilationSetValsReader.readVentilationSetVals()) {
                    is DataFetchResult.Success -> {
                        if (setValsList.data.isNotEmpty()) {
                            val firstItem: VentilationSetVals = setValsList.data.first()
                            val checkResult: SetValsSanityCheckResult<Unit> = ventilationSetValsSanityCheck(ventilationSetVals = firstItem)
                            when (checkResult) {
                                is SetValsSanityCheckResult.Error -> {
                                    DataFetchResult.Error(
                                        exception = null,
                                        errorMessage = checkResult.errorMessage
                                    )
                                }
                                is SetValsSanityCheckResult.Success<*> -> {
                                    ventilationSetValsBackup = firstItem // Update the backup
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
    open val ventilationSetValsFlow: StateFlow<DataFetchResult<VentilationSetVals>> = combine(
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
    open fun updateLocalValues(ventilationSetVals: VentilationSetVals) {
        pendingUpdate.value = ventilationSetVals
    }

    /**
     * Commits the current (potentially local) values to the server.
     */
    open suspend fun updateValues() {
        // Get the most recent values, either from the pending update or the last fetched state
        val currentValues: VentilationSetVals? = pendingUpdate.value ?:
        (ventilationSetValsFlow.value as? DataFetchResult.Success)?.data

        if (currentValues != null) {
            repositoryScope.launch(context = Dispatchers.IO) {
                val success: Boolean = webApiPostRequestExecution(
                    sessionManager = sessionManager,
                    apiUrl = WebApiUrls.VENTILATION_POST_SET_VALS,
                    webApiPostRequest = WebApiPostRequest(),
                    optionalGetParams = mapOf(
                        WebApiParams.VENTILATION_SWITCH_ON_TEMPERATURE to
                                currentValues.ventilationSwitchOnTemp.toString(),
                        WebApiParams.VENTILATION_SWITCH_OFF_TEMPERATURE to
                                currentValues.ventilationSwitchOffTemp.toString()
                    ),
                )
                if (!success) {
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
    fun reset() {
        pendingUpdate.value = null
    }

    /**
     * Triggers a manual refresh of the heating setpoints.
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
