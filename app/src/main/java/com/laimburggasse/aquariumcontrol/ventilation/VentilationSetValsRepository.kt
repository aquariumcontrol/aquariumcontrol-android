package com.laimburggasse.aquariumcontrol.ventilation

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val ventilationSetValsSwitchOnTempInitVal = 28.0
const val ventilationSetValsSwitchOffTempInitVal = 22.0

open class VentilationSetValsRepository(
    private val sessionManager: SessionManager,
    externalScope: CoroutineScope? = null
) {
    private val _ventilationSetValsFlow = MutableStateFlow<DataFetchResult<VentilationSetVals>>(
        value = DataFetchResult.Success(data = VentilationSetVals(
            ventilationSwitchOffTemp = ventilationSetValsSwitchOffTempInitVal,
            ventilationSwitchOnTemp = ventilationSetValsSwitchOnTempInitVal
        ))
    )
    open val ventilationSetValsFlow = _ventilationSetValsFlow.asStateFlow()

    open fun updateLocalValues(ventilationSetVals: VentilationSetVals) {
        _ventilationSetValsFlow.value = DataFetchResult.Success(data = ventilationSetVals)
    }

    private var ventilationSetValsBackup: VentilationSetVals? = null

    open suspend fun fetchVentilationSetVals() {
        return withContext(context = Dispatchers.IO) {
            val url: String = "http://" + sessionManager.getControllerAddress() + WebApiUrls.VENTILATION_GET_SET_VALS
            val ventilationSetValsReader = VentilationSetValsReader(url)
            withContext(context = Dispatchers.IO) {
                val result: DataFetchResult<VentilationSetVals> =
                    when (val setValsList: DataFetchResult<List<VentilationSetVals>> =
                        ventilationSetValsReader.readVentilationSetVals()) {
                        is DataFetchResult.Success -> {
                            ventilationSetValsBackup = setValsList.data.first()
                            DataFetchResult.Success(data = setValsList.data.first())
                        }

                        is DataFetchResult.Error -> {
                            DataFetchResult.Error(
                                exception = setValsList.exception,
                                errorMessage = setValsList.errorMessage
                            )
                        }
                    }
                _ventilationSetValsFlow.value = result
            }
        }
    }

    open suspend fun updateValues() {
        if (_ventilationSetValsFlow.value is DataFetchResult.Success) {
            val ventilationSetVals: VentilationSetVals = (_ventilationSetValsFlow.value as DataFetchResult.Success).data
            webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.VENTILATION_POST_SET_VALS,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = mapOf(
                    WebApiParams.VENTILATION_SWITCH_ON_TEMPERATURE to ventilationSetVals.ventilationSwitchOnTemp.toString(),
                    WebApiParams.VENTILATION_SWITCH_OFF_TEMPERATURE to ventilationSetVals.ventilationSwitchOffTemp.toString()
                ),
            )
        }
    }

    fun reset() {
        ventilationSetValsBackup?.let {
            _ventilationSetValsFlow.value = DataFetchResult.Success(data = ventilationSetValsBackup!!)
        }
    }

    init {
        externalScope?.let {
            externalScope.launch {
                fetchVentilationSetVals()
            }
        }
    }
}