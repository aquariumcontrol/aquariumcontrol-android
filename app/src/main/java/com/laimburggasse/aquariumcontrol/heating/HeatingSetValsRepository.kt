package com.laimburggasse.aquariumcontrol.heating

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

const val heatingSetValsSwitchOnTempInitVal = 21.0
const val heatingSetValsSwitchOffTempInitVal = 29.0

open class HeatingSetValsRepository(
    private val sessionManager: SessionManager,
    externalScope: CoroutineScope? = null
) {
    private val _heatingSetValsFlow = MutableStateFlow<DataFetchResult<HeatingSetVals>>(
        value = DataFetchResult.Success(data = HeatingSetVals(
            heatingSwitchOnTemp = heatingSetValsSwitchOnTempInitVal,
            heatingSwitchOffTemp = heatingSetValsSwitchOffTempInitVal,
        ))
    )
    open val heatingSetValsFlow = _heatingSetValsFlow.asStateFlow()

    private var heatingSetValsBackup: HeatingSetVals? = null

    open suspend fun fetchHeatingSetVals() {
        val url: String = "http://" + sessionManager.getControllerAddress() + WebApiUrls.HEATING_GET_SET_VALS
        val heatingSetValsReader = HeatingSetValsReader(url)
        withContext(context = Dispatchers.IO) {
            val result: DataFetchResult<HeatingSetVals> =
                when (val setValsList: DataFetchResult<List<HeatingSetVals>> =
                    heatingSetValsReader.readHeatingSetVals()) {
                    is DataFetchResult.Success -> {
                        heatingSetValsBackup = setValsList.data.first()
                        DataFetchResult.Success(data = setValsList.data.first())
                    }
                    is DataFetchResult.Error -> {
                        DataFetchResult.Error(
                            exception = setValsList.exception,
                            errorMessage = setValsList.errorMessage
                        )
                    }
                }
            _heatingSetValsFlow.value = result
        }
    }

    open fun updateLocalValues(heatingSetVals: HeatingSetVals) {
        _heatingSetValsFlow.value = DataFetchResult.Success(heatingSetVals)
    }

    open suspend fun updateValues() {
        if (_heatingSetValsFlow.value is DataFetchResult.Success) {
            val heatingSetVals: HeatingSetVals = (_heatingSetValsFlow.value as DataFetchResult.Success).data
            return withContext(context = Dispatchers.IO) {
                webApiPostRequestExecution(
                    sessionManager = sessionManager,
                    apiUrl = WebApiUrls.HEATING_POST_SET_VALS,
                    webApiPostRequest = WebApiPostRequest(),
                    optionalGetParams = mapOf(
                        WebApiParams.HEATING_SWITCH_ON_TEMPERATURE to heatingSetVals.heatingSwitchOnTemp.toString(),
                        WebApiParams.HEATING_SWITCH_OFF_TEMPERATURE to heatingSetVals.heatingSwitchOffTemp.toString()
                    ),
                )
            }
        }
    }

    fun reset() {
        heatingSetValsBackup?.let {
            _heatingSetValsFlow.value = DataFetchResult.Success(it)
        }
    }

    init {
        externalScope?.let {
            externalScope.launch {
                fetchHeatingSetVals()
            }
        }
    }
}