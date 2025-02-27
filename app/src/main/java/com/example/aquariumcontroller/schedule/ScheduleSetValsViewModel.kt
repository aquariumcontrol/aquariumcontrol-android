package com.example.aquariumcontroller.schedule

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.ApiParams
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
        Log.d("ScheduleSetValsViewModel", "onRangeChangedBySlider: $start - $end")
        _sliderRange.value = start..end
        _lowerBoundValue.value = start
        _upperBoundValue.value = end
    }

    val onLowerValueChanged: (Float) -> Unit = { value ->
        val currentRange = _sliderRange.value
        if (currentRange != null && value < currentRange.endInclusive) {
            Log.d("ScheduleSetValsViewModel", "onLowerValueChanged: $value")
            _sliderRange.value = value..currentRange.endInclusive
            _lowerBoundValue.value = value
        }
    }

    val onUpperValueChanged: (Float) -> Unit = { value ->
        val currentRange = _sliderRange.value
        if (currentRange != null && value > currentRange.start) {
            Log.d("ScheduleSetValsViewModel", "onUpperValueChanged: $value")
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
        } else {
            Log.d("ScheduleSetValsViewModel", "updateValues: _sliderRangeBalling.value is null")
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

    private fun getStartTimeString(): String {
        Log.d(
            "ScheduleSetValsViewModel",
            "getStartTimeString: $setValStartTime"
        )
        return formatTime(setValStartTime)
    }

    private fun getStopTimeString(): String {
        Log.d(
            "ScheduleSetValsViewModel",
            "getBallingStopTimeString: $setValStopTime"
        )
        return formatTime(setValStopTime)
    }

    fun getApiParams(): Map<String, String> {
        // todo: move this functionality elsewhere
        return mapOf(
            ApiParams.SCHEDULE_BALLING_START_TIME to getStartTimeString(),
            ApiParams.SCHEDULE_BALLING_STOP_TIME to getStopTimeString(),
            ApiParams.SCHEDULE_BALLING_IS_ACTIVE to setValLimitationActive.toString(),
            /* ApiParams.SCHEDULE_REFILL_START_TIME to getRefillStartTimeString(),
            ApiParams.SCHEDULE_REFILL_STOP_TIME to getRefillStopTimeString(),
            ApiParams.SCHEDULE_REFILL_IS_ACTIVE to scheduleSetVals.refillLimitationActive.toString()
            ApiParams.SCHEDULE_VENTILATION_START_TIME to getVentilationStartTimeString(),
            ApiParams.SCHEDULE_VENTILATION_STOP_TIME to getVentilationStopTimeString(),
            ApiParams.SCHEDULE_VENTILATION_IS_ACTIVE to scheduleSetVals.ventilationLimitationActive.toString()
            ApiParams.SCHEDULE_HEATING_START_TIME to getHeatingStartTimeString(),
            ApiParams.SCHEDULE_HEATING_STOP_TIME to getHeatingStopTimeString(),
            ApiParams.SCHEDULE_HEATING_IS_ACTIVE to scheduleSetVals.heatingLimitationActive.toString()*/
        )
    }

    fun setLimitationActive(newState: Boolean) {
        _isLimitationActive.value = newState
    }
}