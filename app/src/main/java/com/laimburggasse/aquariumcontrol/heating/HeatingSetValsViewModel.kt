package com.laimburggasse.aquariumcontrol.heating

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HeatingSetValsViewModel(
    private var heatingSetVals: HeatingSetVals,
) : ViewModel() {
    // This class will allow the user to edit the heating control parameters
    private val _sliderRange = MutableLiveData<ClosedFloatingPointRange<Float>>()
    private val _lowerBoundValue = MutableLiveData<Double>()
    private val _upperBoundValue = MutableLiveData<Double>()
    val sliderRange: LiveData<ClosedFloatingPointRange<Float>> = _sliderRange
    val lowerBoundValue: LiveData<Double> = _lowerBoundValue
    val upperBoundValue: LiveData<Double> = _upperBoundValue

    var heatingSetValsUiState: HeatingSetValsUiState by mutableStateOf(value = HeatingSetValsUiState.Loading)
        private set

    val onRangeChangedBySlider: (Float, Float) -> Unit = { start, end ->
        Log.d("HeatingSetValsViewModel", "onRangeChangedBySlider: $start - $end")
        _sliderRange.value = start..end
        _lowerBoundValue.value = start.toDouble()
        _upperBoundValue.value = end.toDouble()
    }

    val onLowerValueChanged: (Float) -> Unit = { value ->
        val currentRange = _sliderRange.value
        if (currentRange != null && value < currentRange.endInclusive) {
            Log.d("HeatingSetValsViewModel", "onLowerValueChanged: $value")
            _sliderRange.value = value..currentRange.endInclusive
            _lowerBoundValue.value = value.toDouble()
        }
    }

    val onUpperValueChanged: (Float) -> Unit = { value ->
        val currentRange = _sliderRange.value
        if (currentRange != null && value > currentRange.start) {
            Log.d("HeatingSetValsViewModel", "onUpperValueChanged: $value")
            _sliderRange.value = currentRange.start..value
            _upperBoundValue.value = value.toDouble()
        }
    }

    fun resetValues() {
        _sliderRange.value =
            heatingSetVals.heatingSwitchOnTemp.toFloat()..
                    heatingSetVals.heatingSwitchOffTemp.toFloat()

        _lowerBoundValue.value = heatingSetVals.heatingSwitchOnTemp
        _upperBoundValue.value = heatingSetVals.heatingSwitchOffTemp
    }

    fun updateValues() {
        if (_sliderRange.value != null) {
            heatingSetVals.heatingSwitchOnTemp = sliderRange.value!!.start.toDouble()
            heatingSetVals.heatingSwitchOffTemp = sliderRange.value!!.endInclusive.toDouble()
        } else {
            Log.d("HeatingSetValsViewModel", "updateValues: _sliderRange.value is null")
        }
    }

    fun setValues(newHeatingSetVals: HeatingSetVals) {
        this.heatingSetVals.heatingSwitchOnTemp = newHeatingSetVals.heatingSwitchOnTemp
        this.heatingSetVals.heatingSwitchOffTemp = newHeatingSetVals.heatingSwitchOffTemp
        _sliderRange.value =
            this.heatingSetVals.heatingSwitchOnTemp.toFloat()..this.heatingSetVals.heatingSwitchOffTemp.toFloat()
        _lowerBoundValue.value = heatingSetVals.heatingSwitchOnTemp
        _upperBoundValue.value = heatingSetVals.heatingSwitchOffTemp
    }

    fun getHeatingSwitchOnTempString(): String {
        return heatingSetVals.heatingSwitchOnTemp.toString()
    }

    fun getHeatingSwitchOffTempString(): String {
        return heatingSetVals.heatingSwitchOffTemp.toString()
    }

    fun loadData(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            val result: DataFetchResult<List<HeatingSetVals>> = fetchHeatingSetVals(
                mainViewModel
            )
            // @ todo: transfer content of UiState to other class attributes
            heatingSetValsUiState = when(result) {
                is DataFetchResult.Success -> {
                    val heatingSetValsList: List<HeatingSetVals> = result.data
                    if (heatingSetValsList.isEmpty()) {
                        HeatingSetValsUiState.Empty
                    }
                    else {
                        HeatingSetValsUiState.Success(heatingSetVals = heatingSetValsList.first())
                    }
                }
                is DataFetchResult.Error -> {
                    HeatingSetValsUiState.Error(errorMessage = result.errorMessage)
                }
            }
        }
    }

    private suspend fun fetchHeatingSetVals(
        mainViewModel: MainViewModel,
    ): DataFetchResult<List<HeatingSetVals>> {
        return withContext(Dispatchers.IO) {
            val url: String = "http://" + mainViewModel.getControllerAddress() + WebApiUrls.HEATING_GET_SET_VALS
            val heatingSetValsReader = HeatingSetValsReader(url)
            heatingSetValsReader.readHeatingSetVals()
        }
    }
}