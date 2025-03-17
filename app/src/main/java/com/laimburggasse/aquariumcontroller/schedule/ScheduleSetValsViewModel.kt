package com.laimburggasse.aquariumcontroller.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Locale

class ScheduleSetValsViewModel(
    private var setValStartTime: Float,
    private var setValStopTime: Float,
    private var setValLimitationActive: Boolean,
) : ViewModel() {
    // This class will allow the user to edit the schedule for either
    // - Balling mineral dosing
    // - Refill
    // - Ventilation
    // - Heating

    private val _sliderRange = MutableLiveData<ClosedFloatingPointRange<Float>>()
    private val _lowerBoundValue = MutableLiveData<Float>()
    private val _upperBoundValue = MutableLiveData<Float>()
    private val _isLimitationActive = MutableLiveData<Boolean>()
    val sliderRange: LiveData<ClosedFloatingPointRange<Float>> = _sliderRange
    val lowerBoundValue: LiveData<Float> = _lowerBoundValue
    val upperBoundValue: LiveData<Float> = _upperBoundValue
    val isLimitationActive: LiveData<Boolean> = _isLimitationActive

    init {
        _sliderRange.value = setValStartTime..setValStopTime
        _lowerBoundValue.value = setValStartTime
        _upperBoundValue.value = setValStopTime
        _isLimitationActive.value = setValLimitationActive
    }

    val onRangeChangedBySlider: (Float, Float) -> Unit = { start, end ->
        _sliderRange.value = start..end
        _lowerBoundValue.value = start
        _upperBoundValue.value = end
    }

    val onLowerValueChanged: (Float) -> Unit = { value ->
        val currentRange = _sliderRange.value
        if (currentRange != null && value < currentRange.endInclusive) {
            _sliderRange.value = value..currentRange.endInclusive
            _lowerBoundValue.value = value
        }
    }

    val onUpperValueChanged: (Float) -> Unit = { value ->
        val currentRange = _sliderRange.value
        if (currentRange != null && value > currentRange.start) {
            _sliderRange.value = currentRange.start..value
            _upperBoundValue.value = value
        }
    }

    fun resetValues() {
        _sliderRange.value = setValStartTime..setValStopTime

        _lowerBoundValue.value = setValStartTime
        _upperBoundValue.value = setValStopTime
    }

    fun updateValues() {
        if (_sliderRange.value != null) {
            setValStartTime = sliderRange.value!!.start
            setValStopTime = sliderRange.value!!.endInclusive
            setValLimitationActive = isLimitationActive.value!!
        }
    }

    fun setValues(
        newSetValStartTime: Float,
        newSetValStopTime: Float,
        newSetValLimitationActive: Boolean,
    ) {
        setValStartTime = newSetValStartTime
        setValStopTime = newSetValStopTime
        setValLimitationActive = newSetValLimitationActive
        _sliderRange.value = setValStartTime..setValStopTime
        _lowerBoundValue.value = setValStartTime
        _upperBoundValue.value = setValStopTime
        _isLimitationActive.value = setValLimitationActive
    }

    fun formatTime(value: Float): String {
        val hours = value.toInt()
        val minutes = ((value - hours) * 60).toInt()
        return String.format(locale = Locale.US, "%02d:%02d", hours, minutes)
    }

    fun setLimitationActive(newState: Boolean) {
        _isLimitationActive.value = newState
    }

    fun getStartTime(): Float {
        return setValStartTime
    }

    fun getStopTime(): Float {
        return setValStopTime
    }

    fun isLimitationActive(): Boolean {
        return setValLimitationActive
    }
}