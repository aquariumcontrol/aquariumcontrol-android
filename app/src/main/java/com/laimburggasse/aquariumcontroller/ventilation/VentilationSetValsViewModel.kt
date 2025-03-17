package com.laimburggasse.aquariumcontroller.ventilation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VentilationSetValsViewModel(
    private var ventilationSetVals: VentilationSetVals,
) : ViewModel() {
    // This class will allow the user to edit the ventilation control parameters
    private val _sliderRange = MutableLiveData<ClosedFloatingPointRange<Float>>()
    private val _lowerBoundValue = MutableLiveData<Double>()
    private val _upperBoundValue = MutableLiveData<Double>()
    val sliderRange: LiveData<ClosedFloatingPointRange<Float>> = _sliderRange
    val lowerBoundValue: LiveData<Double> = _lowerBoundValue
    val upperBoundValue: LiveData<Double> = _upperBoundValue

    val onRangeChangedBySlider: (Float, Float) -> Unit = { start, end ->
        Log.d("VentilationSetValsViewModel", "onRangeChangedBySlider: $start - $end")
        _sliderRange.value = start..end
        _lowerBoundValue.value = start.toDouble()
        _upperBoundValue.value = end.toDouble()
    }

    val onLowerValueChanged: (Float) -> Unit = { value ->
        val currentRange = _sliderRange.value
        if (currentRange != null && value < currentRange.endInclusive) {
            Log.d("VentilationSetValsViewModel", "onLowerValueChanged: $value")
            _sliderRange.value = value..currentRange.endInclusive
            _lowerBoundValue.value = value.toDouble()
        }
    }

    val onUpperValueChanged: (Float) -> Unit = { value ->
        val currentRange = _sliderRange.value
        if (currentRange != null && value > currentRange.start) {
            Log.d("VentilationSetValsViewModel", "onUpperValueChanged: $value")
            _sliderRange.value = currentRange.start..value
            _upperBoundValue.value = value.toDouble()
        }
    }

    fun resetValues() {
        _sliderRange.value =
            ventilationSetVals.ventilationSwitchOffTemp.toFloat()..
                    ventilationSetVals.ventilationSwitchOnTemp.toFloat()

        _lowerBoundValue.value = ventilationSetVals.ventilationSwitchOffTemp
        _upperBoundValue.value = ventilationSetVals.ventilationSwitchOnTemp
    }

    fun updateValues() {
        if (_sliderRange.value != null) {
            ventilationSetVals.ventilationSwitchOffTemp = sliderRange.value!!.start.toDouble()
            ventilationSetVals.ventilationSwitchOnTemp = sliderRange.value!!.endInclusive.toDouble()
        } else {
            Log.d("VentilationSetValsViewModel", "updateValues: _sliderRange.value is null")
        }
    }

    fun setValues(newVentilationSetVals: VentilationSetVals) {
        this.ventilationSetVals.ventilationSwitchOnTemp = newVentilationSetVals.ventilationSwitchOnTemp
        this.ventilationSetVals.ventilationSwitchOffTemp = newVentilationSetVals.ventilationSwitchOffTemp
        _sliderRange.value =
            this.ventilationSetVals.ventilationSwitchOffTemp.toFloat()..this.ventilationSetVals.ventilationSwitchOnTemp.toFloat()
        _lowerBoundValue.value = ventilationSetVals.ventilationSwitchOffTemp
        _upperBoundValue.value = ventilationSetVals.ventilationSwitchOnTemp
    }

    fun getVentilationSwitchOnTempString(): String {
        Log.d("VentilationSetValsViewModel", "getVentilationSwitchOnTempString: ${ventilationSetVals.ventilationSwitchOnTemp}")
        return ventilationSetVals.ventilationSwitchOnTemp.toString()
    }

    fun getVentilationSwitchOffTempString(): String {
        Log.d("VentilationSetValsViewModel", "getVentilationSwitchOffTempString: ${ventilationSetVals.ventilationSwitchOffTemp}")
        return ventilationSetVals.ventilationSwitchOffTemp.toString()
    }
}